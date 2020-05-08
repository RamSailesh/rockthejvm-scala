package exercises

object LazyEvaluation extends App {
  lazy val x = throw new RuntimeException()

  //lazy val with filtering
  def lessThan30(x:Int) = x < 30
  def greaterThan20(x: Int) = x > 20
  val nums = List(10, 25, 26, 30, 40)
  println(nums.filter(lessThan30).filter(greaterThan20))

  //

}
