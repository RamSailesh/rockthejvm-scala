package part2Streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import part2Streams.MaterializingStreams.simpleFlow

import scala.util.{Failure, Success}

object MaterializingStreams extends App {
  implicit val system = ActorSystem("materializing-streams")
  implicit val materializer = ActorMaterializer()


  val simpleGraph = Source(1 to 10).to(Sink.foreach(println))
  val simpleGraphMaterializedValue = simpleGraph.run()

  println(s"mat value : $simpleGraphMaterializedValue")

  val source = Source(1 to 10)
  val sink = Sink.reduce[Int]((a,b) => a+b)

  val intMaterializedValue = source.to(sink).run()

  //intMaterializedValue is NotUsed due to source.to as leftmost values

  println(s"first result $intMaterializedValue")

  val intMaterializedValue2 = source.runWith(sink)

  import system.dispatcher

  intMaterializedValue2.onComplete {
    case Success(value) => println(s"success result is $value")
    case Failure(exception) => println(s"failure $exception")
  }

  //choosing materialized values

  val simpleFlow = Flow[Int].map(x => x+1)
  val simpleSink = Sink.foreach[Int](println)

  //val returnSinkMat = source.viaMat(simpleFlow)((source, simpleFlow) => simpleFlow)
  val returnSinkMat = source.viaMat(simpleFlow)(Keep.right).toMat(simpleSink)(Keep.right).run()
  returnSinkMat.onComplete {
    case Success(value) => println("stream processing finished")
    case Failure(ex) => println("stream processing failed")
  }

  //syntactic sugars
  //val sum = Source(1 to 10).runWith(Sink.reduce(_ + _))
  //val totalSum = Source(1 to 10).runReduce(_ + _)

  //both ways
//  Flow[Int].map(2*_).runWith(source, sink)._2.onComplete(partialSuccessFailureFunction)
//
//  def partialSuccessFailureFunction [Int, Unit]: Unit =  {
//    case Success(value) => println(s"stream processing finished $value")
//    case Failure(ex) => println(s"stream processing failed $ex")
//  }

  println("Exercises++++++++++++++")

  //return last element of the source (Sink.last) using materialized value

  val matValue = Source(1 to 10).runWith(Sink.reduce[Int]((x,y) => y))
  matValue.onComplete {
    case Success(value) => println(s"value is $value")
  }

  val lastValue = Source(1 to 10).toMat(Sink.last)(Keep.right).run()
  val lastValue1 = Source(1 to 10).runWith(Sink.last)


  //compute total word count out of a stream of sentences
  // -- map, fold, reduce

  val sentences = List[String] (
    "asdasda asdasd asdasd",
    "qucik brown fox jumps over lazy dog",
    "maria akka streams actor amterialzier ",
    "blah blah blahasdjas djasdas"
  )

  //Source(sentences).runForeach(println)
  //Source(sentences).map(x => (x, x.split(" ").length)).runForeach(x => println(s"count is $x"))
  val sentenceSource = Source(sentences)
  val wordCountSink = Sink.fold[Int, String](0)(
    (currentWords, newSentence) => currentWords + newSentence.split(" ").length)

  val g1 = sentenceSource.toMat(wordCountSink)(Keep.right).run()
  val g2 = sentenceSource.runWith(wordCountSink)
  //val g3 = sentenceSource.runFold(wordCountSink)
}
