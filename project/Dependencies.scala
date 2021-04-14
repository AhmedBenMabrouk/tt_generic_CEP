import sbt._

object Dependencies {
  object Versions {
    val flink = "0.10.1"
    val kafka = "0.8.2.1"
  }
 // lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5" % Test
  lazy val spring= "org.springframework.cloud" % "spring-cloud-dataflow-dependencies" % "2.2.1.RELEASE"
  lazy val flinkscala="org.apache.flink" %% "flink-scala" % "1.9.1"
  //lazy val flinkstreamcore="org.apache.flink" %% "flink-streaming-core" % "0.9.0"
  lazy val flinkstreamscala="org.apache.flink" %% "flink-streaming-scala" % "1.9.1"
  lazy val flinkconkafka= "org.apache.flink" %% "flink-connector-kafka" % "1.9.1"
  lazy val scalalogging= "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
  lazy val kfclient="org.apache.kafka" % "kafka-clients" % "1.0.2"
  lazy val slf4j="org.slf4j" % "slf4j-api" % "1.7.10"
  lazy val logback="ch.qos.logback" % "logback-classic" % "1.1.2"
  lazy val  jacksonModuleScala = "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.4"
  lazy val  json4snative =   "org.json4s" %% "json4s-native" % "3.6.7"
  lazy val  json4score = "org.json4s" %% "json4s-core" % "3.6.7"
  lazy val  flinkjson =  "org.apache.flink" % "flink-json" % "1.9.1"
  lazy val  sparyjson = "com.typesafe.play" %% "play-json" % "2.8.0"
  lazy val  jodatime= "joda-time" % "joda-time" % "2.10.5"
  lazy val  circecore="io.circe" %% "circe-core" % "0.13.0"
  lazy val  circegeneric= "io.circe" %% "circe-generic" % "0.13.0"
  lazy val  circeparser= "io.circe" %% "circe-parser" % "0.13.0"
  //lazy val sql= "com.microsoft.sqlserver" % "mssql-jdbc" % "8.3.0.jre14-preview"
 // lazy val sql=  "com.microsoft.sqlserver" % "mssql-jdbc" % "6.1.0.jre8"
  lazy val java= "mysql" % "mysql-connector-java" % "5.1.24"
  lazy val flinkcep= "org.apache.flink" %% "flink-cep-scala" % "1.9.0"




}
