import example._
import bank.Statement
import bank.ValonBank
import bank.Transaction

@main def hello: Unit =  {
  val valonBank = ValonBank()

  val transactions: Seq[Transaction] = Statement.ingestBankEvents("input-1.json").fold(
    err => 
      println(s"Parsing err $err")
      Seq.empty[Transaction]
    ,
    txns => 
      txns
        .flatMap(valonBank.process)
        .sortBy(txn => (txn.account, txn.time))
  )

  Statement.exportTransactions("output-1.json", transactions)

}



def msg = "I was compiled by Scala 3. :)"
