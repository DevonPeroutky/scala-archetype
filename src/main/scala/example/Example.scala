package example
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import scala.io.Source
import java.nio.file.Files
import java.time.LocalDateTime

sealed trait Foo
case class Bar(xs: Vector[String]) extends Foo
case class Qux(i: Int, d: Option[Double]) extends Foo
def codecExample(): Unit = {

  val foo: Foo = Qux(13, Some(14.0))

  val json = foo.asJson.noSpaces
  println(json)

  val decodedFoo = decode[Foo](json)
  println(decodedFoo)
}

sealed trait Ball
case class Baseball(ball_type: String, swagger: String, created: String) extends Ball
case class Basketball(ball_type: String, example: String, created: String) extends Ball

implicit val decodeBall: Decoder[Ball] = new Decoder[Ball] {
  final def apply(c: HCursor): Decoder.Result[Ball] = {
    val ballType = c.downField("ball_type").as[String]
    ballType match {
      case Right("basket") => c.as[Basketball]
      case _ => c.as[Baseball]
    }
  }
}


def fileDecodingExample(): Either[Error, Seq[Ball]] = {
  val rawFileStr: String = Source.fromResource("example.json").mkString
  val things = decode[Seq[Ball]](rawFileStr)
  println(things)
  things
}
