package com.backend

import com.backend.apigateway.APIGatewayProxy
import com.tetris.model.models.InfoResponseContent
import com.tetris.model.models.SayHelloRequestContent
import com.tetris.model.models.SayHelloResponseContent
import io.github.trueangle.knative.lambda.runtime.LambdaRuntime
import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.events.apigateway.APIGatewayV2Response
import io.github.trueangle.knative.lambda.runtime.handler.LambdaBufferedHandler
import io.github.trueangle.knative.lambda.runtime.log.Log
import io.github.trueangle.knative.lambda.runtime.log.warn
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import platform.posix.getenv

fun main() = LambdaRuntime.run { LambdaMain() }

// Lambda handler:
// com.backend.LambdaMain
class LambdaMain : LambdaBufferedHandler<APIGatewayProxy, APIGatewayV2Response> {
    private fun handleSayHello(input: SayHelloRequestContent): SayHelloResponseContent {
        if (input.name == "400") {
            throw ApiError(errorMessage = "Throwing 400 error")
        }
        if (input.name == "500") {
            throw RuntimeException("This is an unmapped error will result in 500")
        }
        return SayHelloResponseContent(message = input.name)
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun handleInfo(): InfoResponseContent =
        InfoResponseContent(
            region = getenv("REGION")?.toKString() ?: "",
            userPoolId = getenv("USER_POOL_ID")?.toKString() ?: "",
            userPoolWebClientId = getenv("USER_POOL_WEB_CLIENT_ID")?.toKString() ?: "",
            authenticationFlowType = "USER_PASSWORD_AUTH",
        )


    override suspend fun handleRequest(input: APIGatewayProxy, context: Context): APIGatewayV2Response {
        Log.warn("Invoke lambda handler")
        val headers = mapOf(
            Pair("Access-Control-Allow-Origin", "*"),
            Pair("Access-Control-Allow-Headers", "*"),
            Pair("Access-Control-Allow-Methods", "OPTIONS, POST, GET"),
            Pair("Access-Control-Allow-Credentials", "true")
        )
        try {
            val body = when (input.requestContext.operationName) {
                "SayHello" -> Json.encodeToString(
                    handleSayHello(Json.decodeFromString<SayHelloRequestContent>(input.body ?: ""))
                )

                "Info" -> Json.encodeToString(handleInfo())
                else -> throw ApiError("Unknown method")
            }
            return APIGatewayV2Response(
                statusCode = 200,
                headers = headers,
                body = body,
                isBase64Encoded = false,
                cookies = null
            )
        } catch (e: ApiError) {
            return APIGatewayV2Response(
                statusCode = 400,
                headers = headers,
                body = Json.encodeToString(e),
                isBase64Encoded = false,
                cookies = null
            )
        }
    }
}

//TODO this type must be manually kept in sync
@Serializable
data class ApiError(
    val errorMessage: String
) : Throwable()
