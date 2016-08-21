package jArrows;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelAction<In, Out> 
					extends Action<In, Out> {

	protected ParallelAction(Applicable<In, Out> func) {
		super(func);
	}

	@Override
	public <In1, Out1> ParallelAction<Pair<In, In1>, Pair<Out, Out1>> 
	combine(Applicable<In1, Out1> parallelArrow) {
		
		Applicable<Pair<In, In1>, Pair<Out, Out1>> combinedArrs = 
			(Pair<In, In1> input) -> {
				return runInParallel(input.left, input.right, parallelArrow);
			};
					
		return new ParallelAction<Pair<In, In1>, Pair<Out, Out1>>(combinedArrs);
	}

	@Override
	public <Out1> Arrow<In, Pair<Out, Out1>> 
	cloneInput(Applicable<In, Out1> parallelArrow) {
		
		Applicable<In, Pair<Out, Out1>> combinedArrs = 
				(In input) -> {
					return runInParallel(input, input, parallelArrow);
				};
						
		return new ParallelAction<In, Pair<Out, Out1>>(combinedArrs);
	}
	
	public static <In, Out> ParallelAction<In, Out> 
	of(Applicable<In, Out> func) {
		return new ParallelAction<>(func);
	}
	
	protected <InRight, OutRight> Pair<Out, OutRight> 
	runInParallel(In inputLeft, InRight inputRight,
				  Applicable<InRight, OutRight> parallelArrow) {
		ExecutorService es = Executors.newFixedThreadPool(2);
		
		Future<Out> leftRes = es.submit(() -> 
			this.apply(inputLeft));
		Future<OutRight> rightRes = es.submit(() -> 
			parallelArrow.apply(inputRight));
			
		try {
			return Pair.of(leftRes.get(), rightRes.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return Pair.of(null, null);
		}		
	}

}
