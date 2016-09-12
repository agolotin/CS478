package decision_tree_learning;

import java.util.*;

public class TreeNode {
	
	TreeNode() {}
	
	//Only called when TreeNode is root
	TreeNode(String attrName) {
		this.attrName = new AttrName(attrName, attrName);
		this.depth = 0;
	}
//	
//	TreeNode(String attrName, double value, String valueName, boolean labeled) {
//	//	this.labeled = labeled;
//		this.attrName = attrName;
//		this.value = value;
//		this.valueName = valueName;
//	}
	
	
	ArrayList<TreeNode> children = new ArrayList<TreeNode>();
	
	Matrix labels;
	
	int depth;
	
	boolean labeled;
	double label;
	String labelName;
	
	Range1 attr;   // is it Range1 or Range
	AttrName attrName;
	
	Object value;
	Object valueName;
	
	//Methods only after this point
	
	//Creates a new node and returns it
	public TreeNode add() {
		TreeNode newNode = new TreeNode();
		newNode.depth = this.depth + 1;
		
		this.children.add(newNode);
		return newNode;
	}
	
	public void setValue(Object range, Object valueName)
	{
		this.value = range;
		this.valueName = valueName;
	}
	
	public String getLabelName() {
		return this.labelName;
	}
	
	public void setLabel(double label, String labelName) {
		this.label = label;
		this.labelName = labelName;
		this.labeled = true;
	}
	
	public void setLabels(Matrix labels) {
		this.labels = new Matrix(labels, 0, 0, labels.rows(), labels.cols());
	}
	
	public void setAttr(Range1 attr, AttrName attrName) { /// is it Range1 or Range???
		this.attr = attr;
		this.attrName = attrName;
	}
	
	public double getLabel() {
		return this.label;
	}
	
	public AttrName getAttrName() { 
		return attrName; 
	}
	
	public Object getValue() {  /// double or range
		return value; 
	}
	
	public int getDepth() {
		return this.depth;
	}
	
	public Object getValueName() {
		return this.valueName;
	}
	
	public Range1 getAttr() {
		return this.attr;
	}
	
	public boolean isLeaf() {
		return this.children.isEmpty();
	}
	
	public void printTree(TreeNode node) {
		String space = "     |";
		if (node.getValueName() == null) {
			System.out.println();
			
			//System.out.println("Evaluation method based on: Accuracy");
			//System.out.println("Evaluation method based on: Information Gain");
			System.out.println("Root node: " + node.getAttrName());
			for (TreeNode s_node : node.children) 
				printTree(s_node);
			
			System.out.println();
			return;
		}
				
		for (int i = 1; i < node.getDepth(); i++)
			System.out.print(space);
		
		System.out.print(" " + node.getValueName());
		if (node.getAttrName() != null)
			System.out.print(" -> " + node.getAttrName());
		
		if (node.isLeaf())
			System.out.print(" - ***" + node.getLabelName() + "***");
			
		System.out.println();
		for (TreeNode s_node : node.children)
			printTree(s_node);
	}
}