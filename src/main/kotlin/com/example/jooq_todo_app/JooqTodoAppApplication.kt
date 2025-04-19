package com.example.jooq_todo_app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JooqTodoAppApplication

fun main(args: Array<String>) {
	runApplication<JooqTodoAppApplication>(*args)
}
