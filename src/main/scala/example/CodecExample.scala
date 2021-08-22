package example
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import scala.io.Source
import java.io._ 
import java.nio.file.Files
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.Try

sealed trait Foo
case class Bar(xs: Vector[String]) extends Foo
case class Qux(i: Int, d: Option[Double]) extends Foo

/**
 * Simple example of Encoding/Decoding Json using Circie
 */
def codecExample(): Unit = {

  val foo: Foo = Qux(13, Some(14.0))

  val json = foo.asJson.noSpaces
  println(json)

  val decodedFoo = decode[Foo](json)
  println(decodedFoo)
}

/**
 * Conditional Encoding Example based on field value
 */
case class FooBar(funLevel: Int)
implicit val fooBarDecoder: Decoder[FooBar] = Decoder[FooBar](str => {
  str.as[String].map(stringVal => if (stringVal.isEmpty) FooBar(0) else FooBar(stringVal.toInt))
}) 

/**
 * Conditional Encoding Example from JSON file example with custom date decoder
 */
def fileDecodingExample(): Either[Error, Seq[Ball]] = {
  val rawFileStr: String = readInResourceFileAsStr("example.json")
  val things = decode[Seq[Ball]](rawFileStr)
  things
}

val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
def dateTimeFormatDecoder(format: DateTimeFormatter): Decoder[LocalDateTime] = Decoder[String].emapTry(str => Try(LocalDateTime.parse(str, format)))

implicit val dateTimeDecoder: Decoder[LocalDateTime] = dateTimeFormatDecoder(dateTimeFormat)

sealed trait Ball
case class Baseball(ball_type: String, swagger: String, created: LocalDateTime) extends Ball
case class Basketball(ball_type: String, example: String, created: LocalDateTime) extends Ball

implicit val decodeBall: Decoder[Ball] = new Decoder[Ball] {
  final def apply(c: HCursor): Decoder.Result[Ball] = {
    val ballType = c.downField("ball_type").as[String]
    ballType match {
      case Right("basket") => c.as[Basketball]
      case _ => c.as[Baseball]
    }
  }
}
