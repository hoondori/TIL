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

    /*Hard: Can you write foldLeft in terms of foldRight? How about the other way
      around? Implementing foldRight via foldLeft is useful because it lets us implement
    foldRight tail-recursively, which means it works even for large lists without overflowing
      the stack.*/

    // The implementation of `foldRight` in terms of `reverse` and `foldLeft` is a common trick
    // for avoiding stack overflows when implementing a strict `foldRight` function

    import List._
    def reverse[A](l: List[A]): List[A] = foldLeft(l,List[A]())( (acc,h) => Cons(h, acc) )

    def foldRight[A,B](l: List[A], z:B)(f: (A,B) => B): B =
      foldLeft(reverse(l), z)((b,a) => f(a,b))

  }

  "Exercise 3.14" should "do" in {

    /*Implement append in terms of either foldLeft or foldRight.*/
    import List._
    def append[A](l: List[A], z:List[A]): List[A] = foldRight(l,z)( (h,t) => Cons(h,t) )

    append(List(1,2,3),List(4)) should be (List(1,2,3,4))
    append(List(1,2,3),List(4,5)) should be (List(1,2,3,4,5))


  }

  "Exercise 3.15" should "do" in {

/*
    Hard: Write a function that concatenates a list of lists into a single list.
    Its runtime should be linear in the total length of all lists. Try to use functions we have already defined
*/
    import List._
    def append[A](l: List[A], z:List[A]): List[A] = foldRight(l,z)( (h,t) => Cons(h,t) )
    def concat[A](l: List[List[A]]): List[A] = foldRight(l,Nil:List[A])( append(_,_) )

    concat(List( List(1,2), List(3,4), List(5,6))) should be (List(1,2,3,4,5,6))

  }

  "Exercise 3.16" should "do" in {

    //import List._
    def addOne(l: List[Int]): List[Int] = l match {
      case Cons(x,xs) => Cons(x+1, addOne(xs))
      case _ => l
    }

    import List._
    def addOne2(l: List[Int]): List[Int] = foldRight(l,Nil:List[Int])((h,t) => Cons(h+1,t))

    addOne(List(1,2)) should be (List(2,3))
    addOne2(List(1,2)) should be (List(2,3))
  }

  "Exercise 3.17" should "do" in {

    /*Write a function that turns each value in a List[Double] into a String. You can use
    the expression d.toString to convert some d: Double to a String.*/

    import List._
    def convertString(l: List[Double]): List[String] = foldRight(l,Nil:List[String])((h,t) => Cons(h.toString, t))

    convertString(List(3.14, 2.58)) should be (List("3.14", "2.58"))
  }

  "Exercise 3.18" should "do" in {

    /*Write a function map that generalizes modifying each element in a list while maintaining
    the structure of the list.
    def map[A,B](as: List[A])(f: A => B): List[B]*/

    import List._
    def map[A,B](as: List[A])(f: A => B): List[B] =
      foldRight(as, Nil:List[B])((h,t) => Cons(f(h),t))

    map( List(1,2,3) )(x=>x*2) should be (List(2,4,6))
  }

  "Exercise 3.19" should "do" in {

    /*Write a function filter that removes elements from a list unless they satisfy a given
    predicate. Use it to remove all odd numbers from a List[Int].
    def filter[A](as: List[A])(f: A => Boolean): List[A]*/

    import List._
    def filter[A](as: List[A])(f: A => Boolean): List[A] =
      foldRight(as, Nil:List[A])((h,t) => if (f(h)) Cons(h,t) else t)

    filter( List(1,2,3,4) )(x=> x%2==0) should be (List(2,4))
  }

  "Exercise 3.20" should "do" in {
    /*Write a function flatMap that works like map except that the function given
    will return a list instead of a single result, and that list should be inserted into the final resulting list.
    def flatMap[A,B](as: List[A])(f: A => List[B]): List[B]*/

    import List._
    def append[A](l: List[A], z:List[A]): List[A] = foldRight(l,z)( (h,t) => Cons(h,t) )
    def flatMap[A,B](as: List[A])(f: A => List[B]): List[B] =
      foldRight(as, List[B]())((h,t) => append(f(h),t))

    flatMap(List(1,2,3))(i => List(i,i)) should be (List(1,1,2,2,3,3))

    def concat[A](l: List[List[A]]): List[A] = foldRight(l,Nil:List[A])( append(_,_) )
    def map[A,B](as: List[A])(f: A => B): List[B] = foldRight(as, Nil:List[B])((h,t) => Cons(f(h),t))
    def flatMap2[A,B](as: List[A])(f: A => List[B]): List[B] =
      concat( map(as)(f) )

    flatMap2(List(1,2,3))(i => List(i,i)) should be (List(1,1,2,2,3,3))

  }

  "Exercise 3.21" should "do" in {

    /*Use flatMap to implement filter*/

    import List._
    def append[A](l: List[A], z:List[A]): List[A] = foldRight(l,z)( (h,t) => Cons(h,t) )
    def concat[A](l: List[List[A]]): List[A] = foldRight(l,Nil:List[A])( append(_,_) )
    def map[A,B](as: List[A])(f: A => B): List[B] = foldRight(as, Nil:List[B])((h,t) => Cons(f(h),t))
    def flatMap[A,B](as: List[A])(f: A => List[B]): List[B] =
      concat( map(as)(f) )

    def filter[A](as: List[A])(f: A => Boolean): List[A] =
      flatMap(as)(a => if(f(a)) List(a) else Nil )

    filter( List(1,2,3,4) )(x=> x%2==0) should be (List(2,4))

  }

  "Exercise 3.22" should "do" in {
    /*Write a function that accepts two lists and constructs a new list by adding correspond- ing elements.
    For example, List(1,2,3) and List(4,5,6) become List(5,7,9).*/

    def addPairwise(a: List[Int], b: List[Int]): List[Int] = (a,b) match {
      case (_,Nil) => Nil
      case (Nil,_) => Nil
      case (Cons(h1, t1), Cons(h2, t2)) => Cons( h1 + h2, addPairwise(t1,t2))
    }

    addPairwise(List(1,2,3),List(4,5,6)) should be (List(5,7,9))
  }

  "Exercise 3.23" should "do" in {
    /*Generalize the function you just wrote so that it’s not specific to integers or addition.
      Name your generalized function zipWith.*/

    def zipWith[A](a: List[A], b:List[A])(f: (A,A) => A): List[A] = (a,b) match {
      case (_,Nil) => Nil
      case (Nil,_) => Nil
      case (Cons(h1,t1), Cons(h2,t2)) => Cons( f(h1,h2), zipWith(t1,t2)(f) )
    }

    zipWith(List(1,2,3),List(4,5,6))( _ * _ ) should be (List(4,10,18))

  }

  "Exercise 3.24" should "do" in {
    // TODO
  }

  "Exercise 3.25" should "do" in {
    /*Write a function size that counts the number of nodes
      (leaves and branches) in a tree.*/

    def size[A](t: Tree[A]): Int = t match {
      case Leaf(_) => 1
      case Branch(l,r) => 1 + size(l) + size(r)
    }

    size(Branch(Leaf(1), Leaf(2))) should be (3)
    size(
      Branch(
        Leaf(1),
        Branch(Leaf(1), Leaf(2))
      )
    ) should be (5)

  }

  "Exercise 3.26" should "do" in {
    /*Write a function maximum that returns the maximum element in a Tree[Int]. (Note:
      In Scala, you can use x.max(y) or x max y to compute the maximum of two integers x and y.)*/

    def maximum(t: Tree[Int]): Int = t match {
      case Leaf(v:Int) => v
      case Branch(l,r) => maximum(l) max maximum(r)
    }

    maximum(
      Branch(
        Leaf(1),
        Branch(Leaf(2), Leaf(3))
      )
    ) should be (3)

  }

  "Exercise 3.27" should "do" in {
    /*Write a function depth that returns the maximum path length from the root of a tree to any leaf.*/

    def depth[A](t: Tree[A]):Int = t match {
      case Leaf(_) => 0
      case Branch(l,r) => 1 + (depth(l) max depth(r))
    }

    depth(
      Branch(
        Leaf(1),
        Branch(
          Leaf(2),
          Branch(
            Leaf(3),
            Leaf(4)
          )
        )
      )
    ) should be (3)
  }

  "Exercise 3.28" should "do" in {
    /*Write a function map, analogous to the method of the same name on List, that modifies
      each element in a tree with a given function.*/

    def map[A,B](t: Tree[A])(f: A => B): Tree[B] = t match {
      case Leaf(v) => Leaf(f(v))
      case Branch(l,r) => Branch(map(l)(f),map(r)(f))
    }

    map(
      Branch(
        Leaf(1),
        Branch(Leaf(2), Leaf(3))
      )
    )(v => v*2) should be (
      Branch(
        Leaf(2),
        Branch(Leaf(4), Leaf(6))
      )
    )

  }

  "Exercise 3.29" should "do" in {
    /*Generalize size, maximum, depth, and map, writing a new function fold that abstracts
      over their similarities. Reimplement them in terms of this more general function. Can
    you draw an analogy between this fold function and the left and right folds for List?*/

    def size[A](t: Tree[A]): Int = t match {
      case Leaf(_) => 1
      case Branch(l,r) => 1 + size(l) + size(r)
    }

    def maximum(t: Tree[Int]): Int = t match {
      case Leaf(v:Int) => v
      case Branch(l,r) => maximum(l) max maximum(r)
    }

    def depth[A](t: Tree[A]):Int = t match {
      case Leaf(_) => 0
      case Branch(l,r) => 1 + (depth(l) max depth(r))
    }

    def map[A,B](t: Tree[A])(f: A => B): Tree[B] = t match {
      case Leaf(v) => Leaf(f(v))
      case Branch(l,r) => Branch(map(l)(f),map(r)(f))
    }

    def foldRight[A,B](as: List[A], z:B)(f: (A,B) => B): B = as match {
      case Nil => z
      case Cons(x,xs) => f(x, foldRight(xs,z)(f))
    }

    def fold[A,B](t: Tree[A])(l: A => B)(b: (B,B)=>B):B = t match {
      case Leaf(v) => l(v)
      case Branch(left,right) => b(fold(left)(l)(b),fold(right)(l)(b))
    }
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

sealed trait Tree[+A]
case class Leaf[A](value: A) extends Tree[A]
case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

