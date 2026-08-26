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

public class Geek {
    private final Storage storage;
    private final Ui ui;
    private TaskList tasks;

    public Geek(String filePath) {
        this.storage = new Storage(Path.of(filePath));
        this.ui = new Ui();
        this.tasks = new TaskList();
    }

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

    private void saveTasks() {
        try {
            storage.saveTasks(tasks.getTasks());
        } catch (IOException e) {
            ui.showError("I could not save the tasks.");
        }
    }

    public static void main(String[] args) {
        new Geek("data/geek.txt").run();
    }
}
