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
        }

        if (input.equals("bye")) {
            return Command.withType(CommandType.BYE);
        }

        if (input.equals("list")) {
            return Command.withType(CommandType.LIST);
        }

        if (input.equals("sort")) {
            return Command.withType(CommandType.SORT);
        }

        if (matchesCommand(input, "sort")) {
            throw new GeekException(
                    "The sort command does not accept arguments."
            );
        }

        if (matchesCommand(input, "find")) {
            return Command.withKeyword(parseKeyword(input));
        }

        if (matchesCommand(input, "on")) {
            return Command.withDate(parseQueryDate(input));
        }

        if (matchesCommand(input, "mark")) {
            return Command.withTaskNumber(
                    CommandType.MARK,
                    parseTaskNumber(input, "mark")
            );
        }

        if (matchesCommand(input, "unmark")) {
            return Command.withTaskNumber(
                    CommandType.UNMARK,
                    parseTaskNumber(input, "unmark")
            );
        }

        if (matchesCommand(input, "delete")) {
            return Command.withTaskNumber(
                    CommandType.DELETE,
                    parseTaskNumber(input, "delete")
            );
        }

        if (matchesCommand(input, "todo")
                || matchesCommand(input, "deadline")
                || matchesCommand(input, "event")) {
            return Command.withTask(parseTask(input));
        }

        throw new GeekException(
                "I'm sorry, but I don't know what that means :-("
        );
    }

    /**
     * Returns whether input contains the given command word, optionally
     * followed by arguments separated by a space.
     *
     * @param input Complete user input.
     * @param command Command word to match.
     * @return {@code true} if input starts with the complete command word.
     */
    private static boolean matchesCommand(
            String input,
            String command
    ) {
        return input.equals(command)
                || input.startsWith(command + " ");
    }

    /**
     * Extracts and validates the keyword from a find command.
     *
     * @param input Complete find command.
     * @return Trimmed keyword to search for.
     * @throws GeekException If the keyword is missing.
     */
    private static String parseKeyword(String input) {
        String keyword = input
                .substring("find".length())
                .trim();

        if (keyword.isEmpty()) {
            throw new GeekException(
                    "Please provide a keyword after find."
            );
        }

        return keyword;
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
        if (matchesCommand(input, "todo")) {
            return parseTodo(input);
        }

        if (matchesCommand(input, "deadline")) {
            return parseDeadline(input);
        }

        if (matchesCommand(input, "event")) {
            return parseEvent(input);
        }

        throw new GeekException("Unknown task type.");
    }

    private static Task parseTodo(String input) {
        String description = input
                .substring("todo".length())
                .trim();

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
                .substring("deadline".length(), byIndex)
                .trim();
        String deadline = input
                .substring(byIndex + "/by".length())
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
                .substring("event".length(), fromIndex)
                .trim();
        String startTime = input
                .substring(fromIndex + "/from".length(), toIndex)
                .trim();
        String endTime = input
                .substring(toIndex + "/to".length())
                .trim();

        if (description.isEmpty()) {
            throw new GeekException(
                    "The description of an event "
                            + "cannot be empty."
            );
        }

        if (startTime.isEmpty() || endTime.isEmpty()) {
            throw new GeekException(
                    "Both the start and end times are required."
            );
        }

        return Task.newEvent(description, startTime, endTime);
    }

    /**
     * Identifies the operation represented by a parsed command.
     */
    public enum CommandType {
        /** Ends the application. */
        BYE,
        /** Shows every task. */
        LIST,
        /** Sorts dated tasks chronologically and places undated tasks last. */
        SORT,
        /** Finds tasks whose descriptions contain a keyword. */
        FIND,
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
     * @param keyword Keyword carried by a find command, or {@code null}.
     */
    public record Command(
            CommandType type,
            Task task,
            int taskNumber,
            LocalDate date,
            String keyword
    ) {
        private static Command withType(CommandType type) {
            return new Command(type, null, 0, null, null);
        }

        private static Command withTask(Task task) {
            return new Command(
                    CommandType.ADD,
                    task,
                    0,
                    null,
                    null
            );
        }

        private static Command withTaskNumber(
                CommandType type,
                int taskNumber
        ) {
            return new Command(
                    type,
                    null,
                    taskNumber,
                    null,
                    null
            );
        }

        private static Command withDate(LocalDate date) {
            return new Command(
                    CommandType.ON,
                    null,
                    0,
                    date,
                    null
            );
        }

        /**
         * Creates a find command containing the specified keyword.
         *
         * @param keyword Keyword to search for.
         * @return Find command carrying the keyword.
         */
        private static Command withKeyword(String keyword) {
            return new Command(
                    CommandType.FIND,
                    null,
                    0,
                    null,
                    keyword
            );
        }
    }
}
