package com.backend

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables
import uk.org.webcompere.systemstubs.jupiter.SystemStub
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(SystemStubsExtension::class)
class LambdaMainTest {
    private lateinit var subject: LambdaMain

    @SystemStub
    private lateinit var environmentVariables: EnvironmentVariables

    @BeforeEach
    fun setup() {
        subject = LambdaMain()
    }

    @Test
    fun handleSayHelloRequest() {
        assertEquals(
            "{\"message\":\"hi\"}",
            subject.handleRequest(APIGatewayProxyRequestEvent().apply {
                queryStringParameters = mapOf(Pair("name", "hi"))
                requestContext = APIGatewayProxyRequestEvent.ProxyRequestContext().apply { operationName = "SayHello" }
            }, null).body
        )
    }

    @Test
    fun handleSayHelloErrors() {
        assertEquals(
            "{\"errorMessage\":\"Throwing 400 error\"}",
            subject.handleRequest(APIGatewayProxyRequestEvent().apply {
                queryStringParameters = mapOf(Pair("name", "400"))
                requestContext = APIGatewayProxyRequestEvent.ProxyRequestContext().apply { operationName = "SayHello" }
            }, null).body
        )
    }

    @Test
    fun handleInfoRequest() {
        environmentVariables.set("REGION", "value1");
        environmentVariables.set("USER_POOL_ID", "value2");
        environmentVariables.set("USER_POOL_WEB_CLIENT_ID", "value3");
        assertEquals(
            "{\"authenticationFlowType\":\"USER_PASSWORD_AUTH\",\"region\":\"value1\",\"userPoolId\":\"value2\",\"userPoolWebClientId\":\"value3\"}",
            subject.handleRequest(APIGatewayProxyRequestEvent().apply {
                requestContext = APIGatewayProxyRequestEvent.ProxyRequestContext().apply { operationName = "Info" }
            }, null).body
        )
    }
}
