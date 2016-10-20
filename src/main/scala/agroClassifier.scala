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



object classifierForAgro {
  var outputFileNameForAllAdjectiveCount = "hashmapForAllAdjectivesAndItsCount.txt";
  var outputFileNameForInflectedAdjectiveCount = "hashmapForAllAdjectivesAndItsCount.txt";

  var allAdjectivesFromAgigaButUniq= "allAdjectivesFromAgigaButUniq.txt"

  var uniqAdjectivesInAgiga_removedErEst_uniq= "uniqAdjectivesInAgiga_removedErEst_uniq.txt"


  //path in laptop
  //var resourcesDirectory = "/Users/mithun/agro/agigaGradableAdjFinder/src/main/resources/"

  //path in chung.cs.arizona.edu
  var resourcesDirectory = "./src/main/resources/"

  //var outputDirectoryPath = "/Users/mithun/agro/agigaGradableAdjFinder/src/main/outputs/"

  var outputDirectoryPath = "./src/main/outputs/"
  var erRemovedFiles = "AllErEstEndingAdjectivesUniq.txt"

  var completeAgigaFileWithFrequency = "allAdjCombined_withWordCount.txt";


   def initializeAndClassify( ): Unit = {
     val counter = new Counter[String];
     val dataset = new RVFDataset[String, String]
     //
     val runOnServer=false;
     var getCurrentDirectory = new java.io.File(".").getCanonicalPath
     println("value of present directory is: "+getCurrentDirectory)
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

     val cobuildGradableAuto = new File(getClass.getClassLoader.getResource("GPauto").getPath)
     val cobuildNonGradableAuto = new File(getClass.getClassLoader.getResource("GMauto").getPath)
     val cobuildNonGradableManual = new File(getClass.getClassLoader.getResource("GMman").getPath)
     val cobuildGradableManual = new File(getClass.getClassLoader.getResource("GPman").getPath)

     //var inputFilename= resourcesDirectory + uniqAdjectivesInAgiga_removedErEst_uniq;
     //println("reaching here at 9870987")

     //fill in the hashmaps. i.e the maps which has the count of base form adjectives (cold->2342342) and inflected
     //adjectives(colder/coldest:2344)
     ratioCalculator.triggerFunction(resourcesDirectory,outputDirectoryPath);
     //todo: find if we should pick from the top 300 adjectives

     //testing for count of adjectives modified by adverb


    // System.exit(1);

     //for each of the adjectives in gradable COBUILD Auto, go through hash maps, get inflected count/total count ratio, add it to the clasifier
     for (adjToCheck <- Source.fromFile(cobuildGradableAuto).getLines()) {
        //todo: read input from all agiga files

       var inflRatio: Double = 0.911;
       var advrbModifiedRatio: Double = 0.041
       var inflAndAdvModified: Double = 0.0465

       //println("reaching here at 876467")
       //println("value of current adjective is :" + adjToCheck );

       //for each of the adjectives' root forms, get the inflected ratio.
       inflRatio = ratioCalculator.calculateInflectedAdjRatio(adjToCheck);

       if(inflRatio>0) {
         println("value of current adjective is :" + adjToCheck + " and its inflected ratio is:" + inflRatio)
       }


       advrbModifiedRatio=ratioCalculator.calculateAdvModifiedAdjRatio(adjToCheck);

       if(advrbModifiedRatio>0) {
         println("value of current adjective is :" + adjToCheck + " and its adverb modified ratio is:" + advrbModifiedRatio)
       }



       counter.setCount("feature1", inflRatio)
       counter.setCount("feature2", advrbModifiedRatio)
       counter.setCount("feature3", inflAndAdvModified)


       val datum1 = new RVFDatum[String, String]("GRADABLE", counter)
       //val datum2 = new RVFDatum[String, String]("NOT GRADABLE", counter)


       dataset += datum1
      // println("reaching here at 2345245")


       //dataset += datum2
       // add all your datums to the dataset
     }

     //for each of the adjectives in gradable COBUILD Auto, go through hash maps, get inflected count/total count ratio, add it to the clasifier
     for (adjToCheck <- Source.fromFile(cobuildGradableManual).getLines()) {
       //todo: read input from all agiga files

       var inflRatio: Double = 0.911;
       var advrbModifiedRatio: Double = 0.041
       var inflAndAdvModified: Double = 0.0465

       //println("reaching here at 876467")
       //println("value of current adjective is :" + adjToCheck );

       //for each of the adjectives' root forms, get the inflected ratio.
       inflRatio = ratioCalculator.calculateInflectedAdjRatio(adjToCheck);

       if(inflRatio>0) {
         println("value of current adjective is :" + adjToCheck + " and its inflected ratio is:" + inflRatio)
       }


       advrbModifiedRatio=ratioCalculator.calculateAdvModifiedAdjRatio(adjToCheck);

       if(advrbModifiedRatio>0) {
         println("value of current adjective is :" + adjToCheck + " and its adverb modified ratio is:" + advrbModifiedRatio)
       }



       counter.setCount("feature1", inflRatio)
       counter.setCount("feature2", advrbModifiedRatio)
       counter.setCount("feature3", inflAndAdvModified)


       val datum1 = new RVFDatum[String, String]("GRADABLE", counter)
       //val datum2 = new RVFDatum[String, String]("NOT GRADABLE", counter)


       dataset += datum1
       // println("reaching here at 2345245")


       //dataset += datum2
       // add all your datums to the dataset
     }


     //for each of the adjectives in non gradable COBUILD Auto (list of non gradable adjectives auto generated), go through hash maps, get inflected count/total count ratio, add it to the clasifier
     for (adjToCheck <- Source.fromFile(cobuildNonGradableAuto).getLines()) {
       //todo: read input from all agiga files

       println("reaching here at 4576")
       var inflRatio: Double = 0.911;
       var advrbModifiedRatio: Double = 0.041
       var inflAndAdvModified: Double = 0.0465

       //println("reaching here at 876467")
       println("value of current adjective is :" + adjToCheck );

       //for each of the adjectives' root forms, get the inflected ratio.
       inflRatio = ratioCalculator.calculateInflectedAdjRatio(adjToCheck);

       if(inflRatio>0) {
         println("value of current adjective is :" + adjToCheck + " and its inflected ratio is:" + inflRatio)
       }


       advrbModifiedRatio=ratioCalculator.calculateAdvModifiedAdjRatio(adjToCheck);

       if(advrbModifiedRatio>0) {
         println("value of current adjective is :" + adjToCheck + " and its adverb modified ratio is:" + advrbModifiedRatio)
       }



       counter.setCount("feature1", inflRatio)
       counter.setCount("feature2", advrbModifiedRatio)
       counter.setCount("feature3", inflAndAdvModified)


       val datum1 = new RVFDatum[String, String]("NOT GRADABLE", counter)
       //val datum2 = new RVFDatum[String, String]("NOT GRADABLE", counter)


       dataset += datum1
       println("reaching here at 2462467")


       //dataset += datum2
       // add all your datums to the dataset

     }


     //for each of the adjectives in non gradable COBUILD Auto (list of non gradable adjectives auto generated), go through hash maps, get inflected count/total count ratio, add it to the clasifier
     for (adjToCheck <- Source.fromFile(cobuildNonGradableManual).getLines()) {
       //todo: read input from all agiga files

       println("reaching here at 4576")
       var inflRatio: Double = 0.911;
       var advrbModifiedRatio: Double = 0.041
       var inflAndAdvModified: Double = 0.0465

       //println("reaching here at 876467")
       println("value of current adjective is :" + adjToCheck );

       //for each of the adjectives' root forms, get the inflected ratio.
       inflRatio = ratioCalculator.calculateInflectedAdjRatio(adjToCheck);

       if(inflRatio>0) {
         println("value of current adjective is :" + adjToCheck + " and its inflected ratio is:" + inflRatio)
       }


       advrbModifiedRatio=ratioCalculator.calculateAdvModifiedAdjRatio(adjToCheck);

       if(advrbModifiedRatio>0) {
         println("value of current adjective is :" + adjToCheck + " and its adverb modified ratio is:" + advrbModifiedRatio)
       }



       counter.setCount("feature1", inflRatio)
       counter.setCount("feature2", advrbModifiedRatio)
       counter.setCount("feature3", inflAndAdvModified)


       val datum1 = new RVFDatum[String, String]("NOT GRADABLE", counter)
       //val datum2 = new RVFDatum[String, String]("NOT GRADABLE", counter)


       dataset += datum1
       println("reaching here at 2462467")


       //dataset += datum2
       // add all your datums to the dataset

     }

     //train the classifier


     val scaleRanges = Datasets.svmScaleDataset(dataset, lower = -1, upper = 1)
     val perceptron = new PerceptronClassifier[String, String]
     println("Training the LABEL classifier...");



     perceptron.train(dataset)

     //test the classifier for a custom string as of now. Note, we should do the 80-20 thing ideally on COBUILD

     {

       val adjToTest="able";
       
       println("reaching here at 4576")
       var inflRatio: Double = 0.911;
       var advrbModifiedRatio: Double = 0.041
       var inflAndAdvModified: Double = 0.0465

       //println("reaching here at 876467")
       println("value of current adjective is :" + adjToTest );

       //for each of the adjectives' root forms, get the inflected ratio.
       inflRatio = ratioCalculator.calculateInflectedAdjRatio(adjToTest);

       if(inflRatio>0) {
         println("value of current adjective is :" + adjToTest + " and its inflected ratio is:" + inflRatio)
       }


       advrbModifiedRatio=ratioCalculator.calculateAdvModifiedAdjRatio(adjToTest);

       if(advrbModifiedRatio>0) {
         println("value of current adjective is :" + adjToTest + " and its adverb modified ratio is:" + advrbModifiedRatio)
       }
       
       counter.setCount("feature1", inflRatio)
       counter.setCount("feature2", advrbModifiedRatio)
       counter.setCount("feature3", inflAndAdvModified)


       
       val datum3 = new RVFDatum[String, String](adjToTest, counter)

       val label = perceptron.classOf(datum3)
       println("class of the given string:"+adjToTest+" is :"+ label);

     }


   }
}


