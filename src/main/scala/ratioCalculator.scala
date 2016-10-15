import org.clulab.learning.{Datasets, PerceptronClassifier, RVFDataset, RVFDatum}
import org.clulab.struct.Counter

import scala.io.Source

/**
  * Created by mithunpaul on 10/14/16.
  */
object ratioCalculator {


  //var resourcesDirectory = "/work/mithunpaul/testbed/"
  var resourcesDirectory = "resources/"

  var outputDirectoryPath = "outputs/"

  var completeAgigaFileWithFrequency = "allAdjCombined_withWordCount.txt";

  //var outputFileName = "hashmapForErAdjectiveAndItsBaseForm.txt";

  //var hashMapOfAllUniqAdjectivesInAgiga = Map("Long" -> "1")

  var hashMapOfAllUniqAdjectivesInAgigaWithFrequency: Map[String, Int] = Map()

  def calculateInflectedAdjRatio( ): Double= {

    var myratio :Double=0.005;
    return myratio;

  }

  def calculateAdvModifiedAdjRatio( ): Double= {

    var myratio :Double=0.005;
    return myratio;
  }

  def calculateBothInflectedAdvModifiedRatio( ): Double= {

    var myratio: Double = 0.005;
    return myratio;
  }

  def ReadAllAdjectivesAndFrequencyToHashmap(): Unit = {
    println("reaching here at 36857")
    val advInputFile = resourcesDirectory + completeAgigaFileWithFrequency;
    try {
      //println("reaching here at 1263")
      var counterForHashmap = 0;
      for (line <- Source.fromFile(advInputFile).getLines()) {
         println("reaching here at 204. value of line is:"+line)
        //hashMapOfAllUniqAdjectivesInAgigaWithFrequency += (line -> "1");
      }
    } catch {
      case ex: Exception => println("An exception happened. Not able to find the file")
    }
  }
}
