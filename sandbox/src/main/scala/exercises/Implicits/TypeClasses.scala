package exercises.Implicits

object TypeClassesIntro extends App {
  trait Equal[T] {
    def equal(v1: T, v2: T): Boolean
  }

  case class User(name: String, age: Int)
  
  object NameEquality extends Equal[User] {
    override def equal(v1: User, v2: User): Boolean = v1.name == v2.name
  }
  object FullEquality extends Equal[User] {
    override def equal(v1: User, v2: User): Boolean = v1.name == v2.name && v1.age == v2.age
  }


}
