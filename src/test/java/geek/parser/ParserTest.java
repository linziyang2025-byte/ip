package geek.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import geek.exception.GeekException;
import geek.parser.Parser.Command;
import geek.parser.Parser.CommandType;

/**
 * Tests the conversion of user input into executable commands.
 */
class ParserTest {
    @Test
    void parse_simpleCommands_returnsMatchingCommandTypes() {
        Command byeCommand = Parser.parse("bye");
        Command listCommand = Parser.parse("list");

        assertAll(() -> assertEquals(
                        CommandType.BYE,
                        byeCommand.type()
                ), () -> assertEquals(
                        CommandType.LIST,
                        listCommand.type()
                )
        );
    }

    @Test
    void parse_markWithValidTaskNumber_returnsMarkCommand() {
        Command command = Parser.parse("mark 3");

        assertAll(() -> assertEquals(
                        CommandType.MARK,
                        command.type()
                ), () -> assertEquals(3, command.taskNumber())
        );
    }

    @Test
    void parse_findWithKeyword_returnsFindCommand() {
        Command command = Parser.parse("find   BOOK");

        assertAll(() -> assertEquals(
                        CommandType.FIND,
                        command.type()
                ), () -> assertEquals("BOOK", command.keyword())
        );
    }

    @Test
    void parse_findWithoutKeyword_throwsGeekException() {
        GeekException exception = assertThrows(
                GeekException.class, () -> Parser.parse("find   ")
        );

        assertEquals(
                "Please provide a keyword after find.",
                exception.getMessage()
        );
    }

    @Test
    void parse_todoWithDescription_returnsAddCommand() {
        Command command = Parser.parse("todo read book");

        assertAll(() -> assertEquals(
                        CommandType.ADD,
                        command.type()
                ), () -> assertEquals(
                        "[T][ ] read book",
                        command.task().toString()
                )
        );
    }

    @Test
    void parse_deadlineWithDateTime_returnsAddCommand() {
        Command command = Parser.parse(
                "deadline return book /by Dec 2 2019 6:00 PM"
        );

        assertAll(() -> assertEquals(
                        CommandType.ADD,
                        command.type()
                ), () -> assertEquals(
                        "[D][ ] return book "
                                + "(by: Dec 2 2019, 6:00 PM)",
                        command.task().toString()
                )
        );
    }

    @Test
    void parse_eventWithValidRange_returnsAddCommand() {
        Command command = Parser.parse(
                "event project meeting "
                        + "/from 2/12/2019 1800 "
                        + "/to 2/12/2019 2000"
        );

        assertAll(() -> assertEquals(
                        CommandType.ADD,
                        command.type()
                ), () -> assertEquals(
                        "[E][ ] project meeting "
                                + "(from: Dec 2 2019, 6:00 PM "
                                + "to: Dec 2 2019, 8:00 PM)",
                        command.task().toString()
                )
        );
    }

    @Test
    void parse_onWithSupportedDate_returnsOnCommand() {
        Command command = Parser.parse("on Dec 2, 2019");

        assertAll(() -> assertEquals(
                        CommandType.ON,
                        command.type()
                ), () -> assertEquals(
                        LocalDate.of(2019, 12, 2),
                        command.date()
                )
        );
    }

    @Test
    void parse_blankInput_throwsGeekException() {
        GeekException exception = assertThrows(
                GeekException.class, () -> Parser.parse("   ")
        );

        assertEquals(
                "Please enter a command.",
                exception.getMessage()
        );
    }

    @Test
    void parse_markWithoutTaskNumber_throwsGeekException() {
        assertThrows(
                GeekException.class, () -> Parser.parse("mark")
        );
    }

    @Test
    void parse_markWithNonNumericTaskNumber_throwsGeekException() {
        GeekException exception = assertThrows(
                GeekException.class, () -> Parser.parse("mark three")
        );

        assertEquals(
                "Please enter a valid task number.",
                exception.getMessage()
        );
    }

    @Test
    void parse_deadlineWithoutByDelimiter_throwsGeekException() {
        assertThrows(
                GeekException.class, () -> Parser.parse("deadline return book")
        );
    }

    @Test
    void parse_eventEndingAtStartTime_throwsGeekException() {
        assertThrows(
                GeekException.class, () -> Parser.parse(
                        "event meeting "
                                + "/from 2/12/2019 1800 "
                                + "/to 2/12/2019 1800"
                )
        );
    }

    @Test
    void parse_onWithInvalidDate_throwsGeekException() {
        assertThrows(
                GeekException.class, () -> Parser.parse("on 2019-02-29")
        );
    }

    @Test
    void parse_unknownCommand_throwsGeekException() {
        assertThrows(
                GeekException.class, () -> Parser.parse("sing a song")
        );
    }
}
