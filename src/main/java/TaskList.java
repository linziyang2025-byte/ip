import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TaskList {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern(
                    "MMM d yyyy",
                    Locale.ENGLISH
            );

    private final Storage storage =
            new Storage(Path.of("data", "geek.txt"));

    private List<Task> tasks;

    public TaskList() {
        try {
            this.tasks = storage.loadTasks();
        } catch (IOException e) {
            this.tasks = new ArrayList<>();
            printError("I could not load the saved tasks.");
        }
    }

    public boolean receiving(String input) {
        try {
            if (input == null || input.isBlank()) {
                throw new GeekException("Please enter a command.");
            } else if (input.equals("bye")) {
                end();
                return false;
            } else if (input.equals("list")) {
                printTasks();
            } else if (input.equals("on")
                    || input.startsWith("on ")) {
                LocalDate date = parseQueryDate(input);
                printTasksOnDate(date);
            } else if (input.equals("mark")
                    || input.startsWith("mark ")) {
                int taskNumber = parseTaskNumber(input, "mark");
                checkTaskNumber(taskNumber);
                tasks.get(taskNumber - 1).mark();
                saveTasks();
            } else if (input.equals("unmark")
                    || input.startsWith("unmark ")) {
                int taskNumber = parseTaskNumber(input, "unmark");
                checkTaskNumber(taskNumber);
                tasks.get(taskNumber - 1).unmark();
                saveTasks();
            } else if (input.equals("delete")
                    || input.startsWith("delete ")) {
                int taskNumber = parseTaskNumber(input, "delete");
                checkTaskNumber(taskNumber);
                deleteTask(taskNumber);
            } else if (input.startsWith("todo ")
                    || input.equals("todo")
                    || input.startsWith("deadline ")
                    || input.equals("deadline")
                    || input.startsWith("event ")
                    || input.equals("event")) {
                addTask(input);
            } else {
                throw new GeekException(
                        "I'm sorry, but I don't know what that means :-("
                );
            }
        } catch (GeekException e) {
            printError(e.getMessage());
        } catch (DateTimeParseException e) {
            printError(
                    "Use a supported date or time format, "
                            + "such as 2019-12-02, "
                            + "2/12/2019 1800, or "
                            + "Dec 2 2019 6:00 PM."
            );
        } catch (NumberFormatException e) {
            printError(
                    "Please enter a valid task number."
            );
        }

        return true;
    }

    private int getLen() {
        return tasks.size();
    }

    private void deleteTask(int taskNumber) {
        Task deletedTask = tasks.remove(taskNumber - 1);
        saveTasks();

        System.out.println("----------------------");
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + deletedTask);
        System.out.println(
                "Now you have " + tasks.size()
                        + " tasks in the list."
        );
        System.out.println("----------------------\n");
    }

    private int parseTaskNumber(
            String input,
            String command
    ) {
        String numberText =
                input.substring(command.length()).trim();

        if (numberText.isEmpty()) {
            throw new GeekException(
                    "Please provide a task number after "
                            + command + "."
            );
        }

        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new GeekException(
                    "Please enter a valid task number."
            );
        }
    }

    private void printTasks() {
        System.out.println("----------------------");
        System.out.println("Here are the tasks in your list:");
        System.out.println(this);
        System.out.println("----------------------\n");
    }

    private void printError(String message) {
        System.out.println("----------------------");
        System.out.println("OOPS!!! " + message);
        System.out.println("----------------------\n");
    }

    private void end() {
        System.out.println("----------------------");
        System.out.println(
                "Bye. Hope to see you again soon!"
        );
        System.out.println("----------------------");
    }

    private void checkTaskNumber(int taskNumber) {
        if (taskNumber < 1
                || taskNumber > tasks.size()) {
            throw new GeekException(
                    "That task number does not exist."
            );
        }
    }

    public void addTask(String input) {
        Task task;

        if (input.equals("todo")
                || input.startsWith("todo ")) {
            String description =
                    input.substring(4).trim();

            if (description.isEmpty()) {
                throw new GeekException(
                        "The description of a todo cannot be empty."
                );
            }

            task = Task.newTodo(description);

        } else if (input.equals("deadline")
                || input.startsWith("deadline ")) {
            int byIndex = input.indexOf("/by");

            if (byIndex == -1) {
                throw new GeekException(
                        "Deadline format: "
                                + "deadline <description> "
                                + "/by <date or date-time>"
                );
            }

            String description =
                    input.substring(8, byIndex).trim();
            String deadline =
                    input.substring(byIndex + 3).trim();

            if (description.isEmpty()) {
                throw new GeekException(
                        "The description of a deadline "
                                + "cannot be empty."
                );
            }

            if (deadline.isEmpty()) {
                throw new GeekException(
                        "The deadline date cannot be empty."
                );
            }

            task = Task.newDeadline(
                    description,
                    deadline
            );

        } else if (input.equals("event")
                || input.startsWith("event ")) {
            int fromIndex = input.indexOf("/from");
            int toIndex = input.indexOf("/to");

            if (fromIndex == -1
                    || toIndex == -1
                    || fromIndex >= toIndex) {
                throw new GeekException(
                        "Event format: "
                                + "event <description> "
                                + "/from <date-time> "
                                + "/to <date-time>"
                );
            }

            String description =
                    input.substring(5, fromIndex).trim();
            String from =
                    input.substring(
                            fromIndex + 5,
                            toIndex
                    ).trim();
            String to =
                    input.substring(toIndex + 3).trim();

            if (description.isEmpty()) {
                throw new GeekException(
                        "The description of an event "
                                + "cannot be empty."
                );
            }

            if (from.isEmpty() || to.isEmpty()) {
                throw new GeekException(
                        "Both the start and end times "
                                + "are required."
                );
            }

            task = Task.newEvent(
                    description,
                    from,
                    to
            );

        } else {
            throw new GeekException(
                    "Unknown task type."
            );
        }

        tasks.add(task);
        saveTasks();

        System.out.println("----------------------");
        System.out.println(
                "Got it. I've added this task:"
        );
        System.out.printf("  %s%n", task);
        System.out.printf(
                "Now you have %s tasks in the list.%n",
                getLen()
        );
        System.out.println("----------------------\n");
    }

    private LocalDate parseQueryDate(String input) {
        String dateText = input
                .substring("on".length())
                .trim();

        if (dateText.isEmpty()) {
            throw new GeekException(
                    "Please provide a date after on."
            );
        }

        try {
            return DateTimeParser.parseDate(dateText);
        } catch (DateTimeParseException e) {
            throw new GeekException(
                    "Use a supported query date, such as "
                            + "2019-12-02, 2/12/2019, "
                            + "or Dec 2 2019."
            );
        }
    }

    private void printTasksOnDate(LocalDate date) {
        List<Task> matchingTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.occursOn(date)) {
                matchingTasks.add(task);
            }
        }

        System.out.println("----------------------");
        System.out.println(
                "Here are the tasks on "
                        + date.format(DISPLAY_DATE_FORMAT)
                        + ":"
        );

        if (matchingTasks.isEmpty()) {
            System.out.println("No tasks found.");
        } else {
            for (int i = 0; i < matchingTasks.size(); i++) {
                System.out.println(
                        (i + 1) + ". "
                                + matchingTasks.get(i)
                );
            }
        }

        System.out.println("----------------------\n");
    }

    private void saveTasks() {
        try {
            storage.saveTasks(tasks);
        } catch (IOException e) {
            printError("I could not save the tasks.");
        }
    }

    @Override
    public String toString() {
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
