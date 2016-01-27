package fpscala.laziness

import org.scalatest.{Matchers, FlatSpec}

class lazinessTest extends FlatSpec with Matchers {

  "Exercise 5.0" should "do" in {

    def if2[A](cond: Boolean,
               onTrue: () => A,
               onFalse: () => A):A =
      if (cond) onTrue() else onFalse()

    def if2_1[A](cond: Boolean,
               onTrue: => A,
               onFalse: => A):A =
      if (cond) onTrue else onFalse

    if2( 22 > 1, ()=> println("big"), ()=> println("small"))
    if2_1( 22 > 1, println("big"), println("small"))
    if2_1(false, sys.error("fail"), 3)

    def maybeTwice(b: Boolean, i: => Int) =
      if(b) i+i else 0

    val x = maybeTwice(true, { println("hi"); 1+41 })
    x should be (84)

    def maybeTwice2(b: Boolean, i: => Int ) = {
      lazy val j = i
      if(b) j+j else 0
    }

    val y = maybeTwice2(true, { println("hi2"); 1+41 })
    x should be (84)
  }
}

sealed trait Stream[+A]
case object Empty extends Stream[Nothing]
case class Cons[+A](h: () => A, t: () => Stream[A]) extends Stream[A]

object Stream {
  def cons[A](hd: => A, tl: => Stream[A])
}