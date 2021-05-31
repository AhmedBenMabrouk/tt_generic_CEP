package MachinesTalk.configuration
import com.typesafe.scalalogging.StrictLogging
import scala.collection.JavaConversions._

import scala.util.Properties
import java.util
object ConfigurationEntry extends StrictLogging {

  var bootstrapServersprod = "10.0.0.10:9092"
  var bootstrapServerscons = "10.0.0.10:9092"
  var topics = List[String]()
  var telemetry = List[String]()
  var operation= List[String]()
  var value = List[String]()
  var operator = List[String]()
  var key= ""
  var complex = ""
  var outTopic = ""
  var duration: Double =0
  var repetition : Double =0

  def getConfFile(env: String): String = {
    env match {
      case "local" ⇒ "application-local.conf"
      case "dev" ⇒ "application-dev.yml"
      case "prod" ⇒ "application-prod.conf"
      case whoa ⇒
        logger.debug("Unexpected case: {}", whoa.toString)
        "application-local.conf"
    }
  }
  def initConfig(env: String): Unit = {
    val confFile = getConfFile(env)
    println(confFile)
    val myConfig = new ConfigurationReader(Option[String](confFile))
    //kafka
    bootstrapServersprod = myConfig.envOrElseConfig("kafka.producer.servers")
    bootstrapServerscons = myConfig.envOrElseConfig("kafka.consumer.servers")
    outTopic = myConfig.envOrElseConfig("kafka.producer.topics")
    topics = myConfig.readTopics("kafka.consumer.topics")
    telemetry = myConfig.readTopics("kafka.parameter.telemetry")
    value= myConfig.readTopics("kafka.parameter.value")
    duration= myConfig.speed("kafka.parameter.duration")
    repetition = myConfig.speed("kafka.parameter.repetition")
    operator = myConfig.readTopics("kafka.parameter.operator")
    key = myConfig.envOrElseConfig("kafka.parameter.key")
    complex = myConfig.envOrElseConfig("kafka.parameter.complex")
    operation=  myConfig.readTopics("kafka.parameter.operation")

    logger.debug("Kafka: {}", bootstrapServersprod)
    logger.debug("Kafka: {}", bootstrapServerscons)
  }
}