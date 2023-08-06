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
    override fun handleRequest(input: APIGatewayProxyRequestEvent, context: Context?): APIGatewayProxyResponseEvent {
        context?.logger?.log("input $input $context")
        context?.logger?.log(input.requestContext.operationName)
        val gson = Gson()
        val request = SayHelloRequest.invoke { name = input.queryStringParameters["name"] }
        val resp = SayHelloResponse.invoke { message = request.name }
        return APIGatewayProxyResponseEvent().apply { body = gson.toJson(resp) }
    }
}

