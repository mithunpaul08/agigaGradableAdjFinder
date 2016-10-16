package agiga

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
  var resourcesDirectory = "/Users/mithun/agro/agigaGradableAdjFinder/src/main/resources/"
  var outputDirectoryPath = "..outputs/"

  var completeAgigaFileWithFrequency = "allAdjCombined_withWordCount.txt";

  //var outputFileName = "hashmapForErAdjectiveAndItsBaseForm.txt";

  // var hashMapOfAllUniqAdjectivesInAgigaWithFrequency = Map("Long" -> "1")

  var hashMapOfAllUniqAdjectivesInAgigaWithFrequency: Map[String, String] = Map()

  def calculateInflectedAdjRatio(): Double = {

    var myratio: Double = 0.005;
    return myratio;

  }

  def calculateAdvModifiedAdjRatio(): Double = {

    var myratio: Double = 0.005;
    return myratio;
  }

  def calculateBothInflectedAdvModifiedRatio(): Double = {

    var myratio: Double = 0.005;
    return myratio;
  }

  def ReadAllAdjectivesAndFrequencyToHashmap(): Unit = {
    println("reaching here at 36857")
    val advInputFile = resourcesDirectory + completeAgigaFileWithFrequency;
    try {
      //println("reaching here at 1263")

      //println("reaching here at 579084")

      for (line <- Source.fromFile(advInputFile).getLines()) {

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
