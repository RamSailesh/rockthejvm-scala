package exercises.Set

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
