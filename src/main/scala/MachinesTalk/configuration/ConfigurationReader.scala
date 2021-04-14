package MachinesTalk.configuration

import com.typesafe.config.ConfigFactory

import scala.collection.JavaConversions._

class ConfigurationReader(fileNameOption: Option[String] = None) {
  val config = fileNameOption.fold(
    ifEmpty = ConfigFactory.load())(
    file ⇒ ConfigFactory.load(file))
  def readTopics(name: String): List[String] = {
    val list = config.getStringList(name).toList
    list
  }
  def envOrElseConfig(name: String): String = {
    //Properties.envOrElse(
    //      name.toUpperCase.replaceAll("""\.""", "_"),
    config.getString(name)
    //)
  }
  def speed(name: String): Double = {
    config.getDouble(name)
  }
}