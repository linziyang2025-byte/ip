import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.Locale;

public abstract class Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern(
                    "MMM d yyyy",
                    Locale.ENGLISH
            );

    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern(
                    "MMM d yyyy, h:mm a",
                    Locale.ENGLISH
            );
    private final String description;
    private boolean isDone;

    private Task(String description) {
        this(description, false);
    }

    private Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    public static Task newTodo(String description) {
        return new Todo(description);
    }

    public static Task newDeadline(
            String description,
            String deadline
    ) {
        DateTimeParser.ParsedDateTime parsedDeadline =
                DateTimeParser.parseDeadline(deadline);

        return new Deadline(
                description,
                parsedDeadline.value(),
                parsedDeadline.hasTime()
        );
    }

    public static Task newEvent(
            String description,
            String startTime,
            String endTime
    ) {
        LocalDateTime parsedStartTime =
                DateTimeParser.parseDateTime(startTime);

        LocalDateTime parsedEndTime =
                DateTimeParser.parseDateTime(endTime);

        if (!parsedEndTime.isAfter(parsedStartTime)) {
            throw new GeekException(
                    "The event end time must be "
                            + "after the start time."
            );
        }

        return new Event(
                description,
                parsedStartTime,
                parsedEndTime
        );
    }

    public static Task fromDataString(String data) {
        String[] fields = data.split("\\t", -1);

        if (fields.length < 3) {
            throw new IllegalArgumentException(
                    "Saved task has too few fields."
            );
        }

        boolean isDone;

        if (fields[1].equals("1")) {
            isDone = true;
        } else if (fields[1].equals("0")) {
            isDone = false;
        } else {
            throw new IllegalArgumentException(
                    "Saved task has an invalid status."
            );
        }

        String description = decode(fields[2]);

        if (description.isBlank()) {
            throw new IllegalArgumentException(
                    "Saved task has an empty description."
            );
        }

        return switch (fields[0]) {
            case "T" -> {
                requireFieldCount(fields, 3);
                yield new Todo(description, isDone);
            }
            case "D" -> {
                requireFieldCount(fields, 4);

                String deadlineText = decode(fields[3]);

                if (deadlineText.isBlank()) {
                    throw new IllegalArgumentException(
                            "Saved deadline has no date."
                    );
                }

                DateTimeParser.ParsedDateTime deadline;

                try {
                    deadline = DateTimeParser.parseStoredDeadline(
                            deadlineText
                    );
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException(
                            "Saved deadline has an invalid date.",
                            e
                    );
                }

                yield new Deadline(
                        description,
                        deadline.value(),
                        deadline.hasTime(),
                        isDone
                );
            }
            case "E" -> {
                requireFieldCount(fields, 5);

                String startTimeText = decode(fields[3]);
                String endTimeText = decode(fields[4]);

                if (startTimeText.isBlank()
                        || endTimeText.isBlank()) {
                    throw new IllegalArgumentException(
                            "Saved event has an invalid time."
                    );
                }

                LocalDateTime startTime;
                LocalDateTime endTime;

                try {
                    startTime =
                            LocalDateTime.parse(startTimeText);
                    endTime =
                            LocalDateTime.parse(endTimeText);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException(
                            "Saved event has an invalid time.",
                            e
                    );
                }

                if (!endTime.isAfter(startTime)) {
                    throw new IllegalArgumentException(
                            "Saved event ends before it starts."
                    );
                }

                yield new Event(
                        description,
                        startTime,
                        endTime,
                        isDone
                );
            }
            default -> throw new IllegalArgumentException(
                    "Saved task has an unknown type."
            );
        };
    }

    public String toDataString() {
        StringBuilder data = new StringBuilder();

        data.append(getTypeCode())
                .append('\t')
                .append(isDone ? "1" : "0")
                .append('\t')
                .append(encode(description));

        appendAdditionalData(data);
        return data.toString();
    }

    protected abstract String getTypeCode();

    protected void appendAdditionalData(StringBuilder data) {
        // Todo tasks have no additional fields.
    }

    protected static void appendEncodedField(
            StringBuilder data,
            String value
    ) {
        data.append('\t').append(encode(value));
    }

    public void mark() {
        isDone = true;

        System.out.println("----------------------");
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + this);
        System.out.println("----------------------\n");
    }

    public void unmark() {
        isDone = false;

        System.out.println("----------------------");
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + this);
        System.out.println("----------------------\n");
    }

    String getDescription() {
        return description;
    }

    public String getStatus() {
        return isDone ? "X" : " ";
    }

    public boolean occursOn(LocalDate date) {
        return false;
    }

    private static String encode(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        value.getBytes(StandardCharsets.UTF_8)
                );
    }

    private static String decode(String value) {
        return new String(
                Base64.getUrlDecoder().decode(value),
                StandardCharsets.UTF_8
        );
    }

    private static void requireFieldCount(
            String[] fields,
            int expectedCount
    ) {
        if (fields.length != expectedCount) {
            throw new IllegalArgumentException(
                    "Saved task has an invalid number of fields."
            );
        }
    }

    @Override
    public String toString() {
        return String.format(
                "[%s] %s",
                getStatus(),
                description
        );
    }

    private static class Todo extends Task {
        private Todo(String description) {
            super(description);
        }

        private Todo(String description, boolean isDone) {
            super(description, isDone);
        }

        @Override
        protected String getTypeCode() {
            return "T";
        }

        @Override
        public String toString() {
            return String.format(
                    "[T][%s] %s",
                    getStatus(),
                    getDescription()
            );
        }
    }

    private static class Deadline extends Task {
        private final LocalDateTime deadline;
        private final boolean hasTime;

        private Deadline(
                String description,
                LocalDateTime deadline,
                boolean hasTime
        ) {
            super(description);
            this.deadline = deadline;
            this.hasTime = hasTime;
        }

        private Deadline(
                String description,
                LocalDateTime deadline,
                boolean hasTime,
                boolean isDone
        ) {
            super(description, isDone);
            this.deadline = deadline;
            this.hasTime = hasTime;
        }

        @Override
        protected String getTypeCode() {
            return "D";
        }

        @Override
        protected void appendAdditionalData(
                StringBuilder data
        ) {
            appendEncodedField(
                    data,
                    hasTime
                            ? deadline.toString()
                            : deadline.toLocalDate().toString()
            );
        }

        @Override
        public boolean occursOn(LocalDate date) {
            return deadline.toLocalDate().equals(date);
        }

        @Override
        public String toString() {
            String formattedDeadline = hasTime
                    ? deadline.format(DISPLAY_DATE_TIME_FORMAT)
                    : deadline.toLocalDate()
                            .format(DISPLAY_DATE_FORMAT);

            return String.format(
                    "[D][%s] %s (by: %s)",
                    getStatus(),
                    getDescription(),
                    formattedDeadline
            );
        }
    }

    private static class Event extends Task {
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;

        private Event(
                String description,
                LocalDateTime startTime,
                LocalDateTime endTime
        ) {
            super(description);
            this.startTime = startTime;
            this.endTime = endTime;
        }

        private Event(
                String description,
                LocalDateTime startTime,
                LocalDateTime endTime,
                boolean isDone
        ) {
            super(description, isDone);
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        protected String getTypeCode() {
            return "E";
        }

        @Override
        protected void appendAdditionalData(
                StringBuilder data
        ) {
            appendEncodedField(
                    data,
                    startTime.toString()
            );
            appendEncodedField(
                    data,
                    endTime.toString()
            );
        }

        @Override
        public boolean occursOn(LocalDate date) {
            LocalDate startDate = startTime.toLocalDate();
            LocalDate endDate = endTime.toLocalDate();

            return !date.isBefore(startDate)
                    && !date.isAfter(endDate);
        }

        @Override
        public String toString() {
            return String.format(
                    "[E][%s] %s (from: %s to: %s)",
                    getStatus(),
                    getDescription(),
                    startTime.format(
                            DISPLAY_DATE_TIME_FORMAT
                    ),
                    endTime.format(
                            DISPLAY_DATE_TIME_FORMAT
                    )
            );
        }
    }
}
