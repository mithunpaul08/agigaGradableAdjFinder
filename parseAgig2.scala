package agiga

import org.clulab.agiga
import org.clulab.processors.Document
import java.io.File
import scala.collection.parallel.ForkJoinTaskSupport



object agigParser{

val baseDirectoryPath= "/work/mithunpaul/gzips/"

// the xml files are here
//val files = new File("/net/kate/storage/data/nlp/corpora/agiga/data/xml").listFiles.par
val files = new File("/work/mithunpaul/gzips/").listFiles.par
// you'll probably want to increase this...
val nthreads = 4
// limit parallelization
files.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(nthreads))

/** extract words tagged as adjectives */
def processDocument(doc: Document): Array[String] = for {
  s <- doc.sentences
  (t, i) <- s.tags.get.zipWithIndex
  // is it an adjective?
  if t == "JJ"
  w = s.words(i)
} yield w


def printLine (): Unit =
{
println("inside print files code1")
}

def readFiles (): Unit =
{



println("inside print files code2")

/** do something with the extracted adjectives */
for {
  f <- files
//println("inside print files code34")
	val combinedFileName=baseDirectoryPath +f.getName
//println("inside print files code334")
  doc = agiga.toDocument(combinedFileName)
} {
println("inside print files code4")
  val adjectives = processDocument(doc)
println(adjectives.mkString(" "))
//	println(adjectives)
  // write to a file or whatever
  // ...
}
}
}
