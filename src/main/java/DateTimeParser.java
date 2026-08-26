import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class DateTimeParser {
    private static final List<String> DATE_PATTERNS = List.of(
            "uuuu-MM-dd",
            "d/M/uuuu",
            "d-M-uuuu",
            "d MMM uuuu",
            "MMM d uuuu",
            "MMM d, uuuu"
    );

    private static final List<String> TIME_PATTERNS = List.of(
            "HHmm",
            "HH:mm",
            "h:mma",
            "h:mm a",
            "ha",
            "h a"
    );

    private static final List<DateTimeFormatter> DATE_FORMATTERS =
            createDateFormatters();

    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS =
            createDateTimeFormatters();

    private DateTimeParser() {
    }

    public static ParsedDateTime parseDeadline(String input) {
        String normalizedInput = normalize(input);

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return new ParsedDateTime(
                        LocalDateTime.parse(
                                normalizedInput,
                                formatter
                        ),
                        true
                );
            } catch (DateTimeParseException e) {
                continue;
            }
        }

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return new ParsedDateTime(
                        LocalDate.parse(
                                normalizedInput,
                                formatter
                        ).atStartOfDay(),
                        false
                );
            } catch (DateTimeParseException e) {
                continue;
            }
        }

        throw invalidDateTime(input);
    }

    public static LocalDateTime parseDateTime(String input) {
        String normalizedInput = normalize(input);

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(
                        normalizedInput,
                        formatter
                );
            } catch (DateTimeParseException e) {
                continue;
            }
        }

        throw invalidDateTime(input);
    }

    public static LocalDate parseDate(String input) {
        String normalizedInput = normalize(input);

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(
                        normalizedInput,
                        formatter
                );
            } catch (DateTimeParseException e) {
                continue;
            }
        }

        throw invalidDateTime(input);
    }

    public static ParsedDateTime parseStoredDeadline(
            String storedValue
    ) {
        try {
            return new ParsedDateTime(
                    LocalDateTime.parse(storedValue),
                    true
            );
        } catch (DateTimeParseException e) {
            try {
                return new ParsedDateTime(
                        LocalDate.parse(storedValue)
                                .atStartOfDay(),
                        false
                );
            } catch (DateTimeParseException nestedException) {
                throw invalidDateTime(storedValue);
            }
        }
    }

    private static List<DateTimeFormatter> createDateFormatters() {
        List<DateTimeFormatter> formatters = new ArrayList<>();

        for (String pattern : DATE_PATTERNS) {
            formatters.add(createFormatter(pattern));
        }

        return List.copyOf(formatters);
    }

    private static List<DateTimeFormatter> createDateTimeFormatters() {
        List<DateTimeFormatter> formatters = new ArrayList<>();

        for (String datePattern : DATE_PATTERNS) {
            for (String timePattern : TIME_PATTERNS) {
                formatters.add(
                        createFormatter(
                                datePattern + " " + timePattern
                        )
                );
            }
        }

        return List.copyOf(formatters);
    }

    private static DateTimeFormatter createFormatter(
            String pattern
    ) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    private static String normalize(String input) {
        return input.trim().replaceAll("\\s+", " ");
    }

    private static DateTimeParseException invalidDateTime(
            String input
    ) {
        return new DateTimeParseException(
                "Unsupported date or time format.",
                input,
                0
        );
    }

    public record ParsedDateTime(
            LocalDateTime value,
            boolean hasTime
    ) {
    }
}
