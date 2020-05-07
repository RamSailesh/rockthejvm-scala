package exercises.Set

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {
  def contains(elem: A): Boolean

  def +(elem: A): MySet[A]

  def ++(anotherSet: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]

  def flatMap[B](f: A => MySet[B]): MySet[B]

  def filter(predicate: A => Boolean): MySet[A]

  def foreach(f: A => Unit): Unit

  def apply(elem: A): Boolean = contains(elem)

  def -(elem: A): MySet[A]

  def &(anotherSet: MySet[A]): MySet[A]

  def --(anotherSet: MySet[A]): MySet[A]

  def unary_! : MySet[A]
}

object MySet {
  def apply[A](values: A*) :MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]) : MySet[A] = {
      if (valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)
    }
    buildSet(values.toSeq, new EmptySet[A]())
  }
}








