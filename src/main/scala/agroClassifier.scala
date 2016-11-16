package agiga

import java.io._
import scala.collection.mutable.ArrayBuffer
import org.slf4j.LoggerFactory
import org.clulab.agiga
import org.clulab.processors.Document
import java.io.File

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
    var listOfAllAdjectives = ArrayBuffer[String]()





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
    var counterForAdjLabelMatrix = 0;


    //for each of the adjectives in gradable COBUILD Auto, go through hash maps, get inflected count/total count ratio, add it to the clasifier
    for (adjToCheck <- Source.fromFile(cobuildGradable).getLines()) {

      numberOfGoldGradable = numberOfGoldGradable + 1
      counterForAdjLabelMatrix = counterForAdjLabelMatrix + 1;

      //todo: read input from all agiga files

      var inflRatio: Double = 0;
      var advrbModifiedRatio: Double = 0
      var ngramInflectedRatio: Double = 0;


      println("reaching here at 876467")

      println("\n")
      println("****************************************");
      println("Starting a new gradable adjective check, whose value is : " + adjToCheck)



      //for each of the adjectives' root forms, get the inflected ratio.
      inflRatio = ratioCalculator.calculateInflectedAdjRatio(adjToCheck);

      if (inflRatio > 0) {
        println("value of current adjective is :" + adjToCheck + " and its inflected ratio is:" + inflRatio)

      }
      else {
        //if the given adjective is not found, the return value will be zero. In that case
        // ignore it and move onto the next one. We dont want to add zeroes to the datum.

        println("current adjective :" + adjToCheck + " doesnt exist in the database. Moving onto the next one")


      }
      ngramInflectedRatio = ratioCalculator.calculateNgramInflectedRatio(adjToCheck);

      //for each of the adjectives' root forms, get the adverb modified ratio.
      advrbModifiedRatio = ratioCalculator.calculateAdvModifiedAdjRatio(adjToCheck);
      if (advrbModifiedRatio > 0) {
        println("value of current adjective is :" + adjToCheck + " and its adverb modified ratio is:" + advrbModifiedRatio)
      }

      //for each of the adjectives' root forms, get the adverb and adjective modified ratio.
      var inflAndAdvModified: Double = ratioCalculator.calculateBothInflectedAdvModifiedRatio(adjToCheck);


      if (inflAndAdvModified > 0) {
        println("value of current adjective is :" + adjToCheck + " and its inflected and modified ratio is:" + inflAndAdvModified)
      }
      else {
        println("value of current adjective is :" + adjToCheck + " and its inflected and modified ratio is:" + inflAndAdvModified)
      }

      counter.setCount("inflectedRatio", inflRatio)
      counter.setCount("advrbModifiedRatio", advrbModifiedRatio)
      counter.setCount("inflAndAdvModified", inflAndAdvModified)
      println(counter.toString())
      
      val datum1 = new RVFDatum[String, String]("gradable", counter)
      dataset += datum1


      //      //build a tuple of [adjective, predictedLabel, ActualLabel]- used for checking status of each adjective Eg:happy
      //      var adjLabelBuilder = ArrayBuffer[String]()
      //      adjLabelBuilder += adjToCheck
      //      adjGoldPredicted += adjLabelBuilder

      listOfAllAdjectives += adjToCheck
      //adjGoldPredicted (counterForAdjLabelMatrix)(0) =adjToCheck
      //adjGoldPredicted (counterForAdjLabelMatrix)(0) =adjToCheck


      // val scaleRanges2 = Datasets.svmScaleDataset(dataset, lower = -1, upper = 1)
      // println("new value of ranges is:" + scaleRanges2.maxs.toString());

    }

    var numberOfGoldNonGradable = 0;
    //for each of the adjectives in non gradable COBUILD Auto (list of non gradable adjectives auto generated), go through hash maps, get inflected count/total count ratio, add it to the clasifier
    for (adjToCheck <- Source.fromFile(cobuildNonGradable).getLines()) {

      //total number of gold non gradable adjectives
      numberOfGoldNonGradable = numberOfGoldNonGradable + 1;
      counterForAdjLabelMatrix = counterForAdjLabelMatrix + 1;


      println("\n")
      println("****************************************");
      println("Starting a new non-gradable adjective check, whose value is : " + adjToCheck)
      //println("reaching here at 57633")
      var inflRatio: Double = 0;
      var advrbModifiedRatio: Double = 0
      var inflAndAdvModified: Double = 0

      //println("reaching here at 876467")
      //println("value of current adjective is :" + adjToCheck);

      //for each of the adjectives' root forms, get the inflected ratio.
      inflRatio = ratioCalculator.calculateInflectedAdjRatio(adjToCheck);

      if (inflRatio > 0) {
        println("value of current adjective is :" + adjToCheck + " and its inflected ratio is:" + inflRatio)

      }

      else {
        //if the given adjective is not found, the return value will be zero. In that case


        println("current adjective :" + adjToCheck + " doesnt have an inflected ratio ")


      }

      advrbModifiedRatio = ratioCalculator.calculateAdvModifiedAdjRatio(adjToCheck);

      if (advrbModifiedRatio > 0) {
        println("value of current adjective is :" + adjToCheck + " and its adverb modified ratio is:" + advrbModifiedRatio)
      }
      else {
        //if the given adjective is not found, the return value will be zero. In that case
        // ignore it and move onto the next one. We dont want to add zeroes to the datum.

        println("current adjective :" + adjToCheck + " doesnt have an advrbModifiedRatio  ")


      }
      //for each of the adjectives' root forms, get the adverb and adjective modified ratio.
      inflAndAdvModified = ratioCalculator.calculateBothInflectedAdvModifiedRatio(adjToCheck);


      if (inflAndAdvModified > 0) {
        println("value of current adjective is :" + adjToCheck + " and its inflected and modified ratio is:" + inflAndAdvModified)
      }
      else {
        println("current adjective :" + adjToCheck + " doesnt have an inflAndAdvModified  ")

      }

      counter.setCount("inflectedRatio", inflRatio)
      counter.setCount("advrbModifiedRatio", advrbModifiedRatio)
      counter.setCount("inflAndAdvModified", inflAndAdvModified)

      //println("printing the value of counter below me in double")
      //println(f"$counter%1.5f")
      println(counter.toString())
      val datum2 = new RVFDatum[String, String]("notgradable", counter)

      // println("number of features is:" + datum2.features())

      dataset += datum2
      println("reaching here at 2462467")

      //      //build a tuple of [adjective, predictedLabel, ActualLabel]- used for checking status of each adjective Eg:happy
      listOfAllAdjectives += adjToCheck
      //adjGoldPredicted (counterForAdjLabelMatrix)(0) =adjToCheck
      //      var adjLabelBuilder = ArrayBuffer[String]()
      //      adjLabelBuilder += adjToCheck
      //      adjGoldPredicted += adjLabelBuilder
    }




    //val scaleRanges = Datasets.svmScaleDataset(dataset, lower = -1, upper = 1)
    //    println("new max value of ranges is:" + scaleRanges.maxs.toString());
    //    println("new min value of ranges is:" + scaleRanges.mins.toString());

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
    myClassifier.train(dataset)



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
    val predictedLabels = mithunsCrossValidate(dataset, factory, 10) // for 10-fold cross-validation


    //calculate acccuracy.
    //i.e number of times the labels match each other...divided by the total number, will be your accuracy
    var countForAdjArray = 0
    var totalCount: Double = 0;
    var countCorrectlyPredicted: Double = 0;
    for ((predictedLabel, actualLabel) <- predictedLabels) {
      totalCount = totalCount + 1;

      //just store into an array of strings for printing purposes
      var predictedValuesToPrint = ArrayBuffer[String]()
      predictedValuesToPrint += listOfAllAdjectives(countForAdjArray)
      predictedValuesToPrint += actualLabel
      predictedValuesToPrint += predictedLabel

      if (predictedLabel == actualLabel) {
        countCorrectlyPredicted = countCorrectlyPredicted + 1;

      }

      //build a tuple of [adjective, predictedLabel, ActualLabel]- used for checking status of each adjective Eg:happy

      adjGoldPredicted(countForAdjArray) = predictedValuesToPrint
      countForAdjArray = countForAdjArray + 1


    }

    //println("value of adjGoldPredicted is: ")
    //println(adjGoldPredicted.mkString("\n"))
    ratioCalculator.writeToFile(adjGoldPredicted.mkString("\n"), outputFileForPredictedLabels, outputDirectoryPath)

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

  /**
    * Implements classic cross validation; producing pairs of gold/predicted labels across the training dataset
    */
  def mithunsCrossValidate[L, F](
                                  dataset: Dataset[L, F],
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


