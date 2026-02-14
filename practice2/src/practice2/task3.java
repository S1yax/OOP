package practice2;

public class task3 {
	private int hour;
	private int minute;
	private int second;
	
	public task3(int h, int m, int s) {
		this.hour = h;
		this.minute = m;
		this.second = s;
	}
	
	public String toUniversal() {
		StringBuilder sb = new StringBuilder();
		
		if(hour < 10) sb.append("0" + hour + ":");
		else sb.append(hour + ":");
		
		if(minute < 10) sb.append("0" + minute + ":");
		 else sb.append(minute + ":");
		
		if(second < 10) sb.append("0" + second);
		else sb.append(second);
		
		return sb.toString();
	}
	
	public String toStandard() {
		
		boolean ok = false;
		StringBuilder sb = new StringBuilder();
		
		if(hour > 12) {
			ok = true;
			hour -= 12;
		}
		if(hour < 10) sb.append("0" + hour + ":");
		else sb.append(hour + ":");
		
		if(minute < 10) sb.append("0" + minute + ":");
		 else sb.append(minute + ":");
		
		if(second < 10) sb.append("0" + second);
		else sb.append(second);
		
		if(!ok) sb.append(" AM");
		else sb.append(" PM");
		
		return sb.toString();
	}
	
	public task3 add(task3 t1, task3 t2) {
	    int newHour = t1.hour + t2.hour;
	    int newMinute = t1.minute + t2.minute;
	    int newSecond = t1.second + t2.second;

	    if (newSecond >= 60) {
	        newSecond -= 60;
	        newMinute++;
	    }

	    if (newMinute >= 60) {
	        newMinute -= 60;
	        newHour++;
	    }

	    if (newHour >= 24) {
	        newHour -= 24;
	    }

	    return new task3(newHour, newMinute, newSecond);
	}
	
	public static void main(String[] args) {
		task3 t = new task3(23, 5, 6);
		System.out.println(t.toUniversal());
		System.out.println(t.toStandard());
		task3 t2 = new task3(4, 24, 33);
		t.add(t, t2);
		System.out.println(t.toUniversal());
	}

}
