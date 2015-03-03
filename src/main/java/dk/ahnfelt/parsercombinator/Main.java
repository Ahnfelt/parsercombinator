package dk.ahnfelt.parsercombinator;

import java.util.Arrays;
import java.util.function.Function;

import static dk.ahnfelt.parsercombinator.Parsers.*;

public class Main {

    public static void main(String[] arguments) throws Failure {
        ifStatement();
    }

    public static void ifStatement() throws Failure {

        String test = "if a then b elseif c then d elseif c2 then d2 else e end";

        Parser<String> token = regex("([a-z][a-z0-9]*)(\\s+|$)").map(m -> m.group(1));
        Parser<String> variable = token.filter(t -> !Arrays.asList("if", "then", "else", "elseif", "end").contains(t));
        Function<String, Parser<String>> keyword = k -> token.filter(k::equals);

        Parser<Pair<String, String>> parseElseIf =
                skip(keyword.apply("elseif")).
                then(variable).
                skip(keyword.apply("then")).
                then(variable);

        Parser<String> parseIf =
                skip(keyword.apply("if")).then(variable).
                skip(keyword.apply("then")).then(variable).
                then(parseElseIf.zeroOrMore()).
                then(skip(keyword.apply("else")).then(variable).optional()).
                skip(keyword.apply("end")).
                skip(end()).
                map(match((condition, then, elseIfs, otherwise) -> elseIfs.toString()));

        System.out.println(parseIf.parse(test));

    }

}
