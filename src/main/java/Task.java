public class Task {
    private String description;
    private boolean isDone;

    public Task(String description){
        this.description = description;
        this.isDone = false;
    }

    public void mark(){
        this.isDone = true;
        System.out.println("----------------------");
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + this);
        System.out.println("----------------------\n");
    }

    public void unmark(){
        this.isDone = false;
        System.out.println("----------------------");
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + this);
        System.out.println("----------------------\n");
    }

    public String getStatus(){
        return isDone ? "X" : " ";
    }

    @Override
    public String toString(){
        return String.format("[%s] %s", getStatus(), description);
    }
}
