package jArrows;


public class Pair<Left, Right> implements Splittable<Left, Right> {
	
	public Left left; 
	public Right right;
	
	@Override
	public Left left() {
		return this.left;
	}
	
	@Override
	public Right right() {
		return this.right;
	}
	
	private Pair(Left leftVal, Right rightVal) {
		this.left = leftVal;
		this.right = rightVal;
	}
	
	public static <Left, Right> Pair<Left, Right> 
				  of(Left leftVal, Right rightVal) {
		return new Pair<>(leftVal, rightVal);
	}
	
	public Object[] toArray() {
		return new Object[]{left, right};
	}
	
	@Override
	public String toString() {
		return left.toString() + " : " + right.toString();
	}
	
	@Override
	public boolean equals(Object p) {
		if (p instanceof Pair<?, ?>) {
			Pair<?, ?> pChecked = (Pair<?, ?>) p;
			
			return pChecked.left().equals(this.left()) 
					&& pChecked.right().equals(this.right());
		} 
		return false;
	}
}
