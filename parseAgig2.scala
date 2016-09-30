package agiga

import org.clulab.agiga
import org.clulab.processors.Document
import java.io.File
import scala.collection.parallel.ForkJoinTaskSupport
import java.io._


object agigParser{

val baseDirectoryPath= "/work/mithunpaul/gzips/"
val outputDirectoryPath = "/work/mithunpaul/outputs/"
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
//to find if the adjective ends with -est or -er
if w.matches(".*(est|er)$")
  // get the lemma
  lemma = s.lemmas.get(i)
} yield lemma


//} yield w


def printLine (): Unit =
{
println("finished writing adjectives to file")
}

def readFiles (): Unit =
{



println("starting identification of adjectives in agiga")

/** do something with the extracted adjectives */
for {
  f <- files
//println("inside print files code34")
	val combinedFileName=baseDirectoryPath +f.getName
//println("inside print files code334")
  doc = agiga.toDocument(combinedFileName)
} {
println("finished finding adjectives.")
  val adjectives = processDocument(doc)
	val uniqAdj=adjectives.distinct
//println(adjectives.mkString(" "))


//write to files

val pathForOutputFile= outputDirectoryPath+"gradedAdjectivesFromAgiga"+".txt"
// FileWriter
val file = new File(pathForOutputFile)
val bw = new BufferedWriter(new FileWriter(file))
//bw.write(adjectives.mkString("\n "))
bw.write(uniqAdj.mkString("\n "))
bw.close()


//	println(adjectives)
  // write to a file or whatever
  // ...
}
}
}
