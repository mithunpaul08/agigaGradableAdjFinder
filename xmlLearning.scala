package agiga
import scala.xml.XML


object Hi {
 def main(args: Array[String]) = println("Hi!")
	val testFile  = XML.loadFile("testXml.xml")
//	val hello= (testFile \ "DOC" \ "sentences" \ "sentence" \ "tokens"  ).map(_.text)
	val hello= (testFile \\ "POS").map(_.text)

agigParser.readFiles()
agigParser.printLine()


println(hello)
}

