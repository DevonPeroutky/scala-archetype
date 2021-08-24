package bank

import java.math.BigDecimal
import java.time.LocalDateTime

class ValonBank(bankingCore: BankingCore = BankingCore()) {
  private val FeeAmount = new BigDecimal("0.01")
  private val Fee = "Fee"  
  private def chargeFee(account: String, time: LocalDateTime) = fundTransaction(account, time, FeeAmount, Fee, fee = Zero).merge

  private def fundTransaction(account: String, time: LocalDateTime, amount: BigDecimal, transactionType: String, fee: BigDecimal = Zero): Either[Seq[Transaction], Seq[Transaction]] = {
    val totalAuthorizationAmount = amount.add(fee)
    bankingCore.debit(account, totalAuthorizationAmount, amount) fold (
      err => Left(Seq(Transaction(time, account, Zero, amount, Failure, transactionType, err.balance))),
      newBalance => Right(Seq(Transaction(time, account, Zero, amount, Success, transactionType, newBalance.getBalance())))
    )
  }

  private def depositToAccount(account: String, amount: BigDecimal, time: LocalDateTime, transactionType: String) = {
    val newBalance: AccountBalance = bankingCore.credit(account, amount)
    Seq(Transaction(time, account, amount, Zero, Success, transactionType, newBalance.getBalance()))
  }

  def createAccount(account: String): Seq[Transaction] = {
    bankingCore.createAccount(account)
    Seq.empty[Transaction]
  }

  def deposit(event: BankingEvent): Seq[Transaction] = depositToAccount(event.account, event.amount, event.time, ATM)

  def withdraw(event: BankingEvent): Seq[Transaction] = fundTransaction(event.account, event.time, event.amount, ATM).merge

  def instantTransfer(event: BankingEvent): Seq[Transaction] = {
    println("TRANSFERRRING")
    println(event)
    fundTransaction(event.account, event.time, event.amount, InstantTransfer).fold(identity, txns => 
        txns ++ depositToAccount(event.counterpartyAccount.get, event.amount, event.time, InstantTransfer)
    )
  }

  def externalOutgoingTranser(event: BankingEvent): Seq[Transaction] = {
    fundTransaction(event.account, event.time, event.amount, InstantTransfer, FeeAmount).fold(identity, txns => txns ++ chargeFee(event.account, event.time))
  }

  def externalIncomingTransfer(event: BankingEvent): Seq[Transaction] = {
    depositToAccount(event.account, event.amount, event.time, InstantTransfer) ++ chargeFee(event.account, event.time)
  }

  def process(event: BankingEvent): Seq[Transaction] = {
    val externalCounterParty: Boolean = event.counterpartyAccount.getOrElse("").startsWith("EXTER")
    val externalSrcAccount: Boolean = event.account.startsWith("EXTER")

    event.action match {
      case CreateAccount => createAccount(event.account)
      case Withdraw => withdraw(event)
      case Deposit => deposit(event)
      case InstantTransferEvent if externalSrcAccount => externalIncomingTransfer(event)
      case InstantTransferEvent if externalCounterParty => externalOutgoingTranser(event)
      case InstantTransferEvent => instantTransfer(event)
      case _ => {
        println(s"Have not yet implemented ${event.action}")
        Seq.empty[Transaction]
      }
    }
  }

}
