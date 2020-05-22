package exercises.Implicits
import java.util.Date

object ImplicitJsonSerialization extends App {

  case class User(name: String, age: Int, email:String)
  case class Post(content: String, createdAt: Date)
  case class Feed(user: User, post: List[Post])

  sealed trait JsonValue {
    def stringify: String
  }

  final case class JsonString(value: String) extends  JsonValue {
    def stringify: String =
      "\"" + value + "\""
  }

  final case class JsonNumber(value: Int) extends JsonValue {
    def stringify: String = value.toString
  }

  final case class JsonArray(values: List[JsonValue]) extends JsonValue {
    def stringify: String = values.map(_.stringify).mkString("[", "," ,"]")
  }

  final case class JsonObject(values: Map[String, JsonValue]) extends JsonValue {
    def stringify: String = values.map {
      case(key, value) => "\""+ key+ "\":" + value.stringify
    }.mkString("{", ",", "}")
  }

  trait JsonConverter[T] {
    def convert(value: T):JsonValue
  }

  implicit object StringConvertor extends JsonConverter[String] {
    def convert(value: String): JsonValue = JsonString(value)
  }

  implicit object IntConverter extends JsonConverter[Int] {
    def convert(value: Int): JsonValue = JsonNumber(value)
  }

  implicit object UserConvertor extends JsonConverter[User] {
    def convert(value: User): JsonObject = JsonObject(Map(
      "name" -> JsonString(value.name),
      "age" -> JsonNumber(value.age),
      "email" -> JsonString(value.email)
    ))
  }

  implicit object PostConvertor extends JsonConverter[Post] {
    def convert(value: Post): JsonObject = JsonObject(Map(
      "content" -> JsonString(value.content),
      "createdAt" -> JsonString(value.createdAt.toString)
    ))
  }

  implicit object FeedConvertor extends JsonConverter[Feed] {
    def convert(value: Feed): JsonObject = JsonObject(Map(
      "user" -> value.user.toJson,
      "post" -> JsonArray(value.post.map(_.toJson))
    ))
  }

  implicit class JsonOps[T](value: T) {
    def toJson(implicit converter: JsonConverter[T]): JsonValue = {
      converter.convert(value)
    }
  }

  val r = User("ram", 31, "r@gmail.com")
  val post = Post("Hi", new Date())
  val feed = Feed(r, List[Post](post))
  println(feed.toJson.stringify)

}