package com.github.felipegutierrez.kafka.stream.bankbalance

import java.io.IOException

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.Assert.{assertEquals, assertNotNull, assertTrue}
import org.junit.Test
import org.scalatest.flatspec.AnyFlatSpec

class BankTransactionsProducerScalaTest extends AnyFlatSpec {
  @Test def newRandomTransactionsTest(): Unit = {
    val keyTotest: String = "john"
    val record: ProducerRecord[String, String] = BankTransactionsProducer.newRandomTransaction(keyTotest)
    val key: String = record.key

    assertEquals(key, keyTotest)

    val value = record.value
    val mapper = new ObjectMapper
    val factory = mapper.getFactory
    try {
      val parser = factory.createParser(value)
      val actualObj: JsonNode = mapper.readTree(parser)
      assertNotNull(actualObj)
      val jsonNodeName: JsonNode = actualObj.get("name")
      assertEquals(jsonNodeName.asText, keyTotest)
      val jsonNodeAmount = actualObj.get("amount")
      assertTrue("Error, amount is too low", jsonNodeAmount.asInt >= 0)
      assertTrue("Error, amount is too high", jsonNodeAmount.asInt <= 100)
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }
}