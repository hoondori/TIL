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

  "Excercise 2.2" should "do" in {

/*
    Implement isSorted, which checks whether an Array[A] is sorted according to a
      given comparison function:
    def isSorted[A](as: Array[A], ordered: (A,A) => Boolean): Boolean
*/

    def isSorted[A](as : Array[A], ordered: (A,A) => Boolean): Boolean = {
      def loop(n: Int): Boolean = {
        if ( n >= as.length - 1 ) true
        else if ( ordered(as(n),as(n+1)) == false ) false
        else loop(n+1)
      }
      loop(0)
    }

    isSorted( Array(1,2,3), (x:Int,y:Int) => (x <= y) ) should be (true)
    isSorted( Array(3,2,1), (x:Int,y:Int) => (x <= y) ) should be (false)
    isSorted( Array(3,1,2), (x:Int,y:Int) => (x <= y) ) should be (false)
  }

  "Excercise 2.3" should "do" in {

/*
    Let’s look at another example, currying,9 which converts a function f of two arguments
    into a function of one argument that partially applies f. Here again there’s only one
    implementation that compiles. Write this implementation.
    def curry[A,B,C](f: (A, B) => C): A => (B => C)
*/





  }



}
