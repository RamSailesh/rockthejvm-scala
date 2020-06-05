import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Source, Sink}

object HelloStreams extends App {
  //hello streams
  implicit val actorSystem = ActorSystem("someSystem")
  implicit val materilizer = ActorMaterializer()
  Source.single("hello Streams!").to(Sink.foreach(println)).run
}
