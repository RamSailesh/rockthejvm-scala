package exercises.Implicits

object TypeClasses2 extends App {

  trait HtmlSerializer[T] {
    def serialize(value: T): String
  }

  object HtmlSerializer {
    def serialize[T](value:T)(implicit serializer : HtmlSerializer[T]): String =
      serializer.serialize(value)

   // def apply[T](implicit serializer: HtmlSerializer[T])  =serializer
  }

  implicit object IntSerializer extends HtmlSerializer[Int] {
    override def serialize(value: Int) = s"<Int>$value</Int>"
  }

  implicit object StringSerializer extends HtmlSerializer[String] {
    override def serialize(value: String) = s"<String>$value</String>"
  }

  println(HtmlSerializer.serialize(34))
  println(HtmlSerializer.serialize("ram"))

  implicit class HtmlEnrichment[T](value: T) {
    def toHtml(implicit serializer: HtmlSerializer[T])  = serializer.serialize(value  )
  }

  println("john".toHtml)

  println(
  1.123123.toHtml(new HtmlSerializer[Double] {
    override def serialize(value: Double): String = "hmmm"
  }))

  //extend to new types
  //choose implementation
  //super expressive

  /*
   - type class itself   --> HtmlSerializer
   - type class instances --> StringSerializer/ IntSerializer
   - conversion with implicit classes HtmlEnrichment
  */


  def HtmlBoilerPlate[T](content:T)(implicit serializer: HtmlSerializer[T]): String = {
    s"<html>${content.toHtml(serializer)}</html>"
  }

  //context bound
  //compiler injects serializer at content
  def HtmlBoilerPlateSugar[T:HtmlSerializer](content:T): String = {
    s"<html>${content.toHtml}</html>"
    val serializer = implicitly[HtmlSerializer[T]]
    s"<html>${content.toHtml(serializer)}</html>"
  }

  //implicitly
  case class Permissions(mask: String)
  implicit val defaultPermissions: Permissions = Permissions("0744")

  val standardPerms = implicitly[Permissions]




}
