package jArrows;

public class Action<In, Out> implements Arrow<In, Out> {
	
	private final Applicable<In, Out> func;
	
	@Override
	public Applicable<In, Out> getFunc() {
		return func;
	}
	
	protected Action(Applicable<In, Out> func) {
		this.func = func;
	}
	
	public static <In, Out> Action<In, Out> 
	of(Applicable<In, Out> func) {
		return new Action<>(func);
	}
	
	@Override
	public <In1, Out1> Action<In1, Out1> 
	arrowOf(Applicable<In1, Out1> func) {
		return Action.of(func);
	}
}