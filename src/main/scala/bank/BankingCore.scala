package bank

import java.math.BigDecimal
import scala.collection.mutable


case class AccountBalance(account: String, private val balance: BigDecimal, currency: Int = 0) {
  def getBalance(): BigDecimal = balance
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
    val newAccount = AccountBalance(account, Zero, 0)
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
}
