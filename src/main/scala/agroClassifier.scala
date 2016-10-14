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

   def initializeAndClassify(): Unit = {
     //     val datum1 = new BVFDatum[String, String]("NotSpam", Array("hello", "world"))
     //     val datum2 = new BVFDatum[String, String]("Spam", Array("buy", "viagra"))
     //     val dataset = new BVFDataset[String, String]
     //
     //     dataset += datum1
     //     dataset += datum2


     val counter = new Counter[String]
     counter.setCount("feature1", 5.3)
     counter.setCount("feature2", 8.5)

     val datum = new RVFDatum[String, String]("GRADABLE", counter)

     val dataset = new RVFDataset[String, String]
     dataset += datum
     // add all your datums to the dataset

     val scaleRanges = Datasets.svmScaleDataset(dataset, lower = -1, upper = 1)


     val perceptron = new PerceptronClassifier[String, String]
     println("Training the LABEL classifier...");
     perceptron.train(dataset)
     //     val datum3 = new BVFDatum[String, String]("Spam", Array("buy", "something", "else"))
     val datum3 = new RVFDatum[String, String]("LABEL", counter)
     val label = perceptron.classOf(datum3)
     println("value of the classified label is:" + label);
   }
}


