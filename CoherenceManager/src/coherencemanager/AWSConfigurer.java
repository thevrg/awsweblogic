package coherencemanager;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.autoscaling.AmazonAutoScalingAsyncClient;
import com.amazonaws.services.autoscaling.model.CreateAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.CreateLaunchConfigurationRequest;
import com.amazonaws.services.ec2.model.InstanceType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

/**
 *
 * @author vrg
 */
public class AWSConfigurer {

    private static AWSConfigurer instance = new AWSConfigurer();
    private AWSCredentials credentials;

    private AWSConfigurer() {
        InputStream in = getClass().getResourceAsStream("/awsCredentials.properties");
        if (in == null) {
            throw new RuntimeException("awsCredentials.properties must be in classpath");
        }
        
        Properties p = new Properties();
        try {
            p.load(in);
        } catch (IOException ex) {
            throw new RuntimeException("awsCredentials.properties must be readable by classloader");
        }
        String accessKey = p.getProperty("accessKey");
        if (accessKey == null) {
            throw new RuntimeException("accessKey not set in awsCredentials.properties");
        }
        
        String secretKey = p.getProperty("secretKey");
        if (secretKey == null) {
            throw new RuntimeException("secretKey not set in awsCredentials.properties");
        }
        
        credentials = new BasicAWSCredentials(accessKey, secretKey);
    }

    public AWSCredentials getCredentials() {
        return credentials;
    }

    public static AWSConfigurer getInstance() {
        return instance;
    }

    public void createLaunchConfig(String name, String imageId, InstanceType type) {
        AmazonAutoScalingAsyncClient c = new AmazonAutoScalingAsyncClient(credentials);
        
        CreateLaunchConfigurationRequest req = new CreateLaunchConfigurationRequest();
        req.setLaunchConfigurationName(name);
        req.setImageId(imageId);
        req.withInstanceType(type.toString());
        
        c.createLaunchConfiguration(req);
    }

    public void createAutoScalingGroup(String name, int minSize, int maxSize, Collection<String>availabilityZones, Collection<String>loadBalancers) {
        
        AmazonAutoScalingAsyncClient c = new AmazonAutoScalingAsyncClient(credentials);
        CreateAutoScalingGroupRequest req = new CreateAutoScalingGroupRequest();
        req.setAutoScalingGroupName(name);
        req.setMinSize(minSize);
        req.setMaxSize(maxSize);
        req.setLoadBalancerNames(loadBalancers);
        req.setAvailabilityZones(availabilityZones);
        c.createAutoScalingGroup(req);
    }
}
