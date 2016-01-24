package fpscala.errorhandling

import org.scalatest.{Matchers, FlatSpec}


class Errorhandling extends FlatSpec with Matchers {

  "Exercise 4.1" should "do" in {

    Some(1) map (_*2) should be (Some(2))
    Some(1) getOrElse("a") should be (1)
    None getOrElse("a") should be ("a")
    Some(1) flatMap { v => Some(v*2)} should be (Some(2))
    Some(1) flatMap2 { v => Some(v*2)} should be (Some(2))
    Some(1) orElse(Some(2)) orElse(Some(3)) should be (Some(1))
    Some(1) orElse2(Some(2)) orElse(Some(3)) should be (Some(1))
    Some(1) filter { _ == 1 } should be (Some(1))
    Some(1) filter2 { _ == 1 } should be (Some(1))

  }

  "Exercise 4.2" should "do" in {

    /*Implement the variance function in terms of flatMap. If the mean of a sequence is m,
    the variance is the mean of math.pow(x - m, 2) for each element x in the sequence.
    See the definition of variance on Wikipedia (http://mng.bz/0Qsr).
    def variance(xs: Seq[Double]): Option[Double]*/


  }

  "Exercise 4.3" should "do" in {

    /*Write a generic function map2 that combines two Option values using a binary function.
    If either Option value is None, then the return value is too. Here is its signature:
    def map2[A,B,C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C]*/

    def map2[A,B,C](a: Option[A], b:Option[B])(f: (A,B) => C): Option[C] =
      a flatMap { aa =>
        b map { bb =>
          f(aa,bb)
        }
      }

    def map2_alternative[A,B,C](a: Option[A], b:Option[B])(f: (A,B) => C): Option[C] = (a,b) match {
      case (None,_) => None
      case (_,None) => None
      case (Some(v1),Some(v2)) => Some(f(v1,v2))
    }

    def insuranceRateQuote( age: Int, tickets: Int): Double = age*2 + tickets*3
    def parseInsuranceRateQuote( age: String, numberOfSpeedingTickets: String): Option[Double] = {
      val optAge: Option[Int] = Try(age.toInt)
      val optTickets : Option[Int] = Try(numberOfSpeedingTickets.toInt)
      map2(optAge,optTickets)(insuranceRateQuote) // make opt-aware
    }

    parseInsuranceRateQuote("1","2") should be (Some(8.0))
    parseInsuranceRateQuote("a","2") should be (None)

  }

  "Exercise 4.4" should "do" in {

    /*Write a function sequence that combines a list of Options into one Option containing
      a list of all the Some values in the original list. If the original list contains None even
    once, the result of the function should be None; otherwise the result should be Some
    with a list of all the values. Here is its signature
    def sequence[A](a: List[Option[A]]): Option[List[A]]*/

    import List._

    def map2[A,B,C](a: Option[A], b:Option[B])(f: (A,B) => C): Option[C] =
      a flatMap { aa =>
        b map { bb =>
          f(aa,bb)
        }
      }

    def sequence[A](a: List[Option[A]]): Option[List[A]] =
      a.foldRight[Option[List[A]]](Some(Nil))((x,y) => map2(x,y)(_ :: _))

    def sequence_2[A](a: List[Option[A]]): Option[List[A]] = a match {
      case Nil => Some(Nil)
      case h::t =>
        h flatMap { hh:A =>
          sequence_2(t) map {
            tt => hh::tt
          }
        }
    }
  }

  "Exercise 4.5" should "do" in {

    /*Implement this function. It’s straightforward to do using map and sequence,
    but try for a more efficient implementation that only looks at the list once. In fact, implement sequence in terms of traverse*/


    def map2[A,B,C](a: Option[A], b:Option[B])(f: (A,B) => C): Option[C] =
      a flatMap { aa =>
        b map { bb =>
          f(aa,bb)
        }
      }

    def sequence[A](a: List[Option[A]]): Option[List[A]] =
      a.foldRight[Option[List[A]]](Some(Nil))((x,y) => map2(x,y)(_ :: _))

    def traverse_naive[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] =
      sequence( a map (i => f(i) ) )

    def traverse[A,B](a: List[A])(f: A => Option[B]): Option[List[B]] =
      a.foldRight[Option[List[B]]](Some(List[B]()))((x,y) => f(x)::y)

  }

  "Exercise 4.6" should "do" in {

  }

  "Exercise 4.7" should "do" in {

  }


  // convert ordinary function to Option-adapted function
  def lift[A,B](f: A => B): Option[A] => Option[B] = _ map f

  // convert exception-based API to Option-oriented API
  def Try[A](a: => A): Option[A] =
    try Some(a)
    catch { case e: Exception => None }

}

sealed trait Option[+A] {
  def map[B](f: A => B): Option[B] = this match {
    case None => None
    case Some(v) => Some(f(v))
  }
  def getOrElse[B >: A](default: => B): B = this match {
    case None => default
    case Some(v) => v
  }
  def flatMap[B](f: A => Option[B]): Option[B] = map(f) getOrElse None
  def flatMap2[B](f: A => Option[B]): Option[B] = this match {
    case None => None
    case Some(v) => f(v)
  }
  def orElse[B >: A](ob: => Option[B]): Option[B] = this match {
    case None => ob
    case Some(v) => this
  }
  def orElse2[B >: A](ob: => Option[B]): Option[B] = this map (Some(_)) getOrElse ob
  def filter(f: A => Boolean): Option[A] = this match {
    case None => None
    case Some(v) if f(v) => this
  }
  def filter2(f: A => Boolean): Option[A] = flatMap( a => if((f(a))) Some(a) else None )

}
case class Some[+A](get: A) extends Option[A]
case object None extends Option[Nothing]

