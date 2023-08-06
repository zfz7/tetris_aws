package com.backend

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.google.gson.Gson
import com.tetris.model.SayHelloRequest
import com.tetris.model.SayHelloResponse

// Lambda handler:
// com.backend.LambdaMain
class LambdaMain : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private val gson = Gson()

    private fun handleSayHello(input: SayHelloRequest): SayHelloResponse {
        if (input.name == "error") {
            throw RuntimeException("oops something when wrong")
        }
        return SayHelloResponse.invoke { message = input.name }
    }

    override fun handleRequest(input: APIGatewayProxyRequestEvent, context: Context?): APIGatewayProxyResponseEvent {
        val res = when (input.requestContext.operationName) {
            "SayHello" -> handleSayHello(SayHelloRequest.invoke { name = input.queryStringParameters["name"] })
            else -> throw RuntimeException("Not Found")
        }

        return APIGatewayProxyResponseEvent().apply { body = gson.toJson(res) }
    }
}

