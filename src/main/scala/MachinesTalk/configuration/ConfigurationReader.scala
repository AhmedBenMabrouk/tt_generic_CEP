package MachinesTalk.configuration

import com.typesafe.config.ConfigFactory
import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.Yaml

import scala.collection.JavaConversions._

class ConfigurationReader(fileNameOption: Option[String] = None) {
  val config = fileNameOption.fold(
    ifEmpty = ConfigFactory.load())(
    file ⇒ ConfigFactory.load(file))
  val yaml = new Yaml()
  def readTopics(name: String): List[String] = {
    val list = config.getStringList(name).toList
    list
  }
  def envOrElseConfig(name: String): String = {
    config.getString(name)
  }
  def speed(name: String): Double = {
    config.getDouble(name)
  }
}