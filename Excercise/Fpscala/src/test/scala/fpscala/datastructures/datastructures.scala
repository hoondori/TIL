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
    case Cons(0.0, _) => 0.0
    case Cons(x, xs) => x * product(xs)
  }

  def removeFirstElem[A](a: List[A]): List[A] = a match {
    case Nil => Nil
    case Cons(x, xs) => xs
  }

  def apply[A](as: A*): List[A] =
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))
}