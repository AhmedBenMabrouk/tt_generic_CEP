import Dependencies.{spring, _}
import sbt.Keys.{libraryDependencies, version}

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "Machinestalk",
      scalaVersion := "2.12.10",
      version := "2.1.1-SNAPSHOT"
    )),
    name := "tt_generic_CEP",
    libraryDependencies ++= Seq (
      //   scalaTest,

      // sql,
      java,
      flinkscala,
      jodatime,
      circeparser,
      circegeneric,
      circecore,


      // flinkstreamcore,
      flinkstreamscala,
      flinkconkafka,scalalogging,slf4j,logback,kfclient,jacksonModuleScala,json4snative,json4score,flinkjson,flinkcep

    )
  )
//  .settings(Formatting.formatSettings)
//  .settings(scalacOptions ++=  commonScalacOptions)
//  .enablePlugins(AutomateHeaderPlugin)
//  .enablePlugins(JavaAppPackaging, AshScriptPlugin)

//assemblyMergeStrategy in assembly := {
//  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
//  case x => MergeStrategy.first
//}

lazy val commonScalacOptions = Seq(
  "-deprecation", "-encoding", "UTF-8", "-unchecked", "-Ywarn-dead-code", "-feature",
  "-Xlint" , "-language:postfixOps" //, "-Xfatal-warnings"
)


// enable the Java app packaging archetype and Ash script (for Alpine Linux, doesn't have Bash)
//enablePlugins(JavaAppPackaging, AshScriptPlugin)

// set the main entrypoint to the application that is used in startup scripts
//mainClass in Compile := Some("com.machinestalk.cleansing.MainApplication")

// the Docker image to base on (alpine is smaller than the debian based one (120 vs 650 MB)
//dockerBaseImage := "openjdk:8-jre-alpine"
//
//// creates tag 'latest' as well when publishing
//dockerUpdateLatest := true
//
//javaOptions := Seq("-Dmx=1024M")
//javaOptions := Seq("-Denv=" + sys.props.getOrElse("env", "dev"))
//scalacOptions ++= commonScalacOptions
//
//dockerEntrypoint := Seq("bin/ks-cleansing", sys.props.getOrElse("env", "dev"))


organizationName := "Heiko Seeberger"
startYear := Some(2015)
licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))
