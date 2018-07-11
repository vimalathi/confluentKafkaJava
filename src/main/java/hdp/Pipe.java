package hdp;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.StreamsConfig;
//import org.apache.kafka.streams.
import java.util.Properties;

public class Pipe {
    public static void main(String args[]) throws Exception {

        Properties settings = new Properties();
        settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "first-kafks-stream");
        settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "zookeeper:2181");
        //props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        //props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        //settings.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "zookeeper:2181");

        StreamsConfig config = new StreamsConfig(settings);


    }
}
