package com.backend

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import kotlin.test.Test
import kotlin.test.assertEquals

class LambdaMainTest {
    @Test
    fun handRequest() {
        val mainClass = LambdaMain()
        assertEquals("{\"message\":\"hi\"}",
            mainClass.handleRequest(APIGatewayProxyRequestEvent().apply {
                queryStringParameters = mapOf(Pair("name", "hi"))
                requestContext = APIGatewayProxyRequestEvent.ProxyRequestContext().apply { operationName = "SayHello"}
            }, null).body
        )
    }
}
