import java.time.LocalDateTime
import java.math.BigDecimal
import javax.swing.text.StyleConstants.ColorConstants
import io.circe._, io.circe.syntax._, io.circe.parser._, io.circe.generic.auto._, io.circe.generic.semiauto._

package object bank {

  // Data models
  case class BankingEvent(time: LocalDateTime, account: String, action: String, amount: BigDecimal, counterpartyAccount: Option[String] = None) // TODO, why can't parse Option[String]
  case class Transaction(time: LocalDateTime, account: String, credit: BigDecimal, debit: BigDecimal, status: String, `type`: String, balance: BigDecimal) {
    def toStatementRow: String = {
      s"$time, $account, $credit, $debit, $status, ${`type`}, ${balance}"
    }
  }

  // Constants
  val Zero = new BigDecimal("0.00")
  val FeeAmount = new BigDecimal("0.01")


  // Action type
  val Deposit = "deposit"
  val Withdraw = "withdraw"
  val CreateAccount = "create account"
  val InstantTransferEvent = "instant transfer"

  // Statuses
  val Success = "Success"
  val Failure = "Failure"
  val InstantTransfer = "INSTANT TRANSFER"

  // Type
  val ATM = "ATM"
  val Fee = "Fee"  

}
