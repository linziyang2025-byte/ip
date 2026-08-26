package geek;

import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;

import geek.exception.GeekException;
import geek.parser.Parser;
import geek.storage.Storage;
import geek.task.Task;
import geek.task.TaskList;
import geek.ui.Ui;

/**
 * Coordinates command parsing, task management, persistence, and console output.
 */
public class Geek {
    private final Storage storage;
    private final Ui ui;
    private TaskList tasks;

    /**
     * Creates a Geek application that stores tasks at the specified path.
     *
     * @param filePath Path to the task data file, either relative or absolute.
     */
    public Geek(String filePath) {
        this.storage = new Storage(Path.of(filePath));
        this.ui = new Ui();
        this.tasks = new TaskList();
    }

    /**
     * Runs the command loop until the user enters {@code bye}.
     *
     * Invalid input and persistence failures are reported through the UI so that
     * the application can continue when possible.
     */
    public void run() {
        ui.showWelcome();
        loadTasks();

        boolean isRunning = true;

        while (isRunning) {
            try {
                Parser.Command command = Parser.parse(
                        ui.readCommand()
                );
                isRunning = execute(command);
            } catch (GeekException e) {
                ui.showError(e.getMessage());
            } catch (DateTimeParseException e) {
                ui.showError(
                        "Use a supported date or time format, "
                                + "such as 2019-12-02, "
                                + "2/12/2019 1800, or "
                                + "Dec 2 2019 6:00 PM."
                );
            } catch (NumberFormatException e) {
                ui.showError(
                        "Please enter a valid task number."
                );
            }
        }

        ui.close();
    }

    /**
     * Executes a parsed command and reports its result to the user.
     *
     * @param command Command to execute.
     * @return {@code true} if the command loop should continue, or {@code false}
     *         after a bye command.
     */
    private boolean execute(Parser.Command command) {
        return switch (command.type()) {
            case BYE -> {
                ui.showGoodbye();
                yield false;
            }
            case LIST -> {
                ui.showTaskList(tasks.getTasks());
                yield true;
            }
            case ON -> {
                ui.showTasksOnDate(
                        command.date(),
                        tasks.findOn(command.date())
                );
                yield true;
            }
            case MARK -> {
                Task task = tasks.mark(command.taskNumber());
                ui.showTaskMarked(task);
                saveTasks();
                yield true;
            }
            case UNMARK -> {
                Task task = tasks.unmark(command.taskNumber());
                ui.showTaskUnmarked(task);
                saveTasks();
                yield true;
            }
            case DELETE -> {
                Task task = tasks.delete(command.taskNumber());
                saveTasks();
                ui.showTaskDeleted(task, tasks.size());
                yield true;
            }
            case ADD -> {
                Task task = command.task();
                tasks.add(task);
                saveTasks();
                ui.showTaskAdded(task, tasks.size());
                yield true;
            }
        };
    }

    /**
     * Loads saved tasks and reports unreadable or corrupted data.
     *
     * A file-level I/O failure resets the in-memory task list to an empty list.
     */
    private void loadTasks() {
        try {
            Storage.LoadResult result = storage.loadTasks();
            tasks = new TaskList(result.tasks());

            for (int lineNumber : result.corruptedLineNumbers()) {
                ui.showCorruptedTaskWarning(lineNumber);
            }
        } catch (IOException e) {
            tasks = new TaskList();
            ui.showError("I could not load the saved tasks.");
        }
    }

    /**
     * Saves the current task list and reports an I/O failure without ending the
     * command loop.
     */
    private void saveTasks() {
        try {
            storage.saveTasks(tasks.getTasks());
        } catch (IOException e) {
            ui.showError("I could not save the tasks.");
        }
    }

    /**
     * Starts Geek using {@code data/geek.txt} as its task data file.
     *
     * @param args Command-line arguments; they are not used.
     */
    public static void main(String[] args) {
        new Geek("data/geek.txt").run();
    }
}
