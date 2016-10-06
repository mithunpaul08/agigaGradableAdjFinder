package agiga

import org.clulab.agiga
import org.clulab.processors.Document
import java.io.File
import scala.collection.parallel.ForkJoinTaskSupport
import java.io._
import java.util.Arrays
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.comparator.SizeFileComparator;


object agigParser{
val baseDirectoryPath = "/net/kate/storage/data/nlp/corpora/agiga/data/xml/"
//val baseDirectoryPath = "/work/mithunpaul/gzips/"
//val baseDirectoryPath = "/work/mithunpaul/gzipsJustOne/"
val outputDirectoryPath = "/work/mithunpaul/outputs/"
// the xml files are here
val files = new File(baseDirectoryPath).listFiles.par


//val files = new File(baseDirectoryPath).listFiles
//val filesRaw = new File(baseDirectoryPath).listFiles
//val files = filesRaw.sorted(Ordering.by(File.size).reverse)
//val files = filesRaw.sorted
//val files = filesRaw.sorted(Ordering.Any.reverse)
//val files = filesRaw.sorted(Ordering.by(File.getTotalSpace()))
//val files = filesRaw.sortBy(_.size)
//sortWith(sortByLength)
//scala.util.Sorting.quickSort(files)
//filesRaw.sortWith(sortByLength)

//Arrays.sort(files, SizeFileComparator.SIZE_COMPARATOR)
val nthreads = 8
// limit parallelization
//files.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(nthreads))

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
	println("reaching here at 1")


	//run a random number generator inside an always true for loop
	//for(1){
	while(true)
	{
	val rand= scala.util.Random
	val i= rand.nextInt(1000)
	//for ( i <-1 to 10){
	val f=files(i)
	//for (f <- files) {
	val fileName=f.getName
	println("reaching here at 27")
	val outFile = new File(outputDirectoryPath, "gradAdj"+ fileName+".txt")
    	// make sure the file hasn't been already processed
    	// useful when restarting
	println("reaching here at 2")
	if (!outFile.exists)
    	{
	println("starting identification of adjectives in agigafile:" +fileName)
	println("reaching here at 3")
	val doc = agiga.toDocument(f.getAbsolutePath)
    	println("finished finding adjectives in"+fileName)
    	val adjectives = processDocument(doc)
	println("reaching here at 4")
     	// val uniqAdj = adjectives.distinct
    	//write to file
	println("reaching here at 5")
	val bw = new BufferedWriter(new FileWriter(outFile))
	bw.write(adjectives.mkString("\n "))
	println("reaching here at 6")
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
