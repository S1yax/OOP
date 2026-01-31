package lab1;
import java.util.Scanner;

public class task5 {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.print("Initial balance: ");
        double balance = scan.nextDouble();
        System.out.print("Interest Rate: ");
        double interest = scan.nextDouble();
        double a = interest * balance / 100;
        double r = a + balance;
        System.out.println("Interest added: " + a);
        System.out.print("Balance now: " + r);
    }
}
