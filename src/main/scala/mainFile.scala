package agiga

import scala.xml.XML
//Just a main file to trigger the required parsing. Either for adjective or adverb

object mainParser {
  def main(args: Array[String]) = println("Exiting main program")

  val runOnServer = true;
 // val runOnServer = false;
  //call the adjective parser.
  //  agigParser.readFiles();

  //call the adverb parser
  //adverbParser.readFiles();

  //var myngrams =ratioCalculator.characterNgramCalculator("coldest",3);

  //call the adjective -er form checker
  var hashmapOfColderCold = goodAdjectiveFinder.readErRemovedFile(runOnServer);


  //functions that fill up the hashmaps
  //ratioCalculator.triggerFunction();

  //once the hashmaps are filled up with word frequency, call the classifier class
  classifierForAgro.initializeAndClassify(runOnServer, hashmapOfColderCold);

}

