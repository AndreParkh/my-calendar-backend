package io.github.andreparkh

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class MyCalendarBackendApplication

fun main(args: Array<String>) {
    println("Hello, world!")
    runApplication<MyCalendarBackendApplication>(*args)
}
