package exercises.Set

object MySetRunner extends App {
  val s = MySet(1,2,3,4,5)

  val s1 = MySet(3,4,5,7)
  (s1 ++ s).foreach(println)

  s1.map(x => x*10).foreach(println)
  s1.flatMap(x => MySet(x, x*10)).foreach(println)
  s1.filter(_%2 == 0).foreach(println)
}