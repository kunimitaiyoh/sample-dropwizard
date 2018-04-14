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
  "io.dropwizard" % "dropwizard-jdbi3" % "1.3.0",
  "io.dropwizard" % "dropwizard-migrations" % "1.3.0",
  "mysql" % "mysql-connector-java" % "5.1.6"
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
