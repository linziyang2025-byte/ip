package geek;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the shared command-response interface used by the JavaFX UI.
 */
class GeekTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void getResponse_commandSequence_updatesSameTaskList() {
        Geek geek = new Geek(
                tempDirectory.resolve("geek.txt").toString()
        );

        String addResponse = geek.getResponse("todo read book");
        String markResponse = geek.getResponse("mark 1");
        String listResponse = geek.getResponse("list");

        assertTrue(addResponse.contains("I've added this task"));
        assertTrue(markResponse.contains("marked this task as done"));
        assertTrue(listResponse.contains("[T][X] read book"));
    }

    @Test
    void getResponse_newInstance_loadsPreviouslySavedTasks() {
        Path filePath = tempDirectory.resolve("geek.txt");
        Geek firstSession = new Geek(filePath.toString());
        firstSession.getResponse(
                "deadline submit report /by 2/12/2019 1800"
        );

        Geek secondSession = new Geek(filePath.toString());
        String listResponse = secondSession.getResponse("list");

        assertTrue(
                listResponse.contains(
                        "[D][ ] submit report "
                                + "(by: Dec 2 2019, 6:00 PM)"
                )
        );
    }

    @Test
    void getResponse_invalidAndByeCommands_returnFriendlyMessages() {
        Geek geek = new Geek(
                tempDirectory.resolve("geek.txt").toString()
        );

        assertEquals(
                "OOPS!!! Please enter a command.",
                geek.getResponse("   ")
        );
        assertEquals(
                "Bye. Hope to see you again soon!",
                geek.getResponse("bye")
        );
    }
}
