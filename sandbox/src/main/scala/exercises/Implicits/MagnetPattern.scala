package exercises.Implicits

object MagnetPattern extends App {
  trait MessageMagnet[Result] {
    def apply(): Result
  }

  def receive[R](magnet: MessageMagnet[R]):R = magnet()

  case class RequestMessage(value: String)
  case class ResponseMessage(value: String)

  implicit class RequestMessageConverter(msg: RequestMessage) extends MessageMagnet[Int] {
    override def apply(): Int = {
      println(msg.value)
      42
    }
  }

  implicit class ResponseMessageConverter(msg: ResponseMessage) extends MessageMagnet[String] {
    override def apply(): String = {
      println(msg.value)
      "21"
    }
  }

  //no more type-erasure
  println(receive(RequestMessage("ram")))
  println(receive(ResponseMessage("sailesh")))

  //lifting works _


}
