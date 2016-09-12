package backpropogation;

public class OutputNode extends Node {
	
	/*
	 *  Output node has to be initialized only in case when we know the target
	 * 	Output labels have to be set statrting with the letter O
	 */
	public OutputNode(String label) {
		super(label);
	}
	
	public Double target;
	public Double activationOutput; // outA
	public Double deltaOutputError; 
	
	public void calculateDeltaError() { 
		this.deltaOutputError = this.activationOutput * (1 - this.activationOutput) 
				* (this.target - this.activationOutput);
	}
	
	/*
	 * @param net is the sum of all of the (inputs * weights)
	 */
	@Override
	public void calculateActivationValue(double net) {
		this.activationOutput = 1 / (1 + Math.pow(Math.E, -net));
	}
	
	@Override
	public String toString() {
		return "{ nodeLabel = " + this.label + ", activationOutput = " 
				+ this.activationOutput + "}";
	}
	
	///Getters and setters after this point
	public Double getTarget() {
		return this.target;
	}
	
	public void setTarget(double target) {
		this.target = target;
	}
	
	@Override
	public double getActivationValue() {
		return this.activationOutput;
	}
	
	public Double getOutputError() {
		return this.deltaOutputError;
	}
}