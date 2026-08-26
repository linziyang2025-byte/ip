package geek.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import geek.exception.GeekException;
import geek.task.Task;
import geek.time.DateTimeParser;

/**
 * Converts raw user input into structured commands for Geek.
 */
public final class Parser {
    private Parser() {
    }

    /**
     * Parses one line of user input.
     *
     * @param input Raw command entered by the user.
     * @return Structured command containing the data required for execution.
     * @throws GeekException If the command syntax or task data is invalid.
     * @throws DateTimeParseException If a deadline or event contains an
     *         unsupported date or time.
     */
    public static Command parse(String input) {
        if (input == null || input.isBlank()) {
            throw new GeekException("Please enter a command.");
        } else if (input.equals("bye")) {
            return Command.withType(CommandType.BYE);
        } else if (input.equals("list")) {
            return Command.withType(CommandType.LIST);
        } else if (input.equals("on")
                || input.startsWith("on ")) {
            return Command.withDate(parseQueryDate(input));
        } else if (input.equals("mark")
                || input.startsWith("mark ")) {
            return Command.withTaskNumber(
                    CommandType.MARK,
                    parseTaskNumber(input, "mark")
            );
        } else if (input.equals("unmark")
                || input.startsWith("unmark ")) {
            return Command.withTaskNumber(
                    CommandType.UNMARK,
                    parseTaskNumber(input, "unmark")
            );
        } else if (input.equals("delete")
                || input.startsWith("delete ")) {
            return Command.withTaskNumber(
                    CommandType.DELETE,
                    parseTaskNumber(input, "delete")
            );
        } else if (input.startsWith("todo ")
                || input.equals("todo")
                || input.startsWith("deadline ")
                || input.equals("deadline")
                || input.startsWith("event ")
                || input.equals("event")) {
            return Command.withTask(parseTask(input));
        } else {
            throw new GeekException(
                    "I'm sorry, but I don't know what that means :-("
            );
        }
    }

    /**
     * Extracts the task number following a command keyword.
     *
     * @param input Complete user command.
     * @param command Command keyword preceding the number.
     * @return Number entered by the user.
     * @throws GeekException If the number is missing or is not an integer.
     */
    private static int parseTaskNumber(
            String input,
            String command
    ) {
        String numberText = input
                .substring(command.length())
                .trim();

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

    /**
     * Extracts and parses the date in an {@code on} command.
     *
     * @param input Complete {@code on} command.
     * @return Date to search for.
     * @throws GeekException If the date is missing or unsupported.
     */
    private static LocalDate parseQueryDate(String input) {
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

    private static Task parseTask(String input) {
        if (input.equals("todo")
                || input.startsWith("todo ")) {
            return parseTodo(input);
        } else if (input.equals("deadline")
                || input.startsWith("deadline ")) {
            return parseDeadline(input);
        } else if (input.equals("event")
                || input.startsWith("event ")) {
            return parseEvent(input);
        }

        throw new GeekException("Unknown task type.");
    }

    private static Task parseTodo(String input) {
        String description = input.substring(4).trim();

        if (description.isEmpty()) {
            throw new GeekException(
                    "The description of a todo cannot be empty."
            );
        }

        return Task.newTodo(description);
    }

    /**
     * Parses a deadline command and validates its description and delimiter.
     *
     * @param input Complete deadline command.
     * @return Incomplete deadline task described by the command.
     * @throws GeekException If the description, delimiter, or deadline is
     *         missing.
     * @throws DateTimeParseException If the deadline has an invalid format.
     */
    private static Task parseDeadline(String input) {
        int byIndex = input.indexOf("/by");

        if (byIndex == -1) {
            throw new GeekException(
                    "Deadline format: "
                            + "deadline <description> "
                            + "/by <date or date-time>"
            );
        }

        String description = input
                .substring(8, byIndex)
                .trim();
        String deadline = input
                .substring(byIndex + 3)
                .trim();

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

        return Task.newDeadline(description, deadline);
    }

    /**
     * Parses an event command and validates its description and time range.
     *
     * @param input Complete event command.
     * @return Incomplete event task described by the command.
     * @throws GeekException If required fields are missing or the event does
     *         not end after it starts.
     * @throws DateTimeParseException If either event time has an invalid
     *         format.
     */
    private static Task parseEvent(String input) {
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

        String description = input
                .substring(5, fromIndex)
                .trim();
        String from = input
                .substring(fromIndex + 5, toIndex)
                .trim();
        String to = input
                .substring(toIndex + 3)
                .trim();

        if (description.isEmpty()) {
            throw new GeekException(
                    "The description of an event "
                            + "cannot be empty."
            );
        }

        if (from.isEmpty() || to.isEmpty()) {
            throw new GeekException(
                    "Both the start and end times are required."
            );
        }

        return Task.newEvent(description, from, to);
    }

    /**
     * Identifies the operation represented by a parsed command.
     */
    public enum CommandType {
        /** Ends the application. */
        BYE,
        /** Shows every task. */
        LIST,
        /** Shows dated tasks occurring on a specified date. */
        ON,
        /** Marks a task as completed. */
        MARK,
        /** Marks a task as not completed. */
        UNMARK,
        /** Removes a task. */
        DELETE,
        /** Adds a task. */
        ADD
    }

    /**
     * Contains the type-specific data required to execute a parsed command.
     *
     * Components not used by a command type contain {@code null} or {@code 0}.
     * Task numbers are one-based.
     *
     * @param type Operation to perform.
     * @param task Task carried by an add command, or {@code null}.
     * @param taskNumber Task number carried by a task operation, or {@code 0}.
     * @param date Date carried by an on command, or {@code null}.
     */
    public record Command(
            CommandType type,
            Task task,
            int taskNumber,
            LocalDate date
    ) {
        private static Command withType(CommandType type) {
            return new Command(type, null, 0, null);
        }

        private static Command withTask(Task task) {
            return new Command(
                    CommandType.ADD,
                    task,
                    0,
                    null
            );
        }

        private static Command withTaskNumber(
                CommandType type,
                int taskNumber
        ) {
            return new Command(type, null, taskNumber, null);
        }

        private static Command withDate(LocalDate date) {
            return new Command(CommandType.ON, null, 0, date);
        }
    }
}
