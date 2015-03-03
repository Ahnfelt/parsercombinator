# parsercombinator
A parser combinator for Java 8. Proof of concept.

```java
import static dk.ahnfelt.parsercombinator.Parsers.*;
```

```java
Parser<String> parseFoo = string("foo");
parseFoo.parse("foo") // returns "foo"
parseFoo.parse("bar") // throws Parsers.Failure
```

```java
Parser<String> parseIntegerMatch = regex("[0-9]+").map(m -> Integer.parseInt(m.group()));
parseFoo.parse("42") // returns "42"
parseFoo.parse("bar") // throws Parsers.Failure
```

```java
Parser<Pair<String, Integer>> parseFooN = parseFoo.then(parseInteger);
parseFoo.parse("foo7") // returns ("foo", 7)
```

```java
Parser<Integer> parseSkipFooN = skip(parseFoo).then(parseInteger);
parseFoo.parse("foo7") // returns 7
```

```java
Parser<Integer> parseFooOrBar = choice(string("foo"), string("bar"));
parseFoo.parse("foo") // returns "foo"
parseFoo.parse("bar") // returns "bar"
parseFoo.parse("quux") // throws Parsers.Failure
```

```java
Parser<Pair<Integer, Integer>> parsePlus = 
    parseInteger.
    skip(string("+")).
    then(parseInteger);
Parser<Integer> parseAndCompute = parsePlus.map(match((x, y) -> x + y));
parsePlus.parse("7+3") // returns 10
```
