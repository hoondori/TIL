package fpscala.gettingstarted.test

import org.scalatest.{FlatSpec,Matchers}

class GettingStartedTest extends FlatSpec with Matchers {

  "Excercise 2.1" should "do" in {

    def fib(n:Int): Int = {
      def loop(n: Int, prev: Int, cur:Int): Int = {
        if(n==0) prev
        else loop(n-1,cur,prev+cur)
      }
      loop(n,0,1)
    }

    val n = 4
    println(s"fibonacchi of ${n} is ${fib(n)}")

  }


}
