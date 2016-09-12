package decision_tree_learning;

@SuppressWarnings("rawtypes")
public class Range { //class that holds 2 col values for multiple features
	
	Range() {}
	
	Range(Range r) {
		this.val1 = r.getVal1();
		this.val2 = r.getVal2();
	}
	
	Range(Range1 val1, Range1 val2) {
		this.val1 = val1;
		this.val2 = val2;
	}
	
	Range1 val1;
	Range1 val2;
	
	@Override
	public String toString() {
		return "Range [" + val1 + ", " + val2 + "]";
	}

	public Range1 getVal1() {
		return val1;
	}

	public void setVal1(Range1 val1) {
		this.val1 = val1;
	}

	public Range1 getVal2() {
		return val2;
	}

	public void setVal2(Range1 val2) {
		this.val2 = val2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((val1 == null) ? 0 : val1.hashCode());
		result = prime * result + ((val2 == null) ? 0 : val2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Range other = (Range) obj;
		if (val1 == null) {
			if (other.val1 != null)
				return false;
		} else if (!val1.equals(other.val1))
			return false;
		if (val2 == null) {
			if (other.val2 != null)
				return false;
		} else if (!val2.equals(other.val2))
			return false;
		return true;
	}
}