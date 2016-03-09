import model.Coord;
import model.TaxiTrip;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;
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

    /*
    medallion 0
    ,hack_license 1,
    vendor_id 2,
    rate_code 3,
    store_and_fwd_flag 4
    ,pickup_datetime 5,
    dropoff_datetime 6,
    passenger_count 7,
    trip_time_in_secs 8,
    trip_distance 9,
    pickup_longitude 10,
    pickup_latitude 11,
    dropoff_longitude 12,
    dropoff_latitude 13

     */

    public final static int COLUMN_PICKUP_DATETIME = 5;
    public final static int COLUMN_DROPOFF_DATETIME = 6;
    public final static int COLUMN_TRIP_DISTANCE = 9;
    public final static int COLUMN_PICKUP_LATITUDE = 11;
    public final static int COLUMN_PICKUP_LONGITUDE = 10;
    public final static int COLUMN_DROPOFF_LATITUDE = 13;
    public final static int COLUMN_DROPOFF_LONGITUDE = 12;

    public static TaxiTrip parse(String s) throws IOException {
        DateTimeFormatter timeFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.forID("America/New_York"));
        String[] values = s.split(",");
        Coord pickupCoord = new Coord(Double.parseDouble(values[COLUMN_PICKUP_LATITUDE]),
                Double.parseDouble(values[COLUMN_PICKUP_LONGITUDE]));
        Coord dropoffCoord = new Coord(Double.parseDouble(values[COLUMN_DROPOFF_LATITUDE]),
                Double.parseDouble(values[COLUMN_DROPOFF_LONGITUDE]));

        int tripDistance = milesToMeter(Float.parseFloat(values[COLUMN_TRIP_DISTANCE]));
        DateTime pickupDatetime = DateTime.parse(values[COLUMN_PICKUP_DATETIME], timeFormat);
        DateTime dropoffDateTime = DateTime.parse(values[COLUMN_DROPOFF_DATETIME], timeFormat);

        return new TaxiTrip(pickupDatetime, dropoffDateTime, tripDistance, pickupCoord, dropoffCoord);
    }

    public static List<TaxiTrip> parse(InputStream is) throws IOException {
        DateTimeFormatter timeFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.forID("America/New_York"));
        final Reader reader = new InputStreamReader(new BOMInputStream(is), "UTF-8");
        final CSVParser parser = new CSVParser(reader, DEFAULT.withHeader());
        ArrayList<TaxiTrip> trips = new ArrayList<>(5000000);
        for (final CSVRecord record : parser) {
            Coord pickupCoord = new Coord(Float.parseFloat(record.get("pickup_latitude")),
                    Double.parseDouble(record.get("pickup_longitude")));
            Coord dropoffCoord = new Coord(Double.parseDouble(record.get("dropoff_latitude")),
                    Double.parseDouble(record.get("dropoff_longitude")));

            int tripDistance = milesToMeter(Float.parseFloat(record.get("trip_distance")));
            DateTime pickupDatetime = DateTime.parse(record.get("pickup_datetime"), timeFormat);
            DateTime dropoffDateTime = DateTime.parse(record.get("dropoff_datetime"), timeFormat);

            TaxiTrip trip = new TaxiTrip(pickupDatetime, dropoffDateTime, tripDistance, pickupCoord, dropoffCoord);
            trips.add(trip);
        }
        return trips;
    }
}
