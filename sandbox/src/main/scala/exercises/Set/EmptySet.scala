package exercises.Set

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
