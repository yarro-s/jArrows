package jArrows.Tests;

import static org.junit.Assert.*;

import org.junit.Test;

import jArrows.Action;
import jArrows.Arrow;
import jArrows.Pair;
import jArrows.ParallelAction;


public class ParallelActionTest {

	@Test
	public void testParallelCombine() {
		Arrow<Integer, Double> arr1 = ParallelAction.of(i -> i*10.7/5);
		Arrow<Double, String> arr2 = ParallelAction.of(i -> String.valueOf(i*i/2.5));
		
		assertEquals("Parallel Action combination doesn't work", 
				arr1.combine(arr2).apply(Pair.of(105, 1.5)), 
				Pair.of(105*10.7/5, String.valueOf(1.5*1.5/2.5)));
	}
	
	@Test
	public void testParallelSerialCombineConsistence() {
		Arrow<Integer, Double> arr1Parallel = ParallelAction.of(i -> i*10.7/5);
		Arrow<Integer, Double> arr1Serial = Action.of(i -> i*10.7/5);
		Arrow<Double, String> arr2 = Action.of(i -> String.valueOf(i*i/2.5));
		
		assertEquals("Parallel and Serial actions are not consistent", 
				arr1Parallel.combine(arr2).apply(Pair.of(105, 1.5)), 
				arr1Serial.combine(arr2).apply(Pair.of(105, 1.5)));
	}

	@Test
	public void testCloneInput() {
		Arrow<Integer, Double> arr1 = ParallelAction.of(i -> i*i/5.5);
		Arrow<Integer, String> arr2 = ParallelAction.of(i -> String.valueOf(i*i*i+5));
		
		assertEquals("Parallel Action clone input doesn't work", 
				Pair.of(77*77/5.5, String.valueOf(77*77*77+5)),
				arr1.cloneInput(arr2).apply(77));
	}
}
