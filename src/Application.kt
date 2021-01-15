package com.example

import com.example.lotto.lotto
import com.example.user.Users
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.response.*
import io.ktor.routing.*
import org.h2.tools.Server
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

fun Application.module() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092", "-key", "test", "~/test").start()
    Database.connect(url = "jdbc:h2:tcp://localhost:9092/test", driver = "org.h2.Driver")

    transaction {
        SchemaUtils.create(Users)
    }

    routing {
        this.lotto()

        get("/") {
            call.respond(FreeMarkerContent("index.ftl", null))
        }
    }
}

