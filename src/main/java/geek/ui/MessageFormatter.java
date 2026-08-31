package geek.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import geek.task.Task;

/**
 * Creates user-facing response text shared by the console and graphical UIs.
 */
public final class MessageFormatter {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern(
                    "MMM d yyyy",
                    Locale.ENGLISH
            );

    private MessageFormatter() {
    }

    /**
     * Returns the application welcome message.
     *
     * @return Welcome message.
     */
    public static String welcome() {
        return "Hello! I'm Geek.\n"
                + "What can I do for you?";
    }

    /**
     * Returns the application goodbye message.
     *
     * @return Goodbye message.
     */
    public static String goodbye() {
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Formats the complete task list.
     *
     * @param tasks Tasks to show.
     * @return Task-list response.
     */
    public static String taskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "Your task list is empty.";
        }

        return "Here are the tasks in your list:\n"
                + formatTasks(tasks);
    }

    /**
     * Formats tasks matching a search keyword.
     *
     * @param tasks Matching tasks.
     * @return Search response.
     */
    public static String matchingTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "No matching tasks found.";
        }

        return "Here are the matching tasks in your list:\n"
                + formatTasks(tasks);
    }

    /**
     * Formats tasks occurring on a date.
     *
     * @param date Date being shown.
     * @param tasks Tasks occurring on the date.
     * @return Dated-task response.
     */
    public static String tasksOnDate(
            LocalDate date,
            List<Task> tasks
    ) {
        String heading = "Here are the tasks on "
                + date.format(DISPLAY_DATE_FORMAT) + ":";

        if (tasks.isEmpty()) {
            return heading + "\nNo tasks found.";
        }

        return heading + "\n" + formatTasks(tasks);
    }

    /**
     * Formats confirmation that a task was added.
     *
     * @param task Added task.
     * @param taskCount Number of tasks after the addition.
     * @return Addition response.
     */
    public static String taskAdded(Task task, int taskCount) {
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + taskCount
                + " tasks in the list.";
    }

    /**
     * Formats confirmation that a task was marked.
     *
     * @param task Marked task.
     * @return Mark response.
     */
    public static String taskMarked(Task task) {
        return "Nice! I've marked this task as done:\n  " + task;
    }

    /**
     * Formats confirmation that a task was unmarked.
     *
     * @param task Unmarked task.
     * @return Unmark response.
     */
    public static String taskUnmarked(Task task) {
        return "OK, I've marked this task as not done yet:\n  " + task;
    }

    /**
     * Formats confirmation that a task was deleted.
     *
     * @param task Deleted task.
     * @param taskCount Number of tasks after the deletion.
     * @return Deletion response.
     */
    public static String taskDeleted(Task task, int taskCount) {
        return "Noted. I've removed this task:\n  " + task
                + "\nNow you have " + taskCount
                + " tasks in the list.";
    }

    /**
     * Formats a recoverable error.
     *
     * @param message Explanation of the error.
     * @return Error response.
     */
    public static String error(String message) {
        return "OOPS!!! " + message;
    }

    private static String formatTasks(List<Task> tasks) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < tasks.size(); i++) {
            result.append(i + 1)
                    .append(". ")
                    .append(tasks.get(i));

            if (i < tasks.size() - 1) {
                result.append("\n");
            }
        }

        return result.toString();
    }
}
