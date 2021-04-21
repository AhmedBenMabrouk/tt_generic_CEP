package MachinesTalk

import MachinesTalk.configuration.ConfigurationEntry
import MachinesTalk.configuration.ConfigurationEntry.{duration, operator, repetition, telemetry, topics, value}
import org.apache.flink.cep.PatternSelectFunction
import org.apache.flink.cep.scala.CEP
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import java.util
import java.util.Properties
import org.apache.flink.streaming.api.scala._
import org.apache.flink.cep.scala.pattern.Pattern
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer

import scala.collection.mutable.Map
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema

import scala.collection.JavaConversions._

object generic {
  def main(args: Array[String]): Unit = {


    val envirment: String = "local"
    ConfigurationEntry.initConfig(envirment)

    val see: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "localhost:9092")

    val kafkaConsumer = new FlinkKafkaConsumer[ObjectNode](topics, new JSONKeyValueDeserializationSchema(false), properties)
    val src: DataStream[ObjectNode] = see.addSource(kafkaConsumer)

    val mapper = new ObjectMapper() with ScalaObjectMapper

    val keyedStream = src.map(x => {
      val node1 = mapper.readTree(x.toString)
      val node = node1.get("value")
      val value = node.get(telemetry).asDouble()
      (telemetry,value)

    })

    val pat =  Pattern.begin[(String, Double)]("start")
    if (operator == "greater than") {
         pat.where(_._2 > value)
        .within(Time.seconds(duration.toLong))
        .times(repetition.toInt)
    }
    else {
      pat
        .where(_._2 > value)
        .within(Time.seconds(duration.toLong))
        .times(repetition.toInt)
    }


    val patternStream = CEP.pattern(keyedStream, pat)

    val result: DataStream[Map[String, Any]] = patternStream.select(
      new PatternSelectFunction[(String, Double), Map[String, Any]]() {
        override def select(pattern: util.Map[String, util.List[(String, Double)]]): Map[String, Any] = {
          val data = pattern.get("start").get(0) //alternative of iteration
          Map("telemetry" -> data._1, "valeur" -> data._2)
        }
      }
    )


    result.print()
    see.execute("ASK Flink Kafka")
  }


}
