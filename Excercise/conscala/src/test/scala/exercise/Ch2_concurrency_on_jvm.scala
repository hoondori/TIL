package exercise

import org.scalatest.{Matchers, FlatSpec}

import scala.concurrent.Future

class Ch2_concurrency_on_jvm extends FlatSpec with Matchers {

  def thread(body: =>Unit): Thread = {
    val t = new Thread {
      override def run() = body
    }
    t.start()
    t
  }

  "Text 1" should "do" in {

    val t: Thread = Thread.currentThread()
    val name = t.getName
    println(s"I am the thread $name")

  }

  "Text 2" should "do" in {

    /* class-defined style of thread usage */

    class MyThread extends Thread {
      override def run(): Unit = {
        println("New thread running")
      }
    }
    val t = new MyThread
    t.start()
    t.join()
    println("New thread joined")
  }

  "Text 3" should "do" in {

    /* lamda style of thread usage */

    val t = thread {
      Thread.sleep(1000)
      println("New thread running")
      Thread.sleep(1000)
      println("Still running")
      Thread.sleep(1000)
      println("Completed")
    }

    t.join()
    println("New thread joined")
  }

  "Text 4" should "do" in {

    /* nondeterministic result */

    val t = thread { println("New thread running.") }
    println("...1")
    println("...2")
    t.join()
    println("New thread joined")

  }

  "Text 5" should "do" in {

    /* thread termination ensure happen-before relationship */

    var result: String = "old"
    val t = thread { result = "new" }
    t.join()
    println(result)
    result should not equal ("old")
    result should equal ("new")

  }


  "Text 6" should "do" in {

    /* read and write interleaved leads to nondeterministic */

    var uidCount = 0

    def getUniqueId() = {
      val freshUid = uidCount + 1
      uidCount = freshUid
      freshUid
    }

    def printUniqueIds(n: Int): Unit = {
      val uids = for (i <- 0 until n) yield getUniqueId()
      println(s"Geneated uids: $uids")
    }

    // r/w may be interleaved by both of thread execution
    val t = thread { printUniqueIds(5) }
    printUniqueIds(5)

    t.join()

  }

  "Text 7" should "do" in {

    /* use of synchronized leads to deterministic */

    var uidCount = 0

    def getUniqueId() = this.synchronized {
      val freshUid = uidCount + 1
      uidCount = freshUid
      freshUid
    }

    def printUniqueIds(n: Int): Unit = {
      val uids = for (i <- 0 until n) yield getUniqueId()
      println(s"Geneated uids: $uids")
    }

    // r/w may be interleaved by both of thread execution
    val t = thread { printUniqueIds(5) }
    printUniqueIds(5)

    t.join()

  }

  // monitor and synchronization
  "Text 8" should "do" in {

    /* use of nested synchronized */

    import scala.collection._
    val transfers = mutable.ArrayBuffer[String]()
    def logTransfer(name: String, n: Int) = transfers.synchronized {
      transfers += s"transfer to account '$name' = $n"
    }
    class Account(val name: String, var money: Int)
    def add(account: Account, n: Int) = account.synchronized {
      account.money += n
      if(n >10) logTransfer(account.name, n)
    }

    val jane = new Account("Jane", 100)
    val john = new Account("john", 200)
    val t1 = thread { add(jane,20) }
    val t2 = thread { add(john,25) }
    val t3 = thread { add(jane,70)}
    t1.join()
    t2.join()
    t3.join()

    println(s"----transfers----\n$transfers")

  }

  // Deadlocks
  "Text 9" should "do" in {

    /* Deadlock happens */

    class Account(val name: String, var money: Int)

    def send(a: Account, b: Account, n: Int) = a.synchronized {
      b.synchronized {
        a.money -= n
        b.money += n
      }
    }

    val a = new Account("Jack", 1000)
    val b = new Account("Jill", 2000)
    val t1 = thread { for ( i<-0 until 100) send(a,b,1) }
    val t2 = thread { for ( i<-0 until 100) send(b,a,1) }
    t1.join(); t2.join()

    println(s"a = ${a.money}, b = ${b.money}")

  }

  // Deadlocks
  "Text 10" should "do" in {

    /* How to avoid deadlock - make access order of resources */

    var uidCount = 0
    def getUniqueId() = this.synchronized {
      val freshUid = uidCount + 1
      uidCount = freshUid
      freshUid
    }

    class Account(val name: String, var money: Int) {
      val uid = getUniqueId()
    }

    def send(a1: Account, a2: Account, n: Int) = {

      def adjust(): Unit = {
        a1.money -= n
        a2.money += n
      }

      if (a1.uid > a2.uid) a1.synchronized {
        a2.synchronized {
          adjust()
        }
      }
      else a2.synchronized {
        a1.synchronized {
          adjust()
        }
      }
    }


    val a = new Account("Jack", 1000)
    val b = new Account("Jill", 2000)
    val t1 = thread { for ( i<-0 until 100) send(a,b,1) }
    val t2 = thread { for ( i<-0 until 100) send(b,a,2) }
    t1.join(); t2.join()

    println(s"a = ${a.money}, b = ${b.money}")

    a.money should be (1100)
    b.money should be (1900)

  }

  // Guarded blocks
  "Text 11" should "do" in {

    /* busy wait on work to do */

    import scala.collection._

    val tasks = mutable.Queue[() => Unit]()

    val worker = new Thread {

      def poll(): Option[() => Unit] = tasks.synchronized {
        if (tasks.nonEmpty) Some(tasks.dequeue()) else None
      }
      override def run() = while(true) poll() match {
          case Some(task) => task()
          case None =>
      }
    }

    worker.setName("Worker")
    worker.setDaemon(true)
    worker.start()

    def asynchronous(body: => Unit) = tasks.synchronized {
      tasks.enqueue(() => body)
    }
    asynchronous { println("hello") }
    asynchronous { println("world") }
    Thread.sleep(5000)
  }

  // Guarded blocks
  "Text 12" should "do" in {

    /* use of event-based wait along with guard against false-positive wakeup */

    import scala.collection._

    val tasks = mutable.Queue[() => Unit]()

    val worker = new Thread {
      setDaemon(true)
      def poll(): () => Unit = tasks.synchronized {
        while(tasks.isEmpty) tasks.wait()
        tasks.dequeue()
        //if (tasks.nonEmpty) Some(tasks.dequeue()) else None
      }
      override def run() = while(true) {
        val task = poll()
        task()
      }
    }

    worker.setName("Worker")
    worker.start()

    def asynchronous(body: => Unit) = tasks.synchronized {
      tasks.enqueue(() => body)
      tasks.notify()
    }
    asynchronous { println("hello") }
    asynchronous { println("world") }
    Thread.sleep(2000)

  }

  // Interrupt and gracefuly shutdown
  "Text 13" should "do" in {

    /* use of graceful shutdown */

    import scala.collection._
    import scala.annotation.tailrec

    val tasks = mutable.Queue[() => Unit]()

    val worker = new Thread {
      var terminated = false
      def poll(): Option[() => Unit] = tasks.synchronized {
        while(tasks.isEmpty && !terminated) tasks.wait()
        if(!terminated) Some(tasks.dequeue()) else None
      }
      @tailrec override def run() = poll() match {
        case Some(task) => {
          task()
          run()
        }
        case None => { println("I am dying...") }
      }
      def shutdown() = tasks.synchronized {
        terminated = true
        tasks.notify()
      }
    }

    worker.setName("Worker")
    worker.start()

    def asynchronous(body: => Unit) = tasks.synchronized {
      tasks.enqueue(() => body)
      tasks.notify()
    }
    asynchronous { println("hello") }
    worker.shutdown()
    asynchronous { println("world") }
    Thread.sleep(2000)

  }

  // volatile variables
  "Text 14" should "do" in {

    class Page(val txt: String, var position: Int)
    val pages = for( i <- 1 to 5 ) yield new Page("Na" * (100-20*i) + "Batman!", -1)
    @volatile var found = false
    for( p <- pages ) {
      var i = 0
      while( i < p.txt.length && !found) {
        if( p.txt(i) == '!') {
          p.position = i
          found = true
        } else {
          i += 1
        }
      }
    }
    while(!found){}
    pages foreach { p => println(s"txt= ${p.txt}, pos=${p.position}")}
    println(s"results: ${pages.map(_.position)}")

  }

  "Exercise 2.1" should "do" in {

    /*Implement a parallel method, which takes two computation blocks a and b,
    and starts each of them in a new thread.
    The method must return a tuple with the result values of both the computations*/

    case class RetA[A](a: A)

    def parallel[A,B]( a: => A, b: => B): (A,B) = {
      var ret1: A = null.asInstanceOf[A]
      var ret2: B = null.asInstanceOf[B]
      val t1 = thread { ret1 = a }
      val t2 = thread { ret2 = b }
      t1.join(); t2.join()
      (ret1,ret2)
    }

    parallel[Int,Double]( 1+1, 1.0+2.1 ) should be ((2,3.1))

  }


  "Exercise 2.2" should "do" in {

    /*Implement a periodically method, which takes a time interval duration
      specified in milliseconds, and a computation block b. The method starts a
      thread that executes the computation block b every duration milliseconds.
    It should have the following signature:
    def periodically(duration: Long)(b: =>Unit): Unit*/

    def periodically(duration: Long)(b: => Unit): Unit = {
      val t = thread {
        while (true) {
          b
          Thread.sleep(duration)
        }
      }
      t.join()
    }

    periodically(3000) { println("babo") }



  }


  "Exercise 2.3" should "do" in {

    /*Implement a SyncVar class with the following interface:
    class SyncVar[T] {
      def get(): T = ???
      def put(x: T): Unit = ???
    }*/

    class SyncVar[T] {
      var empty:Boolean = true
      var innerVal = null.asInstanceOf[T]

      def get(): T = this.synchronized {
        empty match {
          case true => throw new RuntimeException("syncvar is empty")
          case false => {
            empty = true
            val copy = innerVal
            innerVal = null.asInstanceOf[T]
            copy
          }
        }
      }
      def put(x: T): Unit = this.synchronized {
        empty match {
          case false =>  throw new RuntimeException("syncvar is not empty")
          case true => {
            innerVal = x
            empty = false
          }
        }
      }
    }
  }


  "Exercise 2.4" should "do" in {

    /*The SyncVar object from the previous exercise can be cumbersome to use,
    due to exceptions when the SyncVar object is in an invalid state. Implement
    a pair of methods isEmpty and nonEmpty on the SyncVar object. Then,
    implement a producer thread that transfers a range of numbers 0 until 15
    to the consumer thread that prints them.*/

    class SyncVar[T] {
      var empty:Boolean = true
      var innerVal = null.asInstanceOf[T]

      def get(): T = this.synchronized {
        empty match {
          case true => throw new RuntimeException("syncvar is empty")
          case false => {
            empty = true
            val copy = innerVal
            innerVal = null.asInstanceOf[T]
            copy
          }
        }
      }

      def put(x: T): Unit = this.synchronized {
        empty match {
          case false =>  throw new RuntimeException("syncvar is not empty")
          case true => {
            innerVal = x
            empty = false
          }
        }
      }

      def isEmpty(): Boolean = this.synchronized { this.empty }

      def nonEmpty(): Boolean = this.synchronized{ ! this.empty }
    }

    val mySyncVar = new SyncVar[Int]()
    val producer = thread {
      for(i <- 0 until 15) {
        while(mySyncVar.nonEmpty) {}
        mySyncVar.put(i)
      }
    }
    val consumer = thread {
      for(i <- 0 until 15) {
        while(mySyncVar.isEmpty) {}
        println( mySyncVar.get )
      }
    }
    producer.join()
    consumer.join()
  }


  "Exercise 2.5" should "do" in {

    /*Using the isEmpty and nonEmpty pair of methods from the previous exercise
      requires busy-waiting. Add the following methods to the SyncVar class:
    def getWait(): T
    def putWait(x: T): Unit*/

    class SyncVar[T] {
      var empty:Boolean = true
      var innerVal = null.asInstanceOf[T]

      def getWait(): T = this.synchronized {
        while(empty)
          this.wait()

        // tell it is empty now
        empty = true
        this.notify()

        innerVal
      }
      def putWait(x: T): Unit = this.synchronized {
        while(empty == false) this.wait()

        innerVal = x

        // tell it is not-empty anymore
        empty = false
        this.notify()
      }
    }

    val mySyncVar = new SyncVar[Int]()
    val producer = thread {
      for(i <- 0 until 15) {
        mySyncVar.putWait(i)
      }
    }
    val consumer = thread {
      for(i <- 0 until 15) {
        println( mySyncVar.getWait )
      }
    }
    producer.join()
    consumer.join()


  }


  "Exercise 2.6" should "do" in {

/*
    A SyncVar object can hold at most one value at a time. Implement a
      SyncQueue class, which has the same interface as the SyncVar class, but can
      hold at most n values. The parameter n is specified in the constructor of the
      SyncQueue class
*/


    
  }


  "Exercise 2.7" should "do" in {

  }


}
