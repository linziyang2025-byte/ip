public class Task {
    private String description;
    private boolean isDone;

    private Task(String description){
        this.description = description;
        this.isDone = false;
    }

    public static todoT newTodoT(String input){
        return new todoT(input);
    }

    public static ddlT newDdlT(String input, String ddl){
        return new ddlT(input, ddl);
    }

    public static eventT newEventT(String input, String startTime, String endTime){
        return new eventT(input, startTime, endTime);
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
    String getDescription(){
        return description;
    }

    public String getStatus(){
        return isDone ? "X" : " ";
    }

    @Override
    public String toString(){
        return String.format("[%s] %s", getStatus(), description);
    }

    private static class todoT extends Task{
        public todoT(String input){
            super(input);
        }

        @Override
        public String toString(){
            return String.format("[T][%s] %s", getStatus(), this.getDescription());
        }
    }

    private static class ddlT extends Task{
        private String ddl;
        public ddlT(String input, String ddl){
            super(input);
            this.ddl = ddl;
        }

        @Override
        public String toString(){
            return String.format("[D][%s] %s (by: %s)", getStatus(), this.getDescription(), ddl);
        }
    }

    private static class eventT extends Task{
        private String startTime;
        private String endTime;
        public eventT(String input, String startTime, String endTime){
            super(input);
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public String toString(){
            return String.format("[E][%s] %s (from: %s to: %s)", getStatus(), this.getDescription(), startTime, endTime);
        }
    }

}
