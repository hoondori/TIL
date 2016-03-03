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
