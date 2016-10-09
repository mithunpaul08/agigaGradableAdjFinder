package agiga
import scala.xml.XML
//Just a main file to trigger the required parsing. Either for adjective or adverb

object Hi {
  def main(args: Array[String]) = println("Exiting main program")

  //call the adjective parser.
//  agigParser.readFiles();

  //call the adverb parser
  adverbParser.readFiles();
}

