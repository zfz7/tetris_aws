package com.backend

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.tetris.model.SayHelloRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LambdaMainTest {
    @Test
    fun handRequest() {
        val mainClass = LambdaMain()
        assertEquals("{\"message\":\"hi\"}", mainClass.handleRequest(APIGatewayProxyRequestEvent().apply { queryStringParameters =
            mapOf(Pair("name","hi"))
        }, null).body)
    }
}
