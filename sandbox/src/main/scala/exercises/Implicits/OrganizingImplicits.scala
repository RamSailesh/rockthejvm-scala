package exercises.Implicits

object OrganizingImplicits extends App {

  println(List(1,3,1,0,9).sorted)
  implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  println(List(1,3,1,0,9).sorted)
  // val/var, objects, accesor methods defs with not parenthesis

  case class Person(name: String, age: Int)

  implicit val ordering: Ordering[Person] = Ordering.fromLessThan(
    (x,y) => {
      if (x.age == y.age) x.name.compareTo(y.name) < 0
      else x.age < y.age
    }
  )

  val persons = List(
    Person("Siva", 31),
    Person("Ram", 31),
    Person("Manju", 28),
    Person("Andy", 25)
  )

  println(persons.sorted) // sort function is passed implicitly by declaring above

  /*
  Implicit Scope Priority
  - Normal Scope - Local Scope
  - Imported Scope -
  - Companion objects of all types involved in method signature
    - List
    - Ordering
    - all the types involved = A or any Supertype
  */

  case class Purchase(nunits: Int, unitPrice: Double)
  object Purchase {
    implicit val totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan(
      (x, y) => x.nunits * x.unitPrice < y.nunits * y.unitPrice
    )
  }

  object UnitCountOrdering {
    implicit val unitCountOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.nunits < _.nunits)
  }

  object UnitPriceOrdering {
    implicit val unitPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.unitPrice < _.unitPrice)
  }

}
