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


object agigParser {
	val baseDirectoryPath = "/net/kate/storage/data/nlp/corpora/agiga/data/xml/"
	/* path in local machine */
	//val baseDirectoryPath = "/home/mithunpaul/Desktop/fall2016NLPResearch/agigaParser-without-world-modeling/inputs/"

	//a relative path, instead of absolute path
	//val baseDirectoryPath = "/inputs/"
	//val baseDirectoryPath = "/work/mithunpaul/gzipsJustOne/"
	
	//val outputDirectoryPath = "/data1/nlp/"
	val outputDirectoryPath= "/data1/nlp/users/mithun/allAdjectives/"
	//output path outside the work area on jenny
	//val outputDirectoryPath = "./outputs/"
	// the xml files are here
	//val files = new File(baseDirectoryPath).listFiles.par

	/*dont parallelize it if its a single core machine*/
	val files = new File(baseDirectoryPath).listFiles



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
	var adjCounter = 2
	//var lemma="test"

	/** extract words tagged as adjectives */
	def processDocument(doc: Document): String = {
		var mystring = "this is a string"
		var lemma = "teststring"
		for (s <- doc.sentences) {
			for ((t, i) <- s.tags.get.zipWithIndex) {

				//for {
				// s <- doc.sentences
				// (t, i) <- s.tags.get.zipWithIndex
				// is it an adjective?i
				//if (mystring.startsWith("this")) {
				if (t.startsWith("JJ"))
					{


					var w = s.words(i)


					//lemma = s.lemmas.get(i)
					adjCounter = adjCounter + 1;

				//	println("value of adjCounter is" + adjCounter);
					//to find if the adjective ends with -est or -er
					if (w.matches(".*(est|er)$"))
					{
						// get the lemma
						lemma = s.lemmas.get(i)
					}
				}
			}
		}
		return lemma;
	}


	/*
def processDocument(doc: Document): Array[String] = {
	//for {
	// s <- doc.sentences
	// (t, i) <- s.tags.get.zipWithIndex
	// is it an adjective?i
	var mystring = "this is a string"
	// if (mystring.t.startsWith("JJ"))
	if (mystring.startsWith("this")) {
		println("the string you gave starts with this inside world")

		//var w = s.words(i)

		var lemma ="teststring"
		//lemma = s.lemmas.get(i)
		/ adjCounter = adjCounter + 1;
		//println("value of adjCounter is"+adjCounter);
		//to find if the adjective ends with -est or -er
		//if w.matches(".*(est|er)$")
		// get the lemma
		// lemma = s.lemmas.get(i)
	}
	//}
} yield lemma
*/


	def printLine(): Unit = {
		println("finished writing adjectives to file")
	}

	def readFiles(): Unit = {
		println("reaching here at 1")

		//def getCurrentDirectory = new java.io.File(".").getCanonicalPath
		var getCurrentDirectory = new java.io.File(".").getCanonicalPath
		println("value of present directory is: "+getCurrentDirectory)


		//run a random number generator inside an always true for loop- to pick files randomly and parse
		//for(1){
//
//		while (true) {
//			val rand = scala.util.Random
//			val i = rand.nextInt(1000)
//
			//for ( i <-1 to 10){
			//val f = files(i)
			for (individualFile <- files) {
			val fileName = individualFile.getName
			println("reaching here at 27")
			val outFile = new File(outputDirectoryPath, "gradAdj" + fileName + ".txt")
			// make sure the file hasn't been already processed
			// useful when restarting
			println("reaching here at 2")
			if (!outFile.exists) {
				println("starting identification of adjectives in agigafile:" + fileName)
				println("reaching here at 3")
				println("the absolute path of the current document is: "+individualFile.getAbsolutePath)
				val doc = agiga.toDocument(individualFile.getAbsolutePath)
				//val doc = agiga.toDocument(fileName)
				println("finished finding adjectives in" + fileName)
				val adjectives = processDocument(doc)
				println("value of adjCounter is" + adjCounter);
				println("reaching here at 4")
				// val uniqAdj = adjectives.distinct
				//write to file
				println("reaching here at 5")
				val bw = new BufferedWriter(new FileWriter(outFile))
				bw.write(adjectives.mkString("\n "))
				println("value of adjcounter is ")
				println(adjCounter)
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
