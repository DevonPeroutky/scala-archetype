package bank

class ValonBank(bankingCore: BankingCore = BankingCore()) {

  def createAccount(account: String): Seq[Transaction] = {
    bankingCore.createAccount(account)
    Seq.empty[Transaction]
  }

  def deposit(event: BankingEvent): Seq[Transaction] = {
    val newBalance: AccountBalance = bankingCore.credit(event.account, event.amount)
    Seq(Transaction(event.time, event.account, event.amount, Zero, Success, ATM, newBalance.getBalance()))

  }
  def withdraw(event: BankingEvent): Seq[Transaction] = {
    bankingCore.debit(event.account, event.amount) fold (
      err => Seq(Transaction(event.time, event.account, Zero, event.amount, Failure, ATM, err.balance)),
      newBalance => Seq(Transaction(event.time, event.account, Zero, event.amount, Success, ATM, newBalance.getBalance()))
    )
  }

  def process(event: BankingEvent): Seq[Transaction] = {
    event.action match {

      case CreateAccount => createAccount(event.account)
      case Withdraw => createAccount(event.account)
      case Deposit => createAccount(event.account)
      case _ => 
        println(s"Have not yet implemented ${event.action}")
        Seq.empty[Transaction]
    }


  }

}
