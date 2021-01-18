package com.example

import com.example.lotto.LottoGenerator
import com.example.user.Users
import com.example.web.*
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.h2.tools.Server
import org.jetbrains.exposed.sql.*
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
    install(StatusPages) {
        exception<Throwable> {
            call.respond(HttpStatusCode.InternalServerError)
        }
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
            val request = call.receive<PostUsersRequest>()
            val id: Int = transaction {
                Users.insert {
                    it[name] = request.name
                    it[gender] = request.gender.toString()
                    it[phone] = request.phone
                }[Users.id].value
            }
            call.respond(HttpStatusCode.Created, PostUsersResponse(id))
        }

        get("/users") {
            val result: List<GetUserResponse> = transaction {
                Users.selectAll().map {
                    GetUserResponse(it[Users.id].value, it[Users.name], it[Users.gender], it[Users.phone])
                }
            }
            call.respond(GetUsersResponse(result))
        }

        put("/users") {
            val request = call.receive<PutUsersRequest>()
            val result = transaction {
                Users.update({ Users.id eq request.id }) {
                    it[name] = request.name
                    it[gender] = request.gender.toString()
                    it[phone] = request.phone
                }
            }
            if (result == 0) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("존재하지 않는 회원입니다."))
            } else {
                call.respond(HttpStatusCode.NoContent)
            }

        }

        delete("/users") {
            val request = call.receive<DeleteUsersRequest>()
            val result = transaction {
                Users.deleteWhere { Users.id eq request.id }
            }
            if (result == 0) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("존재하지 않는 회원입니다."))
            } else {
                call.respond(HttpStatusCode.NoContent)
            }
        }

        get("/") {
            call.respond(FreeMarkerContent("index.ftl", null))
        }
    }
}

