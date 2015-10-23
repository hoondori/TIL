
# MapReduce Features

## Counters

* 수행하는 Job의 평가나 diagnostics를 위해 사용

### Built-in Counters

* 모든 job에 대해서 자동으로 산출해 주는 counters

#### task counters

* task attempt 마다 측정되서 app master에서 aggregation
* task가 성공적으로 종료된 것이 aggregation에 반영되는 것이 일반적
* 예외적으로 물리 메모리 관련 counter는 job의 수행 도중의 상황도 알려주는 척도
* 종류
 * mapper, reducer, combiner에서의 입력/출력 레코드수/바이트수
 * **spill된 레코드수/바이트수**
 * **shuffle 된 바이트수/map file수, 실패한 shuffle**
 * 물리/가상 메모리 사용통계 관련
 * GC time

#### Job Counters

* job-level 통계를 나타내며 task 수행 사항을 반영하지 않는다.
* app master에 의해서 운영
* 종류
 * launched/failed/killed 된 map/reduce/uber task 개수
 * data-local/rack-local map task 개수

### User-defined Java Counters

* Java Enum으로 정의
* 그룹명/counter명으로 구조화
* global 하게 유효하며 모든 map/reducer에서 aggregation@됨
* 정적이나 동적으로 만들 수 있다.
* command line에 출력되는 counter 이름을 재정의 할 수 있다.

```java
# 정적으로 선언된 counter
enum Temperature {
     MISSIONG,
     MALFORMED
}

# 동적으로 선언된 counter
context.getCounter("Temperature", MISSING)
```

#### dynamic counters

* 실제 쓰이지 않을 정적 counter를 만드느니 실제 발생하여 쓰이는 동적 counter를 만드는 게 유리할 수 있음

### User-defined Streaming Counters

* reporter:counter:group,counter,amount 양식 준수

```java
sys.stderr.write("reporter:counter:Temperature,Missing,1\n")
```

## Sorting

* MapReduce를 이용해서 데이터 소팅하기

### Partial Sort

* 아무것도 안해도 기본적으로 MapReduce는 정해진 키를 기준으로 정렬하는 것이 기본동작이다.
* key를 어떻게 정렬할지는 Comparator가 정함
* N개의 reducer의 output의 각각은 내부적으로 sort되어 있지만 전체 최종 데이타는 여전히 N개로 나누어져 있다.
 * 이를 합치면 globally sorted 된 데이터를 얻는다.

### Total Sort

* use a partitioner that respect the total order of the output
* 주의할 점은 partition들의 크기가 불균등하지 않도록 범위를 잘 정해야 한다는 것이다.
* 이는 입력 데이터의 키분포 형태를 알아야 하지만(ex. uniform, gaussian, skewed)
* sampling 기법을 사용해서 추정한다.
 * random sampling이 가장 좋다.
 * 기타 : SplitSampler(Split마다 first n개), IntervalSampler(동일 interval로..)
* sampling에 의한 키 분포 추정은 client에서 계산이 일어난다.

### Secondary Sort

* composite key인 경우, ex) (년도 온도) = (1950,35),(1950,34)
 * (1950 35),(1950 34) hash값이 다르므로 전혀 다른 키이고 다른 reducer들로 흩뿌려질 수 있음 => 즉 정렬 안됨
* composite key임에도 불구하고 partitioner가 년도만 보게 하면 같은 reducer로 오게 할 수 있음
* 반면 comparator는 composite key 전체를 보게 하면 (1950 35),(1950 34)를 정렬할 수 있음
* 다시 정리하면
 * partitioner를 이용해서 같은 reducer로 모이게 하고
 * comparator를 이용해서 정렬 방식을 조정한다.=> first by 년도, second by 온도

## Joins

### Map-side Joins

* 두 개의 large inputs이 있고, 양자간에 join key가 존재한다고 할 때
* mapper의 입력으로 동일한 join key를 가지는 partition들이 오면 된다.
 * 사실상 mapper 이전에 어찌됬건 sorting/partition이 잘 되야 하는 전제
 * 사실상 비현실적 가정


### Reduce-side Joins

* map-side와 같은 제약조건은 없음
* 동일한 join key를 갖는 record들을 어떻게 reducer에 모이게 할 것인지가 관건
* 기본 아이디어는 join key를 mapper의 output key의 일부분이 되야 하고 partitioner가 해당 join key를 바탕으로 partition을 나눈다.
 * 이 경우 각 input에서 생성된 partition 두 개가 reducer에 모이게 되고 각 record에는 동일한 join key가 존재할 것이다.

## Side Data Distribution

* Side data는 job에서 main dataset을 처리하기 위해 필요한 추가 데이터, 일종의 글로벌 객체모음
* 모든 map/reduce task에서 이것이 visible 해야 한다.

### Using Job configuration

* job configuration에 key-value pair로 추가하면 어디에서든지 이것이 접근 가능
* 소규모 metadata를 side data로 사용할 때나 적합 (수 kbytes)
* primitive type은 즉시 key-value pair로 설정 가능하나 object type은 serialization/deserialization이 필요
 * Default's Hadoop Stringfier

### Distributed Cache

* hadoop distributed cache를 이용해서 side data 운용
* copy files/archives to the task node and use them
* command line에서 files, libjars, archives 옵션으로 전달
 * 내부적으로 이것들이 HDFS에 저장되고
 * 각 node manager에서 task 수행 전에 이를 복사해서 node 내에 local file system 내에 위치
* 이것은 cache된 것이다. 즉 필요할 때 복사되고, 그 즉시 지우지는 않고 reference count를 추적해서 필요없을 때까지 보관
* 다만 이것이 너무 커지면(ex. 10GB) 각 node manager에서는 Least recently used 정책에 의해 일부 cache 제거
