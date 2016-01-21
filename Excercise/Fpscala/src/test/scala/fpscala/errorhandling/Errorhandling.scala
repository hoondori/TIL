package fpscala.errorhandling

import org.scalatest.{Matchers, FlatSpec}


class Errorhandling extends FlatSpec with Matchers {

  "Exercise 3.1" should "do" in {

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


