package com.backend

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.tetris.model.models.InfoResponseContent
import com.tetris.model.models.SayHelloRequestContent
import kotlinx.serialization.json.Json


// Lambda handler:
// com.backend.LambdaMain
class LambdaMain : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private fun handleInfo(): InfoResponseContent =
        InfoResponseContent(
            region = System.getenv("REGION"),
            userPoolId = System.getenv("USER_POOL_ID"),
            userPoolWebClientId = System.getenv("USER_POOL_WEB_CLIENT_ID"),
            authenticationFlowType = "USER_PASSWORD_AUTH",
        )


    override fun handleRequest(input: APIGatewayProxyRequestEvent, context: Context?): APIGatewayProxyResponseEvent {
        val baseResponseWithCors = APIGatewayProxyResponseEvent().apply {
            headers = mapOf(
                Pair("Access-Control-Allow-Origin", "*"),
                Pair("Access-Control-Allow-Headers", "*"),
                Pair("Access-Control-Allow-Methods", "OPTIONS, POST, GET"),
                Pair("Access-Control-Allow-Credentials", "true")
            )
        }
        try {
            val responseBody: String = when (input.requestContext.operationName) {
                "SayHello" -> Json.encodeToString(
                    SayHelloController.handleSayHello(Json.decodeFromString<SayHelloRequestContent>(input.body))
                )

                "Info" -> Json.encodeToString(handleInfo())
                else -> return baseResponseWithCors.apply { statusCode = 404 }
            }
            return baseResponseWithCors.apply { body = responseBody }
        } catch (e: ApiError) {
            return baseResponseWithCors.apply {
                statusCode = 400; body = Json.encodeToString(e)
            }
        }
    }
}