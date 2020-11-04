package com.example

import com.example.lotto.LottoGenerator
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.response.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

fun Application.module() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    val lottoGenerator = LottoGenerator()

    routing {
        get("/") {
            call.respond(FreeMarkerContent("index.ftl", null))
        }

        get("/lotto") {
            call.respond(
                    FreeMarkerContent(
                            "lotto/lotto.ftl",
                            mapOf("generated" to lottoGenerator.generateLotto())
                    )
            )
        }
    }
}

