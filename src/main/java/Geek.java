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
        List<String> tasks = new ArrayList<>();
        while(true){
            String input = scanner.nextLine();
            if(input.equals("bye")){
                System.out.println("----------------------");
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println("----------------------");
                break;
            } else if (input.equals("list")){
                System.out.println("----------------------");
                printl(tasks);
                System.out.println("----------------------\n");
            } else {
                tasks.add(input);
                System.out.println("----------------------");
                System.out.printf("added: %s\n", input);
                System.out.println("----------------------\n");
            }
        }
        scanner.close();
    }
}