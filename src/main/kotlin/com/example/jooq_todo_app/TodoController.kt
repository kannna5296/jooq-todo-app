package com.example.jooq_todo_app

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/todos")
class TodoController(
	private val todoService: TodoService
) {
	@GetMapping
	fun getAll(): List<TodoDto> = todoService.findAll()

	@PostMapping
	fun create(@RequestBody req: CreateTodoRequest): String =
		todoService.create(req.title)

	@GetMapping("/max-duration-this-month")
	fun getMaxDurationTodosThisMonth(): List<MaxDurationTodoDto> {
		return todoService.getMaxDurationTodosThisMonth()
	}
}

data class CreateTodoRequest(val title: String)
