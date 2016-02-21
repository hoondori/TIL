package exercise

import org.scalatest.{Matchers, FlatSpec}

class Ch2_concurrency_on_jvm extends FlatSpec with Matchers {

  def thread(body: =>Unit): Thread = {
    val t = new Thread {
      override def run() = body
    }
    t.start()
    t
  }

  "Exercise 2.1" should "do" in {

    /*Implement a parallel method, which takes two computation blocks a and b,
    and starts each of them in a new thread*/

    val t = thread { Thread.sleep(1000)}

    def parallel[A, B](a: =>A, b: =>B): (A,B) = ???

    val a: => Unit = {
      println("hello")
    }
    val b = {
      println("babo")
    }


  }


  "Exercise 2.2" should "do" in {

  }


  "Exercise 2.3" should "do" in {

  }


  "Exercise 2.4" should "do" in {

  }


}
