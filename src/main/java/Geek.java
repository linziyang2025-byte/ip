import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Geek {
    private static void printl(List<?> lst){
        for( int i = 1; i <= lst.size(); i++){
            System.out.println(i + ". " + lst.get(i - 1));
        }
    }
    public static void main(String[] args) {
        System.out.println("----------------------");
        System.out.println("Hello! I'm Geek.");
        System.out.println("What can I do for you?");
        System.out.println("----------------------\n");

        Scanner scanner = new Scanner(System.in);
        TaskList tasks = new TaskList();

        boolean isRunning = true;
        while(isRunning){
            String input = scanner.nextLine();
            isRunning = tasks.receiving(input);
        }
        scanner.close();
    }
}