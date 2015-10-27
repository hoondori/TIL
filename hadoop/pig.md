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










[What is Pig and Why Pig](https://youtu.be/MO-YLSOZvoY)

[Understanding Pig Latin](https://youtu.be/Yw4hcSR-DGU)
