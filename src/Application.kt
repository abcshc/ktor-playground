package com.example

import com.example.lotto.LottoGenerator
import com.example.user.Users
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import org.h2.tools.Server
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

fun Application.module() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    install(ContentNegotiation) {
        jackson()
    }

    Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092", "-key", "test", "~/test").start()
    Database.connect(url = "jdbc:h2:tcp://localhost:9092/test", driver = "org.h2.Driver")

    transaction {
        SchemaUtils.create(Users)
    }
    val lottoGenerator = LottoGenerator()

    routing {
        get("/lotto") {
            call.respond(
                FreeMarkerContent(
                    "lotto/lotto.ftl",
                    mapOf("generated" to lottoGenerator.generateLotto())
                )
            )
        }

        post("/user") {
            var id: Int? = null
            transaction {
                id = Users.insert {
                    it[name] = "name"
                    it[gender] = "M"
                    it[phone] = "01012341234"
                }[Users.id].value
            }

            call.respond(object {
                val userId = id
            })
        }

        get("/") {
            call.respond(FreeMarkerContent("index.ftl", null))
        }
    }
}

