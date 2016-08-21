package jArrows.Tests;

import static org.junit.Assert.*;

import org.junit.Test;

import jArrows.Action;
import jArrows.Applicable;
import jArrows.Arrow;
import jArrows.Pair;


public class ArrowLawsTest {

	@Test
	// Action(id) = id
	public void testId(){
		Arrow<Integer, Integer> f = Action.of(i -> i);
		
		assertTrue("Action on identity fails", f.apply(1) == 1);
	}
	
	
	
	@Test
	// Action (f join g) = Action(f) join Action(g)
	public void testDistributiveness() {
		Arrow<Integer, Integer> f = Action.of((Integer i) -> i*i),
								 g = Action.of(i -> 5*i);
		
		Pair<Integer, Object> arrArg = Pair.of(15, 0);
		
		Arrow<Integer, Integer> arrInnerCompose = Action.of(f.join(g));
		Arrow<Integer, Integer> arrOuterCompose = Action.of(f).join(g);
		
		assertTrue("Composition distributiveness doesn't hold", 
				arrInnerCompose.apply(10).equals(arrOuterCompose.apply(10)));
		
		assertTrue("Composition distributiveness with first() doesn't hold", 
				f.join(g).bypass2nd().apply(arrArg)
					.equals( f.bypass2nd().join(g.bypass2nd()).apply(arrArg) ));
	}
	
	// first (arr f) = arr (first f)
	
	@Test
	// bypass2nd (f join g) = bypass2nd(f) join bypass2nd(g)
	public void testFirstCompositionDistributiveness() {
		Action<Integer, Integer> f = Action.of((Integer i) -> i*i*2),
				 				 g = Action.of(i -> 7*i+3);
		
		assertEquals("First of composition doesn't equal composition of firsts",
				f.join(g).bypass2nd().apply(Pair.of(5, 10)), 
					f.bypass2nd().join(g.bypass2nd()).apply(Pair.of(5, 10)));
	}
	
	@Test
	// TODO: consider heterogeneous types
	// bypass2nd(f) join Action(getFirst) = Action(getFirst) join f
	public void testComposedGetFirst() {
		Action<Double, Double> f = Action.of((Double i) -> i*25/5.7);
		Pair<Double, Object> arg = Pair.of(50.5, 7.2);
		
		Applicable<Pair<Double, Object>, Double> 
			getFirst = (Pair<Double, Object> p) -> p.left();
		
		assertEquals("Composed get first element method fails", 
				f.bypass2nd().join(getFirst).apply(arg),
				Action.of(getFirst).join(f).apply(arg));
	}
	
	// TODO: implement
	// bypass2nd(f) join Action(id combine g) = Action (id combine g) join bypass2nd(f)
	// first (first f) >>> arr assoc = arr assoc >>> first f
	
	@Test
	public void testPipingLifting() {
		Action<Integer, Integer> f = Action.of((Integer i) -> i*i*i);
		
		Pair<Integer, Object> arrArg = Pair.of(100, 5);
		
		assertTrue("Piping & lifting conditions don't hold", 
				Action.of(f.bypass2nd()).apply(arrArg).equals(Action.of(f).bypass2nd().apply(arrArg)));
	}
	
	
}
