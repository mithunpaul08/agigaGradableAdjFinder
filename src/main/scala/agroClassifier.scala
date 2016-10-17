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
  var resourcesDirectory = "/Users/mithun/agro/agigaGradableAdjFinder/src/main/resources/"

  var outputDirectoryPath = "/Users/mithun/agro/agigaGradableAdjFinder/src/main/outputs/"

  // var outputDirectoryPath = "..outputs/"
  var erRemovedFiles = "AllErEstEndingAdjectivesUniq.txt"

  var completeAgigaFileWithFrequency = "allAdjCombined_withWordCount.txt";


   def initializeAndClassify( ): Unit = {
     val counter = new Counter[String];
     val dataset = new RVFDataset[String, String]
     //
     val runOnServer=true;
     var getCurrentDirectory = new java.io.File(".").getCanonicalPath
     println("value of present directory is: "+getCurrentDirectory)
     if(runOnServer)
       {
          //resourcesDirectory = "./src/main/resources/"
         resourcesDirectory = "~/testbed/"

          outputDirectoryPath = "~/testbed/"

         // var outputDirectoryPath = "..outputs/"
          erRemovedFiles = "AllErEstEndingAdjectivesUniq.txt"

          completeAgigaFileWithFrequency = "allAdjCombined_withWordCount.txt";

       }
     var inputFilename= resourcesDirectory + uniqAdjectivesInAgiga_removedErEst_uniq;
     println("reaching here at 9870987")

     //fill in the hashmaps. i.e the maps which has the count of base form adjectives (cold->2342342) and inflected
     //adjectives(colder/coldest:2344)
     ratioCalculator.triggerFunction(resourcesDirectory);
     //todo: find if we should pick the top 300 adjectives
     for (line <- Source.fromFile(inputFilename).getLines()) {

       var inflRatio: Double = 0.911;
       var advrbModifiedRatio: Double = 0.041
       var inflAndAdvModified: Double = 0.0465

       println("reaching here at 876467")
       println("value of current adjective is :" + line );
       //for each of the adjectives' root forms, get the inflected ratio.

       inflRatio = ratioCalculator.calculateInflectedAdjRatio(line);

       println("value of current adjective is :" + line + "and its inflected ratio is:" + inflRatio)
       //System.exit(1);
       //for each of the adjectives' root forms, get the inflected ratio.

       //for each of the adjectives' root forms, get the inflected ratio.



       counter.setCount("feature1", inflRatio)
       counter.setCount("feature2", advrbModifiedRatio)
       counter.setCount("feature3", inflAndAdvModified)


       val datum1 = new RVFDatum[String, String]("GRADABLE", counter)
       val datum2 = new RVFDatum[String, String]("NOT GRADABLE", counter)


       dataset += datum1
       dataset += datum2
       // add all your datums to the dataset





       //return label;
     }
     val scaleRanges = Datasets.svmScaleDataset(dataset, lower = -1, upper = 1)
     val perceptron = new PerceptronClassifier[String, String]
     println("Training the LABEL classifier...");
     perceptron.train(dataset)
     val datum3 = new RVFDatum[String, String]("heavy", counter)
     val label = perceptron.classOf(datum3)
     println("value of the classified label is:" + label);
   }
}


