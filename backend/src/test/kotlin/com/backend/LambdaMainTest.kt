package com.backend

import kotlin.test.Test
import kotlin.test.assertNotNull

class LambdaMainTest {
    @Test
    fun handRequest() {
        val mainClass = LambdaMain()
        assertNotNull(mainClass.handleRequest(emptyMap(), null))
    }
}
