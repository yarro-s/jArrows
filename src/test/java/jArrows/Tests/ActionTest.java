package jArrows.Tests;

import static org.junit.Assert.*;

import org.junit.Test;

import jArrows.Action;
import jArrows.Arrow;
import jArrows.Pair;


public class ActionTest {

	@Test
	public void testJoinWith() {
		Action<Integer, Double> arrA = Action.of(i -> i+3.5);
		Action<Double, String> arrB = Action.of(i -> String.valueOf(i/5*7.7));
		
		Arrow<Integer, String> arrAB = arrA.join(arrB);
		
		assertEquals("Action join fails", 
				String.valueOf((56+3.5)/5*7.7), arrAB.apply(56));
	}
	
	@Test
	public void testMultiJoinWith() {
		Action<Integer, Double> arrA = Action.of(i -> i+3.5);
		Action<Double, String> arrB = Action.of(i -> String.valueOf(i/5*7.7));
		Action<String, Boolean> arrC = Action.of(i -> String.valueOf((568+3.5)/5*7.7).equals(i));
		
		Arrow<Integer, Boolean> arrABC = arrA.join(arrB).join(arrC);
		
		assertTrue("Multitype Action join fails", arrABC.apply(568));
	}
	
	@Test
	public void testMultiInlineJoinWith() {
		Arrow<Integer, String> arrABCinline = 
				Action.of((Integer i) -> i+75.8)
					  .join(Action.of(i -> i/5))
					  .join(Action.of(i -> i*15.5))
				      .join(Action.of(i -> String.valueOf(i)));
		
		assertEquals("Multitype inline Action join fails", 
				String.valueOf((35+75.8)/5*15.5), arrABCinline.apply(35));
	}
	
	
	@Test
	public void testInlineJoinWith() {		
		Arrow<Integer, String> arrAB = Action.
				<Integer, Double>of(i -> i+3.5)
				.join(Action.of(i -> String.valueOf(i/5*7.7)));
		
		assertEquals("Inline Action join fails", 
				String.valueOf((56+3.5)/5*7.7), arrAB.apply(56));
	}
	
	@Test
	public void testPairs() {
		Pair<Integer, Double> pairIntDoubl = Pair.of(5, 10.5);
		
		assertEquals("Pair right constructor fails", 
				10.5, pairIntDoubl.right(), Double.MIN_VALUE);
		assertTrue("Pair left constructor fails", 5 == pairIntDoubl.left());
		
		Action<Pair<Integer, String>, Pair<Double, Double>> 
			arrPairToPair = Action.of(i -> Pair.of(i.left()*5.51, 
						               			   Double.parseDouble(i.right())));
		
		Pair<Double, Double> result = arrPairToPair.apply(Pair.of(10, "435.526"));
		
		assertTrue(result.left() == 5.51*10);
		assertEquals(result.right(), 435.526, Double.MIN_VALUE);
	}
	
	@Test
	public void testBypass() {
		Action<Integer, Double> arr1 = Action.of(i -> i*5.7);
		
		Arrow<Pair<Integer, String>, Pair<Double, String>> 
			arrLBypassed = arr1.bypass2nd();
		
		Arrow<Pair<String, Integer>, Pair<String, Double>> 
			arrRBypassed = arr1.bypass1st();
		
		Pair<Double, String> resultL = arrLBypassed.apply(Pair.of(50, "Preserved"));
		Pair<String, Double> resultR = arrRBypassed.apply(Pair.of("Preserved Again", 70));
		
		assertTrue("Left result in bypass2nd fails", 
				resultL.left() == 50*5.7);
		assertTrue("Right result in bypass2nd is not preserved", 
				resultL.right().equals("Preserved"));
		
		assertTrue("Right result in bypass1st fails", 
				resultR.right() == 70*5.7);
		assertTrue("Left result in bypass1st is not preserved", 
				resultR.left().equals("Preserved Again"));
		
		assertEquals("Action first and bypass2nd are not the same", 
				arr1.first().apply(Pair.of(65, "HELLO!")), 
				arr1.bypass2nd().apply(Pair.of(65, "HELLO!")));
		
		assertEquals("Action first and bypass2nd are not the same", 
				arr1.second().apply(Pair.of("Hi!", 75)), 
				arr1.bypass1st().apply(Pair.of("Hi!", 75)));
	}
	
	@Test
	public void testCombine() {
		Action<Integer, Double> arr1 = Action.of(i -> i*10.7/5);
		Action<Double, String> arr2 = Action.of(i -> String.valueOf(i*i/2.5));
		
		assertEquals("Action parrallel combination doesn't work", 
				arr1.combine(arr2).apply(Pair.of(105, 1.5)), 
				Pair.of(105*10.7/5, String.valueOf(1.5*1.5/2.5)));
	}
	
	@Test
	public void testCloneInput() {
		Action<Integer, Double> arr1 = Action.of(i -> i*i/5.5);
		Action<Integer, String> arr2 = Action.of(i -> String.valueOf(i*i*i+5));
		
		assertEquals("Action cloneInput doesn't work", 
				Pair.of(77*77/5.5, String.valueOf(77*77*77+5)),
				arr1.cloneInput(arr2).apply(77));
	}
	
	@Test
	public void testSplitInput() {
		Arrow<Integer, Pair<Double, String>> arr1 = 
				Action.of((Integer i) -> i*i*2.0)
					  .join(i -> 3.0*i)
					  .split(i -> i*10.0, i -> String.valueOf(i)+"!");
		
		double leftResult = 5*5*2.0*3.0*10.0;
		
		assertEquals("Action split doesn't work", 
				Pair.of(leftResult, String.valueOf(leftResult/10)+"!"),
				arr1.apply(5));
	}
	
	public static class Person {
		protected final String name;
		protected final long id;
		
		protected boolean checkPassed;
		
		public boolean isPassed() {
			return checkPassed;
		}
		
		public Person(String name, long id) {
			this.name = name;
			this.id = id;
			this.checkPassed = false;
		}
		
		public Person() {
			this.name = "trespasser";
			this.id = 0;
			this.checkPassed = false;
		}
		
		public boolean verifyIDBool() {
			return (id < 255464 && id > 15435);
		}
		
		public boolean verifyNameBool() {
			return name.equalsIgnoreCase("John") 
					|| name.equalsIgnoreCase("Tomy")
					|| name.equalsIgnoreCase("Jane");
		}
		
		public Person verifyID() {
			this.checkPassed = id < 255464 && id > 15435;
			return this;
		}
		
		public Person verifyName() {
			this.checkPassed =  name.equalsIgnoreCase("John") 
					|| name.equalsIgnoreCase("Tomy")
					|| name.equalsIgnoreCase("Jane");
			return this;
		}
		
		@Override
		public String toString() {
			return this.name + " | " + String.valueOf(this.id);
		}
	}
	
	@Test
	public void applicableMethod() {
		Arrow<ActionTest.Person, Boolean> verificationProcess = 
				Action.of((ActionTest.Person i) -> i.verifyIDBool() ? i : new Person())
					  .join(i -> i.verifyNameBool());
		
		Arrow<ActionTest.Person, Boolean> verificationProcess2 = 
				Action.of(ActionTest.Person::verifyID)
					  .join(ActionTest.Person::verifyName)
					  .join(ActionTest.Person::isPassed);
		
		assertEquals(verificationProcess.apply(new Person("Jane", 235853)), true);
		assertEquals(verificationProcess.apply(new Person("Jim", 253)), false);
		
		assertEquals("Methods can't be properly applied", 
				verificationProcess2.apply(new Person("John", 245752)), true);
		assertEquals("Methods can't be properly applied", 
				verificationProcess2.apply(new Person("Tim", 2645752)), false);
		assertEquals("Methods can't be properly applied", 
				verificationProcess2.apply(new Person("Tomy", 23752)), true);
	}
}
