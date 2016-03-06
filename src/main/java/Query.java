import com.cotdp.hadoop.ZipFileInputFormat;
import model.TaxiTrip;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.rdd.RDD;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by thomas on 06/03/16.
 */
public class Query implements Serializable{

    private static final Router router = new Router();

    public void query() throws IOException {
        //SparkConf sparkConf = new SparkConf().setAppName("TaxiHeatmap").setMaster("local[2]").set("spark.executor.memory","1g");
        SparkConf sparkConf = new SparkConf().setAppName("TaxiHeatmap").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        sc.textFile("/home/thomas/taxi/*", 1)
                 /* SPlit the lines with the CSV header */
                .filter((Function<String, Boolean>) s -> !s.startsWith("medallion"))
                /* Parse the CSV line into a java pojo */
                .map((Function<String, TaxiTrip>) s -> Parser.parse(s))
                /* Route the trip on to the edges to get the edge visits */
                .flatMap(new FlatMapFunction<TaxiTrip, Router.EdgeVisit>() {
                    @Override
                    public Iterable<Router.EdgeVisit> call(TaxiTrip taxiTrip) throws Exception {
                        return router.route(taxiTrip);
                    }
                }).countByValue();
    }

    public Query(){
    }

    public static void main(String[] args) throws IOException {
        new Query().query();
    }
}
