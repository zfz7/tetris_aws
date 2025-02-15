package com.backend

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

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
//            "{\"region\":\"value1\",\"userPoolId\":\"value2\",\"userPoolWebClientId\":\"value3\",\"authenticationFlowType\":\"USER_PASSWORD_AUTH\"}",
            "{\"authenticationFlowType\":\"USER_PASSWORD_AUTH\"}",
            subject.handleRequest(APIGatewayProxyRequestEvent().apply {
                requestContext = APIGatewayProxyRequestEvent.ProxyRequestContext().apply { operationName = "Info" }
            }, null).body
        )
    }
}
