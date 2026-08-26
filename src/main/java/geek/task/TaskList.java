package geek.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import geek.exception.GeekException;

public class TaskList {
    private final List<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task delete(int taskNumber) {
        checkTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    public Task mark(int taskNumber) {
        Task task = getTask(taskNumber);
        task.mark();
        return task;
    }

    public Task unmark(int taskNumber) {
        Task task = getTask(taskNumber);
        task.unmark();
        return task;
    }

    public List<Task> findOn(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.occursOn(date))
                .toList();
    }

    /**
     * Finds tasks whose descriptions contain the specified keyword,
     * ignoring letter case.
     *
     * @param keyword Keyword to search for.
     * @return Unmodifiable result list preserving task-list order.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);

        return tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .toList();
    }

    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    public int size() {
        return tasks.size();
    }

    private Task getTask(int taskNumber) {
        checkTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    private void checkTaskNumber(int taskNumber) {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new GeekException(
                    "That task number does not exist."
            );
        }
    }
}