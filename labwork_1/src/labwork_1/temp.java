package labwork_1;
import java.util.Vector;
public class temp{
	
	    private double value;
	    private char scale;

	    
	    public temp() {
	        this.value = 0;
	        this.scale = 'C';
	    }

	    public temp(double value) {
	        this.value = value;
	        this.scale = 'C';
	    }

	    public temp(char scale) {
	        this.value = 0;
	        this.scale = scale;
	    }
	    public temp(double value, char scale) {
	        this.value = value;
	        this.scale = scale;
	    }
	    public double getCelsius() {
	        if (scale == 'C') return value;
	        return 5 * (value - 32) / 9;
	    }
	    public double getFahrenheit() {
	        if (scale == 'F') return value;
	        return (9 * value / 5) + 32;
	    }
	    public void setValue(double value) { this.value = value; }
	    public void setScale(char scale) { this.scale = scale; }
	    public void setBoth(double value, char scale) {
	        this.value = value;
	        this.scale = scale;
	    }

	    public char getScale() { return scale; }
	
}
