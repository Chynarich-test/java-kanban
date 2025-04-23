package http.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if (duration == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(duration.toSeconds());
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        final String text = jsonReader.nextString();
        if (text == null || text.isEmpty() || text.equals("null")) {
            return null;
        }
        try {
            return Duration.ofSeconds(Long.parseLong(text));
        } catch (NumberFormatException e) {
            return null;
        }

    }
}
