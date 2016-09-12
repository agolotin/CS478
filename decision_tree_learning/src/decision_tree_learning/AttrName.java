package decision_tree_learning;

public class AttrName {
	
	public AttrName() {}
	
	public AttrName(String attrName1, String attrName2) {
		this.attrName1 = attrName1;
		this.attrName2 = attrName2;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attrName1 == null) ? 0 : attrName1.hashCode());
		result = prime * result
				+ ((attrName2 == null) ? 0 : attrName2.hashCode());
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
		AttrName other = (AttrName) obj;
		
		if (attrName1 == null) {
			if (other.attrName1 != null)
				return false;
		} else if (!attrName1.equals(other.attrName1))
			return false;
		if (attrName2 == null) {
			if (other.attrName2 != null)
				return false;
		} else if (!attrName2.equals(other.attrName2))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[ " + attrName1 + ", " + attrName2 + " ]";
	}

	public String getAttrName1() {
		return attrName1;
	}

	public void setAttrName1(String attrName1) {
		this.attrName1 = attrName1;
	}

	public String getAttrName2() {
		return attrName2;
	}

	public void setAttrName2(String attrName2) {
		this.attrName2 = attrName2;
	}

	String attrName1;
	String attrName2;
}