package jArrows.Tests.UseCases;

import static org.junit.Assert.*;

import org.junit.Test;

import jArrows.Action;
import jArrows.Arrow;
import jArrows.Pair;


public class BasicUseCasesTest {
    static double multBy5_0(int in) { return in*5.0; }
    
    @Test
    public void testMult5Plus10toStr() {
        Arrow<Integer, Double> arrMultBy5_0 = 
                Action.of(BasicUseCasesTest::multBy5_0);
        Arrow<Integer, String> mult5Plus10toStr = 
                arrMultBy5_0.join(in -> in+10.5).join(in -> String.valueOf(in));
        
        assertEquals("Mult5Plus10toStr doesn't work", "60.5", 
                mult5Plus10toStr.apply(10));        
    }
    
    @Test
    public void testSumSinCosSqr() {
        Arrow<Pair<Double, Double>, Pair<Double, Double>> 
            sin_cos = Action.of(Math::sin).combine(Math::cos);

        Arrow<Double, Double> sqr = Action.of(i -> i*i);

        Arrow<Pair<Double, Double>, Double> sum_SinCos = sin_cos.join(sqr.combine(sqr))
                                    .join(p -> p.left + p.right);

        assertEquals("SumSinCosSqr doesn't work", 1.38, 
                sum_SinCos.apply(Pair.of(0.7, 0.2)), 0.01);     
    }
    
    @Test
    public void testSqrAndSqrt() {
        Arrow<Integer, Pair<Integer, Double>> sqrAndSqrt = 
                Action.of((Integer i) -> i*i)
                        .cloneInput(Math::sqrt);

        assertEquals("SqrAndSqrt doesn't work", 
                Pair.of(25, 2.23606797749979), sqrAndSqrt.apply(5));     
    }
    
    @Test
    public void testFirstSecond() {
        Arrow<Integer, Double> arr = Action.of(i -> Math.sqrt(i*i*i));
        
        Pair input = Pair.of(20, 10);
        
        assertEquals("First doesn't work", Pair.of(89.44271909999159, 10), 
                arr.<Integer>first().apply(input));   
        assertEquals("Second doesn't work", Pair.of(20, 31.622776601683793), 
                arr.<Integer>second().apply(input));     
    }
}
