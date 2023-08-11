package com.backend

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.tetris.model.SayHelloRequest
import com.tetris.model.SayHelloResponse
import com.tetris.model.InfoRequest
import com.tetris.model.InfoResponse


// Lambda handler:
// com.backend.LambdaMain
class LambdaMain : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private val gson = Gson()
    private fun handleSayHello(input: SayHelloRequest): SayHelloResponse {
        if (input.name == "400") {
            throw ApiError(errorMessage = "Throwing 400 error")
        }
        if (input.name == "500") {
            throw RuntimeException("This is an unmapped error will result in 500")
        }
        return SayHelloResponse.invoke { message = input.name }
    }

    private fun handleInfo(input: InfoRequest): InfoResponse =
        InfoResponse.invoke {
            region = System.getenv("REGION")
            userPoolId = System.getenv("USER_POOL_ID")
            userPoolWebClientId =  System.getenv("USER_POOL_WEB_CLIENT_ID")
            authenticationFlowType = "USER_PASSWORD_AUTH"
        }


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
                "SayHello" -> gson.toJson(handleSayHello(SayHelloRequest.invoke {
                    name = input.queryStringParameters["name"]
                }))

                "Info" -> gson.toJson(handleInfo(InfoRequest.invoke { }))
                else -> return baseResponseWithCors.apply { statusCode = 404 }
            }
            return baseResponseWithCors.apply { body = responseBody }
        } catch (e: ApiError) {
            val exceptionGson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
            return baseResponseWithCors.apply {
                statusCode = 400; body = exceptionGson.toJson(e, ApiError::class.java)
            }
        }
    }
}

//TODO this type must be manually kept in sync
data class ApiError(
    @Expose
    val errorMessage: String
) : Throwable()
