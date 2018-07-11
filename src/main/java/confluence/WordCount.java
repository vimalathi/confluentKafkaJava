package confluence;

import kafka.tools.ConsoleProducer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.ValueMapper;
import scala.collection.immutable.Stream;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;


import java.util.Properties;

public class WordCount {
    public static void main(String args[]) throws Exception {

        Properties settings = new Properties();
        settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "first-kafks-stream");
        settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        settings.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        settings.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        settings.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        //settings.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "zookeeper:2181");
        settings.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Stream.StreamBuilder builder new Stream.StreamBuilder();

        KStream<String, String> source = builder.stream("streams-plaintext-input");

        KTable<String, Long> counts = source
                .flatMapValues(new ValueMapper<String, Iterable<String>>() {
                    //@Override
                    public Iterable<String> apply(String value) {
                        return Arrays.asList(value.toLowerCase(Locale.getDefault()).split(""));
                    }
                })
                .groupBy(new KeyValueMapper<String, String, String>() {
                    //@Override
                    public String apply(String key, String value) {
                        return value;
                    }
                })
                .count();

        // need to override value serde to Long type
        counts.toStream().to("streams-wordcount-output", Produced.with(Serdes.String(), Serdes.Long()));

        final KafkaStreams streams = new KafkaStreams(builder.build(), settings);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-wordcount-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });
        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);

        //StreamsConfig config = new StreamsConfig(settings);

        //Properties producerProp = new Properties();
        //producerProp.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        //        io.confluent.kafka.serializers.KafkaAvroSerializer.class);
        //producerProp.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
//                io.confluent.kafka.serializers.KafkaAvroSerializer.class);
        //producerProp.put("schema.registry.url", "http://localhost:9092");

        //KafkaProducer producer = new KafkaProducer(producerProp);
        //we can pass these producer configurations through config file also.

    }
}
