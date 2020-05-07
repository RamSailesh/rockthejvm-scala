package exercises.Set

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
