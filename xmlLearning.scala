package agiga
import scala.xml.XML
//test

object Hi {
 def main(args: Array[String]) = println("Hi!")
	val testFile  = XML.loadFile("testXml.xml")
println("reaching here at 21")
//	val hello= (testFile \ "DOC" \ "sentences" \ "sentence" \ "tokens"  ).map(_.text)
	val hello= (testFile \\ "POS").map(_.text)
//just sample edit
println("reaching here at 22")
//	val hello= (testFile \ "DOC" \ "sentences" \ "sentence" \ "tokens"  ).map(_.text)
agigParser.readFiles()
println("reaching here at 23")
agigParser.printLine()
println("reaching here at 24")


println(hello)
}

