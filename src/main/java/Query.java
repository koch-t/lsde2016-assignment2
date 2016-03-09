import model.TaxiTrip;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Query implements Serializable {

    final static Router router = new Router();

    /*
    Note that this method should only be used if the resulting map is expected to be small,
    as the whole thing is loaded into the driver's memory.
    To handle very large results, consider using rdd.map(x => (x, 1L)).reduceByKey(_ + _),
     which returns an RDD[T, Long] instead of a map.
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        new Query().query();
    }

    public void query() throws IOException, URISyntaxException {
        final String taskId = UUID.randomUUID().toString();
        SparkConf sparkConf = new SparkConf().setAppName("TaxiHeatmap").setMaster("yarn-cluster");
        try (JavaSparkContext sc = new JavaSparkContext(sparkConf)) {
            sc.textFile("hdfs:///user/lsde01/taxibz2/*.bz2", 32)
                    /* Skip the lines with the CSV header */
                    .filter(new Function<String, Boolean>() {
                        @Override
                        public Boolean call(String s) throws Exception {
                            return !s.startsWith("medallion");
                        }
                    })
                    /* Parse the CSV line into a java pojo */
                    .map(new Function<String, TaxiTrip>() {
                        @Override
                        public TaxiTrip call(String s) throws Exception {
                            try {
                                return Parser.parse(s);
                            } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
                                e.printStackTrace(System.err);
                                System.err.println(s);
                                return null;
                            }
                        }
                    })
                    /* Filter null values occuring when parsing failed and filter time-travel trips */
                    .filter(new Function<TaxiTrip, Boolean>() {
                        @Override
                        public Boolean call(TaxiTrip taxiTrip) throws Exception {
                            return taxiTrip != null &&
                                    taxiTrip.getPickupDatetime().isBefore(taxiTrip.getDropoffDateTime());
                        }
                    })
                    /* Route the trip on to the edges to get the edge visits */
                    .flatMap(new FlatMapFunction<TaxiTrip, Router.EdgeVisit>() {
                        @Override
                        public List<Router.EdgeVisit> call(TaxiTrip taxiTrip) throws Exception {
                            return router.route(taxiTrip);
                        }
                    })
                    /* We cannot use countbyvalue due to memory usage */
                    .mapToPair(new PairFunction<Router.EdgeVisit, Router.EdgeVisit, Long>() {
                        @Override
                        public Tuple2<Router.EdgeVisit, Long> call(Router.EdgeVisit edgeVisit) throws Exception {
                            return new Tuple2<>(edgeVisit, 1L);
                        }
                    /* Reduce edge-visits to tuples (edgevisit,number_of_visits */
                    }).reduceByKey(new Function2<Long, Long, Long>() {
                        @Override
                        public Long call(Long value, Long value2) throws Exception {
                            return value + value2;
                        }
                    })
                    /* Reduce (edge-visits,visits) to tuples (edgevisit,number_of_visits_on_entire_data_set) */
                    .reduceByKey(new Function2<Long, Long, Long>() {
                        @Override
                        public Long call(Long value, Long value2) throws Exception {
                            return value + value2;
                        }
                    },1)
                    /* Map output to csv */
                    .map(new Function<Tuple2<Router.EdgeVisit,Long>, String>() {
                        @Override
                        public String call(Tuple2<Router.EdgeVisit, Long> tuple) throws Exception {
                            return String.format(Locale.US,"%d,%d,%d", tuple._1().getEdgeId(), tuple._1().getHourOfDay(), tuple._2());
                        }
                     }).saveAsTextFile("hdfs:///user/lsde01/result-"+taskId+"-"+System.nanoTime()+".csv");
        }
    }
}
