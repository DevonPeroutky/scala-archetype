package example
import java.io._ 
import scala.io.Source

/**
 * Write a `Seq[String]` to the `filename`.
 */
def writeFile(filename: String, lines: Seq[String]): Unit = {
    val file = new File(filename)
    val bw = new BufferedWriter(new FileWriter(file))
    for (line <- lines) {
        bw.write(s"$line\n")
    }
    bw.close()
}

/**
 * Read in a file's content as a single string. Assumes file is under src/main/resources and fits into a string
 */
def readInResourceFileAsStr(filename: String): String = {
  Source.fromResource(filename).mkString
}
