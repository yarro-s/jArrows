package jArrows;

public interface Arrow<In, Out> extends Applicable<In, Out> {
	
	<In1, Out1> Arrow<In1, Out1> arrowOf(Applicable<In1, Out1> func);
	
	Applicable<In, Out> getFunc();
	
	public default <OutD> Arrow<In, OutD> 
	join(Applicable<Out, OutD> downstreamArrow) {
		return this.<In, OutD>arrowOf(i -> 
						downstreamArrow.apply(this.getFunc().apply(i)));
	}
	
	public default <Bypass> Arrow<Pair<In, Bypass>, Pair<Out, Bypass>> 
	bypass2nd() {
		return this.<Pair<In, Bypass>, Pair<Out, Bypass>>
					arrowOf((Pair<In, Bypass> p) -> 
								Pair.of( getFunc().apply(p.left()), p.right()));
	}
	
	public default <Bypass> Arrow<Pair<In, Bypass>, Pair<Out, Bypass>> 
	first() {
		return this.bypass2nd();
	}
	
	public default <Bypass> Arrow<Pair<Bypass, In>, Pair<Bypass, Out>> 
	bypass1st() {	
		return this.<Pair<Bypass, In>, Pair<Bypass, Out>>
					arrowOf((Pair<Bypass, In> p) -> 
								Pair.of( p.left(), getFunc().apply(p.right())));
	}
	
	public default <Bypass> Arrow<Pair<Bypass, In>, Pair<Bypass, Out>> 
	second() {	
		return this.bypass1st();
	}
	
	public default <In1, Out1> Arrow<Pair<In, In1>, Pair<Out, Out1>> 
	combine(Applicable<In1, Out1> parallelArrow) {
		return this.<Pair<In, In1>, Pair<Out, Out1>>
					arrowOf((Pair<In, In1> input) -> 
								Pair.of(this.apply(input.left()), 
										parallelArrow.apply(input.right())) );
	}
	
	public default <Out1> Arrow<In, Pair<Out, Out1>> 
	cloneInput(Applicable<In, Out1> parallelArrow) {
		return this.<In, Pair<Out, Out1>>
			   arrowOf(i -> Pair.of(this.apply(i), parallelArrow.apply(i)));
	}
	
	public default <OutP1, OutP2> Arrow<In, Pair<OutP1, OutP2>> 
	split(Applicable<Out, OutP1> parallelApp1, Applicable<Out, OutP2> parallelApp2) {
		Arrow<Out, OutP1> parallelArrow1 = this.<Out, OutP1>arrowOf(parallelApp1);
		Arrow<Out, OutP2> parallelArrow2 = this.<Out, OutP2>arrowOf(parallelApp2);
	
		return this.<Pair<OutP1, OutP2>>join(parallelArrow1.cloneInput(parallelArrow2));
	}
	
	public default Out apply(In val) {	
		return getFunc().apply(val);
	}
}