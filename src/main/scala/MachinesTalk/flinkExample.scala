/*package MachinesTalk
import MachinesTalk.configuration.ConfigurationEntry
import MachinesTalk.configuration.ConfigurationEntry.topics

import java.util
import java.util.Properties
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import org.apache.flink.cep.{CEP, PatternSelectFunction, PatternStream}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.cep.scala.pattern.Pattern
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer

import scala.collection.mutable.Map
//import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema
import scala.collection.JavaConversions._
  object flinkExample {
  def main(args: Array[String]): Unit = {

    def traverse(root: JsonNode, x: Map[String, String]): Map[String, String] = {
      if (root.isObject()) {
        val fieldNames = root.fieldNames()
        while (fieldNames.hasNext) {
          val fieldName: String = fieldNames.next()
          val fieldValue: JsonNode = root.get(fieldName)
          if (ConfigurationEntry.relevant_data.contains(fieldName)) {
            x.put(fieldName, fieldValue.toString())
          }
          traverse(fieldValue, x)
        }
      }
      else {
        println("terminated")
      }
      x
    }

    val envirment: String = "local"
    ConfigurationEntry.initConfig(envirment)

    val see: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "localhost:9092")
    val kafkaConsumer = new FlinkKafkaConsumer[ObjectNode](topics, new JSONKeyValueDeserializationSchema(false), properties)
    val src: DataStream[ObjectNode] = see.addSource(kafkaConsumer)


    val values: Map[String, String] = Map()
    val mapper = new ObjectMapper() with ScalaObjectMapper
    val stream = src.map(x => {
      val node = mapper.readTree(x.toString())

      traverse(node, values)
      values
    })



    val pat = Pattern
      .begin[(String, Double)]("start")
      .where(_._2 > value)
      .within(Time.seconds(60))

    val patternStream: PatternStream[Map[String, String]] = CEP.pattern(stream, pat)

    val result: DataStream[Map[String, Any]] = patternStream.select(
      new PatternSelectFunction[(String, Double), Map[String, Any]]() {
        override def select(pattern: util.Map[String, util.List[(String, Double)]]): Map[String, Any] = {
          val data = pattern.get("start").get(0) //alternative of iteration
          Map("driverID" -> data._1, "speed" -> data._2)
        }
      }
    )


    result.print()
    see.execute("ASK Flink Kafka")
  }
}*/