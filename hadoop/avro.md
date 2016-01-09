# Avro

## What and Why

* language-neutral serialization system
* to address the major downside of Hadoop Writables
* supports : C, C++, C#, Java, JavaScript, Perl, PHP, Python, Ruby
 * cf) Thrift supports :  C++, C#, Erlang, Haskell, Java, Objective C/Cocoa, OCaml, Perl, PHP, Python, Ruby, and Squeak
 * cf) Protocol buffers :  C++, Java, and Python, ...
* key feature : code-gen is not needed


## Thrift/Proto와의 비교

* 참고문서
 * http://www.slideshare.net/IgorAnishchenko/pb-vs-thrift-vs-avro
 * http://ganges.usc.edu/pgroupW/images/a/a9/Serializarion_Framework.pdf

### Thrift와의 공통점
* include RPC framework ( not for proto )
* backward/forward compatiblity
* nested/list/set/map 지원

### Thrift와는 다른점
* Interoperability
 * read from proto/thrift
 * write into proto/thrift
* Schema in JSON
 * more rich data structures
* Dynamic schema
* Buillt into Hadoop
 * Avro datafile which looks like SequenceFile
 * Compressible & Splittable
* no need to compile
 * RPC handshake때 schema가 같이 전달
 * 이 schema로 해석 가능
* no need to declare IDs
 * old/new schema가 함께 전달, 비교해가며 해석 가능
* schema resolution
 * write schema랑 read schema가 달라도 됨


### Avro data types

* null
* boolean, int(32-bit), long(64-bit), float, double
* bytes(seq of 8-bit), string(seq of Unicode)
* arrary, map(key is string), record(is struct)
* enum, fixed, union

### Data mapping
1. Generic mapping(or dynamic mapping)
 * code gen 필요없음
2. Specific mapping
 * 특정 언어의 code gen
3. Reflect mapping
 * Java reflection을 이용해서 type guessing
 * slower

참고) Avro String은 Java String이나 Avro UTF8 둘 중의 하나로 매핑 가능하나 UTF8은 Lazy/mutable하므로 더 편리/효과적

### In-memory serialization/deserialization

By Generic API

* JSON-format으로 schema 작성(avsc)
* Generic API를 사용
 * GenericRecord : object instantization
 * GenericDatumWriter를 이용해서 serialization
* DatumWriter는 schema가 있어야 serialization가능

By Specific API

* avro command-line tool을 이용해서 schema 파일로 code generation
* 만들어진 Java class를 이용해 Java POJO를 만듬
* 만들어진 Java POJO를 SpecificDatumWriter를 이용해 serialization

### Avro Datafile

object container file format

* sequence of avro objects를 저장할 수 있다.
* Hadoop의 SequenceFile의 설계와 비슷
* 구성
 * Header 내에 Avro Schema 내장
 * Header 내에 sync marker 내장
 * Header 뒤에 series of Block
 * 각 block은 serialized avro objects 내장
 * block간에는 sync marker로 구분
* block boundary가 명확하게 구분되므로 이를 바탕으로 Splittable & Compressible
* DataFileWriter를 이용해서 만듬
* avro datafile을 쓸 때는 Schema가 필요하나 읽을 때는 필요 없음. 왜나하면 파일에 내장됨
* Read Access Pattern
 * Usually sequential read
 * Random access pattern support by sync marker



### Schema Resolution

reader schema랑 writer schema가 달라도 읽어짐

* added field in reader schema only
 * write할 때는 없던 필드이므로 읽을 때는 default 값을 채운다.
* removed field in reader schema only
 * 읽을 때는 모르는 필드이므로 ignore(projection)
* GenericDatumReader를 사용한다.
 * oldSchema, newSchema 둘 다 있어야 한다.

### Sort order

schema 내에 object의 sorting 방식을 정의할 수 있다.

* 각 필드마다 ascending/descending/ignore
* Record라면 각 구성 필드마다 붙이고 먼저 나온 것 부터 sorting 기준, 동등이면 다음 필드로 sorting..
* ** Binary comparision **
 * deserialization하지 않아도 serialized bytes 상태에서도 비교가 가능하다.
* writer schema의 sorting방식과 다르게 reader schema에서 sorting방식을 다르게 가져갈 수도 있다.
* sorting 기법은 주로 MapReduce에서 진가를 발휘

### Avro MapReduce

Avro MapReduce API

* AvroKey, AvroValue with GenericRecord
* AvroJob
* 예를 들어 text file을 Avro datafile로 저장하거나 그 반대 방향도 가능
* Avro datafile을 입력으로 해서 출력을 avro datafile로 할수도 있고

### Sorting Using Avro MapReduce

핵심은 map의 output을 AvroKey로 하면 AvroKey에 사용된 GenericRecord는 schema에 정의된 sorting 방식에 근거해서 shuffle단계에서 sorting 된다.


































