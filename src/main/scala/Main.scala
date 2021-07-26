import example._

@main def hello: Unit = 
  println(s"$msg\n")
  codecExample()
  fileDecodingExample()


def msg = "I was compiled by Scala 3. :)"
