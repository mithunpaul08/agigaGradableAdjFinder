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


object goodAdjectiveFinder {

  /* generate the base form by removing this suffix.
2. if the base form appears as a JJ* in agiga (go through the big adjective dump...any adjective), you're good: you found a good one. For example, colder is a good adjective.
3. if not, this is not an adjective. For example, "kilometer" or "silver" are not valid adjectives, because their base form, "kilomet" and "silv", will not appear in text.
*/

  var resourcesDirectory = "resources/"

  var outputDirectoryPath = "outputs/"

var completeAgigaFile="CompleteAgigaDump.txt";
  var erRemovedFiles="uniqAdjectivesInAgiga_removedErEst.txt";
  var hashMapOfUniqAdjectives = Map("Long" -> "1")



  def ReadAllUniqAdjectivesToHashmap(): Unit = {
    println("reaching here at 1325")
    val advInputFile = resourcesDirectory + completeAgigaFile;
    try {
      //println("reaching here at 1263")
      var counterForHashmap = 0;
      for (line <- Source.fromFile(advInputFile).getLines()) {
      //  println("reaching here at 184")
        hashMapOfUniqAdjectives += (line -> "1");
      }
    } catch {
      case ex: Exception => println("Bummer, an exception happened.")
    }
  }


//  def processDocumentForAdverbs(doc: Document): ArrayBuffer[String] = {
//    var arrayOflemmas = ArrayBuffer[String]()
//    var lemma = "teststring";
//    for (individualSentence <- doc.sentences) {
//      for ((tag, wordCount) <- individualSentence.tags.get.zipWithIndex) {
//        if (tag == "RB") {
//          var advlemma = individualSentence.lemmas.get(wordCount)
//          var numberOfTokens = individualSentence.words.length;
//          // println("value of wordCount is :"+wordCount+" and the value of numberOf Tokens is:"+numberOfTokens)
//
//          //If total number of tokens == wordCount, it means the adverb is the last word of the sentence. quit/move onto next sentence.
//          //wordcount:16 totaltokens:17
//          if (wordCount < (numberOfTokens - 1)) {
//            //proceed to find the beighboring adjective only if this new found adverb is part of shivade's list
//            println("found an adverb. its value is: " + advlemma);
//            //println( "Keys in colors : " + hashMapOfUniqAdjectives.keys )
//            //println( "Values in colors : " + hashMapOfUniqAdjectives.values )
//            if (hashMapOfUniqAdjectives.contains(advlemma)) {
//              // println("Found adverb exists in shivade's list")
//
//
//              //find the word next to this adverb i.e wordcount+1
//
//
//              //we were hitting adverbs that end a sentence. So this wordcount+1 is hitting null. Doing a null check
//              Option(individualSentence.tags.get(wordCount + 1)) match {
//                case Some(i) => {
//                  // println("given adjective word exists")
//
//                  {
//                    var newTag = individualSentence.tags.get(wordCount + 1)
//                    if (newTag.startsWith("JJ")) {
//                      var adjlemma = individualSentence.lemmas.get(wordCount + 1);
//                      println("found an adjective modified by adverb. The adverb is:" + advlemma + " and the adjective is:" + adjlemma);
//                      //add this newly found lemma to the array of lemmas
//                      arrayOflemmas += advlemma + " " + adjlemma;
//                      // arrayOflemmas += adjlemma;
//
//                    }
//                  }
//                }
//                case None => println(" There is no adjective next to the given adverb. Quitting/moving onto next line.")
//              }
//            } else {
//              // println("Found adverb does not exist in shivade's list ")
//            }
//          }
//        }
//      }
//    }
//    return arrayOflemmas;
//  }

  def writeToFile():Unit= {
    //val outFile = new File(outputDirectoryPath, "adv_adj_" + fileName + ".txt")
    //      // make sure the file hasn't been already processed
    //      // useful when restarting
    //      println("reaching here at 2")
    //      if (!outFile.exists) {
    //val bw = new BufferedWriter(new FileWriter(outFile))

    //        bw.write(adverbs.mkString("\n "))
    //        bw.close()}
  }
  def printLine(): Unit = {
    println("finished writing adverbs to file")
  }

  def readErRemovedFile(): Unit = {
    //println("reaching here at 1")

    //read all lines of uniq adjectives to a hashmap
    ReadAllUniqAdjectivesToHashmap()

    //read all lines of er removed files and check its base form-i.e the er-removed form exists in the hashmap
    val erRemovedInputFile = resourcesDirectory + erRemovedFiles;
    println("reaching here at 3")
    var adjToCheck = "NULL";
    try {
      //var counterForHashmap = 0;
      for (line <- Source.fromFile(erRemovedInputFile).getLines()) {
        adjToCheck = line;
        //println("reaching here at 8367. VALUE OF adjective is"+adjToCheck)

        if (hashMapOfUniqAdjectives.contains(line)) {
          println("found that the given adjective:" + adjToCheck + " has its root form in the file. ")
        }
      }
    } catch {
      case ex: Exception => println("No root form found for adjective:" + adjToCheck)
    }
  }


}


    //System.exit(1);
    //run a random number generator inside an always true for loop- to pick files randomly and parse- right now all my threads are fighting for the same file

    //for(1){
      //
//    while (true) {
//
//     var  randomFileNumber = rand.nextInt(1009)
//    println("value of input folder is:"+baseDirectoryPath+"and the value of output directory is:"+outputDirectoryPath)
//
//      println("value of random number is:" + randomFileNumber)
//     val individualFile = files(randomFileNumber)
//     // for (individualFile <- files) {
//      val fileName = individualFile.getName
//      println("reaching here at 27")
//      println("name of this file am parsing is:" + fileName)
//      val outFile = new File(outputDirectoryPath, "adv_adj_" + fileName + ".txt")
//      // make sure the file hasn't been already processed
//      // useful when restarting
//      println("reaching here at 2")
//      if (!outFile.exists) {
//        println("starting identification of adverbs in agigafile:" + fileName)
//        println("reaching here at 3")
//        println("the absolute path of the current document is: " + individualFile.getAbsolutePath)
//        //for some reason agiga doesnt process the file i had hand made in my laptop. If your code has reached till here,
//        //it means, its time to push it to jenny and test there. Somehow it works fine there.
//        val doc = agiga.toDocument(individualFile.getAbsolutePath)
//        println("finished finding adverbs in" + fileName)
//        val adverbs = processDocumentForAdverbs(doc)
//        println("reaching here at 4")
//        //println("value of adverbs collected so far is "+adverbs.mkString("\n"))
//        // val uniqAdj = adverbs.distinct
//        //write to file
//        println(" returned value of adverbs reaching here at 5")
//        val bw = new BufferedWriter(new FileWriter(outFile))
//        bw.write(adverbs.mkString("\n "))
//        bw.close()
//      }
//    }
//  }
//}
//
//
//
//
