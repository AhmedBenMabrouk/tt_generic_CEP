package MachinesTalk
import MachinesTalk.configuration.ConfigurationEntry
import MachinesTalk.configuration.ConfigurationEntry.{duration, key, operation, operator, outTopic, repetition, telemetry, topics, value}
import org.apache.flink.cep.PatternSelectFunction
import org.apache.flink.cep.scala.CEP
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import java.util
import java.util.Properties
import com.fasterxml.jackson.databind.node.ArrayNode
import org.apache.flink.api.common.serialization.{SerializationSchema, SimpleStringSchema}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.cep.scala.pattern.Pattern
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}
import scala.collection.mutable.Map
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema
import scala.collection.JavaConversions._
object generic {
  def main(args: Array[String]): Unit = {

    def traverse(root: JsonNode, x: Map[String, Double]): Map[String, Double] = {
      if (root.isObject()) {
        val fieldNames = root.fieldNames()
        while (fieldNames.hasNext) {
          val fieldName: String = fieldNames.next()
          val fieldValue: JsonNode = root.get(fieldName)
          if (telemetry.contains(fieldName)) {
            x.put(fieldName, fieldValue.toString().toDouble)
          }
          traverse(fieldValue, x)
        }
      }
      else {
        println("terminated")
      }
      x
    }

    def compare(a : Double, op : String , b : Double) : Boolean = {
      val result = op match {
        case "GREATER_THAN" =>   a > b
        case "GREATER_OR_EQUAL"  =>  a >=b
        case "LESS_THAN"  => a < b
        case "LESS_OR_EQUAL"  => a <= b
        case "EQUAL"  => a == b
        case "DIFFERENT"  => a != b
        case _ => a == b
      }
      result
    }
    val enviroment: String = "local"
    ConfigurationEntry.initConfig(enviroment)
    val see: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "localhost:9092")
    val kafkaConsumer = new FlinkKafkaConsumer[ObjectNode](topics, new JSONKeyValueDeserializationSchema(false), properties)
    val src: DataStream[ObjectNode] = see.addSource(kafkaConsumer)
    var keyy = "null"

    val mapper = new ObjectMapper() with ScalaObjectMapper
    val keyedStream = src.map(x => {
      val node1 = mapper.readTree(x.toString)
      val node = node1.get("value")
      keyy = node.get(key).toString
      val values: Map[String, Double] = Map()
      traverse(node, values)
        for (tel  <- telemetry) {
        if (!(values.keys.contains(tel))) {
          values.put(tel, 0)
        }
      }
      (keyy ,values)
    }).keyBy(_._1)
    val pat = Pattern.begin[(String,Map[String, Double])]("start")
    for (tel  <- telemetry)
    {
      val i = telemetry.indexOf(tel)
      val valeur = value.get(i).toDouble
      val operateur = operator.get(i)
      if ( i == 0 )
      {
        if (operateur == "GREATER_THAN") {
          pat.where( _._2.get(tel).get > valeur)


        }
        else {
          pat.where(  _._2.get(tel).get <= valeur)



        }
      }
      else
      {
        val operationn = operation.get(i-1)
        if (operationn == "AND")
        {
          if (operateur == "GREATER_THAN") {
            pat.where(  _._2.get(tel).get > valeur)


          }
          else {
            pat.where(  _._2.get(tel).get <= valeur)

          }
        }
        else
        {
          if (operateur == "GREATER_THAN") {
            pat.or(  _._2.get(tel).get > valeur)


          }
          else {
            pat.or( _._2.get(tel).get <= valeur)


          }
        }
      }
    }
    pat.within(Time.seconds(duration.toLong))
      .times(repetition.toInt)
      .consecutive()
    val patternStream = CEP.pattern(keyedStream, pat)
    val result: DataStream[Map[String, Any]] = patternStream.select(
      new PatternSelectFunction[(String,Map[String, Double]), Map[String, Any]]() {
        override def select(pattern: util.Map[String, util.List[(String,Map[String, Double])]]): Map[String, Any] = {
          val data = pattern.get("start").get(repetition.toInt - 1) //alternative of iteration
          Map("telemetry" -> data._1, "valeur" -> data._2)
        }
      }
    )
    val propertiess = new Properties
    propertiess.setProperty("bootstrap.servers", "localhost:9092")
    val myProducer = new FlinkKafkaProducer[Map[String, Any]](
      outTopic, // target topic
      new SerializationSchema[Map[String, Any]] {
        override def serialize(element: Map[String, Any]): Array[Byte] = {
          var mapper: ObjectMapper = new ObjectMapper();
          import com.fasterxml.jackson.databind.node.ObjectNode
          var arrayNode: ArrayNode = mapper.createArrayNode();
          var objectNode1: ObjectNode = mapper.createObjectNode();
          for (elements <- element) {
            if(element.get("valeur")!=null)
              objectNode1.put(key, element.get("telemetry").get.toString)
              val map = element.get("valeur").get.asInstanceOf[Map[String, Double]]
              for (tel <- telemetry)
                {
                  objectNode1.put(tel, map.get(tel).get.toString)
                }
              objectNode1.put("result", "yes")

            //            arrayNode.add(objectNode1)
          }
          objectNode1.toString.getBytes("UTF8")
        }
      }, // serialization schema
      propertiess)
    result.addSink(myProducer)
    result.print()
    see.execute("ASK Flink Kafka")
  }
}