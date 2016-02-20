package exercise

import org.scalatest.{Matchers, FlatSpec}

class Ch1_Introduction extends FlatSpec with Matchers {

  "Exercise 1.1" should "do" in {

 /*   Implement a compose method with the following signature:
    def compose[A, B, C](g: B => C, f: A => B): A => C = ???*/

    def compose[A,B,C](g: B => C, f: A => B): A => C =
      x => g(f(x))

    val sq = (x:Int) => x*x
    val add1 = (x:Int) => x +1

    compose(sq,add1)(1) should be (4)

    (sq compose add1)(1) should be (4)
  }

  "Exercise 1.2" should "do" in {

    /*Implement a fuse method with the following signature:
    def fuse[A, B](a: Option[A], b: Option[B]): Option[(A, B)] = ???*/

    def fuse[A,B](a: Option[A], b:Option[B]): Option[(A,B)] = {
      for {
        x <- a
        y <- b
      } yield {
        (x,y)
      }
    }


    def fuse_alt[A,B](a: Option[A], b:Option[B]): Option[(A,B)] = {

      (a,b) match {
        case (Some(x),Some(y)) => Option((x,y))
        case (Some(_), None) => None
        case (None, Some(_)) => None
        case (None,None) => None
      }
    }

    fuse(Option(1),Option("2")) should be (Some((1,"2")))
    fuse(Option(1),None) should be (None)
    fuse(None,Option("2")) should be (None)
    fuse(None,None) should be (None)

    fuse_alt(Option(1),Option("2")) should be (Some((1,"2")))
    fuse_alt(Option(1),None) should be (None)
    fuse_alt(None,Option("2")) should be (None)
    fuse_alt(None,None) should be (None)

  }

  "Exercise 1.3" should "do" in {

    /*Implement a check method, which takes a set of values of the type T
    and a function of the type T => Boolean
    def check[T](xs: Seq[T])(pred: T => Boolean): Boolean = ???*/

    def check[T](xs: Seq[T])(pred: T => Boolean): Boolean = {
      xs.forall { x =>
        try {
          pred(x)
        } catch {
          case _: Exception => false
        }
      }
    }

    def check_bad_impl[T](xs: Seq[T])(pred: T => Boolean): Boolean = {
      try {
        xs.filter(pred).size != 0
      } catch {
        case e:Exception => false
      }
    }

    check(0 until 10)(40 / _ > 0) should be (false)
    check(1 until 10)(40 / _ > 0) should be (true)

  }

  "Exercise 1.4" should "do" in {

    /*Implement a permutations function, which, given a string, returns a sequence of strings that
      are lexicographic permutations of the input string:
    def permutations(x: String): Seq[String]*/

//    def permutations(x: String): Seq[String] = {
//
//      val chars = x.toCharArray.sorted
//      chars foreach println _
//
//      for { char <- chars } {
//
//      }
//
//
//
//    }


    //permutations("bac") should be (Seq("abc", "acb", "bac", "bca", "cab", "cba"))


  }
}
