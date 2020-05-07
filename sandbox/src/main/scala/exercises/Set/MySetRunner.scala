package exercises.Set

object MySetRunner extends App {
  val s = MySet(1,2,3,4,5)

  val s1 = MySet(3,4,5,7)
  (s1 ++ s).foreach(println)


  s1.map(x => x*10).foreach(println)
  s1.flatMap(x => MySet(x, x*10)).foreach(println)
  s1.filter(_%2 == 0).foreach(println)

  val nots1 = !s1
  println(nots1.contains(3))
  println(nots1(3)) // same as nots1.contains due to apply method
  println(nots1.contains(10))
}