package geek.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import geek.exception.GeekException;
import geek.task.Task;
import geek.time.DateTimeParser;

public final class Parser {
    private Parser() {
    }

    public static Command parse(String input) {
        if (input == null || input.isBlank()) {
            throw new GeekException("Please enter a command.");
        } else if (input.equals("bye")) {
            return Command.withType(CommandType.BYE);
        } else if (input.equals("list")) {
            return Command.withType(CommandType.LIST);
        } else if (input.equals("find")
                || input.startsWith("find ")) {
            return Command.withKeyword(parseKeyword(input));
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

    public enum CommandType {
        BYE,
        LIST,
        /** Finds tasks whose descriptions contain a keyword. */
        FIND,
        ON,
        MARK,
        UNMARK,
        DELETE,
        ADD
    }

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