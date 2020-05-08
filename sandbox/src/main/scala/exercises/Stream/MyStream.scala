package exercises.Stream

//lazily evaluated singly linked stream of elements
abstract class MyStream[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: MyStream[A]
  def #::[B >: A](element:B) : MyStream[B] //prepend
  def ++[B >: A](anotherStream : => MyStream[B]) : MyStream[B] //add two streams

  def foreach(f: A=>Unit) : Unit
  def map[B](f: A=>B): MyStream[B]
  def flatMap[B](f: A=>MyStream[B]): MyStream[B]
  def filter(f: A=>Boolean): MyStream[A]

  def take(n: Int): MyStream[A] // takes first n elements
  def takeAsList(n:Int): List[A]

  def toList[B >: A](acc: List[B] = Nil): List[B] = acc
}

object MyStream {
  def from[A](start: A)(generator: A=>A): MyStream[A] = {
    new Cons[A](start, MyStream.from(generator(start))(generator))
  }
}
