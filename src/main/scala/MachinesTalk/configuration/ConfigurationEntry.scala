package MachinesTalk.configuration
import com.typesafe.scalalogging.StrictLogging
import scala.collection.JavaConversions._

import scala.util.Properties
import java.util
object ConfigurationEntry extends StrictLogging {
  var bootstrapServersprod = "10.0.0.10:9092"
  var bootstrapServerscons = "10.0.0.10:9092"
  var topics = List[String]()
  var relevant_data = List[String]()
  var outTopic = ""
  var speedLimit: Double = 0
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
    relevant_data = myConfig.readTopics("kafka.consumer.relevant_data")
    speedLimit = myConfig.speed("kafka.parameter.speedLimit")
    logger.debug("Kafka: {}", bootstrapServersprod)
    logger.debug("Kafka: {}", bootstrapServerscons)
  }
}