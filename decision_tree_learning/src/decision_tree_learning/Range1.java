package decision_tree_learning;

public class Range1<T extends Comparable<T>> {

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Range1 other = (Range1) obj;
		if (max == null) {
			if (other.max != null)
				return false;
		} else if (!max.equals(other.max))
			return false;
		if (min == null) {
			if (other.min != null)
				return false;
		} else if (!min.equals(other.min))
			return false;
		return true;
	}

	private final T min;
    private final T max;

    public Range1( T min, T max ) {
        this.min = min;
        this.max = max;
    }
    
    @Override
	public String toString() {
		return "[ " + min + "; " + max + " ]";
	}

	public T getMin() {
    	return this.min;
    }

    public T getMax() {
    	return this.max;
    }
    
    public boolean within( T value ) {
    	if ((Double)min < (Double)max) {
    		return min.compareTo(value) <= 0 && max.compareTo(value) >= 0;
    	}
    	else 
    		return min.compareTo(value) >= 0 && max.compareTo(value) <= 0;
    		
    }
    
    public double Distance(T val) {
    	if (this.getMax().compareTo(val) <= 0)
    		return (Double)val - (Double)this.getMax();
    	else if (this.getMin().compareTo(val) >= 0)
    		return (Double)this.getMin() - (Double)val;
    	
    	return Double.NEGATIVE_INFINITY;
    }
    
}