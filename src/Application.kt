package com.example

import com.example.lotto.LottoGenerator
import com.example.user.Users
import com.example.web.PostUser
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.h2.tools.Server
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

fun Application.module() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    install(ContentNegotiation) {
        jackson()
    }

    Server.createTcpServer(
        "-tcp",
        "-tcpAllowOthers",
        "-tcpPort",
        "9092",
        "-key",
        "test",
        File(".").absolutePath + "/h2/test"
    ).start()
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

        post("/users") {
            val content = call.receive<PostUser>()
            var id: Int? = null
            transaction {
                id = Users.insert {
                    it[name] = content.name
                    it[gender] = content.gender.toString()
                    it[phone] = content.phone
                }[Users.id].value
            }

            call.respond(object {
                val userId = id
            })
        }

        get("/users") {
            var temp: Any? = null
            transaction {
                temp = Users.selectAll().map {
                    object {
                        val name: String = it[Users.name]
                        val gender: String = it[Users.gender]
                        val phone: String = it[Users.phone]
                    }
                }
            }
            call.respond(temp as Any)
        }

        get("/") {
            call.respond(FreeMarkerContent("index.ftl", null))
        }
    }
}

