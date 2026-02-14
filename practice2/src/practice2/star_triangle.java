package practice2;

public class star_triangle {
private int count;
	
	public star_triangle(int cnt) {
		this.count = cnt;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 1; i <= count; i++) {
			for(int j = 1; j <= i; j++) {
			    sb.append("[*]");
			}
			if(i < count) sb.append("\n");
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		star_triangle small = new star_triangle(3);
		System.out.println(small.toString());
	}
}
