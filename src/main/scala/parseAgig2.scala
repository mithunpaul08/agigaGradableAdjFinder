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
import scala.collection.mutable.ArrayBuffer;
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
	//val files = new File(baseDirectoryPath).listFiles



	//val files = new File(baseDirectoryPath).listFiles
	var filesRaw = new File(baseDirectoryPath).listFiles
	//val files = filesRaw.sorted(Ordering.by(File.size).reverse)
	var files = filesRaw.sorted
	//val files = filesRaw.sorted(Ordering.Any.reverse)
	//val files = filesRaw.sorted(Ordering.by(File.getTotalSpace()))
	//val files = filesRaw.sortBy(_.size)
	//sortWith(sortByLength)
	//scala.util.Sorting.quickSort(files)
	//filesRaw.sortWith(sortByLength)

	//Arrays.sort(files, SizeFileComparator.SIZE_COMPARATOR)
	val nthreads = 2 
	// limit parallelization
//	files.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(nthreads))
	var adjCounter = 2
	//var lemma="test"

	/** code set 3  using for yield loops: note: we cannot do a var assigning withint this kind of for-yield declaratio. so ignoring this as of 110am oct 7th without using for loops.*/
/*
	def processDocument(doc: Document): String = 
	for {
	 s <- doc.sentences
	 (t, i) <- s.tags.get.zipWithIndex
				if (t.startsWith("JJ")==true)
					{

				//	adjCounter = adjCounter + 1;

				//	println("value of adjCounter is" + adjCounter);
						lemma = s.lemmas.get(i)
			//	println("found an adjective whose tag starts with JJ and its lemma  value is"+ lemma)
					}
				
}yield lemma

*/
	/** code set 2 using for loops.Note: this is like any for loop in any language. No yeild shit. Plus extract words tagged as adjectives+concatenate into one big string */
 
	def processDocument(doc: Document): ArrayBuffer[String] = {
		var mystring = "this is a string"

		var arrayOflemmas = ArrayBuffer[String]()
		var lemma = "teststring"
		for (s <- doc.sentences)
		 {

			for ((t, i) <- s.tags.get.zipWithIndex)
				{

				if (t.startsWith("JJ"))
					{


			//	println("found an adjective whose tag starts with JJ and its w value is"+ w)


					adjCounter = adjCounter + 1;

				//	println("value of adjCounter is" + adjCounter);
				//		// get the lemma
						lemma = s.lemmas.get(i)
//add this newly found lemma to the array of lemmas
arrayOflemmas += lemma;
				//println("found an adjective whose tag starts with JJ and its lemma  value is"+ lemma)
					}
				
			}
		}
		return arrayOflemmas;
	}


	/* code set 1which had the yeild statement without for loops
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
			val outFile = new File(outputDirectoryPath, "allAdj_" + fileName + ".txt")
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
				//println("value of adjectives collected so far is "+adjectives.mkString("\n"))
				// val uniqAdj = adjectives.distinct
				//write to file
				println(" returned value of adjectives reaching here at 5")
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
