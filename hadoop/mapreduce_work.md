# How MapReduce Work

## MapReduce Job 실행 상세

![mapreduce_work_1](https://github.com/hoondori/TIL/blob/master/images/mapreduce_work_1.png)


### Job Submission

client-lib 내의 JobSubmiiter에서는 다음의 역할 수행

* resource manager에게 새로운 application ID를 요청
* input splits 계산
* Job 수행에 필요한 리소스(ex.dependent jars,configurations)들을 HDFS에 복사
 * job jar는 보통 10개 이상의 복제를 뜬다.(왜냐하면 다양한 노드에서 이를 병렬로 카피하는게 효율적이므로)
* 위 작업이 완료되는 실제 resoure manager에게 job을 submission한다.

### Job Initialization

job submit을 받은 resource manager에서는 다음을 수행

* YARN scheduler에 연락하여 container를 얻고 applicationMaster를 띄운다.

application master에서는

* job의 크기를 가늠해서 매우 작은 job이라면(uberized job) 단순히 해당 JVM에서 그냥 처리
 * uberized job : 보통 10개 미만의 mapper와 1개의 reducer, input size < single HDFS block

### Task Assignment
* application master에서 판단시에 uberized job이 아니라면 병렬 컴퓨팅을 하기 위해 Resource Manager에게 자원을 요청한다.
* map task를 위한 자원을 reduce task보다 더 일찍 요청한다.
* map task는 data-locality를 최대한 고려해야 자원 요청에도 이를 반영해 달라고 요구한다.
* 자원 요청은 필요 RAM,CPU를 기술하며, default는 1024MB, 1 CPU 이다.

### Task Execution
* resource manager에게서 허락을 받은 자원 사용 허가증을 가지고 node manager에게 간다.
* 해당 node에서 map(or reduce) task 수행한다.
* 사전에 Job jar를 copy해야 하고 distributed cache가 있으면 이를 활용
* 모든 task 수행시에 **commit protocol**을 수행
 * FileCommiter의 경우에는 task의 output을 최종 목적지로 이동하는 것이나 speculative execution을 고려하는 것 정도

### Streaming

![mapreduce_work_2](https://github.com/hoondori/TIL/blob/master/images/mapreduce_work_2.png)


* 일반 map/reduce task와는 다르게 stand-alone running 중인 streaming process를 호출하는 구조


### Progress/Status Update

long-running job인 경우 사용자에게 적절히 feedback을 주는 것이 필수

* map task의 경우에는 progress는 보통 총 input split 대비 처리한 input split 비율
* reduce task의 경우 shuffle and sort를 고려한 3단계 progress
* progress는 application master에게 보고/취합됨

### Job Completion
* Job의 마지막 task로부터의 성공 ack를 받으면 job 전체가 성공했다고 mark
* waitForCompletion()을 건 cliets들은 다음 polling시에 끝남을 감지
* 혹은 설정에 의해 push 방식으로 job done을 clients들에게 알릴 수 있음


## Failures

### Task Failure

* map/reduce task 내의 사용자 코드 내에서 exception이 발생하는 경우
 * exception이 parent application에게 즉시 보고
 * user log에 확실히 남음
 * task attempt를 failed로 처리
* task JVM 자체가 갑자기 죽는 경우
 * node manager가 대신 이를 감지해서 app master에게 알림, 즉시성 떨어짐
* task가 hang이 걸린 경우
 * 10분 간격의 heartbeat to app master, 없으면 감지
* Failure 감지시 Retry
 * 4번정도까지 재시도
 * failure가 발생됬던 node를 피해서 다른 node에 시도
 * 모든 재시도 실패시 전체 Job이 Failed 처리
* speculative attempt는 failure로 count하지 않는다.

### Application Master Failure

* heartbeat to resource manager를 이용해서 failure 감지
* 2번 정도까지 재시도한다.(default)

### Node Manager Failure

* heartbeat to resource manager 를 이용해서 failure 감지
* 10분 주기
* failure node는 pool에서 빼서 scheduling되지 않도록 한다.
* blacklisted node manager by app master
 * node manager 자체는 멀쩡하나 해당 node에서 계속해서 task failure가 나는 경우
 * 3번 이상의 실패시
 * app master는 blacklisted node를 피해서 다른 node에 task 수행 시도를 한다.

### Resource Manager Failure

* default는 single point of failure 이다.
* HA를 하려면 Active-standby 구성
* 중요 데이타만 Zookeeper나 HDFS를 이용해서 Active/Standby 사이에 공유하고 나머지는 warm-up시에 재구성
 * 중요 데이터는 모든 running applications에 대한 사항
 * node manager 정보는 중요 사항이 아니다.
* 재시도시 모든 application을 재시도하는 것이지만 application attempt를 failure count하지는 않는다.
* Active to Stand-by failover는 zookeeper의 leader election 기법을 이용해서 자동화되어 있다.

## Shuffle and Sort

Shuffle이란

* map output을 해당 key들을 처리하는 reducer의 node로 이동(transfer)시키는 과정
* 대용량 데이터가 network 상에서 이동하는 것이라 가장 비용이 많이 발생하므로 최적화의 최우선 고려대상이 됨

### Map Side sort

![mapreduce_work_3](https://github.com/hoondori/TIL/blob/master/images/mapreduce_work_3.png)

* map 과정에서 최대한 부분적이나마 sorting을 수행 (presort)
* 이를 위해 메모리 상에 circular buffer (default 100MB ) 내에서 부분 데이터 sorting
* 메모리의 80% 가 차면 그때까지 정렬된 것을 disk에 한 파일로 만듬
* 하나의 파일 안에는 reducer에 전달할 구분 단위로 partition 되어 있음
* 모든 데이터에 대해 반복하면 N개의 spilled file이 있는데 이를 다시 병합해서 1개로 만듬
 * 정확히는 백그라운드 프로세스가 미리미리 하고 있음
* 최종 파일도 역시 내부적으로 reducer에 전달할 구분 단위로 partition 되어 있음
* map output을 compress해서 전송 사이즈 축소

### Reduce Side sort

* copy phase
 * cluster 내의 모든 map task가 만든 파일 중에서 해당 reducer가 다루는 데이터 전송
 * 일단 reduce task 내의 메모리에 복사하지만 넘치면 spill over to disk
* sort(or merge) phase
 * 여러 map output file을 정렬하면서 하나의 파일로 merge
 * round 방식의 merge sort <- 정리 필요
* reduce phase
 * 실제 reduce function이 작동

### shuffle and sort tuning points

* 결국은 map/reduce 과정 중에서 최대한 disk로의 spill over가 발생하지 않도록 하는 것
* 이를 위해 메모리를 충분히 주는 것이 필요

## Speculative execution

* 병렬로 task를 진행해도 결국 가장 느린 task에 의해 최종 수행 시간이 좌우
* 느리다 싶은 task를 중복(task duplicate)해서 실행하고 빠른 것의 결과 수용
 * 무조건 duplicate를 만드는 것이 아니라 일단 비슷한 크기의 task를 N개 수행하고 그 중에서 유독 느리다 싶은 task들에 대해서만 상응하는 duplicate 수행
* Anti-pattern
 * task가 원래 느린 특성이라서...
 * task에 버그 등으로 느린 건데...
 * cluster 전체가 busy한 상황인데...
* default는 enable이므로 anti-pattern 발생시는 이에 제약을 주어야 함
* reduce task는 웬만하면 duplicate를 안 하는 것으로.. 왜냐하면 data transfer가 너무 심함
























 

