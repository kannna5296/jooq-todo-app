package com.example.jooq_todo_app

import com.example.todo.jooq.generated.tables.Todos.TODOS
import com.example.todo.jooq.generated.tables.Users.USERS
import com.example.todo.jooq.generated.tables.TodoLogs.TODO_LOGS
import org.jooq.DSLContext
import org.jooq.DatePart
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

@Repository
class TodoRepository(
	private val dsl: DSLContext
) {

	fun findAll(): List<TodoDto> {
		val result = dsl
			.select(
				TODOS.ID,
				TODOS.TITLE,
				TODOS.DONE
			).from(TODOS)
			.fetch()

		return result.map { record ->
			TodoDto(
				id = record[TODOS.ID]!!,
				title = record[TODOS.TITLE]!!,
				completed = record[TODOS.DONE]!! == 1.toByte()
			)
		}
	}

	fun create(title: String): String {
		val record = dsl.insertInto(TODOS)
			.set(TODOS.TITLE, title)
			.set(TODOS.DONE, 0)
			.returning(TODOS.ID)  // ← 追加されたIDを取得したい場合
			.fetchOne()

		return record?.get(TODOS.ID).toString()
	}

	// 今月最も時間がかかったタスクをサブクエリを使って取得する
	// サブクエリを使って、今月最も時間がかかったタスクを取得する
	fun fetchMostTimeConsumingTaskForThisMonth(): List<MaxDurationTodoDto> {
		val now = LocalDate.now()
		val currentMonthStart = now.withDayOfMonth(1).atStartOfDay()
		val currentMonthEnd = now.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59)

		val subquery = dsl
			.select(
				TODOS.USER_ID,
				TODOS.ID.`as`("todo_id"),
				TODOS.TITLE,
				timestampDiff(
					DatePart.MINUTE,
					TODO_LOGS.START_TIME.cast(Timestamp::class.java),
					TODO_LOGS.END_TIME.cast(Timestamp::class.java)
				).`as`("duration_in_minutes")
			)
			.from(TODOS)
			.join(TODO_LOGS).on(TODOS.ID.eq(TODO_LOGS.TODO_ID))
			.where(TODOS.DONE.eq(1))  // 完了したタスク
			.and(TODO_LOGS.START_TIME.between(
				currentMonthStart,
				currentMonthEnd
			))  // 今月のタスクをフィルタ
			.orderBy(field("duration_in_minutes").desc()) // 最も時間がかかったタスクを取得
			.asTable("subquery") // サブクエリとして使う

		val result = dsl
			.select(
				USERS.ID.`as`("user_id"),
				USERS.NAME.`as`("user_name"),
				subquery.field("todo_id"),
				subquery.field("title"),
				subquery.field("duration_in_minutes")
			)
			.from(subquery)
			.join(USERS).on(subquery.field("user_id", Int::class.java)?.eq(USERS.ID)) // ユーザー情報を結合
			.fetch() // 結果を取得

		// 結果をMaxDurationTodoDtoにマッピング
		return result.map {
			MaxDurationTodoDto(
				userId = it.get("user_id", Int::class.java),
				userName = it.get("user_name", String::class.java),
				todoId = it.get("todo_id", Int::class.java),
				title = it.get("title", String::class.java),
				duration = it.get("duration_in_minutes", Int::class.java)
			)
		}
	}
}