package com.example.jooq_todo_app

import org.springframework.stereotype.Service

@Service
class TodoService(
	private val repository: TodoRepository,
) {

	fun findAll(): List<TodoDto> {
		return repository.findAll()
	}

	fun create(title: String): String {
		return repository.create(title)
	}

	fun getMaxDurationTodosThisMonth(): List<MaxDurationTodoDto> {
		return repository.fetchMostTimeConsumingTaskForThisMonth()
	}
}
