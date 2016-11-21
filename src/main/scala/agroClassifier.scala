package agiga

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.slf4j.LoggerFactory
import org.clulab.agiga
import org.clulab.processors.Document
import java.io.File
import scala.util.Random
import scala.collection.parallel.ForkJoinTaskSupport
import java.io
import java.io.Writer
import java.io.FileWriter
import java.io.BufferedWriter
import java.util.Arrays
import java.io.File
import java.io.IOException
import java.util.Arrays
import java.util.Comparator

import org.clulab.learning._

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.io.Source
import org.clulab.struct
import org.clulab.struct.Counter
import org.clulab.learning.Datasets

import util.control.Breaks._
import org.clulab.discourse.rstparser


object classifierForAgro {
  var outputFileNameForAllAdjectiveCount = "hashmapForAllAdjectivesAndItsCount.txt";
  var outputFileNameForInflectedAdjectiveCount = "hashmapForAllAdjectivesAndItsCount.txt";

  var allAdjectivesFromAgigaButUniq = "allAdjectivesFromAgigaButUniq.txt"

  var uniqAdjectivesInAgiga_removedErEst_uniq = "uniqAdjectivesInAgiga_removedErEst_uniq.txt"
  var GMCombined_Uniq = "GMCombined_Uniq"

  var GPCombined_Uniq = "GPCombined_Uniq"

  //path in laptop
  //var resourcesDirectory = "/Users/mithun/agro/agigaGradableAdjFinder/src/main/resources/"

  //path in chung.cs.arizona.edu
  var resourcesDirectory = "src/main/resources/"

  //var outputDirectoryPath = "/Users/mithun/agro/agigaGradableAdjFinder/src/main/outputs/"

  var outputDirectoryPath = "./src/main/outputs/"
  var erRemovedFiles = "AllErEstEndingAdjectivesUniq.txt"

  var completeAgigaFileWithFrequency = "allAdjCombined_withWordCount.txt";

  var outputFileForPredictedLabels = "outputFileForPredictedLabels.txt";


  def initializeAndClassify(runOnServer: Boolean, hashmapOfColderCold: Map[String, String]): Unit = {
    val counter = new Counter[String];
    val dataset = new RVFDataset[String, String]
    //var adjGoldPredicted = ArrayBuffer.fill(508,3)("")
    var adjGoldPredicted = ArrayBuffer.fill(509, 3)("")
    //var listOfAllAdjectives = ArrayBuffer[ArrayBuffer[String,String]]()
    var listOfAllAdjectives = ArrayBuffer.fill(1, 2)("");





    //when on jenny we want all the files to come from the testbed folder, and not resources folder. Because the files in
    //resources folder are just smaller subset versions of the actual files, which are really huge in JENNY

    //var getCurrentDirectory = new java.io.File(".").getCanonicalPath
    //println("value of present directory is: "+getCurrentDirectory)
    if (runOnServer) {
      //resourcesDirectory = "./src/main/resources/"
      //resourcesDirectory = "~/testbed/"
      resourcesDirectory = "/work/mithunpaul/testbed/"


      outputDirectoryPath = "/work/mithunpaul/testbed/"
      //outputDirectoryPath = "~/testbed/"

      // var outputDirectoryPath = "..outputs/"
      erRemovedFiles = "AllErEstEndingAdjectivesUniq.txt"

      completeAgigaFileWithFrequency = "allAdjCombined_withWordCount.txt";


    }


    //The gradable adjective files provided by COBUILD had lots of overlapping adjectives. Hence combined them both and too uniq.
    // read through only this file

    val cobuildNonGradable = resourcesDirectory + GMCombined_Uniq;
    val cobuildGradable = resourcesDirectory + GPCombined_Uniq;


    println("reaching here at 9870987")

    //fill in the hashmaps. i.e the maps which has the count of base form adjectives (cold->2342342) and inflected
    //adjectives(colder/coldest:2344)


    ratioCalculator.triggerFunction(resourcesDirectory, outputDirectoryPath);
    //todo: find if we should pick from the top 300 adjectives

    var numberOfGoldGradable = 0;
    var numberOfGoldNonGradable = 0;
    var counterForAdjLabelMatrix = 0;


    //remove the initial empty element in listOfAllAdjectives

    listOfAllAdjectives.remove(0);
    println(listOfAllAdjectives.length)

    //for each of the adjectives in gradable COBUILD Auto, go through hash maps, get inflected count/total count ratio, add it to the clasifier
    for (adjToCheckG <- Source.fromFile(cobuildGradable).getLines()) {

      val adjLabelG = ArrayBuffer(adjToCheckG, "gradable")
      listOfAllAdjectives += adjLabelG
    }


    //for each of the adjectives in non gradable COBUILD Auto (list of non gradable adjectives auto generated), go through hash maps, get inflected count/total count ratio, add it to the clasifier
    for (adjToCheckNG <- Source.fromFile(cobuildNonGradable).getLines()) {

      //total number of gold non gradable adjectives

      counterForAdjLabelMatrix = counterForAdjLabelMatrix + 1;
      var inflRatio: Double = 0;
      var advrbModifiedRatio: Double = 0
      var inflAndAdvModified: Double = 0
      //      //build a tuple of [adjective, predictedLabel, ActualLabel]- used for checking status of each adjective Eg:happy
      val adjLabelNG = ArrayBuffer(adjToCheckNG, "notgradable")

      listOfAllAdjectives += adjLabelNG;
    }

    //to test without shuffling
    //val listOfAllAdjectivesShuffled= listOfAllAdjectives

    //not inplace shuffling
    val listOfAllAdjectivesShuffled = util.Random.shuffle(listOfAllAdjectives)

    val datasetForFillingMixedAdjCounterLabels = new RVFDataset[String, String]



    //for each of the adjectives, find its ratios and labels and add it into the dataset.
    for (individualAdjLabels <- listOfAllAdjectivesShuffled) {
      val adjToCheckD = individualAdjLabels(0);
      val labelOfGivenAdj = individualAdjLabels(1);
      //add only if the adj is not present in both files
      if (!ratioCalculator.isAdjPresentInBothClasses(adjToCheckD)) {

        val trigramCounts = ratioCalculator.FindNgramCharacterFrequencyGivenAdjective(adjToCheckD)

        //characterNgramSplitter(adjToCheckD)

        if (labelOfGivenAdj == "gradable") {
          numberOfGoldGradable = numberOfGoldGradable + 1
        }
        else if (labelOfGivenAdj == "notgradable") {
          numberOfGoldNonGradable = numberOfGoldNonGradable + 1
        }
        findRatiosOfGivenAdjectivesAndAddToDataset(adjToCheckD, labelOfGivenAdj, datasetForFillingMixedAdjCounterLabels)
      }
    }

    println(datasetForFillingMixedAdjCounterLabels.labels.length);

    //train the classifier
    println("\n")
    println("###############done with adding features. starting ten fold cross validation...");

    //    //Try with perceptron classifier
    //             def factory() = new PerceptronClassifier[String, String]
    //             println("doing PerceptronClassifier...");


    //code for LogisticRegressionClassifier with bias
    def factory() = new LogisticRegressionClassifier[String, String](bias = true)
    val myClassifier = new LogisticRegressionClassifier[String, String](bias = true)
    println("doing LogisticRegressionClassifier...");
    //myClassifier.train(dataset)
    myClassifier.train(datasetForFillingMixedAdjCounterLabels)



    //    //replacing myClassifier with LibSVMClassifier
    //    val myClassifier = new LibSVMClassifier[String, String](LinearKernel)
    //    def factory() = new LibSVMClassifier[String, String](LinearKernel)
    //    println("doing LibSVMClassifier...");
    //    myClassifier.train(dataset)


    //cant get weights for LibSVMClassifier
    val weights = myClassifier.getWeights()
    println("done with getting weights...");
    println(s"""Weights for the positive class: ${weights.get("gradable")}""")
    println(s"""Weights for the negative class: ${weights.get("notgradable")}""")

    //this returns a label of the type [predicted, original] Eg: [NON-GRADABLE, GRADABLE]
    //val predictedLabels = Datasets.crossValidate(dataset, factory, 10) // for 10-fold cross-validation

    //code for scaling only the 9 folds of training data set, and not the 1 fold of test dataset
    val predictedLabels = mithunsCrossValidate(datasetForFillingMixedAdjCounterLabels, factory, 10) // for 10-fold cross-validation
    //val predictedLabels = mithunsCrossValidate(dataset, factory, 10) // for 10-fold cross-validation


    //calculate acccuracy.
    //i.e number of times the labels match each other...divided by the total number, will be your accuracy
    var countForAdjArray = 0
    var totalCount: Double = 0;
    var countCorrectlyPredicted: Double = 0;
    for ((predictedLabel, actualLabel) <- predictedLabels) {
      totalCount = totalCount + 1;

      if (predictedLabel == actualLabel) {
        countCorrectlyPredicted = countCorrectlyPredicted + 1;

      }

      //build a tuple of [adjective, predictedLabel, ActualLabel]- used for checking status of each adjective Eg:happy

      //adjGoldPredicted(countForAdjArray) = predictedValuesToPrint
      countForAdjArray = countForAdjArray + 1


    }

    //println("value of adjGoldPredicted is: ")
    //println(adjGoldPredicted.mkString("\n"))
    //ratioCalculator.writeToFile(adjGoldPredicted.mkString("\n"), outputFileForPredictedLabels, outputDirectoryPath)

    val accuracy = (countCorrectlyPredicted / totalCount) * 100;
    // println("value of countCorrectlyPredicted is:"+countCorrectlyPredicted)
    // println("value of totalCount is:"+totalCount)
    println("value of accuracy is:" + accuracy + "%")
    println("value of numberOfGoldGradable is:" + numberOfGoldGradable)
    println("value of numberOfGoldNonGradable is:" + numberOfGoldNonGradable)

    //gus' code on accuracy
    val numFolds = 10

    val POS_CLASS = "gradable"
    val NEG_CLASS = "notgradable"

    // Calculate accuracy for each class
    // label pairs (GOLD, PREDICTED)
    val overallAccuracy = predictedLabels.count { case (g, p) => g == p } / predictedLabels.size.toDouble * 100
    val gradableClassAccuracy = predictedLabels.count { case (g, p) => (g == POS_CLASS) && (g == p) } / numberOfGoldGradable.toDouble * 100
    val nonGradableClassAccuracy = predictedLabels.count { case (g, p) => (g == NEG_CLASS) && (g == p) } / numberOfGoldNonGradable.toDouble * 100

    val accuracy1 =
      f"""
         |FOLDS:\t$numFolds
         |Number of '$POS_CLASS' class instances in dataset:\t${predictedLabels.count(_._1 == POS_CLASS)}
         |Number of '$NEG_CLASS' class instances in dataset:\t${predictedLabels.count(_._1 == NEG_CLASS)}
         |========================================
         |OVERALL ACCURACY:\t$overallAccuracy%3.2f
         |'$POS_CLASS' ACCURACY:\t$gradableClassAccuracy%3.2f
         |'$NEG_CLASS' ACCURACY:\t$nonGradableClassAccuracy%3.2f
     """.stripMargin

    println(accuracy1)

  }

  // Fisher-Yates shuffle, see: http://en.wikipedia.org/wiki/Fisher–Yates_shuffle
  def myShuffle[T](array: Array[T]): Array[T] = {
    val rnd = new java.util.Random
    for (n <- Iterator.range(array.length - 1, 0, -1)) {
      val k = rnd.nextInt(n + 1)
      val t = array(k); array(k) = array(n); array(n) = t
    }
    return array
  }


  def findRatiosOfGivenAdjectivesAndAddToDataset(myAdjToCheck: String, labelOfGivenAdj: String, datasetToAdd: RVFDataset[String, String]): Unit = {
    //for each given adjective find all 3 ratios, attach its corresponding label, and send back a full filled RVFdataset

    //println("reaching here at 57633")
    var inflectedRatio: Double = 0;
    var adverbModifiedRatio: Double = 0
    var inflectedAndAdvModified: Double = 0

    //if an adjective is present in cobuild but not in AGIGA. Low probability, but we should capture that
    //instance also. Because we encountered some edge cases in which this was true, and the denominator
    //was getting added as zero, resulting in division by zero.
    if (ratioCalculator.checkIfExistsInAgiga(myAdjToCheck)) {

      //for each of the adjectives' root forms, get the inflected ratio.
      inflectedRatio = ratioCalculator.calculateInflectedAdjRatio(myAdjToCheck);

      if (inflectedRatio > 0) {
        println("value of current adjective is :" + myAdjToCheck + " and its inflected ratio is:" + inflectedRatio)

      }

      else {
        //if the given adjective is not found, the return value will be zero. In that case

        println("current adjective :" + myAdjToCheck + " doesnt have an inflected ratio ")


      }

      adverbModifiedRatio = ratioCalculator.calculateAdvModifiedAdjRatio(myAdjToCheck);

      if (adverbModifiedRatio > 0) {
        println("value of current adjective is :" + myAdjToCheck + " and its adverb modified ratio is:" + adverbModifiedRatio)
      }
      else {
        //if the given adjective is not found, the return value will be zero. In that case
        // ignore it and move onto the next one. We dont want to add zeroes to the datum.

        println("current adjective :" + myAdjToCheck + " doesnt have an advrbModifiedRatio  ")


      }
      //for each of the adjectives' root forms, get the adverb and adjective modified ratio.
      inflectedAndAdvModified = ratioCalculator.calculateBothInflectedAdvModifiedRatio(myAdjToCheck);


      if (inflectedAndAdvModified > 0) {
        println("value of current adjective is :" + myAdjToCheck + " and its inflected and modified ratio is:" + inflectedAndAdvModified)
      }
      else {
        println("current adjective :" + myAdjToCheck + " doesnt have an inflAndAdvModified  ")

      }
    }
    val counter = new Counter[String];
    counter.setCount("inflectedRatio", inflectedRatio)
    counter.setCount("advrbModifiedRatio", adverbModifiedRatio)
    counter.setCount("inflAndAdvModified", inflectedAndAdvModified)

    println("printing the value of counter below me in double")
    //println(f"$counter(1)%1.5f")
    println(counter.toString())
    //val datum2 = new RVFDatum[String, String]("notgradable", counter)
    val datum2 = new RVFDatum[String, String](labelOfGivenAdj, counter);

    // println("number of features is:" + datum2.features())

    datasetToAdd += datum2
    println("reaching here at 2462467")


  }

  /**
    * Implements classic cross validation; producing pairs of gold/predicted labels across the training dataset
    */
  def mithunsCrossValidate[L, F](
                                  dataset: RVFDataset[L, F],
                                  classifierFactory: () => Classifier[L, F],
                                  numFolds: Int): Iterable[(L, L)] = {

    val folds = Datasets.mkFolds(numFolds, dataset.size)
    val output = new ListBuffer[(L, L)]
    for (fold <- folds) {

      //make a copy of unscaled dataset, and give it to testing. Then scale the rest and give it to training
      val unScaledDataset = dataset;
      val classifier = classifierFactory()
      //scale it before training, but not in testing.
      val scaleRanges = Datasets.svmScaleDataset(dataset, lower = -1, upper = 1)
      classifier.train(dataset, Some(fold.trainFolds))
      for (i <- fold.testFold._1 until fold.testFold._2) {
        val sys = classifier.classOf(unScaledDataset.mkDatum(i))
        val gold = unScaledDataset.labels(i)
        output += new Tuple2(unScaledDataset.labelLexicon.get(gold), sys)
      }
    }
    output.toList
  }
}


