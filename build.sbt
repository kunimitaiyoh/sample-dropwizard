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
