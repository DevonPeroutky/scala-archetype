import example._

@main def hello: Unit = 
  println(msg)
  println("------")

  codecExample()
  fileDecodingExample()


def msg = "I was compiled by Scala 3. :)"
