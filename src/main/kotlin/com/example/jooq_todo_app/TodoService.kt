package com.example.jooq_todo_app

import org.jooq.DSLContext
import com.example.todo.jooq.generated.tables.Todos
import org.springframework.stereotype.Service

@Service
class TodoService(
	private val dsl: DSLContext
) {

	fun findAll(): List<TodoDto> {
		val result = dsl
			.select(
				Todos.TODOS.ID,
				Todos.TODOS.TITLE,
				Todos.TODOS.COMPLETED
			).from(Todos.TODOS)
			.fetch()

		return result.map { record ->
			TodoDto(
				id = record[Todos.TODOS.ID]!!,
				title = record[Todos.TODOS.TITLE]!!,
				completed = record[Todos.TODOS.COMPLETED]!! == 1.toByte()
			)
		}
	}

	fun create(title: String): String {
		val record = dsl.insertInto(Todos.TODOS)
			.set(Todos.TODOS.TITLE, title)
			.set(Todos.TODOS.COMPLETED, 0)
			.returning(Todos.TODOS.ID)  // ← 追加されたIDを取得したい場合
			.fetchOne()

		return record?.get(Todos.TODOS.ID).toString()
	}
}
