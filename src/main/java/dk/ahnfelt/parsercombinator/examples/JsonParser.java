package dk.ahnfelt.parsercombinator.examples;

import java.util.regex.Pattern;

import static dk.ahnfelt.parsercombinator.Parsers.*;
import dk.ahnfelt.parsercombinator.examples.Json.*;
import dk.ahnfelt.parsercombinator.Parser;

// An example of a full parser
public class JsonParser {

    static Parser<Unit> token(String keyword) {
        return regex(Pattern.quote(keyword) + "[\\s\\r\\n]*").map(m -> Unit.instance);
    }

    static Parser<Json> valueP =
            token("null").map(t -> (Json) new JsonNull()).
            or(token("true").map(t -> new JsonBoolean(true))).
            or(token("false").map(t -> new JsonBoolean(false))).
            or(() -> JsonParser.stringP.map(JsonString::new)).
            or(() -> JsonParser.numberP.map(JsonNumber::new)).
            or(() -> JsonParser.objectP).
            or(() -> JsonParser.arrayP);

    static Parser<String> stringP =
            regex("\"([^\"\\\\]*|\\\\[\"\\\\trnbf\\/]|\\\\u[0-9a-f]{4})*\"[\\s\\r\\n]*").
            map(m -> /*StringEscapeUtils.unescapeEcmaScript(*/ m.group(1) /*)*/);

    static Parser<Double> numberP =
            regex("(-?(0|[1-9][0-9]*)([.][0-9]+)?([eE][+-]?[0-9]*)?)[\\s\\r\\n]*").
            map(m -> Double.parseDouble(m.group(1)));

    static Parser<Pair<String, Json>> fieldP =
            stringP.skip(token(":")).then(valueP);

    static Parser<Json> objectP =
            skip(token("{")).then(fieldP.zeroOrMore(token(","))).skip(token("}")).
            map(JsonObject::new);

    static Parser<Json> arrayP =
            skip(token("[")).then(valueP.zeroOrMore(token(","))).skip(token("]")).
            map(JsonArray::new);

    // The final parser skips initial whitespace and requires that the whole input matches.
    public static Parser<Json> jsonP =
            skip(regex("[\\s\\r\\n]*")).
            then(valueP).
            skip(regex("$"));

}
