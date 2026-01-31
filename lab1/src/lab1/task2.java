package lab1;
import java.util.Scanner;
import java.math.BigDecimal;
import java.math.MathContext;

public class task2 {
	public static void main(String [] args) {
	Scanner input= new Scanner ( System.in );
	int a;
	int P;
	int Area;
	double Diagonal;
	System.out.print("Enter the square side a to calculate other:");
	a=input.nextInt();
	Area=a*a;
	P=4*a;
	Diagonal=a*Math.sqrt(2);
	System.out.println("Perimeter is equal to =" + P);
	System.out.println("Area is equal to =" + Area);
	System.out.println("Diagonal is equal to =" + Diagonal);

}}
