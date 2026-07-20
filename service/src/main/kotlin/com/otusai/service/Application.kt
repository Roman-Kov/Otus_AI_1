package com.otusai.service

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    configureRouting()
}

fun main() {
    embeddedServer(Netty, port = 8080) {
        module()
    }.start(wait = true)
}
