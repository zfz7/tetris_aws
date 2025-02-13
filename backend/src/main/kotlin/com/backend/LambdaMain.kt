package com.backend

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.tetris.model.InfoRequest
import com.tetris.model.InfoResponse
import com.tetris.model.SayHelloRequest
import com.tetris.model.SayHelloResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


// Lambda handler:
// com.backend.LambdaMain
class LambdaMain : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
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
            userPoolWebClientId = System.getenv("USER_POOL_WEB_CLIENT_ID")
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
                "SayHello" -> Json.encodeToString(handleSayHello(SayHelloRequest.invoke {
                    name = input.queryStringParameters["name"]
                }).toDto())

                "Info" -> Json.encodeToString(handleInfo(InfoRequest.invoke { }).toDto())
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

//TODO this type must be manually kept in sync
@Serializable
data class ApiError(
    val errorMessage: String
) : Throwable()

@Serializable
data class SayHelloResponseDto(
    val message: String
)

fun SayHelloResponse.toDto() = SayHelloResponseDto(message = this.message)


@Serializable
data class InfoResponseDto(
    val authenticationFlowType: String,
    val region: String,
    val userPoolId: String,
    val userPoolWebClientId: String,
)

fun InfoResponse.toDto() = InfoResponseDto(
    authenticationFlowType = this.authenticationFlowType.toString(),
    region = this.region.toString(),
    userPoolId = this.userPoolId.toString(),
    userPoolWebClientId = this.userPoolWebClientId.toString()
)