package jArrows;


public interface Applicable<In, Ret> {
	
	Ret apply(In val);
}
