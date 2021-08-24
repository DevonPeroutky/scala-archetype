package bank

import java.math.BigDecimal
import scala.collection.mutable


case class PendingTransaction(ogEvent: BankingEvent, balanceSnapshot: BigDecimal)
case class AccountBalance(account: String, private val balance: BigDecimal, currency: Int = 0, pendingTransactions: mutable.Map[String, PendingTransaction]) {
  def getBalance(): BigDecimal = {
    val pendingBalance: BigDecimal = pendingTransactions.values.map(txn => txn.ogEvent.amount).reduce((a, b) => a.add(b))
    balance.add(pendingBalance)
  }

  def addPendingTransaction(entity: String, event: BankingEvent) = {
    val balanceSnapshot: BigDecimal = getBalance()
    pendingTransactions += (entity -> PendingTransaction(event, balanceSnapshot))
  }
  def removePendingTransaction(entity: String) = pendingTransactions.remove(entity)

}

trait BankingError {
  val account: String
  val balance: BigDecimal
}
case class InsufficientFunds(account: String, balance: BigDecimal, txnAmount: BigDecimal) extends BankingError

class BankingCore {
  val accountBalances = mutable.Map.empty[String, AccountBalance]
  private def getBalance(account: String) = accountBalances(account)
  private def updateBalance(account: String, amount: BigDecimal) = {
    val existingBalance: AccountBalance = getBalance(account)
    val newBalanceAmount = existingBalance.getBalance().add(amount)
    val newAccountBalance = existingBalance.copy(balance = newBalanceAmount)
    accountBalances(account) = newAccountBalance
    newAccountBalance
  }


  def createAccount(account: String): AccountBalance = {
    val newAccount = AccountBalance(account, Zero, 0, mutable.Map.empty[String, PendingTransaction])
    accountBalances += (account -> newAccount)
    newAccount
  }

  def debit(account: String, authorizationAmount: BigDecimal, txnAmount: BigDecimal): Either[BankingError, AccountBalance] = {
    val accountBalance = getBalance(account)

    if (accountBalance.getBalance().compareTo(authorizationAmount) < 0) {
      Left(InsufficientFunds(account, accountBalance.getBalance(), authorizationAmount))
    } else {
      Right(updateBalance(account, txnAmount.negate))
    }
  }

  def credit(account: String, amount: BigDecimal): AccountBalance = updateBalance(account, amount)


  def closePendingTransaction(account: String, entity: String, success: Boolean) = {
    val accountBalance = getBalance(account)
    accountBalance.removePendingTransaction(entity)
  }

  def cancelPendingTransaction(account: String, entity: String) = {
    val accountBalance = getBalance(account)
    accountBalance.removePendingTransaction(entity)
  }
}
