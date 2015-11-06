
# What is Hive, Why Hive

[introduction to hive](https://youtu.be/tKNGB5IZPFE)

## What is
**Data warehousing package on Hadoop**
Used for analytics
For users comfortable with SQL(HiveQL)
**For structured data**
Abstract complexity of Hadoop
Allow to use plug-in custom mapper/reducer
JDBC/ODBC (limited)

## Where to use
Data mining
Log processing
Business Intelligence
Predictive Modeling/Hypothesis Testing
Document Indexing

## Where not to use
not for OLTP
not for real-time queries and row level updates


## Hive Architecture

(그림)

## 설치
workstation 에 설치되서 hadoop cluster에 SQL로부터 번역된 jobs를 submit

데이터는 table의 형태로 조직화되어(structure) 저장
* warehouse directory에 저장

table schema에 해당하는 metadata는 metastore에 저장

## 환경 설정

주요 정보

* 접속 정보 ex)hdfs나 yarn의 접속정보
* MetaStore 설정 정보
* Execution Engine 설정 정보
 * MR, Spark, Tez
* 로깅 정보

## Hive Services/Client

(그림)

services

* cli : interactive shell
* hiveserver2 : Hive를 thrift interface 기반 서버로 동작시키고 다양한 언어 기반의 client에게 API 제공
* hwi : Hive Web Interface
* jar : Hive embedded single jar
* metastore : as a standalone service

clients

* thrift client
* JDBC driver
* ODBC driver

## MetaStore

(그림)

* microservice이면서 storage이다
* microserivce 위치와 backend 구성 차이에 따라 여러 구성 가능
 * embedded : derby 기반 single JVM 구성
 * local : MySQL과 같은 독립된 storage, microservice는 local에
 * remote : microservice도 별도의 서버에서 운용












































