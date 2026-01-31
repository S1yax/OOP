package lab1;
import java.util.Scanner;
import java.math.BigDecimal;
import java.math.MathContext;
public class task4 {
	public static void main(String [] args) {
		Scanner input= new Scanner ( System.in );
		int a,b,c;
		System.out.print("Enter coefficient of x^2:");
		a=input.nextInt();
		System.out.print("Enter coefficient of x:");
		b=input.nextInt();
		System.out.print("Enter c:");
		c=input.nextInt();
		int D;
		D=b*b-4*a*c;
		if(D>0) {
			double x1,x2;
			x1=((-b)+(Math.sqrt(D)))/(2*a);
			x2=((-b)-(Math.sqrt(D)))/(2*a);
			System.out.println("The roots of the equation " + x1+" and "+x2);
		}
		else if(D==0) {
			double x;
			x=-b/(2*a);
			System.out.println("The root of the equation = "+x);
		}
		else if(D<0) {
			System.out.print("The equation does not have any roots " );
		}
}
}