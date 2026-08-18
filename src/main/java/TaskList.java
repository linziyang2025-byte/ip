import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private List<Task> tasks;

    public TaskList(){
        this.tasks = new ArrayList<>();
    }

    public boolean receiving(String input){
        String firstFour = input.substring(0, Math.min(4, input.length()));
        String firstFix = input.substring(0, Math.min(6, input.length()));
        if(input.equals("bye")){
            end();
            return false;
        } else if (input.equals("list")){
            printTasks();
        } else if(firstFour.equals("mark")){
            int taskNumber = Integer.parseInt(input.substring(5).trim());
            tasks.get(taskNumber - 1).mark();
        } else if(firstFix.equals("unmark")){
            int taskNumber = Integer.parseInt(input.substring(7).trim());
            tasks.get(taskNumber - 1).unmark();
        } else {
            addTask(input);
        }
        return true;
    }

    private int getLen(){
        return tasks.size();
    }

    private void printTasks(){
        System.out.println("----------------------");
        System.out.println("Here are the tasks in your list:");
        System.out.println(this);
        System.out.println("----------------------\n");
    }

    private void end(){
        System.out.println("----------------------");
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("----------------------");
    }
    public void addTask(String input){
        Task task;
        if(input.startsWith("todo ")){
            task = Task.newTodoT(input.substring(5).trim());
        } else if (input.startsWith("deadline ")){
            int byIndex = input.indexOf("/by");

            if (byIndex == -1) {
                System.out.println("Deadline format should be: deadline <description> /by <time>");
                return;
            }

            String description = input.substring(9, byIndex).trim();
            String deadline = input.substring(byIndex + 3).trim();

            task = Task.newDdlT(description, deadline);
        } else if(input.startsWith("event ")){
            int fromIndex = input.indexOf("/from");
            int toIndex = input.indexOf("/to");

            if (fromIndex == -1 || toIndex == -1 || fromIndex >= toIndex) {
                System.out.println("Event format should be: event <description> /from <start> /to <end>");
                return;
            }

            String description = input.substring(6, fromIndex).trim();
            String from = input.substring(fromIndex + 5, toIndex).trim();
            String to = input.substring(toIndex + 3).trim();

            task = Task.newEventT(description, from, to);
        } else {
            System.out.println("I don't understand :(");
            return;
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
