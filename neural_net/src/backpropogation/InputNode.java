package backpropogation;

public class InputNode extends Node {
	
	//Input label has to start with letter I
	public InputNode(String label) {
		super(label, null);
	}
	
	private double Input = 0.0;
	
	public void setInput(double input) {
		this.Input = input;
	}
	
	public Double getInput() {
		return this.Input;
	}

	@Override
	public double getActivationValue() {
		return this.getInput();
	}

	@Override //has to do nothing in here
	public void calculateActivationValue(double d) {}
}