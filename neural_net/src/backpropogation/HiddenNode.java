package backpropogation;

import java.util.*;

//hidden node
public class HiddenNode extends Node {
	
	/*
	 * Hidden node labels have to be set starting with the letter H
	 * "H2_5" means hidden node 5 of layer 2
	 */
	public HiddenNode(String label) {
		super(label, null);
	}
	
	public Double _deltaError; // backpropgated error from previous node to the next
	public Double activationValue; // sum of (weight*input) passed through sigmoid function
	
	public double getDeltaError() {
		return this._deltaError;
	}
	
	/*
	 * Passes the net though the sigmoid function
	 * @param net is the sum of all of the (inputs * weights)
	 */
	@Override
	public void calculateActivationValue(double net) {
		this.activationValue = 1 / (1 + Math.pow(Math.E, -net));
	}
	
	/*
	 * If the node is called A, this is deltaA
	 * Sets @_deltaError as the net backpropogates
	 * @param deltaError Error on all of the weight nodes
	 */
	public void backpropogateError(ArrayList<Double> deltaError) throws Exception {
		// TODO make sure later that weights and errors are in the same order in both sets
		if (deltaError.size() != weights.size()) 
			throw new Exception("activationValue::backpropogationError() deltaError "
					+ "array and weights array are of different sizes");
		
		double tempError = this.activationValue * (1 - this.activationValue);
		
		Iterator<Double> s_deltaError = deltaError.iterator();
		Iterator<Weight> s_weights = weights.iterator();
		double tempSum = 0.0;
		while (s_deltaError.hasNext()) {
			tempSum += s_deltaError.next() * s_weights.next().getWeight();
		}
		
		this._deltaError = tempError * tempSum;
	}

	@Override
	public double getActivationValue() {
		return this.activationValue;
	}
	
}