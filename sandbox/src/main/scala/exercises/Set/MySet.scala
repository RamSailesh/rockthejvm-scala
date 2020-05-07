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

  def unary_! : MySet[A] = new PropertyBasedSet[A](_=>true)
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

  def unary_! : MySet[A] =  new PropertyBasedSet[A](!contains(_))
}


// {x in A | property(x)} infinite collections
class PropertyBasedSet[A] (property: A=>Boolean) extends MySet[A] {
  def contains(elem: A ): Boolean = property(elem)
  // {x in A | property(x) || x == elem}
  def +(elem: A): MySet[A] =
    new PropertyBasedSet[A]((x) => (x == elem || property(x)))
  // {x in A | property(x) || x in anotherSet}
  def ++(anotherSet: MySet[A]): MySet[A] =
    new PropertyBasedSet[A]((x) => (anotherSet(x)|| property(x)))
  // cannot determine if the set is finite or infinte
  // eg: for set of integers
  // map(x => x%3) will return {0,1,2}
  // map(x => x*2) will return {0,2,4....}
  def map[B](f: A => B): MySet[B] = fail
  def flatMap[B](f: A=>MySet[B]): MySet[B] = fail
  def foreach(f: A=>Unit): Unit = fail

  def filter(predicate: A=>Boolean): MySet[A] =
    new PropertyBasedSet[A]((x) => (predicate(x)|| property(x)))
  // {x in A | property(x) && x != elem}
  def -(elem: A): MySet[A] =
    filter(x => x!=elem)
  // {x in A | property(x) && x != elem}
  def &(anotherSet: MySet[A]): MySet[A] =
    filter(anotherSet)
  def --(anotherSet: MySet[A]): MySet[A] =
    filter(!anotherSet)
  // {x in A | !property(x)}
  def unary_! : MySet[A] =
    new PropertyBasedSet[A](!property(_))

  private def fail = throw new IllegalArgumentException("Illegal Operation")
}






