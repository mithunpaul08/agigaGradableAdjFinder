package agiga

import org.clulab.agiga
import org.clulab.processors.Document
import java.io.File
import scala.collection.parallel.ForkJoinTaskSupport
import java.io
import java.io.Writer;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.Arrays;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import scala.collection.mutable.ArrayBuffer;
import scala.io.Source
//import scala.collection.mutable.Map;
//import collection.mutable.HashMap


object adverbParser {
  val baseDirectoryPath = "/net/kate/storage/data/nlp/corpora/agiga/data/xml/"
  /* path in local machine */
  //val baseDirectoryPath = "/home/mithunpaul/Desktop/fall2016NLPResearch/agigaParser-without-world-modeling/inputs/"

  //a relative path, instead of absolute path
//  val baseDirectoryPath = "inputs/"
  val resourcesDirectory = "resources/"

  //input folder in mithuns laptop
//  val baseDirectoryPath = "/Users/mithun/Desktop/fall2016/agigaGradableAdjFinder/inputs/"


  //output path outside the work area on jenny
   val outputDirectoryPath= "/data1/nlp/users/mithun/adverbsInShivadePlusModifiedAdj/"

  //output directory in mithus laptop

  //val outputDirectoryPath = "outputs/"

  //val outputDirectoryPath = "/Users/mithun/Desktop/fall2016/agigaGradableAdjFinder/outputs/"
  // the xml files are here

  /*uncomment this if running on a core machine. i.e dont parallelize it if its a single core machine
//  val files = new File(baseDirectoryPath).listFiles.par
 // val nthreads = 10
  // limit parallelization
  files.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(nthreads))
*/
  /*uncomment this if running on a non-core machine. i.e dont parallelize it if its a single core machine*/
  var filesRaw = new File(baseDirectoryPath).listFiles
var files = filesRaw.sorted

  //add each line from shivade's adverb list, i.e an adverb to a hashtable with keys as adverbs
  //var hashMapOfAdverbs:Map[String, Int] = Map()
 // val hashMapOfAdverbs = new HashMap[String,String]()
  //{ override def default(key:Int) = "-" }
  //var hashMapOfAdverbs = Map("red" -> "#FF0000", "azure" -> "#F0FFFF")
  var hashMapOfAdverbs = Map("AL" -> "Alabama")
  def ReadToHashmap(): Unit = {
    //val filename = "no-such-file.scala"
    //var A:Map[Char,Int] = Map()
   // hashMapOfAdverbs += hashMapOfAdverbs + ("AK" -> "Alaska")
    //hashMapOfAdverbs += ("AK" -> "Alaska");

    //println(hashMapOfAdverbs)

    val advInputFile = resourcesDirectory+ "adverbs_shivade.txt";
    try {
      var counterForHashmap=0;
      for (line <- Source.fromFile(advInputFile).getLines()) {
        hashMapOfAdverbs += (line -> "1");
      }
    } catch {
      case ex: Exception => println("Bummer, an exception happened.")
    }
  }


  def processDocumentForAdverbs(doc: Document): ArrayBuffer[String] = {
    var arrayOflemmas = ArrayBuffer[String]()
    var lemma = "teststring";
       for (individualSentence <- doc.sentences) {
      for ((tag, wordCount) <- individualSentence.tags.get.zipWithIndex) {
        if (tag == "RB") {

          var advlemma = individualSentence.lemmas.get(wordCount)

          //proceed to find the beighboring adjective only if this new found adverb is part of shivade's list
          println("found an adverb. its value is: "+advlemma);
          //println( "Keys in colors : " + hashMapOfAdverbs.keys )
          //println( "Values in colors : " + hashMapOfAdverbs.values )
          if( hashMapOfAdverbs.contains( advlemma )) {
           // println("Found adverb exists in shivade's list")


            //find the word next to this adverb i.e wordcount+1


            //we were hitting adverbs that end a sentence. So this wordcount+1 is hitting null. Doing a null check
            Option(individualSentence.tags.get(wordCount + 1)) match {
              case Some(i) => {
                // println("given adjective word exists")

                {
                  var newTag = individualSentence.tags.get(wordCount + 1)
                  if (newTag.startsWith("JJ")) {
                    var adjlemma = individualSentence.lemmas.get(wordCount + 1);
                    println("found an adjective modified by adverb. The adverb is:" + advlemma + " and the adjective is:" + adjlemma);
                    //add this newly found lemma to the array of lemmas
                    arrayOflemmas += advlemma + " " + adjlemma;
                    // arrayOflemmas += adjlemma;

                  }
                }
              }
              case None => println(" There is no adjective next to the given adverb. Quitting/moving onto next line.")
            }
          } else
          {
           // println("Found adverb does not exist in shivade's list ")
          }
        }
      }
    }
    return arrayOflemmas;
  }

  def printLine(): Unit = {
    println("finished writing adverbs to file")
  }

  def readFiles(): Unit = {
    println("reaching here at 1")
    ReadToHashmap();

    //System.exit(1);
    for (individualFile <- files) {
      val fileName = individualFile.getName
      println("reaching here at 27")
      println("name of this file am parsing is:"+fileName)
      val outFile = new File(outputDirectoryPath, "adv_adj_" + fileName + ".txt")
      // make sure the file hasn't been already processed
      // useful when restarting
      println("reaching here at 2")
      if (!outFile.exists) {
        println("starting identification of adverbs in agigafile:" + fileName)
        println("reaching here at 3")
        println("the absolute path of the current document is: " + individualFile.getAbsolutePath)
        //for some reason agiga doesnt process the file i had hand made in my laptop. If your code has reached till here,
        //it means, its time to push it to jenny and test there. Somehow it works fine there.
        val doc = agiga.toDocument(individualFile.getAbsolutePath)
        println("finished finding adverbs in" + fileName)
        val adverbs = processDocumentForAdverbs(doc)
        println("reaching here at 4")
        //println("value of adverbs collected so far is "+adverbs.mkString("\n"))
        // val uniqAdj = adverbs.distinct
        //write to file
        println(" returned value of adverbs reaching here at 5")
        val bw = new BufferedWriter(new FileWriter(outFile))
        bw.write(adverbs.mkString("\n "))
        bw.close()
      }
    }


  }
}




