package dk.ahnfelt.parsercombinator.examples;

import dk.ahnfelt.parsercombinator.Parsers.Pair;

import java.util.List;

public class Json {

    public static class JsonNull extends Json {}

    public static class JsonBoolean extends Json {
        public final boolean value;

        public JsonBoolean(boolean value) {
            this.value = value;
        }
    }

    public static class JsonNumber extends Json {
        public final double value;

        public JsonNumber(double value) {
            this.value = value;
        }
    }

    public static class JsonString extends Json {
        public final String value;

        public JsonString(String value) {
            this.value = value;
        }
    }

    public static class JsonArray extends Json {
        public final List<Json> value;

        public JsonArray(List<Json> value) {
            this.value = value;
        }
    }

    public static class JsonObject extends Json {
        public final List<Pair<String, Json>> value;

        public JsonObject(List<Pair<String, Json>> value) {
            this.value = value;
        }
    }

}
