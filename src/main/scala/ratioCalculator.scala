package agiga

import org.clulab.learning.{Datasets, PerceptronClassifier, RVFDataset, RVFDatum}
import org.clulab.struct.Counter

import scala.io.Source

/**
  * Created by mithunpaul on 10/14/16.
  */
object ratioCalculator {


  //var resourcesDirectory = "/work/mithunpaul/testbed/"
  //var resourcesDirectory = "..resources/"

  //on laptop
  var resourcesDirectory = "/Users/mithun/agro/agigaGradableAdjFinder/src/main/resources/"
  var outputDirectoryPath = "..outputs/"
  var erRemovedFiles = "AllErEstEndingAdjectivesUniq.txt"

  var completeAgigaFileWithFrequency = "allAdjCombined_withWordCount.txt";

  //var outputFileName = "hashmapForErAdjectiveAndItsBaseForm.txt";

  // var hashMapOfAllUniqAdjectivesInAgigaWithFrequency = Map("Long" -> "1")

  var inflectedCounter = 0;

  var hashMapOfAllUniqAdjectivesInAgigaWithFrequency: Map[String, String] = Map()
  var hashMapOfAllAdjectivesAndItsCount: Map[String, Int] = Map()

  def calculateInflectedAdjRatio(): Double = {

    var myratio: Double = 0.005;
    return myratio;

  }

  def triggerFunction(): Unit = {
    ReadAllAdjectivesAndFrequencyToHashmap();
    readErRemovedFileAndIncreaseCounter();
  }

  def calculateAdvModifiedAdjRatio(): Double = {

    var myratio: Double = 0.005;
    return myratio;
  }

  def calculateBothInflectedAdvModifiedRatio(): Double = {

    var myratio: Double = 0.005;
    return myratio;
  }


  def readErRemovedFileAndIncreaseCounter(): Unit = {
    //read all lines of uniq adjectives to a hashmap
    //ReadAllUniqAdjectivesToHashmap()
    //read all lines of er removed files and check its base form-i.e the er-removed form exists in the hashmap
    val erRemovedInputFile = resourcesDirectory + erRemovedFiles;
    println("reaching here at 3")
    var adjToCheck = "NULL";
    try {
      //var counterForHashmap = 0;
      for (line <- Source.fromFile(erRemovedInputFile).getLines()) {
        adjToCheck = line;
        //do the -er and -est removal in scala itself
        var erEstRemovedForm = adjToCheck.replaceAll("er", "")
        erEstRemovedForm = erEstRemovedForm.replaceAll("est", "")
        println("reaching here at 234233")
        //get its base form. Check the count of base form. Increase the count value, write it to the new hashmap which contains all adjectives and its counter
        if (hashMapOfAllUniqAdjectivesInAgigaWithFrequency.contains(erEstRemovedForm)) {
          //println("reaching here at 34345 . value of base form is:"+erEstRemovedForm)
          var baseCounter = 0;
          baseCounter = hashMapOfAllUniqAdjectivesInAgigaWithFrequency(erEstRemovedForm).toInt;
          baseCounter=baseCounter+1;
          println("value of basecounter for adjective "+erEstRemovedForm+"is "+baseCounter);

          // var inflectedCounter = 0;
          //println("value of basecounter is "+baseCounter);

         // var rootFormOfAdj=hashMapOfAllUniqAdjectivesInAgigaWithFrequency(adjToCheck);
          hashMapOfAllAdjectivesAndItsCount += (erEstRemovedForm -> baseCounter);


          //get the present value if it exists, and add one

          //hashMapOfAllAdjectivesAndItsCount += (adjToCheck -> baseCounter);


        }


      }
    } catch {
      case ex: Exception => println("Exception occured:")
    }
    // println("value of hashmap is:" + hashMapOfInflAdjToRootForm);
    //writeToFile(hashMapOfInflAdjToRootForm.mkString("\n"))
    //writeToFile(hashMapOfInflAdjToRootForm.mkString)
  }


  def ReadAllAdjectivesAndFrequencyToHashmap(): Unit = {
    //println("reaching here at 36857")
    val advInputFile = resourcesDirectory + completeAgigaFileWithFrequency;
    try {
      //println("reaching here at 1263")

      //println("reaching here at 579084")

      for (line <- Source.fromFile(advInputFile).getLines()) {

        val content = line.split("\\s+");

        if (content.length > 1) {

          hashMapOfAllUniqAdjectivesInAgigaWithFrequency += (content(1) -> content(0));
        }

        //println(hashMapOfAllUniqAdjectivesInAgigaWithFrequency.mkString("\n"));

      }
    } catch {
      case ex: Exception => println("An exception happened.:" + ex.getStackTrace.mkString("\n"))
    }
  }
}
