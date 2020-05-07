package exercises.Set

trait MySet[A] extends (A => Boolean) {
  def contains(elem: A ): MySet[A]
  def +(elem: A): MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A=>B): MySet[B]
  def filter(predicate: A=>Boolean): MySet[A]

  def foreach(f: A=>Unit): Unit
}
