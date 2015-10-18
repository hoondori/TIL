# YARN (Yet Another Resource Negotiator)
generic resource management framework for implementing distributed application

## YARN의 탄생 스토리
### 1세대 : Yahoo 초창기 때 MapReduce 수행을 위해 private-cluster 운영
* 각각의 사용자들이 소수의 자기 노드로 폐쇄형 운영
* **Multitenency 지원 안됨**
* 한정된 자원(node)를 다수의 사용자가 공유하는 문제 => 수동으로 협의 => 불편
* 우리로 예기하면 Vmware 자원을 메일로 사용 알림을 하는 상황

### 2세대 : Hadoop on Demand(HOD)
* 목표 : MapReduce 수행을 위한 multittency on shared cluster of nodes 
* YARN 초창기 모델
* 매 사용자의 요청마다 새로 private cluster를 동적으로 생성/파괴하는 구조
 * 사용자가 어떤 node를 쓸 건지 선택해야
 * **worker node는 다른 사용자와 공유가 안됨**

### 3세대 : Resource managment inside Hadoop
* resource-management가 Hadoop 안으로 들어온 구조
* JobTracker, TaskTracker
* Start running jobtracker daemon as a shared resource across jobs, across users
* ==오직 MapReduce 작업을 위해서만 존재==

### 4세대 : YARN
* MapReduce 밖으로 resource management가 다시 나옴
* 심지어 non-mapreduce 한 작업애 대해서도 resource management 역할
* 모든 computing 자원(ex. CPU, Memory) 에 대해서 multitenency 지원

## 10 Requirements of cluster management
YARN은 이러한 사항을 잘 준수하고 있다.

* Scalability
* Serviceability
* Multitenancy
* **Locality Awareness** : computation을 최대한 data 가깝게 이동
* **High Cluster Utilization** : node의 자원 이용률의 극대화
* Secure and Auditable Operation
* Reliability and Availability
* **Support for Programming Model Diversity** : non MapReduce 지원
* Flexible Resource Model
* Backward Compatibility - legacy MapReduce 지원


## Hadoop 1.0에서의 resource management에 대해 알아보기
![hadoop_yarn_1](https://github.com/hoondori/TIL/blob/master/images/hadoop_yarn_1.png)


* JobTracker의 2가지 역할
 * resource management : managing worker nodes, (or jobtrackers)
 * scheduling/monitoring resource
* TaskTracker는 dumb executor
 * JobTracker가 쪼개준 대로 task들을 순서대로 수행
 * 수행 결과를 jobtracker에게 보고




## Hadoop 2.0 YARN
![hadoop_yarn_2](https://github.com/hoondori/TIL/blob/master/images/hadoop_yarn_2.png)

기존 jobtracker의 두 가지 역할을 ResourceManager와 ApplicationMaster로 이원화

* ResourceMananger는 모든 app에 의해 이용되는 모든 자원에 대한 관리 역할을 하는 반면
* ApplicationMaster는 framework-specific entity로서 ResourceManager와 resource 할당을 협상하는 역할
* NodeManager는 실제 task 수행자
* 비유를 하자면
 * ResourceManager는 일종의 회사 전체의 자원관리를 하는 부서
 * 각 팀은 자신의 팀에 필요한 자원 소요를 측정해서 중앙 자원관리 부서와 협상해서 자원을 타내는 상황
 * NodeManager는 일종의 실무 담당자(사원)

### ResourceManager
* pluggable pure-scheduler를 갖는다.
* 실제 필요한 리소스들을 각 application에 할당해 주는 역할
* pure
  * not based on monitoring/tracking status of job/task
  * but based on resource requirements ==> resource container
* 다양한 sizing의 resource container들이 있고 각 application의 필요량에 대응해서 통밥으로 resource container를 할당
* Hadoop 1.0과 가장 큰 차이점
 * Application-specific or framework-specific한 로직은 모두 application master로 이동하여 가장 generic한 것만 남음
 * 실패 작업 재실행(fault-tolerance)도 여기에서 빠짐


### ApplicationMaster
* Openness
 * ex) MapReduce, MPI, Graph processing
* Scalability
 * 기존에는 jobtracker에서 scheduling하랴, fault-tolerant하랴... not scalable

### ResourceRequest and ResourceContainer
![hadoop_yarn_5](https://github.com/hoondori/TIL/blob/master/images/hadoop_yarn_5.png)

* Resource Request via ApplicationMaster
 * 나한테 이정도의 메모리/CPU/Storage 줘!! (현재 YARN은 메모리랑 CPU만 지원)
 * 그리고 난 map-reduce 고 HDFS사용할 건데 어디어디 노드 좀 주면 나랑 가까워서 좋을 것 같기는 해. 고려좀..
* Accepted by ResoureManager => ResourceContainer
 * 그래 알겠어. 이러이러한 노드에서 이정도의 메모리/CPU 쓸 수 있겠네. 이거 가지고 NodeManager한테 가봐
 * 실제 자원을 준게 아니라 자원 사용 허가증 종이 쪼가리를 준 것임.
 * 실제 해당 NodeManager가 무지 바쁘면 말짱 꽝

#### Resource Reclamation (준거 다시 뺏기)
* ResourceManater says
 * 여기저기서 resource 달라고 난리도 아님 ( resource scarce )
 * App #1 아. 아까 줬던 것 중에서 조금만 반납해.
* ApplicationMaster says
 * 알겠어... 어쩌지.. 남은 container를 쥐어짜자.
 * 일단 container 하나 반납하고, 거기서 하던 것 status 저장해서 다른 container에 시켜야지


### Scheduler

#### 존재 목적
* ResourceManager 내에 위치
* 다양한 목표 존재
 * 정확성 : capacity assurance, SLA
 * 공정성 : 누구만  많이 주면 안됨
 * 효율성 : 모든 node를 쉴 틈 없이 100% 활용하고 싶음
* 모두 충족될 수 없는 Trade-off 관계이므로 특정 목표를 더 선호하는 다양한 scheduler들이 존재
* 이 중 하나를 원하는 대로 plug-in

#### 종류

##### FIFO
![hadoop_yarn_3](https://github.com/hoondori/TIL/blob/master/images/hadoop_yarn_3.png)

* 공정성은 극대화하나 다른 목표들은 매우 poor
* small workloads on large-scale clusters인 경우나 적합하나 자원 경쟁이 극심한 상황에서는 부적합

##### Capacity
![hadoop_yarn_4](https://github.com/hoondori/TIL/blob/master/images/hadoop_yarn_4.png)

* 각 organizational unit or group별로 독립된 queue를 가짐, Admin 설정
* 각 queue는 minimum guranteed capacipty, maximum soft/hard limits of capcity를 가짐
 * 인사팀 용량은 100~150, 개발팀 용량은 500~5000
* Minimum의 의미
 * 비록 요청이 없더라도 즉시 지급할 수 있도록 준비. ex) 은행의 지급준비금
* soft-limit의 의미
 * 한 작업 더 할 때 maximum을 좀 넘더라도 그정도는 허용, 그러나 hard-limit은 못넘음
* minimum requirements of workload 가 잘 알려진 상황일 때 적합하나... 그걸 정하기 쉬울까?
* 정확성 측면에서 가장 우수, 공정성도 good, but 효율성은 완전 꽝

##### Fair
![hadoop_yarn_6](https://github.com/hoondori/TIL/blob/master/images/hadoop_yarn_6.png)

* 공정성, 정확성, 효율성을 모두 적당히 충족하는 optimal (이라 주장)
* 모든 app은 단 하나의 queue에 할당
 * 원한다면 복수 queue운영 가능 : Fair + Capacity
* 각 App이 동일한 resource requirement를 가진다면
 * single App only in cluster => 100% 차지
 * two apps in cluster => 50%, 50% 양분
* Preemption either in a friendly or forcefully manner
* 원한다면 user별 min/max 운영 가능
 * ex) 아무리 Fair해도 특별 대우 => min
 * ex) 과도한 남용 방지 => max

#### Delay scheduling
* 최대한 locality를 고려해 줌
 * Resurce request가 특정 node를 선호하는 경우 이를 보장해주려고 노력
 * 하지만 해당 node가 매우 busy 하면 어쩔 수 없이 그 근방으로 배정( ex. random nodes in the same rack)
 * 이 경우 당연히 성능 저하, but 어쩔 수 없지 않나..
* 관측 결과 아무리 busy해도 약간만 기다리면(수초) 원하는 노드에 배정이 되더라. 너무 빨리 스케줄링하지 말아보자
* 보통 node manager가 heartbeat으로 resource manager에게 1초마다 status 보고. 이 간격으로 scheduling 기회가 생김
* 첫번째 scheduling 기회 때 locality를 포기하지 말고, 일단 2번, 3번째 status까지 받아보고 나서 결정. 
* 3~4번의 scheduling 기회 때까지고 busy가 나오면 locality를 포기하는 수밖에..

#### Dominant resource fairness
* resource type이 1개인 경우(ex. CPU)
 * 총 100 CPU 가 있고 User1이 2개, User2가 6개를 요청했다면, 1:3 의 fair share로 CPU를 배정하면 됨
* resource type이 여러 개인 경우(ex. CPU, Memory)
 * 총 100개 CPU, 10T RAM
 * User1이 (2CPU, 300G), User2가 (6 CPU, 100G )라면 fair share비율은?
 * User1은 전체 resource의 (2% CPU, 3% RAM) 요청, User2는 (3% CPU, 6% RAM) 요청
 * RAM이 dominant resource로 선정되고, User1와 User2는 1:3의 fair share를 갖는다.
* DRF는 기본적으로 비활성화, 이때는 RAM만 고려됨
* Fair share는 언제 의미가 있나?
 * 자원이 넘처날 때는 의미 없음
 * 자원이 희소해져서 공평성이 부각될 때, 즉 preemption의 동작의 기본 바탕 제공


## YARN As a framework
Hadoop 생태계를 벗어나다.

* Distributed Shell
* Hadoop MapReduce
* Apache Tez
 * generalized MapReduce => As a DAG(Direct Acyclic Graph)
* Apache Giraph
* Hoya : HBase on YARN => dead project?
* Dryad on YARN
 * microsoft's dag processing flow
* Apache Spark
 * intermediate results in memory, extension of operations
 * cluster environments supported : EC2, Mesos, YARN
* Apache Storm
* Hamster: Hadoop and MPI on the Same Cluster




























(그림1) hadoop 1.0의 jobtracker/tasktacker 기반 그림
단점 제시

(그림2) YARN의 그림 
장점 제시
기존 jobtracker의 두 가지 역할을 다음으로 분할한 것이 핵심
* resource management to a Global ResourceManager
* job scheduling/monitoring to per-application ApplicationMaster



# Beyond MapReduce 
* (그림 3.2) 