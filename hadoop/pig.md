# Pig

## Why Pig

참고자료
[Introduction to Pig](https://youtu.be/J0Nc8qJCO9s)


* No Java required
 * script-like, JSP vs Java
* No Ad-hoc => standardized
* Rapid development
 * smaller lines of codes, smaller development time
* declarative rather than procedural
* focus on what rather than how(esp. parallelism)

Pig is ** Data flow language **

* possible to create complex jobs on top of Hadoop to process large volumes from various many data sources

When not helpful

* completely unstructured data
* When performance is important
 * Pig is slower than MapReduce
* want to optimize the code pig generated

Use cases in Yahoo

* As a data factory
 * as pipelines (ex. log cleansing)
 * as research playground (ex. write script to test naive idea quickly)

Program Structure

* Script : command-line tool
* Grunt : interactive shell
* Embedded : from java

Pig Execution

* Pig는 client-side에서 동작
* Hadoop cluster는 Pig와 전혀 상관없음

Pig Latin Program

* series of operations or transformations
* turns into series of MapReduce Jobs

## 설치 및 수행

Pig는 client-side에서만 동작하므로 server에는 아무 것도 설치 안해도 된다.

수행 모드
* local mode
 * local의 JVM 내에서 local file system만 접근
* MapReduce mode
 * query를 map reduce job으로 해석한 후 hadoop cluster에 제출한다.
 * hadoop cluster는 pseudo 혹은 fully distributed되어 있다.
 * 주의점 : Pig Client와 Hadoop Cluster의 버젼이 상호 운용한지(Compatible)한지 조사 필요
 * auto-local mode를 enable하면 input이 작은 경우(ex. 100MB) local mode에서 수행

수행 방법
* Script
* Grunt
* Embedded

## Pig Latins

statement의 sequence이다.

각 statement는 각각 parsing되어 수행된다.
* 정확히는 각 statement는 logical plan이 되고
* 모든 logical plan을 고려한 후 최적화도 있고
* 실제 execution이 trigger될 때 physical plan으로 변환 후 수행된다.

언제 execution이 trigger되나?(lazy execution)
* operation들의 결과가 사용자에게 필요되어질 때
 * ex) DUMP
 * STORE는 interactive mode일 때만 execution을 trigger하고 batch mode에서는 execution을 trigger하지 않는다.

debugging용 statement
* DESCRIBE, EXPLAIN, ILLUSTRATE

pig script 수행 방법 exec v.s. run
* run을 한 경우에만 script내의 statement가 history에 남는다. (bash history 연상)

## Schema

Relation은 schema랑 연관될 수 있다.

schema내에 각 필드의 이름과 type을 정의한다.

굳이 schema가 없아도 relation을 이용할 수 있다.

* 다만 이 경우 모든 type이 bytearrary로 해석된다.
* 심지어 이름도 지정을 안해줘도 된다. $1, $2 이렇게 접근 가능

명확성을 위해서 일반적으로는 schema 지정이 권장된다.


### Using Hive tables with HCatalog

Hive table을 읽는 경우에는 HCatalog로부터 schema를 조회해서 해석한다.

### Validation and nulls

invalid에 대한 대처

* schema에 정의된 type으로 cast가 안되는 경우 null 채우고 진행
* Warning을 띄우나 프로세스 전체를 halt하지는 않는다.

일반적으로는 최대한 data cleansing을 하라.

* 알려진 invalid(missing or corrupted) 패턴에 대해 조사/필터링


### schema merging

만들어지는 모든 intermediate relation마다 schema를 지정하지 않아도 대부분 정확히 추론된다.

* Input schema가 output에 그대로 승계되는 경우, ex. LIMIT
* Input이 두 개 이상인 경우 output에 merge된다.
* merge하기에는 incompatible한 경우, Unknown처리, ex. UNION


## Function

### Eval function

복수 개의 expression들을 입력으로 해서 또다른 expression을 만들어 낸다.

Built-in eval functions

* AVG, CONCAT, COUNT, DIFF,MAX, MIN, ...

### Filter function

logical한 Boolean 값을 리턴

대부분 기준에 맞지 않는 row를 제거하는 역할 담당

Built-in filter function

* isEmpty

### Load/Store function

Text/JSON/Avro/Parquet/Orc/HBase Loader/Storage


## UDF

### 사용법

* 만든 UDF를 compile하여 jar로 만든 후 REGISTER statement로 PIG 에 등록한다.
* 호출시에는 Java class 사용하듯이 namespace 및 class 명칭을 사용
* DEFINE을 사용하면 full namespace 대신 class명만으로도 사용가능

### Leveraging types

UDF 내에서는 Tuple 내의 요소들을 특정 type으로 cast 함
schema 없으면 bytearray로 모든 input이 매핑되고 이 경우 특정 타입으로 cast가 exception 발생
getArgToFuncMapping에서 어떠한 타입이 기대되는지를 적어준다.




































