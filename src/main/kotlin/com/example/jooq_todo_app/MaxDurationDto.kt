package com.example.jooq_todo_app

data class MaxDurationTodoDto(
	val userId: Int,
	val userName: String,
	val todoId: Int,
	val title: String,
	val duration: Int // 分
)