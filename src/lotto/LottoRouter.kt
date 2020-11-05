package com.example.lotto

import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.response.*
import io.ktor.routing.*

fun Routing.lotto() {
    val lottoGenerator = LottoGenerator()

    get("/lotto") {
        call.respond(
            FreeMarkerContent(
                "lotto/lotto.ftl",
                mapOf("generated" to lottoGenerator.generateLotto())
            )
        )
    }
}