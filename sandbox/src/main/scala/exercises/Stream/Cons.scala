package exercises.Stream

import scala.annotation.tailrec

class Cons[+A] (hd: A, tl: => MyStream[A]) extends MyStream[A] {
  def isEmpty: Boolean = false
  override val head: A = hd
  override lazy val tail: MyStream[A] = tl

  //
  def #::[B >: A](element:B) : MyStream[B] = new Cons(element, this)
  def ++[B >: A](anotherStream : => MyStream[B]) : MyStream[B] =
    new Cons[B](head, tail ++ anotherStream)

  def foreach(f: A=>Unit) : Unit = {
    f(head)
    tail.foreach(f)
  }
  //call by name
  //function is evaulated by need

  def map[B](f: A=>B): MyStream[B] = new Cons[B](f(head), tail.map(f))
  def flatMap[B](f: A=>MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f)
  //expression is byname
  def filter(f: A=>Boolean): MyStream[A] =
    if (f(head)) new Cons[A](head, tail.filter(f))
    else tail.filter(f) // preserve lazy evaluation
  def take(n: Int): MyStream[A] =
    if (n <= 0) EmptyStream
    else if (n == 1) new Cons[A](head, EmptyStream)
    else new Cons[A](head, tail.take(n-1))

  def takeAsList(n:Int): List[A] =
    take(n).toList()

  override def toList[B >: A](acc: List[B] = Nil): List[B] =
    if (isEmpty) acc
    else tail.toList(acc :+ head)
}

// remember call by name and call by need
// all the expressions are lazily evaluated here