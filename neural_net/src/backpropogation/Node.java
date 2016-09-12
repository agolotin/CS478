package backpropogation;

import java.util.*;

public abstract class Node {
	
	//output node doesn't need array of weights
	public Node(String label) {
		this.label = label;
		this.weights = null;
	}
	
	public Node(String label, Object dummy) { 
		this.label = label;
		this.weights = new ArrayList<Weight>();
	}
	
	public abstract double getActivationValue();
	public abstract void calculateActivationValue(double net);
	public ArrayList<Weight> weights; // weight from this node to the next
	final String label; /// the name of the node, can be set arbitrarily, but has to be unique through the net
	/*
	 * Sets the weight from this node to the next
	 * @param nextNode is the label of the next node
	 * @param weight is the weight from this node to the @nextNode
	 */
	public void addNextWeight(Object nextNode, double weight) {
		this.weights.add(new Weight(nextNode, weight));
	}

	public double getWeight(int position) {
		return this.weights.get(position).getWeight();
	}
	
	public ArrayList<Double> getWeightsArray() {
		ArrayList<Double> temp = new ArrayList<Double>();
		for (int i = 0; i < this.weights.size(); i++)
			temp.add(this.weights.get(i).getWeight());
		
		return temp;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public int getVectorSizeToNextLayer() {
		return this.weights.size();
	}
	
	@Override 
	public String toString() {
		String out = "nodeLabel = " + this.label 
				+ ", -> {\n";
		for (Weight w : weights) 
			out += w.toString() + "\n";
		out += "}";
		
		return out;
	}
	
	public void changeAllWeights(ArrayList<Double> weights) throws Exception{
		if (weights.size() != this.weights.size())
			throw new Exception("Node::changeAllWeights() given array "
					+ "and actual weights array are of different sizes");
		Iterator<Double> weight = weights.iterator();
		Iterator<Weight> prevWeight = this.weights.iterator();
		while (weight.hasNext())
			prevWeight.next().weight = weight.next();
	}
	
	
	class Weight {
		
		public Weight() {}
		public Weight(Object nextNode) {
			this.nextNode = nextNode;
		}
		public Weight(Object nextNode, double weight) {
			this.nextNode = nextNode;
			this.weight = weight; // weight from this node to @nextNode
		}
		
		Object nextNode; // if the node is output, make the @nextNode null
		Double weight; //weight from current node to the @next
		
		@Override
		public String toString() {
			return "nextNode = " + String.valueOf(nextNode) + 
					", weight = " + String.valueOf(weight);
		}
		
		public Object getNextNode() {
			return this.nextNode;
		}
		
		public void setNextNode(Object nextNode) {
			this.nextNode = nextNode;
		}
		
		public Double getWeight() {
			return this.weight;
		}
		
		public void setWeight(double weight) {
			this.weight = weight;
		}
	}


}