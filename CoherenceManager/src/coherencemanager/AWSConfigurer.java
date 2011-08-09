package coherencemanager;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.autoscaling.AmazonAutoScalingAsyncClient;
import com.amazonaws.services.autoscaling.model.CreateAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.CreateLaunchConfigurationRequest;
import com.amazonaws.services.ec2.model.InstanceType;
import java.util.Collection;

/**
 *
 * @author vrg
 */
public class AWSConfigurer {

    private static AWSConfigurer instance = new AWSConfigurer();
    private AWSCredentials credentials = new BasicAWSCredentials("AKIAJZHJ462NHXHWOMLQ", "wskDXaQ7Owkh6SGKAieBEy8pIgDvjKNFO/LTx5+R");

    private AWSConfigurer() {
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
