package dk.ahnfelt.parsercombinator.examples;

import dk.ahnfelt.parsercombinator.Parser;

import java.util.Arrays;
import java.util.function.Function;

import static dk.ahnfelt.parsercombinator.Parsers.*;

public class Main {

    public static void main(String[] arguments) throws Failure {

        //String input = "{ \"name\": \"anna\", \"age\": 21, \"interests\": [\"diving\", \"programming\"] }";
        String input = "true";

        Json output = JsonParser.jsonP.parse(input);

        System.out.println(output);

        // ifStatement()
    }

    public static void ifStatement() throws Failure {

        String test = "if a then b elseif c then d elseif c2 then d2 else e end";

        Parser<Pair<String, String>> parseElseIf =
                skip(keyword("elseif")).
                then(variable).
                skip(keyword("then")).
                then(variable);

        Parser<String> parseIf =
                skip(keyword("if")).then(variable).
                skip(keyword("then")).then(variable).
                then(parseElseIf.zeroOrMore()).
                then(skip(keyword("else")).then(variable).optional()).
                skip(keyword("end")).
                skip(end()).
                map(match((condition, then, elseIfs, otherwise) -> elseIfs.toString()));

        System.out.println(parseIf.parse(test));

    }

    private static Parser<String> token = regex("([a-z][a-z0-9]*)(\\s+|$)").map(m -> m.group(1));
    private static Parser<String> variable = token.filter(t -> !Arrays.asList("if", "then", "else", "elseif", "end").contains(t));
    private static Parser<String> keyword(String k) { return token.filter(k::equals); }


}
