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

    /*Use foldRight to implement takeWhile.*/

    Stream(1,2,3,4).takeWhile_byFoldRight( _ < 3).toList should be (List(1,2))
  }

  "Exercise 5.6" should "do" in {

    /*Hard: Implement headOption using foldRight*/


  }

  "Exercise 5.7" should "do" in {

    /*Implement map, filter, append, and flatMap using foldRight.
    The append method should be non-strict in its argument*/

    Stream(1,2,3,4).map( _ * 2 ).toList should be (List(2,4,6,8))
    Stream(1,2,3,4).flatMap( x => Stream(x*2) ).toList should be (List(2,4,6,8))
    Stream(1,2,3,4).filter( _ < 3).toList should be (List(1,2))

  }

  "Exercise 5.8" should "do" in {

    /*Generalize ones slightly to the function constant, which returns an infinite Stream of a given value.
    def constant[A](a: A): Stream[A]*/

    Stream.constant(3).take(4).toList should be (List(3,3,3,3))

  }

  "Exercise 5.9" should "do" in {

    /*Write a function that generates an infinite stream of integers,
    starting from n, then n + 1, n + 2, and so on.
    def from(n: Int): Stream[Int]*/

    Stream.from(1).take(4).toList should be (List(1,2,3,4))

  }

  "Exercise 5.10" should "do" in {

    /*Write a function fibs that generates the infinite stream
    of Fibonacci numbers: 0, 1, 1, 2, 3, 5, 8, and so on.*/

    Stream.fibs().take(6).toList should be (List(0,1,1,2,3,5))

  }

  "Exercise 5.11" should "do" in {

    /*Write a more general stream-building function called unfold. It takes an initial state,
    and a function for producing both the next state and the next value in the generated stream.
    def unfold[A, S](z: S)(f: S => Option[(A, S)]): Stream[A]*/



  }
  "Exercise 5.12" should "do" in {

  }
  "Exercise 5.13" should "do" in {

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

  def exists(p: A => Boolean): Boolean = foldRight(false)((h,t) => p(h) || t )

  def forAll(p: A => Boolean): Boolean = foldRight(true)((h,t) => p(h) && t )

  def takeWhile_byFoldRight(p: A => Boolean): Stream[A] =
    foldRight(empty[A])((h,t) => if(p(h)) cons(h, t) else empty[A])

  def headOption: Option[A] =
    foldRight(None: Option[A])((h,_) => Some(h))

  def map[B](f: A => B): Stream[B] = {
    foldRight(empty[B])((h,t) => cons(f(h),t))
  }

  def filter(p: A => Boolean): Stream[A] = {
    foldRight(empty[A])((h,t) => if (p(h)) cons(h,t) else t )
  }

  def append[B>:A](z: => Stream[B]): Stream[B] =
    foldRight(z)((h,t) => cons(h,t))

  def flatMap[B](f: A => Stream[B]): Stream[B] =
    foldRight(empty[B])((h,t) => f(h).append(t))

  def find(p: A => Boolean): Option[A] =
    filter(p).headOption


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

  // This is more efficient than `cons(a, constant(a))` since it's just
  // one object referencing itself.
  def constant[A](a: A): Stream[A] = {
    lazy val tail: Stream[A] = Cons(() => a, () => tail)
    tail
  }

  def constant_1[A](a: A): Stream[A] = cons(a, constant(a))

  def from(n: Int): Stream[Int] = cons(n, from(n+1))

  def fibs(): Stream[Int] = {

    def go(f0:Int, f1:Int): Stream[Int] = cons(f0, go(f1,f0+f1))
    go(0,1)
  }

  def unfold[A,S](z: S)(f: S => Option[(A,S)]): Stream[A] = {

    f(z) match {
      case None => empty
      case Some((a,s)) => cons(a, unfold(s)(f))
    }
  }
}