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

  // Deadlocks
  "Text 15" should "do" in {

    /* How to avoid deadlock - make access order of resources */


  }


  "Exercise 2.1" should "do" in {

    /*Implement a parallel method, which takes two computation blocks a and b,
    and starts each of them in a new thread*/


  }


  "Exercise 2.2" should "do" in {
    Future
  }


  "Exercise 2.3" should "do" in {

  }


  "Exercise 2.4" should "do" in {

  }


}
