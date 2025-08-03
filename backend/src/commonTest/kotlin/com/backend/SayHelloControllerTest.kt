package com.backend

import com.tetris.model.models.SayHelloRequestContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SayHelloControllerTest {

    @Test
    fun handleSayHelloRequest() {
        val result = SayHelloController.handleSayHello(SayHelloRequestContent(
            name = "hi"
        ))
        assertEquals(result.message, "hi")
    }

    @Test
    fun handleSayHelloRequestError() {
        val ex = assertFailsWith<ApiError> {
            SayHelloController.handleSayHello(SayHelloRequestContent(name = "400"))
        }
        assertEquals(ex.errorMessage, "Throwing 400 error")

        val e = assertFailsWith<RuntimeException> {
            SayHelloController.handleSayHello(SayHelloRequestContent(name = "500"))
        }
        assertEquals(e.message, "This is an unmapped error will result in 500")
    }
}
