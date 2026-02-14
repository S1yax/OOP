package labwork_1;
import java.util.Scanner;

public class analyzer {
  public static void main(String[] args) {
    Scanner sc=new Scanner(System.in);
    data data=new data();
  while(true) {
    System.out.print("Enter number or enter 'Q' to quit");
    if(sc.hasNextDouble()) {
      double value=sc.nextDouble();
      data.adddValue(value);
    } else {
      String input=sc.next();
      if(input.equalsIgnoreCase("Q")){
        break;
    } 
      else {
        System.out.println("Invalid input, please try it again");
      }
  }}
    System.out.println("average=" + data.getAverage());
        System.out.println("maximum= " + data.getLargest());
        
        sc.close();
  
  }
  }