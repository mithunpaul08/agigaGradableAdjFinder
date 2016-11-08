package agiga

import scala.util.matching.Regex
import util.control.Breaks._
import java.io.{BufferedWriter, File, FileWriter}

import org.clulab.learning.{Datasets, PerceptronClassifier, RVFDataset, RVFDatum}
import org.clulab.struct.Counter

import scala.io.Source

/**
  * Created by mithunpaul on 10/14/16.
  */
object ratioCalculator {


  var AllErEstEndingAdjectives = "AllErEstEndingAdjectives.txt"
  var completeAgigaFileWithFrequency = "allAdjCombined_withWordCount.txt";
  var outputFileNameForAllAdjectiveCount = "hashmapForAllAdjectivesAndItsCount.txt";
  var outputFileNameForInflectedAdjectiveCount = "hashmapForAllAdjectivesAndItsCount.txt";
  var allAdjectivesFromAgigaButUniq = "allAdjectivesFromAgigaButUniq.txt"
  var uniqAdjectivesInAgiga_removedErEst_uniq = "uniqAdjectivesInAgiga_removedErEst_uniq.txt"
  var FreqOfAdjAdv_withoutAgainAt = "FreqOfAdjAdv_withoutAgainAt.txt";

  // var hashMapOfAllUniqAdjectivesInAgigaWithFrequency = Map("Long" -> "1")

  //the denominator remains same for a given adjective for all the ratios
  var denominatorOfRatio: Double = 0.000000;

  var hashMapOfAllUniqAdjectivesInAgigaWithFrequency: Map[String, Int] = Map()
  var hashMapOfAllAdjectivesAndItsCount: Map[String, Int] = Map()
  var hashMapOfInflectedAdjectivesAndItsCount: Map[String, Int] = Map()
  var hashMapOfAdvModifiedAdjCount: Map[String, Int] = Map()

  def calculateInflectedAdjRatio(adjToGetRatio: String): Double = {
    //println("reaching here at 4393897")
    var myratio: Double = 0;
    var totalBaseCount: Double = 0
    var noOfTimesThisAdjInflected = 1;
    println("---starting calculateInflectedAdjRatio. value of base form is:"+adjToGetRatio)


    //get the total number of times this adjective occurs in AGIGA.
    // //count from the total count hashmap:hashMapOfAllAdjectivesAndItsCount
    //if (hashMapOfAllAdjectivesAndItsCount.contains(adjToGetRatio))
      if (hashMapOfAllUniqAdjectivesInAgigaWithFrequency.contains(adjToGetRatio)) {
       // println("reaching here at 089345978")
        totalBaseCount = hashMapOfAllUniqAdjectivesInAgigaWithFrequency(adjToGetRatio).toDouble;
        println("found that the given adjective:" + adjToGetRatio + " exists in the hashMapOfAllUniqAdjectivesInAgigaWithFrequency and its base value is:" + totalBaseCount)

        //println("reaching here at 34522")
        //if the adjective exists get the inflected count from the inflected count hashmap:hashMapOfInflectedAdjectivesAndItsCount
        //var baseForm = adjToGetRatio.replaceAll("er", "")
        //baseForm = baseForm.replaceAll("est", "")
        if (hashMapOfInflectedAdjectivesAndItsCount.contains(adjToGetRatio)) {
         // println("reaching here at 9876")
          noOfTimesThisAdjInflected = hashMapOfInflectedAdjectivesAndItsCount(adjToGetRatio)
          println("found that the given adjective:" + adjToGetRatio + " exists in the hashMapOfInflectedAdjectivesAndItsCount and its  value is" + noOfTimesThisAdjInflected)
        }
      }
      else {
        //if the adjective doesnt exist, return a dummy value- no point continuing---dont add it to the loop...so break?
        println("the given adjective:" + adjToGetRatio + " does not exist in the hashMapOfAllUniqAdjectivesInAgigaWithFrequency")
        return 0;
      }
    //println("reaching here at 347234")

    //note: this value is the sum of number of times cold occurs by itself + number of times its inflected
    denominatorOfRatio = totalBaseCount.toDouble
    //println("value of this denominatorOfRatio is:" + denominatorOfRatio.toDouble.toString())
    //println("value of this numerator  is:" + noOfTimesThisAdjInflected.toDouble.toString())
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

    var adverbModifiedCounter = 1;
    //println("reaching here at 345345");
    try {

      if (hashMapOfAdvModifiedAdjCount.contains(adjToSearch)) {
        //println("reaching here at 53573687");
        println("---starting calculateAdvModifiedAdjRatio. value of base form is:"+adjToSearch)
        adverbModifiedCounter = hashMapOfAdvModifiedAdjCount(adjToSearch);
        println("number of times this adjective was modified by an adverb is is:"+ adverbModifiedCounter)
      }
      else {
        //throw new CustomException("Given adjective is not found in the file.")
        println("not able to find the given adjective");
      }
    }
    catch {

      case ex: Exception => println("An exception occoured.:\n" + ex.getStackTrace.mkString("\n"))
    }

    //the denominator remains same for all ratios. This will be filled by now, hopefully
    println("value of total times the word " + adjToSearch + " occurs in AGIGA is" + denominatorOfRatio)

    var advModifiedratio: Double = adverbModifiedCounter / denominatorOfRatio;

    return advModifiedratio;
  }

  def calculateBothInflectedAdvModifiedRatio(adjToCheck:String): Double = {

    //aim: find phrases like "much colder" where its both self inflected and also modified by adverb

    var advInflModifiedratio: Double = 0.0;
    println("---starting calculateBothInflectedAdvModifiedRatio. value of base form is:"+adjToCheck)

    var adverbModifiedInflectedCounter = 0.0;
    //if the given adjective is self inflected, check if its modified by an adverb also

    //go through the Colder->cold hashmap and find the er version of given adjective: i.e cold
    //get both versions of cold: coldest and colder.
   // var inflectedAndModifiedCount=0;
    var totalInflectedAndModifiedCount=0;
    for ((key,value) <-goodAdjectiveFinder.hashMapOfInflAdjToRootForm) {
      if (value == adjToCheck)

      //now for each of these versions (Eg: colder, coldest), see if its present in hashMapOfAdvModifiedAdjCount
      //if yes, pick its count.
      {
        printf("key: %s, value: %s\n", key, value)
        if (hashMapOfAdvModifiedAdjCount.contains(key)) {
          val inflectedAndModifiedCount = hashMapOfAdvModifiedAdjCount(key);
          println("found that the adjective inflected:" + key + "is both modified by an adverb and self inflected and its frequency is:" + inflectedAndModifiedCount)
          //System.exit(1)
          totalInflectedAndModifiedCount=totalInflectedAndModifiedCount+inflectedAndModifiedCount;
        }
      }
    }

    //the denominator remains same for all ratios. This will be filled by now, hopefully
    println("value of total times the word " + adjToCheck + " occurs is" + denominatorOfRatio)
    println("found that the adjective inflected:" + adjToCheck + "is both modified by an adverb and self inflected and its frequency is:" +totalInflectedAndModifiedCount )

     advInflModifiedratio= totalInflectedAndModifiedCount / denominatorOfRatio;
    println("ratio is:" +advInflModifiedratio )

    return advInflModifiedratio;
  }


  def readErRemovedFileAndIncreaseCounter(resourcesDirectory: String, outputDirectoryPath: String): Unit = {

    //read all lines of adjectives that end in er or est. Note: this is the actual number of times that adjective
    // occurs, not unique.
     val adjWithErEstEnding = resourcesDirectory + AllErEstEndingAdjectives;

    println("path of input file is:"+adjWithErEstEnding)
    var adjToCheck = "NULL";
    try {


      //var counterForHashmap = 0;
      for (line <- Source.fromFile(adjWithErEstEnding).getLines()) {

        adjToCheck = line;
        //do the -er and -est removal in scala itself
        //note: ideally we should pass it through the coldest->cold hashmap to get the base value. This is a wrong method
        var erEstRemovedForm = adjToCheck.replaceAll("er", "")
        erEstRemovedForm = erEstRemovedForm.replaceAll("est", "")

        //convert happier->happy
        val numPattern = new Regex(".*i$")
        val match1 = numPattern.findFirstIn(erEstRemovedForm)
        match1 match {
          case Some(s) => {
            //println(s"Found: $s")
            erEstRemovedForm=erEstRemovedForm.dropRight(1)
            erEstRemovedForm=erEstRemovedForm+"y"
          }
          case None =>
            //println("pattern not found")
        }

        //convert fattest->fat
        //find repeating letters repeating twice at the end
        val fatPattern= new Regex("(\\w)\\1+$")
        val match2 = fatPattern.findFirstIn(erEstRemovedForm)
        match2 match {
          case Some(s) => {
            //println(s"Found: $s")
            //remove that last repeating letter Eg:Fatt->fat
            erEstRemovedForm=erEstRemovedForm.dropRight(1)
          }
          case None =>
            //println("pattern not found")
        }



        //This is where we are increasing the count for base form. i.e cold might be already occuring say 434354 times in
        // the corpus. For every time we see colder, or coldest, we need to increase that count by one. In this code here,
        // we check if the base form exists in the hash map hashMapOfAllUniqAdjectivesInAgigaWithFrequency- which we had
        //filled in last step. If it exists, get its current count, and increase by one.
        if (hashMapOfAllUniqAdjectivesInAgigaWithFrequency.contains(erEstRemovedForm)) {
          //println("reaching here at 34345 . value of base form is:"+erEstRemovedForm)
          var baseCounter = 0;
          baseCounter = hashMapOfAllUniqAdjectivesInAgigaWithFrequency(erEstRemovedForm).toInt;
          //println("reaching here at 234234 . value of base form  frequency is:"+baseCounter)
          baseCounter = baseCounter + 1;


          hashMapOfAllUniqAdjectivesInAgigaWithFrequency += (erEstRemovedForm -> baseCounter);


          //the parent big hashmap which has ALLLLL adjectives (hashMapOfAllUniqAdjectivesInAgigaWithFrequency),
          // now gets split into two: viz., one hashmap
          //which has base forms and its count (hashMapOfAllUniqAdjectivesInAgigaWithFrequency) -note: This is the same
          // as the parent hashmap...also this is
          // the number of times
          //cold occurs in its base form plus number of times it was inflected. This becomes the denominator of the big
          //ratio henceforth
          //and another hashmap which has ONLY the inflected count:hashMapOfInflectedAdjectivesAndItsCount

          //if two adjectives has the same root form, increase teh counter by 1. I.e colder, and coldest,
          // will have the same base forms-cold
          //so the inflectedCounter for Cold must increase+1 every time such an inflection happened.
          // Store that in another hashmap

          //so at the end of the day, you must have the value, which is a sum of the number of times colder appears,
          //and the number of times coldest appears.
          // if it exists, retrieve it, increase its counter value and write it back. Else just write 1
          //i.e very first time you encounter, coldest- initialize its value to 1. Now if you see colder again,
          //this value must increase. Note that the key here will be the base: cold

          //in other words, the hashmap:hashMapOfInflectedAdjectivesAndItsCount, is simply the number of times the
          //base word, cold, was inflected.

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
//        if (hashMapOfAllAdjectivesAndItsCount.contains(adjToCheck)) {
//          inflectedCounter = hashMapOfAllAdjectivesAndItsCount(adjToCheck).toInt;
//          inflectedCounter = inflectedCounter + 1;
//        }
//        else {
//          inflectedCounter = 1
//        }
//        hashMapOfAllAdjectivesAndItsCount += (adjToCheck -> inflectedCounter);

      }
    } catch {
      case ex: Exception => println("Exception occured:"+ex.toString())

    }

     //println("reaching here at 79578")


    //println("value of hashmap is:" + hashMapOfAllUniqAdjectivesInAgigaWithFrequency.mkString("\n "));
    //writeToFile(hashMapOfAllAdjectivesAndItsCount.mkString("\n"),outputFileNameForAllAdjectiveCount,outputDirectoryPath)
    // println("reaching here at 9217468")

    //System.exit(1);

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

    //this file must be present in the testbed directory in jenny- didnt want to move 1gb file onto github
    val advInputFile = resourcesDirectory + FreqOfAdjAdv_withoutAgainAt;

    //to get the relative path from resources file
   // val advInputFile = new File(getClass.getClassLoader.getResource(FreqOfAdjAdv_withoutAgainAt).getPath)


    //read from all the adjectives and its frequency into a hash table

  println("path of input file is:"+advInputFile)
    try {
      println("reaching here at 45645")

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
      println("reaching here at 1263. value of the file path is:" + advInputFile)

      for (line <- Source.fromFile(advInputFile).getLines()) {
        //println("reaching here at 3462323")
        if (!line.isEmpty()) {
          val content = line.split("\\s+");
          //println("value of content0 is:" + content(0))
          //println("value of content1 is:" + content(1))
          val frequency: Int = content(0).toInt;

          if (content.length > 1) {

            hashMapOfAllUniqAdjectivesInAgigaWithFrequency += (content(1) -> frequency);
          }

          //println(hashMapOfAllUniqAdjectivesInAgigaWithFrequency.mkString("\n"));
        }
      }
    } catch {
      case ex: Exception => println("An exception happened.:" + ex.getStackTrace.mkString("\n"))
    }
  }
}
