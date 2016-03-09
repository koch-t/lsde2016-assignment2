import junit.framework.Assert;
import model.TaxiTrip;
import org.junit.Test;

import java.io.*;
import java.util.List;

import static junit.framework.Assert.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ParserTest {

    @Test
    public void milesMeterConversion() throws IOException {
        assertEquals(1609, Parser.milesToMeter(1));
        assertEquals(804, Parser.milesToMeter(0.5));
    }

    @Test
    public void testParser() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = new FileInputStream(new File(classLoader.getResource("input.csv").getFile()));
        List<TaxiTrip> trips = Parser.parse(is);
        assertEquals(20, trips.size());
        TaxiTrip firstTrip = trips.get(0);
        assertEquals(1609, firstTrip.getTripDistance());
        assertEquals(40.7579765, firstTrip.getPickupCoord().getLatitude(), 0.0001);
        assertEquals(-73.97816467, firstTrip.getPickupCoord().getLongitude(), 0.00001);
        assertEquals(40.75117111, firstTrip.getDropoffCoord().getLatitude(), 0.00001);
        assertEquals(-73.989837646, firstTrip.getDropoffCoord().getLongitude(), 0.00001);
        /*
        assertEquals("2013-01-01T15:11:48-05:00[America/New_York]", firstTrip.getPickupDatetime().toString());
        assertEquals("2013-01-01T15:18:10-05:00[America/New_York]", firstTrip.getDropoffDateTime().toString());*/
    }
}