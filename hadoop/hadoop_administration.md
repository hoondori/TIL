# Hadoop Administration

## Setting Up Hadoop Cluster

* 설치 방법들
 * by tarball
 * by packages
 * by management tools, like cloudera, mapR, ambari

### Cluster Specification

* mid-range 급의 H/W 사용
 * 대용량 DB 운영급의 고성능은 필요없음. ROI가 안나옴
 * two hex/octo core 3GHz CPU, 64~512 GB RAM, 12-24 x 1-4 TB SATA, Gigabit with link aggregation
* 어차피 replication을 하므로 RAID는 필요없음

### Cluster Sizing

* 처음엔 작은 cluster(ex. 10 nodes)로 시작, 필요할 때마다 늘려라
* 작은 cluster라면 단일 장비에 namenode와 resource manager 둘다 돌려도 됨
 * but...HDFS나 YARN 모두 active-standby를 지원하므로 양자의 master는 별도 머신으로 하는 것이 바람직
* secondary namenode도 primary namenode와 비슷한 메모리 요구량을 갖는다.
 * secondary에서 checkpoint 생성도 해야 하므로 넉넉히

### Network toloplogy

* two-level network topology - intra/inter rack
* 랙당 보통 30-40 머신, 랙간 10G 통신, uplink 최소 10G
* intra rack 통신이 inter-rack 통신보다는 무지막지하게 빨라야 함

#### Rack Awareness

* default는 Rack unware하므로 일일이 설정 반드시 필요
 * 안해주면 모든 node가 default-rack 하나로 소속됨
* node address와 network location간의 mapping 정보 설정
 * DNStoSwitchMapping
 * ScriptBasedMapping

### cluster setup and  installaction

* HDSF, MapReduce, YARN 서비스들은 각각 서로 다른 Unix user 계정으로 띄워라
* 다만 모두 hadoop group내에 소속되도록

### Configuring SSH

* cluser-wide operation을 하려면 대상 machine들에 대해 password-less login을 해야 한다.
* 이를 위해 pub/pri key를 하나 만들고 이를 공유(NFS 올려서 공유)


### Configuring Hadoop

* 신규 설치시 HDFS 파일시스템으로 포멧팅 at namenode
* cluster 내 Hadoop deamon start (start-dfs.sh)
* cluster 내 YARN deamons start (start-yarn.sh)
* cluster 내 MapRedue daemon start (historyserver)

### Configuration Management

* 공유하는 환경설정 파일은 없고 각 노드에 각각 복사본으로 사용
* 즉 중앙에서 고쳐서 전부 배포하는 configuration mangement 필요
 * paralllel shell tools like dsh, pdsh
 * Cloudera/Ambari/MapR
* 노드별 성능이 다른 점을 고려하면 노드 클래스별 별도 설정 운영 필요 =>더 복잡
 * Chef, Puppet 등도 고려

### Environment Settings

#### Memory Heap Size

* 기본 1G for each daemon
* daemon별로 필요 heap size가 다른데 별도 설정 못함 ㅠㅠ
 * 다만 namenode deamon의 heap size는 설정 가능
* Namenode는 얼마나 메모리 필요?
 * 백만 블록당 1GB 필요하다고 알려짐
* primary namenode랑 secondary namenode는 비슷한 메모리량을 잡아주어야함
* 한 노드 안에 deamon들뿐만 아니라 container들도 들어와서 메모리를 차자할 것이기 때문에 이를 고려해서 산정해야 한다.

#### important Hadoop deamon properties

* for namenode
 * IP, port, filesystem(HDFS)
 * storage directories : 복수 개 설정하면 복제본으로 여러 개 유지
* for secondary namenode
 * checkpoint directories : 여러 개 지정하면 복제본
* for datanode
 * data directories : 여러 개 지정하면 복제는 아니고 round-robin 저장
* for YARN's resource manager
* for YARN's nodemanager
 * map/reduce 중간 파일들 저장 위치 지정, 충분히 큰 저장소 지정 필요
* for YARN's shuffle handler
 * map output을 reducer로 shuffling 하는 데몬

#### Memory setting in YARN and MapReduce

* datanode deamon이 1GB, node manager가 1GB 일단 사용
* 남은 메모리는 최대한 node manager의 container들에게 할당 필요
 * 기본값은 8GB 이므로 반드시 고처야 한다.
* 각 container의 최대 메모리량은 default 1GB, container 내의 JVM heap 최대값은 200MB 이므로 이를 고쳐서 사용 필요
* 안 고치고 1GB이상 메모리 사용시 node manager가 해당 container 강제 종료
* YARN scheduler's min/max memory allocation (1G~8G)
* virtual memory constraint : 2.1

#### CPU settings in YARN and MapReduce

* number of vcores to allocation by YARN scheduler
* CPU 남용 방지 => executor를 cgroup으로 설정

#### 기타 주요 셋팅

* cluster membership 관리
 * 여기에 명시적으로 표시된 node들만 commision/decommision
* Buffer Size
 * default 4kB, 요즘은 128kB가 대세
* HDFS block size
 * 128MB default, 보통은 그 이상으로 사용이 대세, ex) 256MB
* Job scheduler 설정
 * multiuser/multi-organation 고려해서 운영
* slow start 조정
 * 보통 reducer가 mapper보다 늦게 시작되지만(5% map task 종료후 시작)
 * mapper task가 long running이라면 더 늦게 시작해라
* short-circuit local reads
 * 만약 client가 data가 위치한 node상에 있다면 굳이 통신으로 데이터 전달하지 말고 unix socket과 같은 것으로 더 빨리 전달






















































































