package com.backend

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.tetris.model.models.SayHelloResponseContent
import kotlinx.datetime.Clock.System
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class LambdaMainTest {
    private lateinit var subject: LambdaMain

    @BeforeTest
    fun setup() {
        subject = LambdaMain()
    }

    @Test
    fun handleSayHelloRequest() {
        assertEquals(
            "{\"message\":\"hi\",\"runtime\":\"Java Virtual Machine\"}",
            subject.handleRequest(APIGatewayProxyRequestEvent().apply {
                body = "{\"name\":\"hi\"}"
                requestContext = APIGatewayProxyRequestEvent.ProxyRequestContext().apply { operationName = "SayHello" }
            }, null).body
        )
    }

    @Test
    fun handleSayHelloRequestWithTIme() {
        val result = Json.decodeFromString<SayHelloResponseContent>(subject.handleRequest(APIGatewayProxyRequestEvent().apply {
            body = "{\"name\":\"time\"}"
            requestContext = APIGatewayProxyRequestEvent.ProxyRequestContext().apply { operationName = "SayHello" }
        }, null).body)
        assertEquals(result.message, "time")
        assertTrue(result.time!!.minus(System.now()) < 10.seconds)
    }

    @Test
    fun handleSayHelloErrors() {
        assertEquals(
            "{\"errorMessage\":\"Throwing 400 error\"}",
            subject.handleRequest(APIGatewayProxyRequestEvent().apply {
                body = "{\"name\":\"400\"}"
                requestContext = APIGatewayProxyRequestEvent.ProxyRequestContext().apply { operationName = "SayHello" }
            }, null).body
        )
    }

    @Test
    fun handleInfoRequest() {
        assertEquals(
            "{\"authenticationFlowType\":\"USER_PASSWORD_AUTH\"}",
            subject.handleRequest(APIGatewayProxyRequestEvent().apply {
                requestContext = APIGatewayProxyRequestEvent.ProxyRequestContext().apply { operationName = "Info" }
            }, null).body
        )
    }
}
