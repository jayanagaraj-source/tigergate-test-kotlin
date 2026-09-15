package com.tigergate.fixture.sast

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import org.yaml.snakeyaml.Yaml

object Deserialization {
    // CWE-502: native Java deserialisation of untrusted bytes (gadget chains available via commons-collections 3.2.1).
    fun fromBytes(payload: ByteArray): Any? = ObjectInputStream(ByteArrayInputStream(payload)).use { it.readObject() }

    // CWE-502: Jackson default typing enabled on jackson-databind 2.9.8.
    private val mapper = ObjectMapper().apply { enableDefaultTyping() }
    fun fromJson(json: String): Any? = mapper.readValue(json, Any::class.java)

    // CWE-502: SnakeYAML 1.26 default constructor instantiates arbitrary classes (CVE-2022-1471).
    fun fromYaml(yaml: String): Any? = Yaml().load(yaml)
}
