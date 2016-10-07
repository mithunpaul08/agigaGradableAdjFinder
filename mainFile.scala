package agiga
import scala.xml.XML
//test
/*
object Hi {
	def main(args: Array[String]) = println("Hi!")

	val testFile = XML.loadFile("./inputs/testXml.xml")
	println("reaching here at 21")
	//	val hello= (testFile \ "DOC" \ "sentences" \ "sentence" \ "tokens"  ).map(_.text)
	val hello = (testFile \\ "POS").map(_.text)
	//just sample edit
	println("reaching here at 22")
	//	val hello= (testFile \ "DOC" \ "sentences" \ "sentence" \ "tokens"  ).map(_.text)
	agigParser.readFiles()
	println("reaching here at 23")
	var counter = 1;
	var mystring = "this is an immutable string"

	println(mystring)

	var mynewstring = "this is an immutable string";
	var mylen = mynewstring.length()

	println(mynewstring)

	println("length of the string is" + mylen)
	println("value of counter is" + counter)

	if (mynewstring.startsWith("this")) {
		println("given string starts with this")
		counter = counter + 1
	}
	println("value of counter is" + counter)
	agigParser.printLine()
	println("reaching here at 24")


	println(hello)
}

*/