import java.nio.charset.StandardCharsets;
import java.util.Base64;

public abstract class Task {
    private final String description;
    private boolean isDone;

    private Task(String description) {
        this(description, false);
    }

    private Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    public static Task newTodoT(String description) {
        return new Todo(description);
    }

    public static Task newDdlT(String description, String deadline) {
        return new Deadline(description, deadline);
    }

    public static Task newEventT(
            String description,
            String startTime,
            String endTime
    ) {
        return new Event(description, startTime, endTime);
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

                String deadline = decode(fields[3]);

                if (deadline.isBlank()) {
                    throw new IllegalArgumentException(
                            "Saved deadline has no date."
                    );
                }

                yield new Deadline(description, deadline, isDone);
            }
            case "E" -> {
                requireFieldCount(fields, 5);

                String startTime = decode(fields[3]);
                String endTime = decode(fields[4]);

                if (startTime.isBlank() || endTime.isBlank()) {
                    throw new IllegalArgumentException(
                            "Saved event has an invalid time."
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
        private final String deadline;

        private Deadline(String description, String deadline) {
            super(description);
            this.deadline = deadline;
        }

        private Deadline(
                String description,
                String deadline,
                boolean isDone
        ) {
            super(description, isDone);
            this.deadline = deadline;
        }

        @Override
        protected String getTypeCode() {
            return "D";
        }

        @Override
        protected void appendAdditionalData(StringBuilder data) {
            appendEncodedField(data, deadline);
        }

        @Override
        public String toString() {
            return String.format(
                    "[D][%s] %s (by: %s)",
                    getStatus(),
                    getDescription(),
                    deadline
            );
        }
    }

    private static class Event extends Task {
        private final String startTime;
        private final String endTime;

        private Event(
                String description,
                String startTime,
                String endTime
        ) {
            super(description);
            this.startTime = startTime;
            this.endTime = endTime;
        }

        private Event(
                String description,
                String startTime,
                String endTime,
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
        protected void appendAdditionalData(StringBuilder data) {
            appendEncodedField(data, startTime);
            appendEncodedField(data, endTime);
        }

        @Override
        public String toString() {
            return String.format(
                    "[E][%s] %s (from: %s to: %s)",
                    getStatus(),
                    getDescription(),
                    startTime,
                    endTime
            );
        }
    }
}