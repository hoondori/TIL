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

  "Exercise 5.1" should "do" in {

    /*Write a function to convert a Stream to a List, which will force its evaluation and let you look at it in the REPL.
    You can convert to the regular List type in the standard library.
      You can place this and other functions that operate on a Stream inside the Stream trait.*/

    Stream[Int](1,2,3,4).toList should be (List(1,2,3,4))
    Stream.empty.toList should be (List())

  }

  "Exercise 5.2" should "do" in {

    /*Write the function take(n) for returning the first n elements of a Stream,
    and drop(n) for skipping the first n elements of a Stream.*/

    Stream(1,2,3,4).take(2).toList should be (List(1,2))
    Stream(1,2,3,4).drop(2).toList should be (List(3,4))

  }

  "Exercise 5.3" should "do" in {

    /*Write the function takeWhile for returning all starting elements of a Stream
    that match the given predicate.
    def takeWhile(p: A => Boolean): Stream[A]*/

    Stream(1,2,3,4).takeWhile( _ < 3 ).toList should be (List(1,2))

  }

  "Exercise 5.4" should "do" in {

   /* Implement forAll, which checks that all elements in the Stream match a given predicate.
      Your implementation should terminate the traversal as soon as it encounters a nonmatching value.
    def forAll(p: A => Boolean): Boolean*/

    Stream(1,2,3,4).exists( _ == 3 ) should be (true)
    Stream(1,2,3,4).exists( _ == 5 ) should be (false)

    Stream(1,2,3,4).forAll( _ < 5 ) should be (true)
    Stream(1,2,3,4).forAll( _ % 2 == 0 ) should be (false)



  }

  "Exercise 5.5" should "do" in {

  }

  "Exercise 5.6" should "do" in {

  }

  "Exercise 5.7" should "do" in {

  }

  "Exercise 5.8" should "do" in {

  }
}

sealed trait Stream[+A] {

  import Stream._

  def toList: List[A] = {

    def go(s: Stream[A], acc: List[A]): List[A] = s match {
      case Cons(h,t) => go( t(), h() :: acc)
      case _ => acc
    }
    go(this, List()).reverse
  }

  // will be very slow because append very long sequence op is very time consuming
  // instead, prepend and then reverse
  def toList_1: List[A] = this match {
    case Empty => List()
    case Cons( h, t) => List(h()) ++ t().toList
  }

  def take(n: Int): Stream[A] = this match {
    case Cons(h,t) if n > 1 => cons( h(), t().take(n-1) )
    case Cons(h,_) if n == 1 => cons( h(), Stream.empty )
    case _ => empty
  }

  def drop(n: Int): Stream[A] = this match {
    case Cons(h,t) if n > 0 => t().drop(n-1)
    case _ => this
  }

  def takeWhile(p: A => Boolean): Stream[A] = this match {
    case Cons(h,t) if p(h()) => cons( h(), t().takeWhile(p))
    case _ => empty
  }

  def exists_1(p: A => Boolean): Boolean = this match {
    case Cons(h,t) => p(h()) || t().exists_1(p)
    case _ => false
  }

  def foldRight[B](z: => B)(f: (A, => B) => B): B = this match {
    case Cons(h,t) => f(h(), t().foldRight(z)(f))
    case _ => z
  }

  def exists(p: A => Boolean): Boolean = foldRight(false)((a,b) => p(a) || b )

  def forAll(p: A => Boolean): Boolean = foldRight(true)((a,b) => p(a) && b )
}
case object Empty extends Stream[Nothing]
case class Cons[+A](h: () => A, t: () => Stream[A]) extends Stream[A]

object Stream {
  // smart constructor that make the value lazily evaluated
  def cons[A](hd: => A, tl: => Stream[A]): Stream[A] = {
    lazy val head = hd
    lazy val tail = tl
    Cons(()=> head, () => tail)
  }

  def empty[A]: Stream[A] = Empty

  def apply[A](as: A*): Stream[A] =
    if (as.isEmpty) empty else cons(as.head, apply(as.tail: _*))



}