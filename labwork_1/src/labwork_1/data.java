package labwork_1;

public class data {
	private double sum;
	private double max;
	private int count;
	
	public data() { }
		public void adddValue(double value) {
			sum+=value;
			if(count==0 || value>max) {
				max=value;
			}
			count++;
			
		}
		public double getAverage() {
			if(count==0) {
				return 0;
				}
				return sum/count;
		}
		public double getLargest() {
			return max;
			
		}
	}

