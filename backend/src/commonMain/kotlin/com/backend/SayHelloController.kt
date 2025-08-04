package com.backend

import com.tetris.model.models.Runtime
import com.tetris.model.models.SayHelloRequestContent
import com.tetris.model.models.SayHelloResponseContent
import kotlinx.serialization.Serializable
import kotlin.time.Clock

object SayHelloController {
    fun handleSayHello(input: SayHelloRequestContent, runtime: Runtime): SayHelloResponseContent {
        if (input.name == "400") {
            throw ApiError(errorMessage = "Throwing 400 error")
        }
        if (input.name == "500") {
            throw RuntimeException("This is an unmapped error will result in 500")
        }
        if (input.name == "time") {
            return SayHelloResponseContent(
                message = input.name,
                runtime = runtime,
                time = Clock.System.now()
            )
        }
        return SayHelloResponseContent(message = input.name, runtime = runtime)
    }
}

//TODO this type must be manually kept in sync
@Serializable
data class ApiError(
    val errorMessage: String
) : Throwable()
