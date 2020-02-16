package frc.team6027.robot.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

public class DataClient implements MessageListener<Double> {

    @Override
    public void onMessage(Message<Double> ultrasonic) {
        Double currentReading = ultrasonic.getMessageObject();
        System.out.println("Ultrasonic: " + currentReading);
    }

    public static void main(String[] args) {
        ClientConfig config = new ClientConfig();
        GroupConfig groupConfig = config.getGroupConfig();
        groupConfig.setName("robot");
        ClientNetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.addAddress("192.168.254.99:5806");
 
        config.setNetworkConfig(networkConfig);
        HazelcastInstance hz = HazelcastClient.newHazelcastClient(config);
        ITopic<Double> topic = hz.getTopic("ultrasonic");
        topic.addMessageListener(new DataClient());
    }

}