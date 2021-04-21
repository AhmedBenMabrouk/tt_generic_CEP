package MachinesTalk.configuration
import com.typesafe.scalalogging.StrictLogging
import scala.collection.JavaConversions._

import scala.util.Properties
import java.util
object ConfigurationEntry extends StrictLogging {

  var bootstrapServersprod = "10.0.0.10:9092"
  var bootstrapServerscons = "10.0.0.10:9092"
  var topics = List[String]()
  var telemetry = ""
  var outTopic = ""
  var value: Double = 0
  var duration: Double =0
  var repetition : Double =0
  var operator = ""

  def getConfFile(env: String): String = {
    env match {
      case "local" ⇒ "application-local.conf"
      case "dev" ⇒ "application-dev.conf"
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
    telemetry = myConfig.envOrElseConfig("kafka.consumer.telemetry")
    value= myConfig.speed("kafka.parameter.value")
    duration= myConfig.speed("kafka.parameter.duration")
    repetition = myConfig.speed("kafka.parameter.repetition")
    operator = myConfig.envOrElseConfig("kafka.parameter.operator")

    logger.debug("Kafka: {}", bootstrapServersprod)
    logger.debug("Kafka: {}", bootstrapServerscons)
  }
}