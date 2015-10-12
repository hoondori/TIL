# HDFS overview

## Design of HDFS
* 매우 매우 큰 파일
 * up to petabytes
* streaming data access
 * write-once, read many
 * immutable, for analytics
* 범용(commidity) 하드웨어

## HDFS가 적합하지 않은 영역
* 매우 빠른 데이터 접근이 필요한 경우( low-latency )
 * Hadoop은 대용량 데이터 전달에 최적화되어 있지 빠른 전송에는 HBase가 더 적합
* Lots of Small file
 * namenode 상에 파일들의 메타 데이터 관리로 인해 namenode의 메모리에 견주어 파일수 제약
* Multiple writer, arbitrary file modification
 * HDFS에서 파일은 항상 single writer에 의해서 쓰여진다.
 * 파일의 최초 생성이나 기 생성된 파일로의 첨부(append)만 허용됨


## HDFS Concepts
### Block
* single r/w에 의해 처리되는 최소 단위
* 일반 HDD에서는 수 kbytes 인데 반해 HDFS는 128MB (default)
* 왜 이렇게 큰가?
 * seek에 소요되는 비용을 줄이기 위해
 * seek time 1%, transfer time 99% design

### Block 추상화의 효과
* 하나의 파일이 매우 커질 수 있다. (partitioned to multiple node)
* 추상화함으로서 storage subsystem 디자인의 간결화(simplication)
* replication 및 fault-tolerance의 설계의 간결화

### Namenode
* master-workers pattern
* filesystem tree 및 파일/디렉토리의 메타데이터 관리
* local disk상에 영구적으로 저장
* edit log 운영
* namenode's resiliant
 * by back-up
 * by secondary namenode (as hot-standby)

### Block Caching
* 일반적으로는 datanode에서 데이터를 disk상에서 읽음
* 자주 쓰이는 block은 datanode의 메모리상에다 cache
* 사용자가 명시적으로 어떤 파일들(그것들의 blocks)을 cache할 것인지 지정 가능

## HDFS Federation
* 복수 개의 namenode 운용, 각 namenode는 각각의 filesystem namespace를 관장
* 예를 들어 어떤 namenode는 /usr 관리, 다른 namenode는 /share 관리
* namenode끼리는 완전히 독립된 개념, 서로 이야기할 필요 없음

## HDFS High-Availability
* namenode 는 SPOF이다.
* backup namenode 를 복구에 사용시 상당시간 warm-up 시간 필요 => long recovery time
* HDFS-HA는 Hot-standby namenode 지원
* 다음의 요건들이 충족 필요
 * edit log는 HA shared storage에 저장해서 namenode끼리 빠른 공유 가능케..
 * datanode들은 양쪽의 namenode들에게 모두 block reports 전송해야
 * client는 namenode failover를 감지해서 대응할 수 있어야
* HA shared storage : Quorum journal manager(QJM), ZooKeeper

### fencing
* failover시에 previous active namenode가 적절히 죽어줘야 한다. 안그러면 conflict
* 방안들
 * SSH fencing
 * NFS filter for shared edit log block
 * power control - STONITH(shoot the other node in the head)










