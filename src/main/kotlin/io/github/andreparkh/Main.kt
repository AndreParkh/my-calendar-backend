package io.github.andreparkh

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener


@SpringBootApplication
class MyCalendarBackendApplication

fun main(args: Array<String>) {
    println("Hello, world!")
    runApplication<MyCalendarBackendApplication>(*args)
}
