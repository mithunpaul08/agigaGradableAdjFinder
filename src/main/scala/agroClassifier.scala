package agiga

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

import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import org.clulab.struct
import org.clulab.struct.Counter
import org.clulab.learning.Datasets
import util.control.Breaks._



object classifierForAgro {
  var outputFileNameForAllAdjectiveCount = "hashmapForAllAdjectivesAndItsCount.txt";
  var outputFileNameForInflectedAdjectiveCount = "hashmapForAllAdjectivesAndItsCount.txt";

  var allAdjectivesFromAgigaButUniq= "allAdjectivesFromAgigaButUniq.txt"

  var uniqAdjectivesInAgiga_removedErEst_uniq= "uniqAdjectivesInAgiga_removedErEst_uniq.txt"
  var GMCombined_Uniq="GMCombined_Uniq"

 var GPCombined_Uniq="GPCombined_Uniq"

  //path in laptop
  //var resourcesDirectory = "/Users/mithun/agro/agigaGradableAdjFinder/src/main/resources/"

  //path in chung.cs.arizona.edu
  var resourcesDirectory = "./src/main/resources/"

  //var outputDirectoryPath = "/Users/mithun/agro/agigaGradableAdjFinder/src/main/outputs/"

  var outputDirectoryPath = "./src/main/outputs/"
  var erRemovedFiles = "AllErEstEndingAdjectivesUniq.txt"

  var completeAgigaFileWithFrequency = "allAdjCombined_withWordCount.txt";


   def initializeAndClassify( runOnServer: Boolean, hashmapOfColderCold: Map[String, String]): Unit = {
     val counter = new Counter[String];
     val dataset = new RVFDataset[String, String]


     //when on jenny we want all the files to come from the testbed folder, and not resources folder. Because the files in
     //resources folder are just smaller subset versions of the actual files, which are really huge in JENNY

     //var getCurrentDirectory = new java.io.File(".").getCanonicalPath
     //println("value of present directory is: "+getCurrentDirectory)
     if(runOnServer)
       {
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

     //println("reaching here at 9870987")

     //fill in the hashmaps. i.e the maps which has the count of base form adjectives (cold->2342342) and inflected
     //adjectives(colder/coldest:2344)


     ratioCalculator.triggerFunction(resourcesDirectory,outputDirectoryPath);
     //todo: find if we should pick from the top 300 adjectives




     //for each of the adjectives in gradable COBUILD Auto, go through hash maps, get inflected count/total count ratio, add it to the clasifier
     for (adjToCheck <- Source.fromFile(cobuildGradable).getLines()) {
       //todo: read input from all agiga files

       var inflRatio: Double = 0;
       var advrbModifiedRatio: Double = 0


       //println("reaching here at 876467")
       //println("value of current adjective is :" + adjToCheck );


       //for each of the adjectives' root forms, get the inflected ratio.
       inflRatio = ratioCalculator.calculateInflectedAdjRatio(adjToCheck);

       if (inflRatio > 0) {
         println("value of current adjective is :" + adjToCheck + " and its inflected ratio is:" + inflRatio)



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

         counter.setCount("feature1", inflRatio)
         counter.setCount("feature2", advrbModifiedRatio)
         counter.setCount("feature3", inflAndAdvModified)
         val datum1 = new RVFDatum[String, String]("GRADABLE", counter)
         dataset += datum1
       }
       else {
         //if the given adjective is not found, the return value will be zero. In that case
         // ignore it and move onto the next one. We dont want to add zeroes to the datum.

         println("current adjective :" + adjToCheck + " doesnt exist in the database. Moving onto the next one")


       }

     }


     //for each of the adjectives in non gradable COBUILD Auto (list of non gradable adjectives auto generated), go through hash maps, get inflected count/total count ratio, add it to the clasifier
     for (adjToCheck <- Source.fromFile(cobuildNonGradable).getLines()) {
       //todo: read input from all agiga files

       println("reaching here at 4576")
       var inflRatio: Double = 0;
       var advrbModifiedRatio: Double = 0
       var inflAndAdvModified: Double = 0

       //println("reaching here at 876467")
       println("value of current adjective is :" + adjToCheck);

       //for each of the adjectives' root forms, get the inflected ratio.
       inflRatio = ratioCalculator.calculateInflectedAdjRatio(adjToCheck);

       if (inflRatio > 0) {
         println("value of current adjective is :" + adjToCheck + " and its inflected ratio is:" + inflRatio)



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

         counter.setCount("feature1", inflRatio)
         counter.setCount("feature2", advrbModifiedRatio)
         counter.setCount("feature3", inflAndAdvModified)

         println(counter.toString())
         val datum2 = new RVFDatum[String, String]("NOT GRADABLE", counter)
         //val datum2 = new RVFDatum[String, String]("NOT GRADABLE", counter)


         dataset += datum2
         println("reaching here at 2462467")
       }

       else {
         //if the given adjective is not found, the return value will be zero. In that case
         // ignore it and move onto the next one. We dont want to add zeroes to the datum.

         println("current adjective :" + adjToCheck + " doesnt exist in the database. Moving onto the next one")


       }

     }


     //train the classifier
     println("starting ten fold cross validation...");

     //val ranges = new Sc
     //the crossValidate needs a class of the classifier
    // def factory() = new PerceptronClassifier[String, String]

     //try with svm classifier
     //def factory() = new SVMClassifier[String, String](LinearKernel)
      def factory() = new LiblinearClassifier[String, String]
     println("doing LiblinearClassifier...");

     //this returns a label of the type [predicted, original] Eg: [NON-GRADABLE, GRADABLE]
     val predictedLabels = Datasets.crossValidate(dataset, factory, 10)  // for 10-fold cross-validation


     //calculate acccuracy.
     //i.e number of times the labels match each other...divided by the total number, will be your accuracy
     var totalCount:Double=0;
     var countCorrectlyPredicted: Double=0;
     for ((predictedLabel,actualLabel) <- predictedLabels) {
       totalCount = totalCount + 1;

       if (predictedLabel == actualLabel) {
         countCorrectlyPredicted = countCorrectlyPredicted + 1;

       }

     }

     val accuracy =(countCorrectlyPredicted/totalCount)*100;
    // println("value of countCorrectlyPredicted is:"+countCorrectlyPredicted)
    // println("value of totalCount is:"+totalCount)
     println("value of accuracy is:"+accuracy +"%")


    //gus' code on accuracy
     val numFolds = 10

     val POS_CLASS = "GRADABLE"
     val NEG_CLASS = "NOT GRADABLE"

     // Calculate accuracy for each class
     // label pairs (GOLD, PREDICTED)
     val overallAccuracy = predictedLabels.count{ case (g, p) => g == p} / predictedLabels.size.toDouble * 100
     val gradableClassAccuracy = predictedLabels.count{ case (g, p) => (g == POS_CLASS) && (g == p) } / predictedLabels.size.toDouble * 100
     val nonGradableClassAccuracy = predictedLabels.count{ case (g, p) => (g == NEG_CLASS) && (g == p) } / predictedLabels.size.toDouble * 100

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
}


