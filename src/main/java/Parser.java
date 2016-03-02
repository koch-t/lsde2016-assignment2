import model.Coord;
import model.TaxiTrip;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.apache.commons.csv.CSVFormat.*;

/**
 * Created by thomas on 02/03/16.
 */
public class Parser {

    public static int milesToMeter(double miles) {
        return (int) (miles * 1609.344);
    }

    public static List<TaxiTrip> parse(InputStream is) throws IOException {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US).withZone(ZoneId.of("America/New_York"));
        final Reader reader = new InputStreamReader(new BOMInputStream(is), "UTF-8");
        final CSVParser parser = new CSVParser(reader, DEFAULT.withHeader());
        ArrayList<TaxiTrip> trips = new ArrayList<>(5000000);
        for (final CSVRecord record : parser) {
            Coord pickupCoord = new Coord(Float.parseFloat(record.get("pickup_latitude")),
                    Float.valueOf(record.get("pickup_longitude")));
            Coord dropoffCoord = new Coord(Float.parseFloat(record.get("dropoff_latitude")),
                    Float.valueOf(record.get("dropoff_longitude")));

            int tripDistance = milesToMeter(Float.parseFloat(record.get("trip_distance")));
            ZonedDateTime pickupDatetime = ZonedDateTime.parse(record.get("pickup_datetime"), timeFormat);
            ZonedDateTime dropoffDateTime = ZonedDateTime.parse(record.get("dropoff_datetime"), timeFormat);

            TaxiTrip trip = new TaxiTrip(pickupDatetime, dropoffDateTime, tripDistance, pickupCoord, dropoffCoord);
            trips.add(trip);
        }
        return trips;
    }
}
