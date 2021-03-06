package agiga

import scala.util.matching.Regex
import util.control.Breaks._
import java.io.{BufferedWriter, File, FileWriter}
import scala.util;
import org.clulab.learning.{Datasets, PerceptronClassifier, RVFDataset, RVFDatum}
import org.clulab.struct.Counter
import org.apache.commons
import scala.io.Source
import scala.collection.immutable.ListMap
import scala.collection.mutable.ArrayBuffer
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
  val fileAdjPresentInBothClasses = "adjPresentInBothClasses.txt";

  // var hashMapOfAllUniqAdjectivesInAgigaWithFrequency = Map("Long" -> "1")

  //the denominator remains same for a given adjective for all the ratios
  var denominatorOfRatio: Double = 0.000000;


  var hashMapOfAllUniqAdjectivesInAgigaWithFrequency: Map[String, Int] = Map()
  var hashMapOfAllAdjectivesAndItsCount: Map[String, Int] = Map()
  var hashMapOfInflectedAdjectivesAndItsCount: Map[String, Int] = Map()
  var hashMapOfAdvModifiedAdjCount: Map[String, Int] = Map()
  var hashMapOfAdjInBothClasses: Map[String, String] = Map()


  //  def calculateNgramInflectedRatio(adjToGetRatio: String): Double = {
  //
  //    var myratio: Double = 0;
  //    var totalBaseCount: Double = 0
  //    var noOfTimesThisAdjInflected = 1;
  //
  //    //split it into ngrams
  //    var splitAdj = characterNgramSplitter(adjToGetRatio, 3)
  //    if (hashMapOfAllUniqAdjectivesInAgigaWithFrequency.contains(adjToGetRatio)) {
  //
  //      totalBaseCount = hashMapOfAllUniqAdjectivesInAgigaWithFrequency(adjToGetRatio).toDouble;
  //
  //      for (trigrams <- splitAdj) {
  //
  //        if (hashMapCharacterNgramsAndFrequency.contains(trigrams)) {
  //          noOfTimesThisAdjInflected = hashMapCharacterNgramsAndFrequency(trigrams).toInt;
  //        }
  //        myratio = noOfTimesThisAdjInflected.toDouble / totalBaseCount;
  //        println("ratio for the trigram:" + trigrams + " is:" + noOfTimesThisAdjInflected + "/" + totalBaseCount + "which is:" + myratio)
  //
  //      }
  //    }
  //
  ////this must be a arraybuffer of int values: i.e number of times col occurs, er$ occurs etc
  //    //todo:
  //    return myratio;
  //
  //
  //  }

  def checkIfExistsInAgiga(adjToCheckExists: String): Boolean = {
    if (hashMapOfAllUniqAdjectivesInAgigaWithFrequency.contains(adjToCheckExists)) {
      return true;
    }
    else {
      return false;
    }
  }

  //println("reaching here at 4393897")

  def calculateInflectedAdjRatio(adjToGetRatio: String): Double = {
    //println("reaching here at 4393897")
    var myratio: Double = 0;
    var totalBaseCount: Double = 0
    var noOfTimesThisAdjInflected = 0;
    println("---starting calculateInflectedAdjRatio. value of base form is:" + adjToGetRatio)


    //get the total number of times this adjective occurs in AGIGA.
    // //count from the total count hashmap:hashMapOfAllAdjectivesAndItsCount
    //if (hashMapOfAllAdjectivesAndItsCount.contains(adjToGetRatio))
    if (hashMapOfAllUniqAdjectivesInAgigaWithFrequency.contains(adjToGetRatio)) {
      // println("reaching here at 089345978")
      totalBaseCount = hashMapOfAllUniqAdjectivesInAgigaWithFrequency(adjToGetRatio).toDouble;
      println("found that the given adjective:" + adjToGetRatio + " exists in the hashMapOfAllUniqAdjectivesInAgigaWithFrequency and the number of times its modified in total is:" + totalBaseCount)

      //println("reaching here at 34522")
      //if the adjective exists get the inflected count from the inflected count hashmap:hashMapOfInflectedAdjectivesAndItsCount
      //var baseForm = adjToGetRatio.replaceAll("er", "")
      //baseForm = baseForm.replaceAll("est", "")
      if (hashMapOfInflectedAdjectivesAndItsCount.contains(adjToGetRatio)) {
        // println("reaching here at 9876")
        noOfTimesThisAdjInflected = hashMapOfInflectedAdjectivesAndItsCount(adjToGetRatio)
        //println("found that the given adjective:" + adjToGetRatio + " exists in the hashMapOfInflectedAdjectivesAndItsCount and its  value is" + noOfTimesThisAdjInflected)
        println("found that the given adjective:" + adjToGetRatio + " is inflected so many times: " + noOfTimesThisAdjInflected)
      }
      else {
        println("the given adjective:" + adjToGetRatio + " is not ever inflected")
      }
    }
    else {
      //if the adjective doesnt exist, return a dummy value- no point continuing---dont add it to the loop...so break?
      // -i.e we encounter an adjective which is there in cobuild but not there in agiga
      println("the given adjective:" + adjToGetRatio + " does not exist in the hashMapOfAllUniqAdjectivesInAgigaWithFrequency")
      return 0;
    }
    //println("reaching here at 347234")

    //note: this value is the sum of number of times cold occurs by itself + number of times its inflected+number of times its modified by an adverb
    denominatorOfRatio = totalBaseCount.toDouble
    //println("value of this denominatorOfRatio is:" + denominatorOfRatio.toDouble.toString())
    //println("value of this numerator  is:" + noOfTimesThisAdjInflected.toDouble.toString())
    myratio = noOfTimesThisAdjInflected.toDouble / denominatorOfRatio;
    //println("value of this ratio is:" + myratio.toDouble.toString())


    return myratio;

  }

  def triggerFunction(resourcesDirectory: String, outputDirectoryPath: String): Unit = {

    //FindNgramCharacterFrequency(resourcesDirectory, outputDirectoryPath)
    ReadAllAdjectivesAndFrequencyToHashmap(resourcesDirectory, outputDirectoryPath);
    readErRemovedFileAndIncreaseCounter(resourcesDirectory, outputDirectoryPath);
    ReadAllAdvAdjectivesAndFrequencyToHashmap(resourcesDirectory, outputDirectoryPath);
    ReadCommonAdjInBothClassesToHashmap(resourcesDirectory, outputDirectoryPath, fileAdjPresentInBothClasses)

  }

  def isAdjPresentInBothClasses(adjToSearch: String): Boolean = {
    if (hashMapOfAdjInBothClasses.contains(adjToSearch))
      return true
    else
      return false;
  }


  def calculateAdvModifiedAdjRatio(adjToSearch: String): Double = {

    var myratio: Double = 0.000;

    //read from the frequency file of adverb modified//    For a given adjective (string input),
    //For any given adjective (string input),

    var adverbModifiedCounter = 1;
    //println("reaching here at 345345");
    try {

      if (hashMapOfAdvModifiedAdjCount.contains(adjToSearch)) {
        //println("reaching here at 53573687");
        println("---starting calculateAdvModifiedAdjRatio. value of base form is:" + adjToSearch)
        adverbModifiedCounter = hashMapOfAdvModifiedAdjCount(adjToSearch);
        println("number of times this adjective was modified by an adverb is is:" + adverbModifiedCounter)
      }
      else {
        //throw new CustomException("Given adjective is not found in the file.")
        println("not able to find the given adjective");
      }
    }
    catch {

      case ex: Exception => println("An exception occoured.:\n" + ex.getStackTrace.mkString("\n"))
        System.exit(1);
    }

    //the denominator remains same for all ratios. This will be filled by now, hopefully
    println("value of total times the word " + adjToSearch + " occurs in AGIGA is" + denominatorOfRatio)

    // At this point if denominatorOfRatio=0 , it means there are adjectives,
    // which are not inflected, but only modified by adverbs...
    // assign denominatorOfRatio= same as adverbModifiedCounter- because it is atleast modified so many times now
    //denominatorOfRatio= adverbModifiedCounter
    var advModifiedratio: Double = 0;
    //if it the given adjective doesnt exist in agiga, THE DENOMinator will be zero. So dont divide
    if (denominatorOfRatio > 0) {
      advModifiedratio = adverbModifiedCounter / denominatorOfRatio;
    } else {
      advModifiedratio = 0
    }
    return advModifiedratio;
  }

  def calculateBothInflectedAdvModifiedRatio(adjToCheck: String): Double = {

    //aim: find phrases like "much colder" where its both self inflected and also modified by adverb

    var advInflModifiedratio: Double = 0.0;
    println("---starting calculateBothInflectedAdvModifiedRatio. value of base form is:" + adjToCheck)

    var adverbModifiedInflectedCounter = 0.0;
    //if the given adjective is self inflected, check if its modified by an adverb also

    //go through the Colder->cold hashmap and find the er version of given adjective: i.e cold
    //get both versions of cold: coldest and colder.
    // var inflectedAndModifiedCount=0;
    var totalInflectedAndModifiedCount = 0;
    for ((key, value) <- goodAdjectiveFinder.hashMapOfInflAdjToRootForm) {
      if (value == adjToCheck)
      //now for each of these versions (Eg: colder, coldest), see if its present in hashMapOfAdvModifiedAdjCount
      //if yes, pick its count.
      {
        printf("key: %s, value: %s\n", key, value)
        if (hashMapOfAdvModifiedAdjCount.contains(key)) {
          val inflectedAndModifiedCount = hashMapOfAdvModifiedAdjCount(key);
          println("found that the adjective inflected:" + key + "is both modified by an adverb and self inflected and its frequency is:" + inflectedAndModifiedCount)
          totalInflectedAndModifiedCount = totalInflectedAndModifiedCount + inflectedAndModifiedCount;
        }
      }
    }

    //the denominator remains same for all ratios. This will be filled by now, hopefully
    println("value of total times the word " + adjToCheck + " occurs is" + denominatorOfRatio)
    println("found that the adjective inflected:" + adjToCheck + "is both modified by an adverb and self inflected and its frequency is:" + totalInflectedAndModifiedCount)

    if (denominatorOfRatio > 0) {
      advInflModifiedratio = totalInflectedAndModifiedCount / denominatorOfRatio
    }
    else {
      advInflModifiedratio = 0
    }


    println("ratio is:" + advInflModifiedratio)

    return advInflModifiedratio;
  }

  def characterNgramSplitter(adjectiveToCalculate: String, noOfgrams: Int): List[String] = {

    //attach a special character. this is to make sure there is no est in the word itself
    var modifiedAdjectiveToCalculate = "$" + adjectiveToCalculate + "$"
    var splitChars = modifiedAdjectiveToCalculate.sliding(3).toList

    return splitChars;


  }

  def readErRemovedFileAndIncreaseCounter(resourcesDirectory: String, outputDirectoryPath: String): Unit = {

    //read all lines of adjectives that end in er or est. Note: this is the actual number of times that adjective
    // occurs, not unique.
    val adjWithErEstEnding = resourcesDirectory + AllErEstEndingAdjectives;

    println("path of input file is:" + adjWithErEstEnding)
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
            erEstRemovedForm = erEstRemovedForm.dropRight(1)
            erEstRemovedForm = erEstRemovedForm + "y"
          }
          case None =>
          //println("pattern not found")
        }

        //convert fattest->fat
        //find repeating letters repeating twice at the end
        val fatPattern = new Regex("(\\w)\\1+$")
        val match2 = fatPattern.findFirstIn(erEstRemovedForm)
        match2 match {
          case Some(s) => {
            //println(s"Found: $s")
            //remove that last repeating letter Eg:Fatt->fat
            erEstRemovedForm = erEstRemovedForm.dropRight(1)
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
            // if it exists, retrieve it, increase its counter value and write it back. Else just write 1
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
      case ex: Exception => println("Exception occured:" + ex.toString())

    }

    //println("reaching here at 79578")


    //println("value of hashmap is:" + hashMapOfAllUniqAdjectivesInAgigaWithFrequency.mkString("\n "));
    //writeToFile(hashMapOfAllAdjectivesAndItsCount.mkString("\n"),outputFileNameForAllAdjectiveCount,outputDirectoryPath)
    // println("reaching here at 9217468")

    //System.exit(1);

    // println("value of hashmap is:" + hashMapOfInflectedAdjectivesAndItsCount.mkString("\n "));
    //write the inflected value count also to file
    writeToFile(hashMapOfInflectedAdjectivesAndItsCount.mkString("\n"), outputFileNameForInflectedAdjectiveCount, outputDirectoryPath)

  }



  def appendToFile(stringToWrite: String, outputFilename: String, outputDirectoryPath: String): Unit = {

    val outFile = new File(outputDirectoryPath, outputFilename)
    val bw = new BufferedWriter(new FileWriter(outFile, true))
    bw.write(stringToWrite)
    bw.close()
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

    println("path of input file is:" + advInputFile)
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
        System.exit(1);
    }
  }


  //  def FindNgramCharacterFrequency(resourcesDirectory: String, outputDirectoryPath: String): Unit = {
  //    //read from all the adjectives and its frequency into a hash table
  //    //val advInputFile = resourcesDirectory + completeAgigaFileWithFrequency;
  //    val adjWithErEstEndingInputFile = resourcesDirectory + AllErEstEndingAdjectives;
  //    try {
  //
  //      //for each adjective-split it to trigrams
  //      for (line <- Source.fromFile(adjWithErEstEndingInputFile).getLines()) {
  //        //println("reaching here at 3462323")
  //        if (!line.isEmpty()) {
  //          var splitAdj = characterNgramSplitter(line,3)
  //
  //          for(trigrams<-splitAdj)
  //          {
  //            var inflectedCounterForSameWord = 0;
  //            if (hashMapCharacterNgramsAndFrequency.contains(trigrams)) {
  //              inflectedCounterForSameWord = hashMapCharacterNgramsAndFrequency(trigrams).toInt;
  //              inflectedCounterForSameWord = inflectedCounterForSameWord + 1;
  //            }
  //            else {
  //              // if it exists, retrieve it, increase its counter value and write it back. Else just write 1
  //              inflectedCounterForSameWord = 1
  //            }
  //            hashMapCharacterNgramsAndFrequency += (trigrams -> inflectedCounterForSameWord);
  //
  //
  //          }
  //
  //
  //        }
  //      }
  //
  //      //sort it by the most common trigrams
  //      var sortedhashMapCharacterNgramsAndFrequency= ListMap(hashMapCharacterNgramsAndFrequency.toSeq.sortWith(_._2 < _._2):_*)
  //      //println(sortedhashMapCharacterNgramsAndFrequency.mkString("\n"));
  //
  //    } catch {
  //      case ex: Exception => println("An exception happened.:" + ex.getStackTrace.mkString("\n"))
  //    }
  //  }
  //
  //
  def FindNgramCharacterFrequencyGivenAdjective(adjForNgram: String): Map[String, Double] = {

    //check for ratio of 3 forms of cold, colder, coldest
    //go through the Colder->cold hashmap and find the er version of given adjective: i.e cold
    //get both versions of cold: coldest and colder.



    //store all 3 forms of this base form into an array of strings.
    var all3InflectedForms = ArrayBuffer[String]()

    all3InflectedForms += adjForNgram

    //note this hashMapOfInflAdjToRootForm is in the form colder->cold
    //println(goodAdjectiveFinder.hashMapOfInflAdjToRootForm.mkString("\n"))
    for ((key, value) <- goodAdjectiveFinder.hashMapOfInflAdjToRootForm) {
      if (value == adjForNgram) {
        all3InflectedForms += key
      }
    }

    //by now, the arraybfufer all3InflectedForms should have [cold, colder, coldest]

    //for each of these value increase count in hashMapCharacterNgramsAndFrequency
    var hashMapCharacterNgramsAndFrequency: Map[String, Double] = Map()

    var totalTrigramCount=0;
    val denominatorForNgram:Double=3.00000;

    val testNr=2
    var test = 1/denominatorForNgram;
    var test2 = 2/denominatorForNgram;
    var test3 =testNr/denominatorForNgram

    for (adjForms <- all3InflectedForms) {
      //now for each of these versions (Eg: colder, coldest),create trigrams
      //so for adjToCheckD=cold this if will fire twice: once for colder, and once for coldest
      {
        val splitAdj = characterNgramSplitter(adjForms, 3)

        //for each of the trigram,($co, old, col)-increase count
        for (trigrams <- splitAdj) {
          totalTrigramCount=totalTrigramCount+1
          var inflectedCounterForSameWord: Double = 0
          if (hashMapCharacterNgramsAndFrequency.contains(trigrams)) {
            inflectedCounterForSameWord = hashMapCharacterNgramsAndFrequency(trigrams).toInt;
            inflectedCounterForSameWord = inflectedCounterForSameWord + 1;
          }
          else {
            // if it exists, retrieve it, increase its counter value and write it back. Else just write 1
            inflectedCounterForSameWord = 1
          }
          hashMapCharacterNgramsAndFrequency += (trigrams -> (inflectedCounterForSameWord/denominatorForNgram));
        }
      }
    }


    return hashMapCharacterNgramsAndFrequency;
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
        System.exit(1);
    }
  }

  def ReadCommonAdjInBothClassesToHashmap(resourcesDirectory: String, outputDirectoryPath: String, fileToRead: String): Unit = {
    //println("reaching here at 36857")

    //read from all the adjectives and its frequency into a hash table
    val advInputFile = resourcesDirectory + fileToRead;
    try {
      //println("reaching here at 1263. value of the file path is:" + advInputFile)
      for (line <- Source.fromFile(advInputFile).getLines()) {
        //println("reaching here at 3462323")
        if (!line.isEmpty()) {
          val content = line.split("\\s+")
          val columnCount = content.length

          //add if 2nd column exists add it to hashmap as value, else initialize value to 1
          if (columnCount > 1) {
            hashMapOfAdjInBothClasses += (content(0) -> content(1));
          }
          else {
            hashMapOfAdjInBothClasses += (content(0) -> "1");
          }
        }
        //println(hashMapOfAllUniqAdjectivesInAgigaWithFrequency.mkString("\n"));
      }
    }

    catch {
      case ex: Exception => println("An exception happened.:" + ex.getStackTrace.mkString("\n"))
        System.exit(1);
    }

  }


}
