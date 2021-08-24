package bank

import io.circe._, io.circe.syntax._, io.circe.parser._, io.circe.generic.auto._
import java.time.LocalDateTime
import scala.util.Try
import java.time.format.DateTimeFormatter
import java.math.BigDecimal
import scala.io.Source
import java.io._ 


object Statement {


  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss")
  implicit val dateTimeDecoder: Decoder[LocalDateTime] = Decoder[String].emapTry(str => Try(LocalDateTime.parse(str, dateTimeFormatter)))

  implicit val bigDecimalDecoder: Decoder[BigDecimal] = Decoder[String].emapTry(str => 
      if (str.isEmpty) Try(Zero) else Try(new BigDecimal(str.stripPrefix("$").replaceAll(",", "")))
  )

  def ingestBankEvents(filename: String): Either[Error, Seq[BankingEvent]] = {
    val rawJsonStr = Source.fromResource(filename).mkString
    decode[Seq[BankingEvent]](rawJsonStr)
  }

  def exportTransactions(filename: String, txns: Seq[Transaction]) = {
    println(filename)
    txns foreach println
    writeFile(filename, txns.map(_.toStatementRow))
  }

  /**
   * Write a `Seq[String]` to the `filename`.
   */
  def writeFile(filename: String, lines: Seq[String]): Unit = {
      val file = new File(filename)
      val bw = new BufferedWriter(new FileWriter(file))
      for (line <- lines) {
          bw.write(s"$line\n")
      }
      bw.close()
  }
}
