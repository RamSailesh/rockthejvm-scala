package exercises.Stream

object EmptyStream extends MyStream [Nothing] {
  def isEmpty: Boolean = true
  def head: Nothing = throw new NoSuchElementException
  def tail: MyStream[Nothing] = throw new NoSuchElementException

  def #::[B >: Nothing](element:B) : MyStream[B] = new Cons[B](element, EmptyStream)
  def ++[B >: Nothing](anotherStream: => MyStream[B]) : MyStream[B] = anotherStream

  def foreach(f: Nothing=>Unit) : Unit = ()
  def map[B](f: Nothing=>B): MyStream[B] = this
  def flatMap[B](f: Nothing=>MyStream[B]): MyStream[B] = this
  def filter(f: Nothing=>Boolean): MyStream[Nothing] = this

  def take(n: Int): MyStream[Nothing] = this
  def takeAsList(n:Int): List[Nothing] = Nil

  ///def toList [B >: Nothing](accumulator: List[Nothing]): List[Nothing] = Nil
}
