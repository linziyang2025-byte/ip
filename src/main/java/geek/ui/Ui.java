package geek.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import geek.task.Task;

/**
 * Reads console commands and displays Geek's user-facing messages.
 */
public class Ui {
    private static final String LINE = "----------------------";

    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern(
                    "MMM d yyyy",
                    Locale.ENGLISH
            );

    private final Scanner scanner;
    private final PrintStream output;
    private final PrintStream errorOutput;

    /**
     * Creates a UI connected to the process's standard streams.
     */
    public Ui() {
        this(System.in, System.out, System.err);
    }

    /**
     * Creates a UI using the supplied streams.
     *
     * @param input Stream from which commands are read.
     * @param output Stream for normal responses and recoverable errors.
     * @param errorOutput Stream for warnings about corrupted saved data.
     */
    public Ui(
            InputStream input,
            PrintStream output,
            PrintStream errorOutput
    ) {
        this.scanner = new Scanner(input);
        this.output = output;
        this.errorOutput = errorOutput;
    }

    /**
     * Reads the next complete command line.
     *
     * @return Command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows the welcome message.
     */
    public void showWelcome() {
        output.println(LINE);
        output.println("Hello! I'm geek.Geek.");
        output.println("What can I do for you?");
        output.println(LINE + "\n");
    }

    /**
     * Shows the goodbye message.
     */
    public void showGoodbye() {
        output.println(LINE);
        output.println("Bye. Hope to see you again soon!");
        output.println(LINE);
    }

    /**
     * Shows all tasks in numbered order.
     *
     * @param tasks Tasks to show.
     */
    public void showTaskList(List<Task> tasks) {
        output.println(LINE);
        output.println("Here are the tasks in your list:");
        output.println(formatTasks(tasks));
        output.println(LINE + "\n");
    }

    /**
     * Shows tasks whose descriptions match a search keyword.
     *
     * @param tasks Matching tasks in their original task-list order.
     */
    public void showMatchingTasks(List<Task> tasks) {
        output.println(LINE);
        output.println(
                "Here are the matching tasks in your list:"
        );

        if (tasks.isEmpty()) {
            output.println("No matching tasks found.");
        } else {
            output.println(formatTasks(tasks));
        }

        output.println(LINE + "\n");
    }

    /**
     * Shows tasks occurring on a specified date.
     *
     * @param date Date being displayed.
     * @param tasks Tasks occurring on the date.
     */
    public void showTasksOnDate(
            LocalDate date,
            List<Task> tasks
    ) {
        output.println(LINE);
        output.println(
                "Here are the tasks on "
                        + date.format(DISPLAY_DATE_FORMAT)
                        + ":"
        );

        if (tasks.isEmpty()) {
            output.println("No tasks found.");
        } else {
            output.println(formatTasks(tasks));
        }

        output.println(LINE + "\n");
    }

    /**
     * Confirms that a task was added and shows the new task count.
     *
     * @param task Added task.
     * @param taskCount Number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.println(LINE);
        output.println("Got it. I've added this task:");
        output.println("  " + task);
        output.println(
                "Now you have " + taskCount
                        + " tasks in the list."
        );
        output.println(LINE + "\n");
    }

    /**
     * Confirms that a task was marked as completed.
     *
     * @param task Marked task.
     */
    public void showTaskMarked(Task task) {
        output.println(LINE);
        output.println("Nice! I've marked this task as done:");
        output.println("  " + task);
        output.println(LINE + "\n");
    }

    /**
     * Confirms that a task was marked as not completed.
     *
     * @param task Unmarked task.
     */
    public void showTaskUnmarked(Task task) {
        output.println(LINE);
        output.println(
                "OK, I've marked this task as not done yet:"
        );
        output.println("  " + task);
        output.println(LINE + "\n");
    }

    /**
     * Confirms that a task was deleted and shows the new task count.
     *
     * @param task Deleted task.
     * @param taskCount Number of tasks after the deletion.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println(LINE);
        output.println("Noted. I've removed this task:");
        output.println("  " + task);
        output.println(
                "Now you have " + taskCount
                        + " tasks in the list."
        );
        output.println(LINE + "\n");
    }

    /**
     * Shows a recoverable error on the normal output stream.
     *
     * @param message Explanation of the error.
     */
    public void showError(String message) {
        output.println(LINE);
        output.println("OOPS!!! " + message);
        output.println(LINE + "\n");
    }

    /**
     * Warns that one corrupted stored record was skipped.
     *
     * @param lineNumber One-based line number of the corrupted record.
     */
    public void showCorruptedTaskWarning(int lineNumber) {
        errorOutput.println(
                "Warning: Skipping corrupted task on line "
                        + lineNumber + "."
        );
    }

    /**
     * Closes the command scanner and its underlying input stream.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Formats tasks as a one-based numbered list.
     *
     * @param tasks Tasks to format.
     * @return Numbered task list without a trailing newline.
     */
    private String formatTasks(List<Task> tasks) {
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
