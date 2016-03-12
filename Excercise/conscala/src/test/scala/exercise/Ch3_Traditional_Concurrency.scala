package exercise

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.{AtomicReference, AtomicBoolean}
import org.scalatest.{Matchers, FlatSpec}
import scala.annotation.tailrec
import scala.collection.concurrent.TrieMap
import scala.concurrent.ExecutionContext


/**
  * Created by hoondori on 2016. 3. 11..
  */
class Ch3_Traditional_Concurrency extends FlatSpec with Matchers {

  def execute(body: =>Unit) = ExecutionContext.global.execute(
    new Runnable { def run() = body }
  )

  def thread(body: =>Unit): Thread = {
    val t = new Thread {
      override def run() = body
    }
    t.start()
    t
  }

  //The Executor and ExecutionContext objects
  "Text 1" should "do" in {
    // Thread Pool by ForkJoinPool

    import scala.concurrent.forkjoin.ForkJoinPool
    import java.util.concurrent.TimeUnit

    val executor = new ForkJoinPool()
    executor.execute( new Runnable {
      def run() = println("This task is run asynchronously")
    })
    executor.shutdown()
    executor.awaitTermination(60, TimeUnit.SECONDS)
  }

  //The Executor and ExecutionContext objects
  "Text 2" should "do" in {
    import scala.concurrent.ExecutionContext

    val ectx = ExecutionContext.global

    ectx.execute( new Runnable {
      def run() = println("This task is run asynchronously")
    })
    Thread.sleep(500)
  }

  // Atomic variables
  "Text 3" should "do" in {

    import java.util.concurrent.atomic._

    val uid = new AtomicLong(0L)
    def getUniqueId(): Long = uid.incrementAndGet()
    execute {
      println(s"Uid asynchronously: ${getUniqueId()}")
    }
    println(s"Got a unique id: ${getUniqueId()}")
  }

  // Atomic variables
  "Text 4" should "do" in {
    // pseudo code for CAS

    case class MyAtomicLong(var id:Long) {
      def get() = this.id
      def compareAndSet(ov: Long, nv: Long): Boolean = this.synchronized {
        if (this.id != ov)
          false
        else {
          this.id = nv
          true
        }
      }
    }

    val uid = new MyAtomicLong(0L)

    @tailrec def getUniqueId(): Long = {
      val oldUid = uid.get()
      val newUid = oldUid + 1
      if( uid.compareAndSet(oldUid,newUid)) newUid
      else getUniqueId()
    }

    execute {
      println(s"Uid asynchronously: ${getUniqueId()}")
    }
    println(s"Got a unique id: ${getUniqueId()}")

  }

  //Lock-free programming
  "Text 5" should "do" in {

    // atomic variables is a necessary precondition for lock-freedom, but it is not sufficient

    val lock = new AtomicBoolean(false)
    def mySynchronized(body: => Unit): Unit = {
      while(!lock.compareAndSet(false,true)) {}  // incur busy-wait
      try body finally lock.set(false)
    }

    var count = 0
    for (i<- 0 until 10) execute { mySynchronized { count += 1 } }
    Thread.sleep(1000)
    println(s"Count is: $count")
  }

  // Implementing locks explicitly
  "Text 6" should "do" in {

    sealed trait State
    class Idle extends State
    class Creating extends State
    class Copying(val n: Int) extends State
    class Deleting extends State

    class Entry(val isDir: Boolean) {
      val state = new AtomicReference[State](new Idle)
    }

    @tailrec def prepareForDelete(entry: Entry): Boolean = {
      val s0 = entry.state.get()

      s0 match {
        case i: Idle =>
          if( entry.state.compareAndSet(s0,new Deleting)) true
          else prepareForDelete(entry) // retry
        case c: Creating =>
          println("File currently created, cannot delete."); false
        case c: Copying =>
          println("File currently copied, cannot delete."); false
        case d: Deleting =>
          false
      }
    }
  }

  // lazy values
  "Text 7" should "do" in {

    // it is a good practice to initializethe lazy value with an expression that does not depend on the current state of the program

    lazy val obj = new AnyRef
    lazy val non = s"made by ${Thread.currentThread().getName}"
    execute {
     // println(s"EC sees obj = $obj")
      println(s"EC sees non = $non")
    }
   // println(s"Main sees obj = $obj")
    println(s"Main sees non = $non")
    Thread.sleep(500)
  }

  // lazy values
  "Text 8" should "do" in {

    // lazy value deadlock by cyclic referencing, but it cannot be compiled

//    object A { lazy val x: Int = B.y } <- wrong forward reference
//    object B { lazy val y: Int = A.x }
//    execute { B.y }
//    A.x

  }

  // lazy values
  "Text 9" should "do" in {

    // lazy value initialization expression can block a thread until some other value becomes available
    // Never invoke blocking operations inside lazy value initialization expressions or singleton object constructors.
    lazy val x: Int = {
      val t = thread { println(s"Initializing $x")}
      t.join()
      1
    }
    x

  }


  // lazy values
  "Text 10" should "do" in {

    // lazy value initialization expression can block a thread until some other value becomes available
    // Never invoke blocking operations inside lazy value initialization expressions or singleton object constructors.
    lazy val x = 1
    this.synchronized {
      val t = thread { x }
      t.join()
    }

  }

  // Concurrent collections
  "Text 11" should "do" in {

    import scala.collection.mutable.ArrayBuffer

    // standard collection does not provide any synchronization
    val buffer = ArrayBuffer[Int]()
    def asyncAdd(numbers: Seq[Int]) = execute {
      buffer ++= numbers
      println(s"buffer = $buffer")
    }
    asyncAdd(0 until 10)
    asyncAdd(10 until 20)
    Thread.sleep(500)

  }

  // Concurrent collections
  "Text 12" should "do" in {

    // atomic variable based concurrent collection
    // but leading to scalability problems
    class AtomicBuffer[T] {
      private val buffer = new AtomicReference[List[T]](Nil)
      def +=(x: T): Unit = {
        val xs = buffer.get
        val nxs = x::xs
        if(!buffer.compareAndSet(xs,nxs)) this += x
      }
    }

    val buf = new AtomicBuffer[Int]
    execute { for ( i <- 1 to 10 ) buf += i }
    execute { for ( i <- 11 to 20 ) buf += i }

    Thread.sleep(500)

  }

  // Concurrent collections
  "Text 13" should "do" in {
    import scala.collection.mutable.ArrayBuffer

    // standard collection with synchronized
    // but leading to scalability problems
    val buffer = ArrayBuffer[Int]()
    def asyncAdd(numbers: Seq[Int]) = execute {
      buffer.synchronized {
        buffer ++= numbers
        println(s"buffer = $buffer")
      }
    }
    asyncAdd(0 until 10)
    asyncAdd(10 until 20)
    Thread.sleep(500)
  }


  // concurrent queues
  "Text 14" should "do" in {
    // linked-list based blocking queue
    // as a producer-consumer pattern
    import java.util.concurrent.LinkedBlockingQueue

    val messages = new LinkedBlockingQueue[String]()
    val logger = new Thread {
      setDaemon(true)
      override def run() = while(true) println(messages.take())
    } // message consumer
    logger.start()
    def logMessage(msg: String): Unit = messages.offer(msg)

    for (i <- 1 to 100) logMessage(s"$i")

  }

  // concurrent queues
  "Text 15" should "do" in {
    // wealkly consistent iterators
    import java.util.concurrent.LinkedBlockingQueue

    val queue = new LinkedBlockingQueue[String]()
    for (i <- 1 to 5500) queue.offer(i.toString)
    execute {
      val it = queue.iterator()
      while(it.hasNext) println(it.next)
    }
    for( i <- 1 to 5495) queue.poll()
    Thread.sleep(1000)
  }


  // concurrent sets and maps
  "Text 16" should "do" in {

    import scala.collection.JavaConversions._

    // weak consistent iterator by standard concurrent hash map
    val names = new ConcurrentHashMap[String, Int]()
    names("Johonny") =0; names("Jane") = 0; names("Jack") = 0
    execute { for( n <- 0 until 10) names(s"Johnny $n") = n }
    execute {
      for( n <- names ) println(s"name: $n")
    }
    Thread.sleep(1000)


  }

  // concurrent sets and maps
  "Text 17" should "do" in {
    // strong consistent iterator by TriMap
    val names = new TrieMap[String,Int]()
    names("Janiece") = 0; names("Jackie") = 0; names("Jill") = 0;
    execute { for( n <- 10 until 100) names(s"John $n") = n }
    execute {
      println("snapshot time!")
      for( n <- names.map(_._1).toSeq.sorted) println(s"name: $n")
    }
    Thread.sleep(1000)
  }

  // creating and handling processes
  "Text 18" should "do" in {
    import scala.sys.process._
    val command = "ls"
    val exitcode = command.!
    println(s"command exited with status $exitcode")
  }

  // creating and handling processes
  "Text 19" should "do" in {
    import scala.sys.process._
    val command = s"wc build.sbt"
    val output = command.!!
    println( output.trim.split(" ").head.toInt )

  }

  // creating and handling processes
  "Text 20" should "do" in {
    import scala.sys.process._
    val lsProcess = "ls -R /".run()  // async run
    lsProcess.exitValue() // like await.ready
  }

  // creating and handling processes
  "Text 21" should "do" in {
    import scala.sys.process._
    val lsProcess = "ls -R /".run()  // async run
    Thread.sleep(1000)
    println("Timeout - killing ls!")
    lsProcess.destroy()
  }

}
