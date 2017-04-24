package cases;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import kafka.api.OffsetResponse;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.TopicAndPartition;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.consumer.SimpleConsumer;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.message.MessageAndMetadata;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * Created by zhengfan on 2017/3/22 0022.
 */
public class KafkaTest {
    private Producer producer;
    private ConsumerConnector consumerconnector;
    private String queueTopic = "doss.pack.quque";

    private void provideKafkaProducerConnection() {
        Properties properties = new Properties();
        //添加参数
        properties.put("metadata.broker.list", "sh01-sjws-cache55.sh01:8092");
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("request.required.acks", "1");
        ProducerConfig producerConfig = new ProducerConfig(properties);
        this.producer = new Producer<String, String>(producerConfig);
    }

    private void provideKafkaQueueConsumerConnector() {
        Properties properties2 = new Properties();
        //添加参数
        properties2.put("zookeeper.connect", "sh01-sjws-cache55.sh01:8181");
        properties2.put("zookeeper.connectiontimeout.ms", 10000);
        //consumer group id
        properties2.put("group.id", "0");
        properties2.put("zookeeper.session.timeout.ms", "4000");
        properties2.put("zookeeper.sync.time.ms", "200");
        properties2.put("auto.commit.interval.ms", "1000");
        ConsumerConfig consumerConfig = new ConsumerConfig(properties2);
        consumerconnector = Consumer.createJavaConsumerConnector(consumerConfig);
    }

    public KafkaTest() {
        provideKafkaProducerConnection();
        provideKafkaQueueConsumerConnector();

    }

    @Test
    public void Publish() {
        String message = new Date().toString();
        KeyedMessage<String, String> keyedMessage = new KeyedMessage<String, String>(queueTopic, message);
        System.out.println(message);
        producer.send(keyedMessage);
    }

    @Test
    public void Consumer() throws UnsupportedEncodingException {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(queueTopic, 1);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerconnector.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(queueTopic);
        for (KafkaStream<byte[], byte[]> kafkaStream : streams) {
            for (MessageAndMetadata<byte[], byte[]> aStream : (Iterable<MessageAndMetadata<byte[], byte[]>>)
                    kafkaStream) {
                System.out.println(new String(aStream.message(), "utf-8"));
            }
        }
    }

    @Test
    public void test(){
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(queueTopic, 1);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerconnector.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(queueTopic);
        for (KafkaStream<byte[], byte[]> stream : streams) {
            System.out.println(stream);
            for (MessageAndMetadata<byte[], byte[]> aStream : stream) {
                try {
                    System.out.println(new String(aStream.message(), "UTF8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
