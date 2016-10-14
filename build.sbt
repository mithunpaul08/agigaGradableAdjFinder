lazy val root = (project in file(".")).
  settings(
    name := "hello",
    version := "1.0",
    scalaVersion := "2.11.8",


libraryDependencies ++= Seq(
 "org.scala-lang.modules" %% "scala-xml" % "1.0.2",
	"com.github.myedibleenso" %% "processors-agiga" % "0.0.1",
  "org.clulab" %% "processors-corenlp" % "6.0.0",
  "org.clulab" %% "processors-main" % "6.0.0",
  "org.clulab" %% "processors-models" % "6.0.0"
)
  )


