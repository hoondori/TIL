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
      a.foldRight[Option[List[B]]](Some(Nil))((x,y) => map2(f(x),y)(_::_))

    def sequenceViaTraverse[A](a: List[Option[A]]): Option[List[A]] =
      traverse(a)( x => x )
  }

  "Exercise 4.6" should "do" in {

    /*Implement versions of map, flatMap, orElse, and map2 on Either that operate on the Right value*/


  }

  "Exercise 4.7" should "do" in {

    /*Implement sequence and traverse for Either. These should return the first error that’s encountered, if there is one.*/

  }

  "Exercise 4.8" should "do" in {

    /*In this implementation, map2 is only able to report one error, even if both the name and the age are invalid.
    What would you need to change in order to report both errors?
    Would you change map2 or the signature of mkPerson?
      Or could you create a new data type that captures this requirement better than Either does,
    with some additional structure? How would orElse, traverse, and sequence behave differently for that data type?*/


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

sealed trait Either[+E,+A] {
  def map[B](f: A => B): Either[E,B] = this match {
    case Left(e) => Left(e)
    case Right(a) => Right(f(a))
  }
  def flatMap[EE >:E, B](f: A => Either[EE,B]): Either[EE,B] = this match {
    case Left(e) => Left(e)
    case Right(a) => f(a)
  }
  def orElse[EE >:E, B >: A](b: => Either[EE,B]): Either[EE,B] = this match {
    case Left(_) => b
    case Right(a) => Right(a)
  }

  def map2[EE >:E,B,C](b: Either[EE,B])(f: (A,B) => C): Either[EE,C] =
    this flatMap { aa =>
      b map { bb =>
        f(aa,bb)
      }
    }

  def map2_1[EE >:E,B,C](b: Either[EE,B])(f: (A,B) => C): Either[EE,C] =
    for {
      aa <- this
      bb <- b
    } yield f(aa, bb)

  def sequence[E, A](es: List[Either[E, A]]): Either[E, List[A]] =
    es.foldRight[Either[E,List[A]]](Right(Nil))((x,y) => x.map2(y)(_::_))

  def traverse[E, A, B](as: List[A])(f: A => Either[E, B]): Either[E, List[B]] =
    as.foldRight[Either[E,List[B]]](Right(Nil))((x,y) => f(x).map2(y)(_::_))

  def sequenceViaTraverse[E,A](es: List[Either[E,A]]): Either[E, List[A]] =
    traverse(es)( x => x )

}

case class Left[+E](value: E) extends Either[E, Nothing]
case class Right[+A](value: A) extends Either[Nothing,A]
