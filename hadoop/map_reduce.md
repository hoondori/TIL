# MapReduce

[TOC]

## The Problem
병렬 처리가 어려운 세가지 이유
- 하나의 작업을 동일 사이즈로 쪼개는 것의 어려움
- 쪼개져서 처리된 부분 결과를 다시 하나로 합치는 것의 어려움
- 하나의 머신에서 이러한 병렬 처리를 하는 것의 한계

## Hadoop을 이용한 병렬 처리

![hadoop_mapreduce_1]
(https://github.com/hoondori/TIL/blob/master/images/hadoop_mapreduce_1.png)

### Mapper setup
- input key/value type
- output key/value type

```java
public class MaxTemperatureMapper
   extends Mapper<LongWritable, Text, Text, IntWritable> {
...

	@Override
	public void map(LongWritable key, Text value, Context context)
	throws IOException, InterruptedException {
...
}
```

### Reducer setup
- input key/value type
- output key/value type

```java
public class MaxTemperatureReducer
	extends Reducer<Text, IntWritable, Text, IntWritable> {

    @Override
	public void reduce(Text key, Iterable<IntWritable> values, Context context)
	throws IOException, InterruptedException {
...
}
```

### Driver setup

```java
Job job = new Job();
job.setJarByClass(XXX.class);    // Job class 지정

// Input/Output file 지정, 보통 HDFS상의 경로
FileInputFormat.addInputPath(job, new Path(args[0]));
FileOutputFormat.setOutputPath(job, new Path(args[1]));

// Mapper,Reducer 지정
job.setMapperClass(MaxTemperatureMapper.class);
job.setReducerClass(MaxTemperatureReducer.class);

// Output key/value 타입 지정
job.setOutputKeyClass(Text.class);
job.setOutputValueClass(IntWritable.class);

// Job 실행 및 수행 대기
job.waitForCompletion(true)

```

### command line 을 이용한 수행
```bash
% export HADOOP_CLASSPATH=hadoop-examples.jar
% hadoop MaxTemperature input/ncdc/sample.txt output
```

## Scale out 수행 구조

![hadoop_mapreduce_2](https://github.com/hoondori/TIL/blob/master/images/hadoop_mapreduce_2.png)

![hadoop_mapreduce_3](https://github.com/hoondori/TIL/blob/master/images/hadoop_mapreduce_3.png)

*  scale out 구조를 위해서 In/Out 파일을 HDFS에 저장
* resource management로 YARN 사용
* input을 fixed-size의 input splits으로 쪼갠 후 이를 map task에 할당
 * split을 작게 해서 최대한의 parallelism이 보장
 * 너무 split을 작게 하면 관리 overhead 증가
 * split 크기는 HDFS block 크기 정도로..
* Data locality 고려
 * HDFS block이 있는 곳에 최대한 가깝게
 * inter-rack transmit은 최악의 시나리오!
* Combiner function
 * mapper의 output에 적용
 * 한 번 이상 적용될 수 있으므로 그래도 되는 것만 해야 함. ex) find max


## Hadoop Streaming
* Unix standard를 인터페이스로 사용
* Text analysis등의 응용에 적합
* Java API와는 다르게 key가 grouping되서 들어오는 것이 아니므로 stream에서 key group을 parsing해야 함
* hadoop-streaming-XXX.jar 등의 라이브러리를 library path에 정의해야 함




















