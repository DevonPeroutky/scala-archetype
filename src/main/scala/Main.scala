import example._
import bank.Statement
import bank.ValonBank
import bank.Transaction

@main def hello: Unit =  {
  val valonBank = ValonBank()

  val transactions: Seq[Transaction] = Statement.ingestBankEvents("input-3.json").fold(
    err => 
      println(s"Parsing err $err")
      Seq.empty[Transaction]
    ,
    events => 
      println("Events")
      println(events)
      events
        .flatMap(e => valonBank.process(e))
        .sortBy(txn => (txn.account, txn.time))
  )

  Statement.exportTransactions("output-3.json", transactions)
}



def msg = "I was compiled by Scala 3. :)"
