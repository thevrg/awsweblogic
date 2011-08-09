package coherencemanager;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsyncClient;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vrg
 */
public class AWSNotifier {
    
    public static final String AUTO_SCALING_GROUP_NAME="JO_COH_ASG";
    
    private static final AWSNotifier instance = new AWSNotifier();

    private AmazonCloudWatchAsyncClient client;

    private AWSNotifier() {
        client = new AmazonCloudWatchAsyncClient(AWSConfigurer.getInstance().getCredentials());
    }

    public void sendMetrics(int clusterSize, int clusterFreeMemory, int clusterAllMemory, int numberOfObjects, Date time) {
        int clusterUsed = clusterAllMemory - clusterFreeMemory;
        if (time == null) {
            time = new Date();
        }
        Dimension dimension = new Dimension();
        dimension.withName("AutoScalingGroupName").withValue(AUTO_SCALING_GROUP_NAME);
        List<MetricDatum>metricData = new ArrayList<MetricDatum>();
        MetricDatum metricDatum = new MetricDatum();
        metricDatum.withUnit(StandardUnit.Megabytes.name())
                .withMetricName("cluster.freeMem")
                .withDimensions(dimension)
                .withTimestamp(time)
                .withValue(Double.valueOf(clusterFreeMemory));
        metricData.add(metricDatum);
        metricDatum = new MetricDatum();
        metricDatum.withUnit(StandardUnit.Megabytes.name())
                .withMetricName("cluster.usedMem")
                .withDimensions(dimension)
                .withTimestamp(time)
                .withValue(Double.valueOf(clusterUsed));
        metricData.add(metricDatum);
        metricDatum = new MetricDatum();
        metricDatum.withUnit(StandardUnit.Megabytes.name())
                .withMetricName("cluster.allMem")
                .withDimensions(dimension)
                .withTimestamp(time)
                .withValue(Double.valueOf(clusterAllMemory));
        metricData.add(metricDatum);
        metricDatum = new MetricDatum();
        metricDatum.withUnit(StandardUnit.None.name())
                .withMetricName("cluster.size")
                .withDimensions(dimension)
                .withTimestamp(time)
                .withValue(Double.valueOf(clusterSize));
        metricData.add(metricDatum);
        metricDatum = new MetricDatum();
        metricDatum.withUnit(StandardUnit.None.name())
                .withMetricName("cluster.objects")
                .withDimensions(dimension)
                .withTimestamp(time)
                .withValue(Double.valueOf(numberOfObjects));
        metricData.add(metricDatum);
        

        PutMetricDataRequest req = new PutMetricDataRequest();
        req.withNamespace("Coherence").withMetricData(metricData);

        client.putMetricDataAsync(req);
    }

    public static AWSNotifier getInstance() {
        return instance;
    }
    
    public static void main(String[] args) {
        AWSNotifier not = new AWSNotifier();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1);
        int i = 1200;
        while (i > 10) {
            double add = Math.sin(c.getTimeInMillis() / (60000 * 10)) * 100 + 100;
            not.sendMetrics(3, (int)(i + add), 1500, 45, c.getTime());
            c.add(Calendar.MINUTE, 1);
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(AWSNotifier.class.getName()).log(Level.SEVERE, null, ex);
            }
            i -= 1;
        }
    }
}
