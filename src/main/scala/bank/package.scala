import java.time.LocalDateTime
import java.math.BigDecimal
import javax.swing.text.StyleConstants.ColorConstants

package object bank {

  // Data models
  case class BankingEvent(time: LocalDateTime, account: String, action: String, amount: BigDecimal)
  case class Transaction(time: LocalDateTime, account: String, credit: BigDecimal, debit: BigDecimal, status: String, `type`: String, balance: BigDecimal) {
    def toStatementRow: String = {
      s"$time, $account, $credit, $debit, $status, ${`type`}, ${balance}"
    }
  }

  // Constants
  val Zero = new BigDecimal("0.00")


  // Action type
  val Deposit = "deposit"
  val Withdraw = "withdraw"
  val CreateAccount = "create account"
  val InstantTransfer = "instant transfer"

  // Statuses
  val Success = "Success"
  val Failure = "Failure"

  // Type
  val ATM = "ATM"

}
