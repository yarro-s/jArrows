## jArrows
#### A Ligthweight Arrows Implementation In Java
This library implements a general interface to computation called [Arrow](https://www.haskell.org/arrows/) first [defined](https://www.haskell.org/arrows/biblio.html#Hug00) by John Hughes. Get the [latest](https://github.com/yarric/jArrows/tree/master/release/latest) JAR and use it with 

```Java import jArrows.Arrows.*;```

#### Quick Tutorial
An `Arrow<In, Out>` of a function `f` represents a computation `->[f(x)]->` defined by the function `Out f(In x)`. Arrows allow us to compose computations (actions) in various ways.
For example let's define a computation `->[*5]-->[/10.5]-->[toString]->` which takes some integer number, multiplies it by 5 and divides the result by 10.5, and then returns the final result converted to a string
```Java
Arrow<Integer, String> numProcessor =   // the input type is Integer, the final output is String
		Action.of((Integer i) -> i*5)       // at the beginning the input type is declared explicitly
		      .join(i -> i/10.5)            
		      .join(String::valueOf);       
			    
// returns string of (10 *5/10.5) = "4.762..."
String procRes = numProcessor.apply(10); 
```                                                                               
Another real-world example represents the verification process of a person. 

```Java
//                (left)     ->[verifyID]---> 
//  ->[new Person(name, id)]-|              |-->[Person or trespasser]->
//                (right)    ->[verifyName]->
Arrow<String, Person> personVerifier = 
		Action.of((String name) ->               // pass a new person's name
		            new Person(name, 214653))    // create the person and his or her ID
		      // check both attributes in parallel
              .split(Person::verifyID, Person::verifyName)
              // p is a Pair with the results of both previous checks
              .join(p -> p.left.isPassed() && p.right.isPassed() 
			        ? p.left : new Person("trespasser", 0));      
						  
Person verifiedPerson1 = personVerifier.apply("Jane").isPassed(); // true
```

#### Arrows By Example
The `Arrow` interface declares and implements useful methods for combining actions (i. e. classes that implement the `Arrow<In, Out>` interface). Classes such as `Action` or `ParallelAction` represent specific `Arrow` implementations, for example `ParallelAction` supports parallel execution of the connected actions. These methods do not mutate anything, they only construct and return new actions. 

##### Joining actions
Method `f.join(g)` joins two arrows (actions) `f` and `g` into a new action so that the output of the first action `f` is fed into the input of the second action `g`. 

![Image](https://github.com/yarric/jArrows/blob/master/docs/images/join.png)

Example:
```Java
Arrow<Integer, String> f = Action.of((Integer i) -> "-- "+String.valueOf(2*i));
Arrow<String, String> g = Action.of((String s) -> s+"!!!");

Arrow<Integer, String> fThenG = f.join(g);

fThenG.apply(5);   // result "-- 10!!!"
```

Or in the shortened form 
```Java
Arrow<Integer, String> fThenG = Action.of((Integer i) -> "-- "+String.valueOf(2*i)) // f
				      .join(s -> s+"!!!");    // g
String res = fThenG.apply(5);   // res is "-- 10!!!"
```

##### Bypassing actions
Method `f.first()` (the same as `f.bypass2nd()`) creates a new action which takes the input of `f` and outputs a `Pair` in which the first element is the output of `f` and the second element is `f`'s input unchanged.

![Image](https://github.com/yarric/jArrows/blob/master/docs/images/first.png)

Example:
```Java
Arrow<Integer, Double> f = Action.of((Integer i) -> 5*i/4);

Arrow<Integer, Pair<Double, Integer>> f_andInput = f.first();

Pair<Double, Integer> res = f_andInput.apply(2);   // res is Pair(2.5, 2)
System.out.println(res.left, "|", res.right);      // prints: 2.5 | 2
```

The `f.second()` (the same as `f.bypass1st()`) is similar, but here the first element of the result is unchanged.

![Image](https://github.com/yarric/jArrows/blob/master/docs/images/second.png)

```Java
Arrow<Integer, Double> f = Action.of((Integer i) -> 5*i/4);

Arrow<Integer, Pair<Integer, Double>> inputAnd_f = f.second();

Pair<Integer, Double> res = inputAnd_f.apply(2);   // res is Pair(2, 2.5)
System.out.println(res.left, "|", res.right);      // prints: 2 | 2.5
```

##### Combining actions
Method `f.combine(g)` combines two actions `f` and `g` into a new action which takes the inputs of `f` and `g` as a `Pair` and returns the results of these actions as another pair.

![Image](https://github.com/yarric/jArrows/blob/master/docs/images/combine.png)

```Java
Arrow<Integer, String> f = Action.of((Integer i) -> String.valueOf(2*i));
Arrow<Double, Double> g = Action.of((Double d) -> 7.5*d);

Arrow<Pair<Integer, Double>, Pair<String, Double>> f_with_g = f.combine(g);

Pair<Double, Integer> res = f_with_g.apply(Pair.of(10, 2.5));   // res is Pair("20", 18.75)
```

##### Clonning input
Method `f.clone(g)` returns a new action in which the input of `f` is also clonned to `g` and the results of both actions `f` and `g` are returned as a pair.

![Image](https://github.com/yarric/jArrows/blob/master/docs/images/clone.png)

```Java
Arrow<Integer, String> f = Action.of((Integer i) -> String.valueOf(5*i));
Arrow<Integer, Double> g = Action.of((Integer d) -> 2.75*d);

Arrow<Integer, Pair<String, Double>> f_and_g = f.clone(g);

Pair<Double, Integer> res = f_and_g.apply(10);   // res is Pair("50", 27.5)
```

##### Splitting the flow
Method `f.split(g, h)` returns a new action in which the result of `f` is fed into both actions `g` and `h` and the final result of `g` and `h` applied to the result of `f` is returned as a pair.

![Image](https://github.com/yarric/jArrows/blob/master/docs/images/split.png)


```Java
Arrow<String, Integer> f = Action.of((Integer i) -> 2*Integer.valueOf(i));
Arrow<Integer, Double> g = Action.of((Integer d) -> 5.77*d);
Arrow<Integer, Integer> h = Action.of((Integer d) -> 10*d);

Arrow<String, Pair<Double, Integer>> f_and_split_gh = f.split(g, h);

Pair<Double, Integer> res = f_and_split_gh.apply(10);   // res is Pair(115.4, 200)
```
