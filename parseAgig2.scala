package agiga

import org.clulab.agiga
import org.clulab.processors.Document
import java.io.File
import scala.collection.parallel.ForkJoinTaskSupport
import java.io._


object agigParser{
val completeAgigaFiles = "net/kate/storage/data/nlp/corpora/agiga/data/xml/"
//val baseDirectoryPath = "/work/mithunpaul/gzips/"
val baseDirectoryPath = "/work/mithunpaul/gzipsJustOne/"
val outputDirectoryPath = "/work/mithunpaul/outputs/"
// the xml files are here
val files = new File(baseDirectoryPath).listFiles.par
val nthreads = 2
// limit parallelization
files.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(nthreads))

/** extract words tagged as adjectives */
def processDocument(doc: Document): Array[String] = for {
  s <- doc.sentences
  (t, i) <- s.tags.get.zipWithIndex
  // is it an adjective?
  //if t == "JJ"
  if t.startsWith("JJ")
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
	for (f <- files) {
	val fileName=f.getName
	println("starting identification of adjectives in agigafile:" +fileName)
	val outFile = new File(outputDirectoryPath, "gradAdj"+ fileName+".txt")
    	// make sure the file hasn't been already processed
    	// useful when restarting
	if (!outFile.exists)
    	{
	val doc = agiga.toDocument(f.getAbsolutePath)
    	println("finished finding adjectives in"+fileName)
    	val adjectives = processDocument(doc)
     	 val uniqAdj = adjectives.distinct
    	//write to file
	val bw = new BufferedWriter(new FileWriter(outFile))
	bw.write(uniqAdj.mkString("\n "))
	bw.close()
 	}
}
 }


}





/** do something with the extracted adjectives */
//for  {
 // f <- files



//println("inside print files code34")
//	val combinedFileName=baseDirectoryPath +f.getName
//println("inside print files code334")

//val doc = agiga.toDocument(combinedFileName)

//println("finished finding adjectives.")
 // val adjectives = processDocument(doc)
//	val uniqAdj=adjectives.distinct
//println(adjectives.mkString(" "))


//write to files

//val pathForOutputFile= outputDirectoryPath+"gradedAdjectivesFromAgiga"+combinedFileName+".txt"
// FileWriter
//val file = new File(pathForOutputFile)
//if (!file.exists)

//{val bw = new BufferedWriter(new FileWriter(file))
//bw.write(adjectives.mkString("\n "))
//bw.write(uniqAdj.mkString("\n "))
//bw.close()
//}
//}

//}

//}
