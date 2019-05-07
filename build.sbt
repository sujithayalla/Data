name := "MetaData"

version := "0.1"

scalaVersion := "2.11.8"
libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.11" % "2.0.0",
  "org.apache.spark" % "spark-sql_2.11" % "2.0.0",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test",
  "org.apache.poi" % "poi" % "4.0.0",
  "org.apache.poi" % "poi-ooxml" % "4.0.0"
)