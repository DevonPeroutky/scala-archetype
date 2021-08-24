import example._
import bank.Statement
import bank.ValonBank
import bank.Transaction

@main def hello: Unit =  {
  val valonBank = ValonBank()

  val step: Int = 4

  val transactions: Seq[Transaction] = Statement.ingestBankEvents(s"input-$step.json").fold(
    err => 
      println(s"Parsing err $err")
      Seq.empty[Transaction]
    ,
    events => 
      events
        .flatMap(e => valonBank.process(e))
        .sortBy(txn => (txn.account, txn.time))
  )

  Statement.exportTransactions(s"output-$step.json", transactions)
}
