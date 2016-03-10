package exercise

import java.util.concurrent.atomic.{AtomicReference, AtomicBoolean}
import org.scalatest.{Matchers, FlatSpec}
import scala.annotation.tailrec
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


}
