package agiga

import java.io.{BufferedWriter, File, FileWriter}

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
  //var resourcesDirectory = "/Users/mithun/agro/agigaGradableAdjFinder/src/main/resources/"

  //var outputDirectoryPath = "/Users/mithun/agro/agigaGradableAdjFinder/src/main/outputs/"
 // var outputDirectoryPath = "/Users/mithun/agro/agigaGradableAdjFinder/src/main/outputs/"

 // var outputDirectoryPath = "..outputs/"
  var erRemovedFiles = "AllErEstEndingAdjectivesUniq.txt"

  var completeAgigaFileWithFrequency = "allAdjCombined_withWordCount.txt";

  var outputFileNameForAllAdjectiveCount = "hashmapForAllAdjectivesAndItsCount.txt";
  var outputFileNameForInflectedAdjectiveCount = "hashmapForAllAdjectivesAndItsCount.txt";

  var allAdjectivesFromAgigaButUniq= "allAdjectivesFromAgigaButUniq.txt"

  var uniqAdjectivesInAgiga_removedErEst_uniq= "uniqAdjectivesInAgiga_removedErEst_uniq.txt"

  // var hashMapOfAllUniqAdjectivesInAgigaWithFrequency = Map("Long" -> "1")


  var hashMapOfAllUniqAdjectivesInAgigaWithFrequency: Map[String, String] = Map()
  var hashMapOfAllAdjectivesAndItsCount: Map[String, Int] = Map()
  var hashMapOfInflectedAdjectivesAndItsCount: Map[String, Int] = Map()

  def calculateInflectedAdjRatio(adjToGetRatio: String): Double = {
    //println("reaching here at 4393897")
    var myratio: Double = 0.005;
    var totalBaseCount = 2;
    var noOfTimesThisAdjInflected = 1;
   // println("value of current adjective is :" + adjToGetRatio);
    //Go through the uniq adjectives list in all the adjectives...for each adjective, pick the total count value and inflected count value.
    //    for (line <- Source.fromFile(resourcesDirectory+uniqAdjectivesInAgiga_removedErEst_uniq).getLines())
    //      {
    //       // println("getting into function calculateInflectedAdjRatio and the given adjective is:" +line)
    //total count=basecount+inflectedErCount+inflectedEstCount

    //get the total count from the total count hashmap:hashMapOfAllAdjectivesAndItsCount
    if (hashMapOfAllAdjectivesAndItsCount.contains(adjToGetRatio)) {
      println("reaching here at 089345978")
      totalBaseCount = hashMapOfAllAdjectivesAndItsCount(adjToGetRatio)
      println("found that the given adjective:" + adjToGetRatio + " exists in the hashMapOfAllAdjectivesAndItsCount and its base value is" + totalBaseCount)
    }
    //println("reaching here at 34522")
    //get the inflected count from the inflected count hashmap:hashMapOfInflectedAdjectivesAndItsCount
    var baseForm = adjToGetRatio.replaceAll("er", "")
    baseForm = baseForm.replaceAll("est", "")
    if (hashMapOfInflectedAdjectivesAndItsCount.contains(baseForm)) {
      println("reaching here at 9876")
      noOfTimesThisAdjInflected = hashMapOfInflectedAdjectivesAndItsCount(baseForm)
      println("found that the given adjective:" + baseForm + " exists in the hashMapOfInflectedAdjectivesAndItsCount and its  value is" + noOfTimesThisAdjInflected)
    }
    //println("reaching here at 347234")

    myratio = noOfTimesThisAdjInflected / totalBaseCount;
    //println("value of this ratio is:" + myratio)

    return myratio;

  }

  def triggerFunction(resourcesDirectory:String,outputDirectoryPath:String): Unit = {
    ReadAllAdjectivesAndFrequencyToHashmap(resourcesDirectory,outputDirectoryPath);
    readErRemovedFileAndIncreaseCounter(resourcesDirectory,outputDirectoryPath);
     }

  def calculateAdvModifiedAdjRatio(): Double = {

    var myratio: Double = 0.005;
    return myratio;
  }

  def calculateBothInflectedAdvModifiedRatio(): Double = {

    var myratio: Double = 0.005;
    return myratio;
  }


  def readErRemovedFileAndIncreaseCounter(resourcesDirectory:String,outputDirectoryPath :String): Unit = {
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
          hashMapOfAllAdjectivesAndItsCount += (erEstRemovedForm -> baseCounter);

          //hashmap for an  adjectives in its inflected form and its count. Note it will start from zero
          //if two adjectives has the same root form, increase teh counter by 2. I.e colder, and coldest, will have the same base forms-cold
          //so the inflectedCounter for Cold must increase+2. Because cold was inflected twice.Store that in another hashmap

          //so at the end of the day, you must have the value, which is a sum of the number of times colder appears,
          //and the number of times coldest appears.
          // if it exists, retrieve it, increase its counter value and write it back. Else just write 1
          //i.e very first time you encounter, coldest- initialize its value to zero. Now if you see colder again,
          //this value must increase. Note that the key here will be the base: cold
         var inflectedCounterForSameWord=0;
          if (hashMapOfInflectedAdjectivesAndItsCount.contains(erEstRemovedForm)){
            inflectedCounterForSameWord = hashMapOfInflectedAdjectivesAndItsCount(erEstRemovedForm).toInt;
            inflectedCounterForSameWord = inflectedCounterForSameWord + 1;
          }
          else {
            inflectedCounterForSameWord = 1
          }
          hashMapOfInflectedAdjectivesAndItsCount += (erEstRemovedForm -> inflectedCounterForSameWord);


        }


        //below hash table hashMapOfAllAdjectivesAndItsCount just shows all the adjectives in its 3 forms and the counts
        //Eg: Cold:234234 colder:154 coldest:231. Now the problem is, how to sum the counts of colder and coldest. How
        //do we determine, that they are both inflections of same word, cold...So created hashMapOfInflectedAdjectivesAndItsCount.
        //leaving this as is, so that i can show to mihai
        var inflectedCounter = 0;


        // if it exists, retrieve it, increase its counter value and write it back. Else just write 1
        if (hashMapOfAllAdjectivesAndItsCount.contains(adjToCheck)){
          inflectedCounter = hashMapOfAllAdjectivesAndItsCount(adjToCheck).toInt;
          inflectedCounter = inflectedCounter + 1;
        }
        else {
          inflectedCounter = 1
        }
        hashMapOfAllAdjectivesAndItsCount += (adjToCheck -> inflectedCounter);

      }
    } catch {
      case ex: Exception => println("Exception occured:")
    }
   // println("value of hashmap is:" + hashMapOfAllAdjectivesAndItsCount.mkString("\n "));
    //writeToFile(hashMapOfAllAdjectivesAndItsCount.mkString("\n"),outputFileNameForAllAdjectiveCount,outputDirectoryPath)

   // println("value of hashmap is:" + hashMapOfInflectedAdjectivesAndItsCount.mkString("\n "));
    //write the inflected value count also to file
    //writeToFile(hashMapOfInflectedAdjectivesAndItsCount.mkString("\n"),outputFileNameForInflectedAdjectiveCount,outputDirectoryPath)

  }

  def writeToFile(stringToWrite: String, outputFilename: String,outputDirectoryPath :String): Unit = {


    val outFile = new File(outputDirectoryPath, outputFilename)

    val bw = new BufferedWriter(new FileWriter(outFile))


    bw.write(stringToWrite)
    bw.close()


  }
  def ReadAllAdjectivesAndFrequencyToHashmap(resourcesDirectory:String,outputDirectoryPath:String): Unit = {
    println("reaching here at 36857")

    //read from all the adjectives and its frequency into a hash table
    val advInputFile = resourcesDirectory + completeAgigaFileWithFrequency;
    try {
      println("reaching here at 1263")

      for (line <- Source.fromFile(advInputFile).getLines()) {
        //println("reaching here at 3462323")
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
