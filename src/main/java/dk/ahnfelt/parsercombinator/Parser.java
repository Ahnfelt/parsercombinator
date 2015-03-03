package dk.ahnfelt.parsercombinator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Parser<A> {

    public A parse(Parsers.Input in) throws Parsers.Failure;

    public default A parse(CharSequence in) throws Parsers.Failure {
        return parse(new Parsers.Input(in, 0));
    }

    public default Optional<A> tryParse(CharSequence in) throws Parsers.Failure {
        return tryParse(new Parsers.Input(in, 0));
    }

    public default Optional<A> tryParse(Parsers.Input in) {
        try {
            return Optional.of(parse(in));
        } catch (Parsers.Failure e) {
            return Optional.empty();
        }
    };

    public default <U> Parser<Parsers.Pair<A, U>> then(Parser<U> that) {
        return in -> new Parsers.Pair<>(parse(in), that.parse(in));
    }

    public default <U> Parser<Parsers.Pair<A, U>> then(Supplier<Parser<U>> that) {
        return in -> then(that.get()).parse(in);
    }

    public default <U> Parser<A> skip(Parser<U> that) {
        return in -> { A result = parse(in); that.parse(in); return result; };
    }

    public default <U> Parser<A> skip(Supplier<Parser<U>> that) {
        return in -> skip(that.get()).parse(in);
    }

    public default Parser<A> or(Parser<A> that) {
        return new Parser<A>() {
            public A parse(Parsers.Input in) throws Parsers.Failure {
                Parsers.Input input = in.remember();
                try {
                    return Parser.this.parse(in);
                } catch (Parsers.Failure e) {
                    return that.parse(input);
                }
            }
        };
    }

    public default Parser<A> or(Supplier<Parser<A>> that) {
        return in -> or(that.get()).parse(in);
    }

    public default Parser<Optional<A>> optional() {
        return in -> {try {
            return Optional.of(parse(in));
        } catch (Parsers.Failure e) {
            return Optional.empty();
        }};
    }

    public default Parser<List<A>> zeroOrMore() {
        return in -> {
            List<A> result = new ArrayList<A>();
            Optional<A> element;
            while((element = tryParse(in)).isPresent()) {
                result.add(element.get());
            }
            return result;
        };
    }

    public default Parser<List<A>> zeroOrMore(Parser<?> separator) {
        return in -> {
            List<A> result = new ArrayList<A>();
            Optional<A> element;
            while((element = tryParse(in)).isPresent()) {
                result.add(element.get());
                if(!separator.tryParse(in).isPresent()) break;
            }
            return result;
        };
    }

    public default Parser<List<A>> oneOrMore() {
        return in -> {
            List<A> result = new ArrayList<A>();
            result.add(parse(in));
            Optional<A> element;
            while((element = tryParse(in)).isPresent()) {
                result.add(element.get());
            }
            return result;
        };
    }

    public default Parser<List<A>> oneOrMore(Parser<?> separator) {
        return in -> {
            List<A> result = new ArrayList<A>();
            result.add(parse(in));
            Optional<A> element;
            Parser<A> parser = Parsers.skip(separator).then(this);
            while((element = parser.tryParse(in)).isPresent()) {
                result.add(element.get());
            }
            return result;
        };
    }

    public default Parser<A> filter(Predicate<A> f) {
        return in -> { A result = parse(in); if(!f.test(result)) throw Parsers.Failure.exception; return result; };
    }

    public default <U> Parser<U> map(Function<A, U> f) {
        return in -> f.apply(parse(in));
    }

    public default <U> Parser<U> flatMap(Function<A, Parser<U>> f) {
        return in -> f.apply(parse(in)).parse(in);
    }

}
