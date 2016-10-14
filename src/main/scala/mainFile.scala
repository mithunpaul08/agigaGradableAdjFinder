package agiga

import scala.xml.XML
//Just a main file to trigger the required parsing. Either for adjective or adverb

object mainParser {
  def main(args: Array[String]) = println("Exiting main program")

  //call the adjective parser.
//  agigParser.readFiles();

  //call the adverb parser
  //adverbParser.readFiles();

  //call the adjective -er form checker
  //goodAdjectiveFinder.readErRemovedFile();


  //call the classifier class -note, as of now, this is for testing classifier only.
  // Once we have the frequency and labels from the corpus, we will be feeding it to this classifier
  classifierForAgro.initializeAndClassify();

}
