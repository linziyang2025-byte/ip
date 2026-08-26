package geek.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import geek.task.Task;

public class Storage {
    private final Path filePath;

    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    public LoadResult loadTasks() throws IOException {
        ensureDataFileExists();

        List<Task> tasks = new ArrayList<>();
        List<Integer> corruptedLineNumbers = new ArrayList<>();
        List<String> lines = Files.readAllLines(
                filePath,
                StandardCharsets.UTF_8
        );

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.isBlank()) {
                continue;
            }

            try {
                tasks.add(Task.fromDataString(line));
            } catch (IllegalArgumentException e) {
                corruptedLineNumbers.add(i + 1);
            }
        }

        return new LoadResult(tasks, corruptedLineNumbers);
    }

    public void saveTasks(List<Task> tasks) throws IOException {
        ensureDataFileExists();

        List<String> lines = tasks.stream()
                .map(Task::toDataString)
                .toList();

        Files.write(
                filePath,
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    private void ensureDataFileExists() throws IOException {
        Path parentDirectory = filePath.getParent();

        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        if (Files.notExists(filePath)) {
            Files.createFile(filePath);
        }
    }

    public record LoadResult(
            List<Task> tasks,
            List<Integer> corruptedLineNumbers
    ) {
        public LoadResult {
            tasks = List.copyOf(tasks);
            corruptedLineNumbers = List.copyOf(
                    corruptedLineNumbers
            );
        }
    }
}
