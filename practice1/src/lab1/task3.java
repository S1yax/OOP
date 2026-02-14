package lab1;
import java.util.Scanner;
public class task3 {
	public static void main(String [] args) {
		Scanner input= new Scanner ( System.in );
		int a;
		System.out.print("Enter your grade:");
		a=input.nextInt();
		if(a>0 && a>100) {
			System.out.print("Your grade does not exist");
		}
		else if(95<=a && a<=100) {
			System.out.println("Your grade" + " A");
		}else if(90<=a  && a<=94) {
			System.out.println("Your grade" + " A-");
		}
		else if(85<=a && a<=89) {
			System.out.println("Your grade" + " B+");
		}
		else if(80<=a && a<=84) {
			System.out.println("Your grade" + " B");
		}
		else if(75<=a && a<=79) {
			System.out.println("Your grade" + " B-");
		}
		else if(70<=a && a<=74) {
			System.out.println("Your grade" + " C+");
		}
		else if(65<=a && a<=69) {
			System.out.println("Your grade" + " C");
		}
		else if(60<=a && a<=64) {
			System.out.println("Your grade" + " C-");
		}
		else if(55<=a && a<59) {
			System.out.println("Your grade" + " D+");
		}
		else if(50<=a && a<=54) {
			System.out.println("Your grade" + " D");
		}
		else if(a<50) {
			System.out.println("Your grade" + " F");
		}
		
	}}
