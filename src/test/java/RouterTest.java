import model.Coord;
import org.junit.Test;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Created by thomas on 05/03/16.
 */
public class RouterTest {

    @Test
    public void testRouterLoad() throws IOException {
        Router router = new Router();
    }

    @Test
    public void testRoute() throws Throwable {
        Router router = new Router();
        Coord fromCoord = new Coord(40.7579765f, -73.97816467f);
        Coord toCoord = new Coord(40.75117111f, -73.989837646f);
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US).withZone(ZoneId.of("America/New_York"));
        ZonedDateTime pickupDatetime = ZonedDateTime.parse("2013-01-01 15:11:48", timeFormat);
        ZonedDateTime dropoffDateTime = ZonedDateTime.parse("2013-01-01 15:18:10", timeFormat);
        List<Router.EdgeVisit> visits = router.route(fromCoord, toCoord, 1609, pickupDatetime, dropoffDateTime);
        assertEquals(18, visits.size());
        visits.stream().filter(vs -> vs.getHourOfDay() != 15).forEach(vs -> {
            assertEquals(15, vs.getHourOfDay());
        });
    }
}
