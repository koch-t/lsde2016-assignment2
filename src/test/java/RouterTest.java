import com.fasterxml.jackson.databind.ObjectMapper;
import model.Coord;
import model.GeoJsonCollection;
import model.GeoJsonFeature;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

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
        DateTimeFormatter timeFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.forID("America/New_York"));
        DateTime pickupDatetime = DateTime.parse("2013-01-01 15:11:48", timeFormat);
        DateTime dropoffDateTime = DateTime.parse("2013-01-01 15:18:10", timeFormat);
        List<Router.EdgeVisit> visits = router.route(fromCoord, toCoord, 1609, pickupDatetime, dropoffDateTime);
        assertEquals(16, visits.size());
        for (Router.EdgeVisit vs : visits){
            assertEquals(15, vs.getHourOfDay());
        }
    }

    @Test
    public void testRouteGeoJson() throws Throwable {
        Router router = new Router();
        Coord fromCoord = new Coord(40.7579765f, -73.97816467f);
        Coord toCoord = new Coord(40.75117111f, -73.989837646f);
        DateTimeFormatter timeFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.forID("America/New_York"));
        DateTime pickupDatetime = DateTime.parse("2013-01-01 15:11:48", timeFormat);
        DateTime dropoffDateTime = DateTime.parse("2013-01-01 15:18:10", timeFormat);
        List<Router.EdgeVisit> visits = router.route(fromCoord, toCoord, 1609, pickupDatetime, dropoffDateTime);
        ObjectMapper mapper = new ObjectMapper();
        GeoJsonCollection collection = new GeoJsonCollection();
        for (Router.EdgeVisit vs : visits){
            GeoJsonFeature f = router.geometryEdge(vs.getEdgeId());
            collection.addGeoJsonFeature(f);
        }
        System.out.println(mapper.writeValueAsString(collection));
    }
}
