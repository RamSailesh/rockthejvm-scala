package part2Streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FirstPrinciples extends App {
  implicit val system = ActorSystem("first-principles")
  implicit val materializer = ActorMaterializer()

  // sources
  val source = Source(1 to 10)

  //sink
  val sink = Sink.foreach[Int](println)

  //connect source and sink
  //the link is called as graph
  val graph = source.to(sink)
  //graph.run()

  //flows transform elements

  val flow = Flow[Int].map(x => x+1)

  val sourceWithFlow = source.via(flow)
  val flowWithSink = flow.to(sink)

//  sourceWithFlow.to(sink).run()
//  source.to(flowWithSink).run()

  //types of sources
  val singleElementSource = Source.single(12)
  val anotherFiniteSource = Source(List(1,2,3))
  val emptySource = Source.empty[Int]
  val infiniteSource = Source(Stream.from(1))

  val futureSource = Source.fromFuture(Future(42))

  //types of sinks
  val mostBoringSink = Sink.ignore
  val foreachSink = Sink.foreach[Int](println)

  val headSink = Sink.head[Int] //retreives head and closes stream
  val foldSink = Sink.fold[Int, Int] (0)((a,b) => a+b)


  //types of flows
  val mapFlow = Flow[Int].map(x => x*2)
  val takeFlow = Flow[Int].take(5) // converts stream into finite stream
  // drop, filter
  // not have flatMap

  // source -> flow -> flow ..... -> sink

  val doubleFlowGraph = source.via(mapFlow).via(takeFlow).to(sink)
  doubleFlowGraph.run()

  // syntactic sugars
  val mapSource = Source(1 to 10).map(x => x*10) // Source(1 to 10).via(Flow[Int].map(x => x*10))
  mapSource.runForeach(println) //mapSource.to(Sink.foreach[Int](println)).run()


  //Exercise, stream takes first 2 name with words of length > 4
  val list = List("ram", "sailesh", "june", "may", "march", "april", "september",  "melon")

  Source(list)
    .via(Flow[String].filter(p => p.length > 4)) // filter
    .via(Flow[String].take(2)) //take first 2
    .to(Sink.foreach(println)) //pass to sink :)
      .run()

  //Source(list).filter(p => p.length > 5).take(2).runForeach(println)
}
