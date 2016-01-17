package fpscala.datastructures

import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by hoondori on 2016. 1. 18..
  */
class datastructuresTest extends FlatSpec with Matchers {

  "Exercise 3.1" should "do" in {

   /* What will be the result of the following match expression?
    val x = List(1,2,3,4,5) match {
      case Cons(x, Cons(2, Cons(4, _))) => x
      case Nil => 42
      case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
      case Cons(h, t) => h + sum(t)
      case _ => 101
    }*/

    import List._

    val x = List(1,2,3,4,5) match {
      case Cons(x, Cons(2, Cons(4, _))) => x
      case Nil => 42
      case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
      case Cons(h, t) => h + sum(t)
    }

    x should be (3)  // 1 + 2
  }

  "Exercise 3.2" should "do" in {

/*
    Implement the function tail for removing the first element of a List. Note that the
    function takes constant time. What are different choices you could make in your
    implementation if the List is Nil?
*/

    def tail[A](a: List[A]): List[A] = a match {
      case Nil => throw new RuntimeException("Emtpy list")
      case Cons(_, xs) => xs
    }

    tail(List(1,2,3,4)) should be (List(2,3,4))

    intercept[RuntimeException] {
      tail(Nil)
    }

  }

  "Exercise 3.3" should "do" in {

/*  Using the same idea, implement the function setHead for replacing the first element
    of a List with a different value.*/

    def setHead[A](h: A, l: List[A]): List[A] = l match {
      case Nil => throw new RuntimeException("Emtpy list")
      case Cons(_,xs) => Cons(h,xs)
    }

    setHead(10,List(1,2,3,4)) should be (List(10,2,3,4))

  }

  "Exercise 3.4" should "do" in {

/*
    Generalize tail to the function drop, which removes the first n elements from a list.
      Note that this function takes time proportional only to the number of elements being
      dropped—we don’t need to make a copy of the entire List.
    def drop[A](l: List[A], n: Int): List[A]
*/
    def drop[A](l: List[A], n: Int): List[A] = l match {
      case Nil => Nil
      case Cons(_, xs) if n == 1 => xs
      case Cons(_, xs) if n > 0 => drop(xs,n-1)
    }

    drop(List(1,2,3,4,5), 2) should be (List(3,4,5))

  }

  "Exercise 3.5" should "do" in {

/*
    Implement dropWhile, which removes elements from the List prefix as long as they
    match a predicate.
    def dropWhile[A](l: List[A], f: A => Boolean): List[A]
*/

    def dropWhile[A](l: List[A], f: A => Boolean): List[A] = l match {
      case Cons(x,xs) if f(x) => dropWhile(xs,f)
      case _ => l
    }

    dropWhile(List(1,2,3,4), (x:Int) => (x<=2)) should be (List(3,4))

  }

  "Exercise 3.6" should "do" in {

/*
    Not everything works out so nicely. Implement a function, init, that returns a List consisting of all but the last element of a List. So, given List(1,2,3,4), init will
    return List(1,2,3). Why can’t this function be implemented in constant time like tail?

    def init[A](l: List[A]): List[A]
*/

    def init[A](l: List[A]): List[A] = l match {
      case Cons(_,Nil) => Nil
      case Cons(x,xs) => Cons(x, init(xs))
    }

    def init2[A](l: List[A]): List[A] = {
      import collection.mutable.ListBuffer
      val buf = new ListBuffer[A]
      @annotation.tailrec
      def go(cur: List[A]): List[A] = cur match {
        case Nil => sys.error("init of empty list")
        case Cons(_,Nil) => List(buf.toList: _*)
        case Cons(h,t) => buf += h; go(t)
      }
      go(l)
    }

    init(List(1,2,3,4)) should be (List(1,2,3))
    init2(List(1,2,3,4)) should be (List(1,2,3))
  }

  "Exercise 3.7" should "do" in {

/*
    Can product, implemented using foldRight, immediately halt the recursion and
    return 0.0 if it encounters a 0.0? Why or why not? Consider how any short-circuiting
    might work if you call foldRight with a large list.
*/

/*
    No, this is not possible! The reason is because _before_ we ever call our function,
    `f`, we evaluate its argument, which in the case of `foldRight` means traversing the list
    all the way to the end. We need _non-strict_ evaluation to support early termination
    we discuss this in chapter 5.
*/

  }

  "Exercise 3.8" should "do" in {

    /*See what happens when you pass Nil and Cons themselves to foldRight, like this:
      foldRight(List(1,2,3), Nil:List[Int])(Cons(_,_)).10 What do you think this
    says about the relationship between foldRight and the data constructors of List?*/

    import List._

    println( foldRight(List(1,2,3), Nil:List[Int])(Cons(_,_)) )
  }

  "Exercise 3.9" should "do" in {

    /*Compute the length of a list using foldRight.
    def length[A](as: List[A]): Int*/

    import List._

    def length[A](as: List[A]): Int =
      foldRight(as,0)( (_,acc) => acc+1 )

    length(List(1,2,3)) should be (3)

  }

  "Exercise 3.10" should "do" in {

/*
    Our implementation of foldRight is not tail-recursive and will result in a StackOverflowError
    for large lists (we say it’s not stack-safe). Convince yourself that this is the
    case, and then write another general list-recursion function, foldLeft, that is tail-recursive,
    using the techniques we discussed in the previous chapter. Here is its signature:
    def foldLeft[A,B](as: List[A], z: B)(f: (B, A) => B): B
*/

  }

  "Exercise 3.11" should "do" in {

/*
    Write sum, product, and a function to compute the length of a list using foldLeft
*/

    import List._
    def sum3(ns: List[Int]) = foldLeft(ns,0)(_+_)
    def product3(ds: List[Double]) = foldLeft(ds,1.0)(_*_)
    def length2[A](l: List[A]): Int = foldLeft(l,0)( (acc,_) => acc+1 )

    sum3(List(1,2,3)) should be (6)
    product3(List(1,2,3,4)) should be (24)
    length2(List(1,2,3)) should be (3)
  }

  "Exercise 3.12" should "do" in {

/*
    Write a function that returns the reverse of a list (given List(1,2,3) it returns
      List(3,2,1)). See if you can write it using a fold.
*/
    import List._
    def reverse[A](l: List[A]): List[A] = foldLeft(l,List[A]())( (acc,h) => Cons(h, acc) )

    reverse(List(1,2,3)) should be (List(3,2,1))

  }

  "Exercise 3.13" should "do" in {

  }

  "Exercise 3.14" should "do" in {

  }

  "Exercise 3.15" should "do" in {

  }

}


sealed trait List[+A]
case object Nil extends List[Nothing]
case class Cons[+A](head: A, tail: List[A]) extends List[A]

object List {

  def sum[A](ints: List[Int]): Int = ints match {
    case Nil => 0
    case Cons(x, xs) => x + sum(xs)
  }

  def product[A](ds: List[Double]): Double = ds match {
    case Nil => 1.0
    //case Cons(0.0, _) => 0.0
    case Cons(x, xs) => x * product(xs)
  }

  def foldRight[A,B](as: List[A], z:B)(f: (A,B) => B): B = as match {
    case Nil => z
    case Cons(x,xs) => f(x, foldRight(xs,z)(f))
  }

  def sum2(ns: List[Int]) = foldRight(ns,0)(_+_)
  def product2(ns: List[Int]) = foldRight(ns,0)(_*_)

  def foldLeft[A,B](as: List[A], z: B)(f: (B,A) => B): B = as match {
    case Nil => z
    case Cons(h,t) => foldLeft(t,f(z,h))(f)
  }


  def apply[A](as: A*): List[A] =
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))
}

