# Avro

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
 * Compressible & Splittable
* no need to compile
 * RPC handshake때 schema가 같이 전달
 * 이 schema로 해석 가능
* no need to declare IDs
 * old/new schema가 함께 전달, 비교해가며 해석 가능



