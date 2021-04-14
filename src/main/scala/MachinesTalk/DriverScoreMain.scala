/* package MachinesTalk

import java.sql.DriverManager
import java.util
import java.util.{Collections, Properties}
import java.util.concurrent.TimeUnit
import java.util.UUID.randomUUID

import MachinesTalk.configuration.ConfigurationEntry
import cats.instances.tuple
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.typesafe.scalalogging.StrictLogging
import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.serialization.{SerializationSchema, SimpleStringSchema}
import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.{TimeCharacteristic, environment}
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.assigners.{EventTimeSessionWindows, GlobalWindows, WindowAssigner}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.triggers.{EventTimeTrigger, ProcessingTimeTrigger, PurgingTrigger, Trigger, TriggerResult}
import org.apache.flink.streaming.api.windowing.windows.{GlobalWindow, TimeWindow}
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}
import org.apache.flink.streaming.util.keys.KeySelectorUtil.OneKeySelector
import org.apache.flink.util.Collector
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable._
import scala.util.parsing.json.JSONObject
import java.util
import java.util.Properties
import org.apache.flink.cep.PatternSelectFunction
import org.apache.flink.cep.scala.CEP
import org.apache.flink.streaming.api.scala._
import org.apache.flink.cep.scala.pattern.Pattern
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

object DriverScoreMain extends App with StrictLogging {

  var driverTrip: List[(String, String, String)] = List()
  var info: List[(String, String)] = List()
  var envirment: String = "local"
  try {
    if (args.length >= 1) {
      logger.debug("Setting Env => env = {} ", args(0))
      envirment = args(0)
    }
    else {
      logger.debug("Env Arg not found => Default env = prod")
    }
  }
  catch {
    case e: Exception ⇒ logger.error(e.getMessage, e)
  }

  ConfigurationEntry.initConfig(envirment)
  // val window = Time.of(10, TimeUnit.SECONDS)

  val env = StreamExecutionEnvironment.createLocalEnvironment()
  env.setParallelism(1)
  val kafkaConsumerProperties = Map(
    "zookeeper.connect" -> "localhost:2181",
    "group.id" -> "flink",
    "bootstrap.servers" -> "localhost:9092"

    /*"zookeeper.connect" -> "localhost:2181",
        "group.id" -> "flink",
        "bootstrap.servers" -> "localhost:9092",
        //"enable.auto.commit"-> "true"*/

  )

  //def exist(d:String,i:String,)
  //  env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

  val kafkaConsumer = new FlinkKafkaConsumer[ObjectNode](ConfigurationEntry.topics.asJava, new SimpleStringSchema, kafkaConsumerProperties)

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

  val producerProps = new Properties
  producerProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  val kafkaProducer = new FlinkKafkaProducer[List[(String, Double)]](ConfigurationEntry.outTopic,
    new SerializationSchema[List[(String, Double)]] {
      override def serialize(element: List[(String, Double)]): Array[Byte] = {
        var mapper: ObjectMapper = new ObjectMapper();
        var arrayNode: ArrayNode = mapper.createArrayNode();
        for (elements <- element) {
          var objectNode1: ObjectNode = mapper.createObjectNode();
          objectNode1.put(elements._1, elements._2)
          arrayNode.add(objectNode1)
        }
        arrayNode.toString.getBytes("UTF8")
      }
    },
    producerProps)
  val TEMPERATURE_THRESHOLD: Double = 50.00
  var driver: String = _
  var imei: String = _
  var ch: String = _
  val lines: DataStream[String] = env.addSource(kafkaConsumer)
  val mapper = new ObjectMapper() with ScalaObjectMapper
  val stream = src.map(x => {
    val node = mapper.readValue(x, classOf[JsonNode])
    val relevant_data: Map[String, String] = Map()
    traverse(node, relevant_data)
  })
  var loc:String=""
  val keyedStream = src.map(v => v.get("value"))
    .map {
      v =>
        loc = v.get("locationID").asText()
        val temp = v.get("temp").asDouble()
        (loc, temp)
    }
    .keyBy(v => v._2)
  val pat = Pattern
    .begin[(String, Double)]("start")
    .where(_._2 > TEMPERATURE_THRESHOLD)
  val patternStream = CEP.pattern(keyedStream, pat)
  val result: DataStream[Map[String, Any]] = patternStream.select(
    new PatternSelectFunction[(String, Double), Map[String, Any]]() {
      override def select(pattern: util.Map[String, util.List[(String, Double)]]): Map[String, Any] = {
        val data = pattern.get("start").get(0) //alternative of iteration
        Map("locationID" -> data._1, "temperature" -> data._2)
      }
    }
  )

      result.print()

//  stream.addSink(kafkaProducer)


  env.execute()


  implicit def map2Properties(map: Map[String, String]): java.util.Properties = {
    (new java.util.Properties /: map) { case (props, (k, v)) => props.put(k, v); props }
  }

}

//jdbc:sqlserver://<host>[:<port1433>];databaseName=<database>

//,a(1)._2)}
//GlobalWindows.create()

 */