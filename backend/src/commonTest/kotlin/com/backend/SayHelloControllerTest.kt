package com.backend

import com.tetris.model.models.Runtime
import com.tetris.model.models.SayHelloRequestContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SayHelloControllerTest {

    @Test
    fun handleSayHelloRequest() {
        val result = SayHelloController.handleSayHello(
            input = SayHelloRequestContent(name = "hi"),
            runtime = Runtime.JAVA_VIRTUAL_MACHINE
        )
        assertEquals(result.message, "hi")
    }

    @Test
    fun handleSayHelloRequestError() {
        val ex = assertFailsWith<ApiError> {
            SayHelloController.handleSayHello(
                input = SayHelloRequestContent(name = "400"),
                runtime = Runtime.JAVA_VIRTUAL_MACHINE
            )
        }
        assertEquals(ex.errorMessage, "Throwing 400 error")

        val e = assertFailsWith<RuntimeException> {
            SayHelloController.handleSayHello(
                input = SayHelloRequestContent(name = "500"),
                runtime = Runtime.JAVA_VIRTUAL_MACHINE
            )
        }
        assertEquals(e.message, "This is an unmapped error will result in 500")
    }
}
