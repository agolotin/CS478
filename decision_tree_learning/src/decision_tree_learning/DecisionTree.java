package decision_tree_learning;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;

@SuppressWarnings("rawtypes")
public class DecisionTree extends SupervisedLearner {
	public DecisionTree() {}
	public DecisionTree(boolean accuracy) {
		infoGain = accuracy;
	}
	
	public boolean infoGain = false;   /////false = accuracy; true = IG
	public TreeNode root;
	
	@Override
	public void train(Matrix features, Matrix labels) throws Exception {
			root = new TreeNode("root");
        	boolean check = partition(root.add(), features, labels);
        	//no recursion happened at all; IG or accuracy weren't improved on 1st iteration
        	if (!check) {
                	double mcl = labels.mostCommonValue(0);
                	root.children.get(0).setLabel(mcl, labels.attrValue(0, (int)mcl));
        	}
        	
		root.printTree(root.children.get(0));
	}
	
	
	@Override
	public void predict(double[] features, double[] labels) throws Exception {
		this.predict(root.children.get(0), features, labels);
	}
	
	@SuppressWarnings("unchecked")
	public void predict(TreeNode node, double[] features, double[] labels) throws Exception {
		if (node.isLeaf()) {
			labels[0] = node.getLabel();
			return;
		}
		
		Range1 attr = node.getAttr();
		double[] featureCopy = Arrays.copyOf(features, features.length);
		double max = (Integer) ((Integer)attr.getMin() > (Integer)attr.getMax() ? attr.getMin() : attr.getMax());
		double min = (Integer) ((Integer)attr.getMin() < (Integer)attr.getMax() ? attr.getMin() : attr.getMax());
		featureCopy = ArrayUtils.remove(featureCopy, (int)max);
		featureCopy = ArrayUtils.remove(featureCopy, (int)min);
		
		for (TreeNode s_node : node.children) {
			Range compare = (Range) s_node.getValue();
			double val1 = features[(Integer)attr.getMin()];
			double val2 = features[(Integer)attr.getMax()];
//			Range1 r_to_compare = new Range1(val1, val2);
//			if (s_node.getValue().within(features[(int)attr]) && s_node.getValueName() == "?")
//				compare = UNKNOWN_VALUE;
//			
			if (compare.getVal1().within(val1) && compare.getVal2().within(val2)) {
				this.predict(s_node, featureCopy, labels);
				return;
			}
		}
		//If the value is something we haven't seen before
		TreeNode closestNode = findClosestNode(node, features, attr);
		if (closestNode != null) {
			this.predict(closestNode, featureCopy, labels);
			return;
		}
		
//		System.out.println("Error: Attribute " + node.getAttr() + " - " + node.getAttrName()); //+ " value: " + features[(int)attr]);
		throw new Exception("No matching attribute-value pair for input feature");
	}
	
	@SuppressWarnings("unchecked")
	private TreeNode findClosestNode(TreeNode node, double[] features, Range1 attr) {
		Range1 min1 = new Range1(100, 0);
		Range1 max1 = new Range1(100, 0);
		Range1 min2 = new Range1(100, 0);
		Range1 max2 = new Range1(100, 0);
		TreeNode max_node = new TreeNode();
		TreeNode min_node = new TreeNode();
		for (TreeNode s_node : node.children) {
			Range1 r1 = ((Range)s_node.getValue()).getVal1();
			Range1 r2 = ((Range)s_node.getValue()).getVal2();
			if (r1.getMax().compareTo(Double.parseDouble(max1.getMax().toString())) > 0
				&&	r2.getMax().compareTo(Double.parseDouble(max2.getMax().toString())) > 0) {
				max1 = new Range1(r1.getMin(), r1.getMax());
				max2 = new Range1(r2.getMin(), r2.getMax());
				max_node = s_node;
			}
			if (r1.getMin().compareTo(Double.parseDouble(min1.getMin().toString())) < 0
				&&	r2.getMin().compareTo(Double.parseDouble(min2.getMin().toString())) < 0) {
				min1 = new Range1(r1.getMin(), r1.getMax());//new Range1(s_node.getValue().getMin(), s_node.getValue().getMax());
				min2 = new Range1(r2.getMin(), r2.getMax());
				min_node = s_node;
			}
		}
		double val1 = features[(Integer)attr.getMin()];  //attr1
		double val2 = features[(Integer)attr.getMax()];  //attr2
		
		Range1 max_r1 = ((Range)max_node.getValue()).getVal1();
		Range1 max_r2 = ((Range)max_node.getValue()).getVal2();
		Range1 min_r1 = ((Range)min_node.getValue()).getVal1();
		Range1 min_r2 = ((Range)min_node.getValue()).getVal2();
		
		double dist_max_node_a1 = max_r1.Distance(val1);
		double dist_max_node_a2 = max_r2.Distance(val2);
		double dist_min_node_a1 = min_r1.Distance(val1);
		double dist_min_node_a2 = min_r2.Distance(val2);
		
		if (dist_max_node_a1 < dist_min_node_a1 || dist_max_node_a2 < dist_min_node_a2) {
			return max_node;
		}
		else if (dist_max_node_a1 > dist_min_node_a1 || dist_max_node_a2 > dist_min_node_a2) {
			return min_node;
		}
		else if (dist_max_node_a1 == 0 || dist_max_node_a2 == 0) {
			return max_node;
		}
		else if (dist_min_node_a1 == 0 || dist_min_node_a2 == 0) {
			return max_node;
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public boolean partition(TreeNode node, Matrix features, Matrix labels) throws Exception {
		if (features.rows() != labels.rows())
			throw new Exception("Partition::Feature and label matrix have to be of the same size.");
		
		double f_rows = ((double)features.rows());
		Map<Double, Double> classes = labels.getNominalValueCounts(0);    
		
		double numClasses = classes.size();
		if (numClasses == 1) 
		{
			//label node with class
			double c = classes.keySet().iterator().next().doubleValue();
			node.setLabel(c, labels.attrValue(0, (int)c));
			return true;
		}
		else if (numClasses < 1 || f_rows < 1 || features.cols() < 1)
			//something's not right...
			return false;
		
		double _entropy = this.calculateEntropy(features, labels);
		double _accuracy = this.calculateAccuracy(features, labels);
		double maxGain = 0.0;
		//define ranges for maxAttr and maxValue
		Range1 r = new Range1(-1.0, -1.0);
		Range1 maxAttr = new Range1(-1.0, -1.0);
		Range maxValue = new Range(r,r);
		
		
		for (int attr = 0; attr < features.cols(); attr++)
		{
			for (int attr2 = 0; attr2 < features.cols(); attr2++) 
			{
				if (attr != attr2) 
				{
					Map<Range, Double> counts = features.getContinuousValueCounts(attr, attr2);
			
					//Skip it if attribute has only one value
					if (counts.size() == 1)
						continue;
					double info = 0.0;
					double gainRatio = 0.0;
					Range1 t_r = new Range1(Matrix.MISSING, Matrix.MISSING);
					Range t_rn = new Range(t_r,t_r);
					Range value = new Range(t_rn);
			
					for (Entry<Range, Double> single_feature : counts.entrySet()) 
					{
						//set value to invalid unless it's valid
						//double value = -1;
						if (!single_feature.getKey().equals(t_rn))
							value = single_feature.getKey(); // uncomment for nominal attributes
						
						Matrix reducedFeatures = new Matrix();
						Matrix reducedLabels = new Matrix();
						ArrayList<Integer> rowIndicies = features.reduce(reducedFeatures, attr, attr2, value);
						labels.reduce(reducedLabels, rowIndicies);
				
						double redRows = reducedFeatures.rows(); // number of features from reduced matrices
                		double rowRatio = redRows / f_rows;
						if (infoGain) {
                    		double attrEntropy = this.calculateEntropy(reducedFeatures, reducedLabels);
                        
                    		info += rowRatio * attrEntropy;
                    
                    		gainRatio -= rowRatio * (Math.log(rowRatio)/Math.log(2));
						}
						else {
                    		double attrAccuracy = this.calculateAccuracy(reducedFeatures, reducedLabels);
                       
                    		info += rowRatio * attrAccuracy;
						}
					}
					double gain = 0.0;
					if (infoGain)
						gain = _entropy - info;
					else 
						gain = info - _accuracy;
                
            		if (gain > maxGain)
            		{
            			maxGain = gain;
                		maxAttr = new Range1(attr, attr2);
                		maxValue = value;
            		}
				}
			}
		}
		
		// if nothing has changed either IG or accuracy
		if (maxAttr.equals(new Range1(-1.0, -1.0)))
			return false; //exit recursion and label with majority class
		
		//label node with best attribute
		String attrName1 = features.attrName((Integer)maxAttr.getMin());
		String attrName2 = features.attrName((Integer)maxAttr.getMax());
		AttrName name = new AttrName(attrName1, attrName2);
		node.setAttr(maxAttr, name);
		Map<Range, Double> values = features.getContinuousValueCounts((Integer)maxAttr.getMin(), (Integer)maxAttr.getMax());
		
		for (Entry<Range, Double> value : values.entrySet())
		{
			TreeNode newNode = node.add();
			AttrName newNodeAttrName = new AttrName(value.getKey().getVal1().toString(), value.getKey().getVal2().toString());
			newNode.setValue(value.getKey(), newNodeAttrName);//attrNumValue(value.getKey()));
			
			//reduce matrices for further recursive use
			Matrix reducedFeatures = new Matrix();
			Matrix reducedLabels = new Matrix();
			ArrayList<Integer> rowIndicies = features.reduce(reducedFeatures, (Integer)maxAttr.getMin(), (Integer)maxAttr.getMax(), value.getKey());
			labels.reduce(reducedLabels, rowIndicies);
			
			//call partition on the node and reduced matrices
			boolean labeled = this.partition(newNode, reducedFeatures, reducedLabels);
			if (!labeled) {
				double mcl = labels.mostCommonValue(0);
				newNode.setLabel(mcl, labels.attrValue(0, (int)mcl));
			}
		}
		
		return true;
	}
	
	private double calculateEntropy(Matrix features, Matrix labels) {
		Map<Double, Double> counts = labels.getNominalValueCounts(0);
		
		double numRows = labels.rows();
		
		double entropy = 0.0;
		for (Entry<Double, Double> s_counts : counts.entrySet()) {
			double p = s_counts.getValue() / numRows;
			if (p != 0.0)
				entropy += p * (Math.log(p) / Math.log(2));
		}
		entropy *= -1;
		
		return entropy;
	}
	
	private double calculateAccuracy(Matrix features,Matrix labels) {
		Map<Double, Double> counts = labels.getNominalValueCounts(0);
		
		double numRows = labels.rows();
		
		double accuracy = 0.0;
		for (Entry<Double, Double> s_counts : counts.entrySet()) {
			double p = s_counts.getValue() / numRows;
			if (p > accuracy)
				accuracy = p;
		}
		
		return accuracy;
	}
}