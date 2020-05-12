package exercises.Implicits

object TypeClassesIntro extends App {
  trait Equal[T] {
    def equal(v1: T, v2: T): Boolean
  }

  object Equal{
    def apply[T] (v1: T, v2:T)(implicit equalizer: Equal[T]): Boolean = equalizer.equal(v1, v2)
  }

  case class User(name: String, age: Int)

  //AD-HOC Polymorphism
  implicit object NameEquality extends Equal[User] {
    override def equal(v1: User, v2: User): Boolean = v1.name == v2.name
  }
  object FullEquality extends Equal[User] {
    override def equal(v1: User, v2: User): Boolean = v1.name == v2.name && v1.age == v2.age
  }


  println(Equal(User("ram", 31), User("ram",30)))

  implicit class EqualityEnrichment[T](value: T) {
    //all the objects can use === if we added the corresponding implicit object which extends Equal
    def ===(anotherValue: T) (implicit equalityComparer: Equal[T]) = equalityComparer.equal(value, anotherValue)
    def !==(anotherValue: T) (implicit equalityComparer: Equal[T]) = !equalityComparer.equal(value, anotherValue)
  }


  println(User("ram", 31) === (User("sailesh", 31)))

  println(User("ram", 31) !== (User("sailesh", 31)))

  List(1,2,3).foldLeft(0)((x,y) => {
    x
  })



}
