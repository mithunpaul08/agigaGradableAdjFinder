package agiga

import util.control.Breaks._
import java.io.{BufferedWriter, File, FileWriter}

import org.clulab.learning.{Datasets, PerceptronClassifier, RVFDataset, RVFDatum}
import org.clulab.struct.Counter

import scala.io.Source

/**
  * Created by mithunpaul on 10/14/16.
  */
object ratioCalculator {


  var erRemovedFiles = "AllErEstEndingAdjectivesUniq.txt"
  var completeAgigaFileWithFrequency = "allAdjCombined_withWordCount.txt";
  var outputFileNameForAllAdjectiveCount = "hashmapForAllAdjectivesAndItsCount.txt";
  var outputFileNameForInflectedAdjectiveCount = "hashmapForAllAdjectivesAndItsCount.txt";
  var allAdjectivesFromAgigaButUniq = "allAdjectivesFromAgigaButUniq.txt"
  var uniqAdjectivesInAgiga_removedErEst_uniq = "uniqAdjectivesInAgiga_removedErEst_uniq.txt"
  var FreqOfAdjAdv_withoutAgainAt = "FreqOfAdjAdv_withoutAgainAt.txt";

  // var hashMapOfAllUniqAdjectivesInAgigaWithFrequency = Map("Long" -> "1")

  //the denominator remains same for a given adjective for all the ratios
  var denominatorOfRatio: Double = 0.0000001;

  var hashMapOfAllUniqAdjectivesInAgigaWithFrequency: Map[String, String] = Map()
  var hashMapOfAllAdjectivesAndItsCount: Map[String, Int] = Map()
  var hashMapOfInflectedAdjectivesAndItsCount: Map[String, Int] = Map()
  var hashMapOfAdvModifiedAdjCount: Map[String, Int] = Map()

  def calculateInflectedAdjRatio(adjToGetRatio: String): Double = {
    println("reaching here at 4393897")
    var myratio: Double = 0;
    var totalBaseCount: Double = 0
    var noOfTimesThisAdjInflected = 0;
    println("value of current adjective is :" + adjToGetRatio);

    //get the total number of times this adjective occurs in AGIGA.
    // //count from the total count hashmap:hashMapOfAllAdjectivesAndItsCount
    //if (hashMapOfAllAdjectivesAndItsCount.contains(adjToGetRatio))
      if (hashMapOfAllUniqAdjectivesInAgigaWithFrequency.contains(adjToGetRatio)) {
        println("reaching here at 089345978")
        totalBaseCount = hashMapOfAllAdjectivesAndItsCount(adjToGetRatio)
        println("found that the given adjective:" + adjToGetRatio + " exists in the hashMapOfAllAdjectivesAndItsCount and its base value is" + totalBaseCount)

        println("reaching here at 34522")
        //if the adjective exists get the inflected count from the inflected count hashmap:hashMapOfInflectedAdjectivesAndItsCount
        var baseForm = adjToGetRatio.replaceAll("er", "")
        baseForm = baseForm.replaceAll("est", "")
        if (hashMapOfInflectedAdjectivesAndItsCount.contains(baseForm)) {
          println("reaching here at 9876")
          noOfTimesThisAdjInflected = hashMapOfInflectedAdjectivesAndItsCount(baseForm)
          println("found that the given adjective:" + baseForm + " exists in the hashMapOfInflectedAdjectivesAndItsCount and its  value is" + noOfTimesThisAdjInflected)
        }
      }
      else {
        //if the adjective doesnt exist, return a dummy value- no point continuing---dont add it to the loop...so break?
        println("the given adjective:" + adjToGetRatio + " does not exist in the hashMapOfAllUniqAdjectivesInAgigaWithFrequency")
        break;
      }
    //println("reaching here at 347234")
    denominatorOfRatio = totalBaseCount.toDouble
    //println("value of this denominatorOfRatio is:" + denominatorOfRatio.toDouble.toString())
    myratio = noOfTimesThisAdjInflected.toDouble / denominatorOfRatio;
    //println("value of this ratio is:" + myratio.toDouble.toString())

    return myratio;

  }

  def triggerFunction(resourcesDirectory: String, outputDirectoryPath: String): Unit = {
    ReadAllAdjectivesAndFrequencyToHashmap(resourcesDirectory, outputDirectoryPath);
    readErRemovedFileAndIncreaseCounter(resourcesDirectory, outputDirectoryPath);
    ReadAllAdvAdjectivesAndFrequencyToHashmap(resourcesDirectory, outputDirectoryPath);
  }

  def calculateAdvModifiedAdjRatio(adjToSearch: String): Double = {

    var myratio: Double = 0.005;

    //read from the frequency file of adverb modified//    For a given adjective (string input),
    //For any given adjective (string input),

    var adverbModifiedCounter = 0.000000005;
    println("reaching here at 345345");
    try {

      if (hashMapOfAdvModifiedAdjCount.contains(adjToSearch)) {
        println("reaching here at 53573687");
        println("reaching here at 34345 . value of base form is:"+adjToSearch)
        adverbModifiedCounter = hashMapOfAdvModifiedAdjCount(adjToSearch);
        println("reaching here at 52577676. value of adverbModifiedCounter is:"+ adverbModifiedCounter)
      }
      else {
        //throw new CustomException("Given adjective is not found in the file.")
        println("not able to find the given adjective");
      }
    }
    catch {

      case ex: Exception => println("An exception occoured.:\n" + ex.getStackTrace.mkString("\n"))
    }
     println("value of total times the word " + adjToSearch + " is modified by an adverb is" + adverbModifiedCounter)

    //the denominator remains same for all ratios. This will be filled by now, hopefully
    println("value of total times the word " + adjToSearch + " occurs is" + denominatorOfRatio)

    var advModifiedratio: Double = adverbModifiedCounter / denominatorOfRatio;

    return advModifiedratio;
  }

  def calculateBothInflectedAdvModifiedRatio(adjToCheck:String): Double = {

    var myratio: Double = 0.0000001;
    println("reaching here at 208763")
    var adverbModifiedInflectedCounter = 0.000000005;
    //if the given adjective is self inflected, check if its modified by an adverb also
    if (hashMapOfInflectedAdjectivesAndItsCount.contains(adjToCheck))
    {
      println("reaching here at 2553863876")
      if (hashMapOfAdvModifiedAdjCount.contains(adjToCheck)) {
        println("found that the adjective" + adjToCheck + "is both modified by an adverb and self inflected")
        //System.exit(1)
      }
    }



    return myratio;
  }


  def readErRemovedFileAndIncreaseCounter(resourcesDirectory: String, outputDirectoryPath: String): Unit = {
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
        //println("reaching here at 234233")
        //get its base form. Check the count of base form. Increase the count value, write it to the new hashmap which contains all adjectives and its counter
        if (hashMapOfAllUniqAdjectivesInAgigaWithFrequency.contains(erEstRemovedForm)) {
          //println("reaching here at 34345 . value of base form is:"+erEstRemovedForm)
          var baseCounter = 0;
          baseCounter = hashMapOfAllUniqAdjectivesInAgigaWithFrequency(erEstRemovedForm).toInt;
          baseCounter = baseCounter + 1;
          hashMapOfAllAdjectivesAndItsCount += (erEstRemovedForm -> baseCounter);

          //hashmap for an  adjectives in its inflected form and its count. Note it will start from zero
          //if two adjectives has the same root form, increase teh counter by 2. I.e colder, and coldest, will have the same base forms-cold
          //so the inflectedCounter for Cold must increase+2. Because cold was inflected twice.Store that in another hashmap

          //so at the end of the day, you must have the value, which is a sum of the number of times colder appears,
          //and the number of times coldest appears.
          // if it exists, retrieve it, increase its counter value and write it back. Else just write 1
          //i.e very first time you encounter, coldest- initialize its value to zero. Now if you see colder again,
          //this value must increase. Note that the key here will be the base: cold
          var inflectedCounterForSameWord = 0;
          if (hashMapOfInflectedAdjectivesAndItsCount.contains(erEstRemovedForm)) {
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
        if (hashMapOfAllAdjectivesAndItsCount.contains(adjToCheck)) {
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

  def writeToFile(stringToWrite: String, outputFilename: String, outputDirectoryPath: String): Unit = {


    val outFile = new File(outputDirectoryPath, outputFilename)

    val bw = new BufferedWriter(new FileWriter(outFile))


    bw.write(stringToWrite)
    bw.close()


  }

  //read all adjective phrases and its count to a hash table
  def ReadAllAdvAdjectivesAndFrequencyToHashmap(resourcesDirectory: String, outputDirectoryPath: String): Unit = {
    // println("reaching here at 2342")

    //to get the relative path from resources file
    val advInputFile = new File(getClass.getClassLoader.getResource(FreqOfAdjAdv_withoutAgainAt).getPath)


    //read from all the adjectives and its frequency into a hash table

    //println("path of input file is:"+advInputFile)
    try {
      // println("reaching here at 45645")

      var loopCounter = 1;
      for (line <- Source.fromFile(advInputFile).getLines()) {
        //println("reaching here at 3462323")
        val content = line.split("\\s+");
        //println("reaching here at 5435435")
        loopCounter = loopCounter + 1;
        //if it already contains the word, increase its count by the present count. Else initiate it as present count

        if (content.length > 1) {
          //println("reaching here at 21304562")

          var countOfPresentAdj = content(0).toInt;

          //assuming the file has data of the form "234234  very much"- then content(2) should have the adjective we are
          //looking for
          var adjToCheck = content(2);


          if (hashMapOfAdvModifiedAdjCount.contains(adjToCheck)) {
            //  println("found that the given adjective:" + adjToCheck + " already exists in the hash map")

            //retreive the current value
            //println("value of its present counter is:" + countOfPresentAdj)
            var totalCount = hashMapOfAdvModifiedAdjCount(adjToCheck);
            totalCount = totalCount + countOfPresentAdj
            //println("value of total count is:" + totalCount)
            hashMapOfAdvModifiedAdjCount += (adjToCheck -> totalCount);
          }
          else {
            //println("found that the given adjective:" + adjToCheck + " does not already exists in the hash map")
            hashMapOfAdvModifiedAdjCount += (adjToCheck -> content(0).toInt);
            //println("value of its present counter is:" + countOfPresentAdj)
          }
        }
      }
      // println(hashMapOfAdvModifiedAdjCount.mkString("\n"));
      //println(hashMapOfAdvModifiedAdjCount("happy"))
    } catch {
      case ex: Exception => println("An exception happened.:" + ex.getStackTrace.mkString("\n"))
    }
  }

  def ReadAllAdjectivesAndFrequencyToHashmap(resourcesDirectory: String, outputDirectoryPath: String): Unit = {
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
