package com.backend

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class LambdaMainTest {

    private lateinit var subject: LambdaMain

    @BeforeEach
    fun setup(){
        subject = LambdaMain()
    }
    @Test
    fun handRequest() {
        assertEquals("{\"message\":\"hi\"}",
            subject.handleRequest(APIGatewayProxyRequestEvent().apply {
                queryStringParameters = mapOf(Pair("name", "hi"))
                requestContext = APIGatewayProxyRequestEvent.ProxyRequestContext().apply { operationName = "SayHello"}
            }, null).body
        )
    }

    @Test
    fun handErrors() {
        assertEquals("{\"errorMessage\":\"Throwing 400 error\"}",
            subject.handleRequest(APIGatewayProxyRequestEvent().apply {
                queryStringParameters = mapOf(Pair("name", "400"))
                requestContext = APIGatewayProxyRequestEvent.ProxyRequestContext().apply { operationName = "SayHello"}
            }, null).body
        )
    }
}
