package exercises.Monad


/*
2 fundamental operations of a monad

unit <- apply function in scaa, pure in
-> unit constructs monad of a one or more values
flatMap <- bind
-> transforms a monad of one type parameter into another type parameter

Monad Laws
Left Identity -> unit(x).flatmap(f) == f(x)
Right Identity -> monadinstance.flatmap(unit) == monadinstance
Associativity  -> m.flatmap(f).flatmap(g) == m.flatmap(x => f(x).flatmap(g))
*/


object Monads extends App {
  //List is a monad
  val list = List(1,2,3)
  val f = (x:Int) => List(x, x+1)
  val g = (x:Int) => List(x, x*2)
  val leftIdentity = (List(1).flatMap(f) == f(1)) // only for single element as f takes only element in the list
  println(leftIdentity)
  val rightIdentity = (list.flatMap(x => List(x)) == list)
  println(rightIdentity)
  val associativity = list.flatMap(f).flatMap(g) == list.flatMap(x => f(x).flatMap(g))
  println(associativity)

  //Option is a monad
  //val option = Option(2)
  //val leftIdentityOption = (option.flatMap(f) == f(2))
  //println(leftIdentityOption)

}
