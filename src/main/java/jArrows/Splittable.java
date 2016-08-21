package jArrows;

public interface Splittable<Left, Right> {
	<A> Left left();
	
	<A> Right right();
}