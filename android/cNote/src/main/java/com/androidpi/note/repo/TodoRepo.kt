package com.androidpi.note.repo

import com.androidpi.note.entity.Todo
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 * Created by jastrelax on 2017/11/2.
 */
interface TodoRepo {

    /**
     * 获取所有的待办事项。
     */
    fun todoList(): Single<Array<Todo>>

    /**
     * 添加一条新的待办事项。
     */
    fun saveTodoItem(todo: Todo): Completable

    /**
     * 添加一条新的待办事项。
     *
     * @param startTime 待办事项的开始时间
     * @param deadline  待办事项的截止时间
     * @param whatTodo  待办的内容
     */
    fun saveTodoItem(startTime: Date?, deadline: Date?, whatTodo: String): Completable

    /**
     * 获取今天的待办事项。
     *
     * @return 今天的待办事项
     */
    fun todoToday(): Single<Array<Todo>>

    /**
     * 根据id获取本地的todo.
     *
     * @param id 要查询的todo的id
     * @return 根据id查询到的todo事项
     */
    fun getTodo(id: Long): Single<Todo>

    /**
     * 更新一条todo事项。
     *
     * @param todo 要更新的todo事项
     */
    fun updateTodo(todo: Todo): Completable
}