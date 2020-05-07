package exercises.Set

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {
  def contains(elem: A ): Boolean
  def +(elem: A): MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A=>MySet[B]): MySet[B]
  def filter(predicate: A=>Boolean): MySet[A]
  def foreach(f: A=>Unit): Unit

  def apply(elem: A): Boolean = contains(elem)

  def -(elem: A): MySet[A]
  def &(anotherSet: MySet[A]): MySet[A]
  def --(anotherSet: MySet[A]): MySet[A]
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

class EmptySet[A] extends MySet[A] {
  def contains(elem: A ): Boolean = false
  def +(elem: A): MySet[A] = new NonEmptySet(elem, new EmptySet)
  def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  def map[B](f: A => B): MySet[B] = new EmptySet[B]
  def flatMap[B](f: A=>MySet[B]): MySet[B] = new EmptySet[B]
  def filter(predicate: A=>Boolean): MySet[A] = this
  def foreach(f: A=>Unit): Unit = ()

  def -(elem: A): MySet[A] = this
  def &(anotherSet: MySet[A]): MySet[A] = this
  def --(anotherSet: MySet[A]): MySet[A] = this
}


class NonEmptySet[A] (head: A, tail: MySet[A]) extends MySet[A] {
  def contains(elem: A ): Boolean =
    (head == elem) || tail.contains(elem)

  def +(elem: A): MySet[A] =
    if(contains(elem)) this
    else new NonEmptySet[A](elem, this)

  def ++(anotherSet: MySet[A]): MySet[A] =
    tail ++ anotherSet + head

  def map[B](f: A => B): MySet[B] =
    tail.map(f) + f(head)

  def flatMap[B](f: A=>MySet[B]): MySet[B] =
    tail.flatMap(f) ++ f(head)

  def filter(predicate: A=>Boolean): MySet[A] = {
    val filteredTail = tail.filter(predicate)
    if (predicate(head)) filteredTail + head
    else filteredTail
  }

  def foreach(f: A=>Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  def -(elem: A): MySet[A] =
    if (head == elem) tail
    else tail - elem + head

  def &(anotherSet: MySet[A]): MySet[A] =
    filter(anotherSet)

  def --(anotherSet: MySet[A]): MySet[A] =
    filter(!anotherSet(_))
}






