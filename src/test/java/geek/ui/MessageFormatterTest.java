package geek.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import geek.task.Task;

/**
 * Tests user-facing task-list formatting.
 */
class MessageFormatterTest {
    @Test
    void taskList_multipleTasks_numbersAndJoinsTasksInOrder() {
        List<Task> tasks = List.of(
                Task.newTodo("read book"),
                Task.newTodo("write code")
        );

        assertEquals(
                "Here are the tasks in your list:\n"
                        + "1. [T][ ] read book\n"
                        + "2. [T][ ] write code",
                MessageFormatter.taskList(tasks)
        );
    }
}
