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
            tasks.get(Character.getNumericValue(input.charAt(5)) - 1).mark();
        } else if(firstFix.equals("unmark")){
            tasks.get(Character.getNumericValue(input.charAt(7)) - 1).unmark();
        } else {
            addTask(input);
        }
        return true;
    }

    private void printTasks(){
        System.out.println("----------------------");
        System.out.println(this);
        System.out.println("----------------------\n");
    }

    private void end(){
        System.out.println("----------------------");
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("----------------------");
    }
    public void addTask(String input){
        Task newTask = new Task(input);
        tasks.add(newTask);
        System.out.println("----------------------");
        System.out.printf("added: %s\n", newTask);
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
