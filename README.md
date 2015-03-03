# parsercombinator
A parser combinator for Java 8. Working, but mostly a proof of concept.

```java
import static dk.ahnfelt.parsercombinator.Parsers.*;
```

```java
Parser<String> parseFoo = string("foo");
parseFoo.parse("foo") // returns "foo"
parseFoo.parse("bar") // throws Parsers.Failure
```

```java
Parser<Integer> parseIntegerMatch = regex("[0-9]+").map(m -> Integer.parseInt(m.group()));
parseFoo.parse("42") // returns "42"
parseFoo.parse("bar") // throws Parsers.Failure
```

```java
Parser<Pair<String, Integer>> parseFooN = parseFoo.then(parseInteger);
parseFoo.parse("foo7") // returns ("foo", 7)
```

```java
Parser<Integer> parseFooN2 = skip(parseFoo).then(parseInteger);
parseFooN2.parse("foo7") // returns 7
```

```java
Parser<String> parseFooOrBar = choice(string("foo"), string("bar"));
parseFoo.parse("foo") // returns "foo"
parseFoo.parse("bar") // returns "bar"
parseFoo.parse("quux") // throws Parsers.Failure
```

```java
// Alternative version of the above
Parser<String> parseFooOrBar2 = 
    regex("[a-z]+").
    map(m -> m.group()).
    filter(t -> t.equals("foo") || t.equals("bar"));
```

```java
Parser<Pair<Integer, Integer>> parsePlus = 
    parseInteger.
    skip(string("+")).
    then(parseInteger);
Parser<Integer> parseAndCompute = parsePlus.map(match((x, y) -> x + y));
parseAndCompute.parse("7+3") // returns 10
```

```java
Parser<List<Integer>> parseList = parseInteger.zeroOrMore(string(","));
parseList.parse("1,2,4,8,16,32") // returns [1, 2, 4, 8, 16, 32]
```

```java
Parser<String> parseToken = regex("\\s*([a-z0-9]+)\\s*").map(m -> m.group(1));
Parser<String> keyword(String name) { return parseToken.filter(t -> t.equals(name)); }

// The nested pairs can get hairy - use .map(match(...) -> ...) to get rid of them before you have to write types like this
Parser<<Pair<Pair<String, String>, Optional<String>>> parseIf = 
    skip(keyword("if")).then(parseToken).
    skip(keyword("then")).then(parseToken).
    then(skip(keyword("else")).then(parseToken).optional()).
    skip(keyword("end"));

parseIf.parse("if x then y else z end") // returns (("x", "y"), Optional["z"])

Parser<String> parseIfAndCompute = 
    parseIf.map(match((x, y, z) -> x.equals("true") ? y : z.orElse("void")));

parseIfAndCompute.parse("if true then y else z end") // returns "y"
parseIfAndCompute.parse("if false then y end") // returns "void"
```
