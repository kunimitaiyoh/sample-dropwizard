name := "sample"
organization := "com.example"
version := "0.0.1"
scalaVersion := "2.12.4"
javacOptions ++= Seq("-encoding", "UTF-8")

val workaround: Unit = {
  sys.props += "packaging.type" -> "jar"
  ()
}
libraryDependencies ++= Seq(
  "io.dropwizard" % "dropwizard-core" % "1.3.0",
  "org.glassfish.jersey.media" % "jersey-media-sse" % "2.25.1",
  "org.apache.httpcomponents" % "httpmime" % "4.5.3",
)

initialCommands := "import com.example.sample._"

assemblyOutputPath in assembly := file(s"target/${name.value}.jar")
assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.first
  case PathList(ps @ _*) if Seq(".properties", ".xml", ".types", ".class")
    .exists(x =>  ps.last.endsWith(x)) => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
