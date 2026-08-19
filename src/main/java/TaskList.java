import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private List<Task> tasks;

    public TaskList(){
        this.tasks = new ArrayList<>();
    }

    public boolean receiving(String input){
        try {
            if (input == null || input.isBlank()) {
                throw new GeekException("Please enter a command.");
            } else if (input.equals("bye")) {
                end();
                return false;
            } else if (input.equals("list")) {
                printTasks();
            } else if (input.equals("mark") || input.startsWith("mark ")) {
                int taskNumber = parseTaskNumber(input, "mark");
                checkTaskNumber(taskNumber);
                tasks.get(taskNumber - 1).mark();
            } else if (input.equals("unmark") || input.startsWith("unmark ")) {
                int taskNumber = parseTaskNumber(input, "unmark");
                checkTaskNumber(taskNumber);
                tasks.get(taskNumber - 1).unmark();
            } else if (input.startsWith("todo ") || input.equals("todo")
                    || input.startsWith("deadline ") || input.equals("deadline")
                    || input.startsWith("event ") || input.equals("event")) {
                addTask(input);
            } else {
                throw new GeekException("I'm sorry, but I don't know what that means :-(");
            }
        } catch (GeekException e) {
            printError(e.getMessage());
        } catch (NumberFormatException e) {
            printError("Please enter a valid task number.");
        }

        return true;
    }

    private int getLen(){
        return tasks.size();
    }

    private int parseTaskNumber(String input, String command) {
        String numberText = input.substring(command.length()).trim();

        if (numberText.isEmpty()) {
            throw new GeekException(
                    "Please provide a task number after " + command + "."
            );
        }

        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new GeekException("Please enter a valid task number.");
        }
    }

    private void printTasks(){
        System.out.println("----------------------");
        System.out.println("Here are the tasks in your list:");
        System.out.println(this);
        System.out.println("----------------------\n");
    }

    private void printError(String message) {
        System.out.println("----------------------");
        System.out.println("OOPS!!! " + message);
        System.out.println("----------------------\n");
    }

    private void end(){
        System.out.println("----------------------");
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("----------------------");
    }

    private void checkTaskNumber(int taskNumber) {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new GeekException("That task number does not exist.");
        }
    }

    public void addTask(String input){
        Task task;

        if (input.equals("todo") || input.startsWith("todo ")) {
            String description = input.substring(4).trim();

            if (description.isEmpty()) {
                throw new GeekException(
                        "The description of a todo cannot be empty."
                );
            }

            task = Task.newTodoT(description);

        } else if (input.equals("deadline") || input.startsWith("deadline ")) {
            int byIndex = input.indexOf("/by");

            if (byIndex == -1) {
                throw new GeekException(
                        "Deadline format: deadline <description> /by <time>"
                );
            }

            String description = input.substring(8, byIndex).trim();
            String deadline = input.substring(byIndex + 3).trim();

            if (description.isEmpty()) {
                throw new GeekException(
                        "The description of a deadline cannot be empty."
                );
            }

            if (deadline.isEmpty()) {
                throw new GeekException(
                        "The deadline time cannot be empty."
                );
            }

            task = Task.newDdlT(description, deadline);

        } else if (input.equals("event") || input.startsWith("event ")) {
            int fromIndex = input.indexOf("/from");
            int toIndex = input.indexOf("/to");

            if (fromIndex == -1 || toIndex == -1 || fromIndex >= toIndex) {
                throw new GeekException(
                        "Event format: event <description> /from <start> /to <end>"
                );
            }

            String description = input.substring(5, fromIndex).trim();
            String from = input.substring(fromIndex + 5, toIndex).trim();
            String to = input.substring(toIndex + 3).trim();

            if (description.isEmpty()) {
                throw new GeekException(
                        "The description of an event cannot be empty."
                );
            }

            if (from.isEmpty() || to.isEmpty()) {
                throw new GeekException(
                        "Both the start and end times are required."
                );
            }

            task = Task.newEventT(description, from, to);

        } else {
            throw new GeekException("Unknown task type.");
        }

        tasks.add(task);
        System.out.println("----------------------");
        System.out.println("Got it. I've added this task:");
        System.out.printf("  %s\n", task);
        System.out.printf("Now you have %s tasks in the list.\n", getLen());
        System.out.println("----------------------\n");
    }

    @Override
    public String toString(){
        StringBuilder res = new StringBuilder();
        for( int i = 1; i <= tasks.size(); i++){
            if(i == tasks.size()){
                res.append(i + ". " + tasks.get(i - 1));
            } else {
                res.append(i + ". " + tasks.get(i - 1) + "\n");
            }
        }
        return res.toString();
    }
}
