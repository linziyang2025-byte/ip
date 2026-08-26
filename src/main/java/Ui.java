import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

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

    public Ui() {
        this(System.in, System.out, System.err);
    }

    public Ui(
            InputStream input,
            PrintStream output,
            PrintStream errorOutput
    ) {
        this.scanner = new Scanner(input);
        this.output = output;
        this.errorOutput = errorOutput;
    }

    public String readCommand() {
        return scanner.nextLine();
    }

    public void showWelcome() {
        output.println(LINE);
        output.println("Hello! I'm Geek.");
        output.println("What can I do for you?");
        output.println(LINE + "\n");
    }

    public void showGoodbye() {
        output.println(LINE);
        output.println("Bye. Hope to see you again soon!");
        output.println(LINE);
    }

    public void showTaskList(List<Task> tasks) {
        output.println(LINE);
        output.println("Here are the tasks in your list:");
        output.println(formatTasks(tasks));
        output.println(LINE + "\n");
    }

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

    public void showTaskMarked(Task task) {
        output.println(LINE);
        output.println("Nice! I've marked this task as done:");
        output.println("  " + task);
        output.println(LINE + "\n");
    }

    public void showTaskUnmarked(Task task) {
        output.println(LINE);
        output.println(
                "OK, I've marked this task as not done yet:"
        );
        output.println("  " + task);
        output.println(LINE + "\n");
    }

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

    public void showError(String message) {
        output.println(LINE);
        output.println("OOPS!!! " + message);
        output.println(LINE + "\n");
    }

    public void showCorruptedTaskWarning(int lineNumber) {
        errorOutput.println(
                "Warning: Skipping corrupted task on line "
                        + lineNumber + "."
        );
    }

    public void close() {
        scanner.close();
    }

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
