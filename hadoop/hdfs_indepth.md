# HDFS in-depth

## Hadoop FileSystem
* Local-HDFS
 * local disk로 연결된 single node에서 정의된 HDFS
* Distriubted-HDFS
 * 분산된 datanode에서 정의된 HDFS
 * MapReduce가 잘 동작하는 모드
* Web-HDFS or SecureWeb-HDFS
 * HTTP/HTTPS 통신을 통해 read/write가 가능
* HAR
 * 압축 파일을 고려한 HDFS


## Interface
### HTTP REST API to Web-HTTPS
* native java client보다 느리므로 large file을 다루는 경우는 지양

#### 구현 방식
* Direct 방식 : webClient가 namenode 및 datanode들에게 직접 접근
* Proxy 방식
 * proxy가 webClient 대신 다수 노드에 접근하고 결과만 webClient에 노출
 * bandwidth가 제약된 환경에서 적합

### C
* libhdfs
* 내부적으로 JNI 사용
* 일반적으로 java보다 new feature 반영이 느리다.

### FUSE
* Filesystem in Userspace

### Java
#### FileSystem API 및 Seekable을 이용한 데이터 읽기
```java
public static void main(String[] args) throws Exception {
	String uri = args[0];
	Configuration conf = new Configuration();
	FileSystem fs = FileSystem.get(URI.create(uri), conf);
	FSDataInputStream in = null;
	try {
		in = fs.open(new Path(uri));
		IOUtils.copyBytes(in, System.out, 4096, false);
		in.seek(0); // go back to the start of the file
		IOUtils.copyBytes(in, System.out, 4096, false);
	} finally {
		IOUtils.closeStream(in);
	}
}
```

#### FileSystem API 및 Progressable을 이용한 데이터 쓰기
```java
public static void main(String[] args) throws Exception {
	String localSrc = args[0];
	String dst = args[1];
	InputStream in = new BufferedInputStream(new FileInputStream(localSrc));
	Configuration conf = new Configuration();
	FileSystem fs = FileSystem.get(URI.create(dst), conf);
	OutputStream out = fs.create(new Path(dst), new Progressable() {
	public void progress() {
		System.out.print(".");
	}
	});
	IOUtils.copyBytes(in, out, 4096, true);
}
```


#### 정보 조회 / 필터링
* FileSytem.getFileStatus : 개별 정보 조회
* FileSystem.listStatus : 목록 나열
* FileSystem.globStatus : Path 내 패턴 만족하는 파일들 정보 조회

### Data Flow
#### Read 분석

![hadoop_mapreduce_2](https://github.com/hoondori/TIL/blob/master/images/hadoop_hdfs_1.png)

* Namenode를 통해 block들을 소유한 datanode 목록 조회
* datanode에서 block 읽기
* network topology 고려
 * client가 위치한 같은 node 최우선
 * 같은 rack에 위치한 다른 datanode
 * 다른 rack에 위치한 다른 datanode
 * 다른 datacenter에 위치한 datanode

![hadoop_mapreduce_2](https://github.com/hoondori/TIL/blob/master/images/hadoop_hdfs_2.png)


#### Write 분석

![hadoop_mapreduce_2](https://github.com/hoondori/TIL/blob/master/images/hadoop_hdfs_3.png)

* namenode에 create()를 통해 파일 존재 생성
* client가 데이터를 쓰면 packet들로 split되서 data queue에 들어감
* data queue는 namenode에 질의해서 block이 쓰여질 datanode pipeline을 얻고 쓰기 시도
* 모든 packet이 성공적으로 pipe-lined datanode에 쓰여졌으면 complete() to namenode


### Replica placement
* tradeoff between reliability and read/write bandwidth
* 기본 전략 (수정 가능)
 * client가 위치한 datanode에 first replica 위치 (for write BW)
 * 다른 rack에 위치한 datanode에 second replica (for reliability)
 * second replica가 위치한 rack의 다른 datanode에 third replica (for read BW)

### Coherency Model
* data visibility of reads and writes

#### hflush
* OutputStream의 flush()은 visibile하지 않다.
* hflush를 해야 모든 data가 pipelined datanode들에게 전달된 것까지 보장한다.
* 그제서야 client에게 visible하게 된다.

#### hsync
* hflush는 datanode의 메모리에까지 data가 전달되는 것까지만 보장
* hsync는 datanode의 disk에까지 저장되는 것까지 보장

#### 어떻게 사용?
* 큰 파일의 write를 하는 경우 적절한 간격으로 hflush 정도는 명시적으로 호출하는 것이 바람직
* 어플리케이션이 추구하는 degree of consistency에 따라 다르다.
















