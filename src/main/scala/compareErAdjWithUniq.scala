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


object goodAdjectiveFinder {

  /* generate the base form by removing this suffix.
2. if the base form appears as a JJ* in agiga (go through the big adjective dump...any adjective), you're good: you found a good one. For example, colder is a good adjective.
3. if not, this is not an adjective. For example, "kilometer" or "silver" are not valid adjectives, because their base form, "kilomet" and "silv", will not appear in text.
*/


  var resourcesDirectory = "/work/mithunpaul/testbed/"
  //var resourcesDirectory = "resources/"

  var outputDirectoryPath = "outputs/"

 // var completeAgigaFile = "allAdjCombined.txt";
 // var completeAgigaFile = "allAdjectivesFromAgigaButUniq.txt";
  

var completeAgigaFile = "sortedUniqAllAdjFromAgiga.txt";
  //var erRemovedFiles="uniqAdjectivesInAgiga_removedErEst.txt";
  var AllErEstEndingAdjectivesUniq = "AllErEstEndingAdjectivesUniq.txt"

  var outputFileName = "hashmapForErAdjectiveAndItsBaseForm.txt";

  var hashMapOfAllUniqAdjectivesInAgiga = Map("Long" -> "1")

  var hashMapOfInflAdjToRootForm: Map[String, String] = Map()


  def ReadAllUniqAdjectivesToHashmap(): Unit = {
  //  println("reaching here at 1325")
    val advInputFile = resourcesDirectory + completeAgigaFile;
    try {
    //  println("reaching here at 1263.value of the path of the file is :"+advInputFile)
      var counterForHashmap = 0;
      for (line <- Source.fromFile(advInputFile).getLines()) {
         // println("reaching here at 184")
        hashMapOfAllUniqAdjectivesInAgiga += (line -> "1");
      }
    } catch {
      case ex: Exception => println("An exception happened. Not able to find the file")
    }
  }


  def writeToFile(stringToWrite: String): Unit = {
    val outFile = new File(outputDirectoryPath, outputFileName)

    val bw = new BufferedWriter(new FileWriter(outFile))


    bw.write(stringToWrite)
    bw.close()


  }

  def printLine(): Unit = {
    println("finished writing adverbs to file")
  }


  def testForNull(x: Option[String]) = x match {
    case Some(s) => s
    case None => "value not found"
  }

  def readErRemovedFile(runOnServer: Boolean): Map[String, String] = {
    //println("reaching here at 1")
    if (runOnServer) {
       resourcesDirectory = "/work/mithunpaul/testbed/"
      //var resourcesDirectory = "resources/"

       outputDirectoryPath = "/work/mithunpaul/testbed/"
    }
    else {

      //resourcesDirectory = "src/main/resources/"
     resourcesDirectory= "/Users/mithun/agro/agigaGradableAdjFinder/src/main/resources/"

     // outputDirectoryPath = "src/main/outputs/"
      outputDirectoryPath= "/Users/mithun/agro/agigaGradableAdjFinder/src/main/outputs/"
    }
    //read all lines of uniq adjectives to a hashmap
    ReadAllUniqAdjectivesToHashmap()
    //read all lines of er removed files and check its base form-i.e the er-removed form exists in the hashmap
    val erRemovedInputFile = resourcesDirectory + AllErEstEndingAdjectivesUniq;
   // println("reaching here at 956395.value of the path of the file is :"+erRemovedInputFile)
   // println("reaching here at 3")
    var adjToCheck = "NULL";
    try {
      //var counterForHashmap = 0;
      for (line <- Source.fromFile(erRemovedInputFile).getLines()) {
        adjToCheck = line;

        //do the -er and -est removal in scala itself
        var erEstRemovedForm = adjToCheck.replaceAll("er", "")
        erEstRemovedForm = erEstRemovedForm.replaceAll("est", "")
        // println("the root form of the given adjective:" + adjToCheck  + " is:"+erEstRemovedForm)
        //if the er and est removed root form exists in the hashtable store it into another hashtable in the form "deepest->deep

        if (hashMapOfAllUniqAdjectivesInAgiga.contains(erEstRemovedForm)) {
          //println("found that the given adjective:" + adjToCheck + " has its root form in the file. adding to hash map")
          //System.exit(1);
          hashMapOfInflAdjToRootForm += (adjToCheck -> erEstRemovedForm);
        }

      }
    } catch {
      case ex: Exception => println("Exception occured:")
    }
   // println("value of hashmap is:" + hashMapOfInflAdjToRootForm);
    //System.exit(1)
    writeToFile(hashMapOfInflAdjToRootForm.mkString("\n"))
    return hashMapOfInflAdjToRootForm;

  }
}


