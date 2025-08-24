package top.ilovemyhome.dag.server.event;

import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.RetriableException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.commons.common.jackson.JacksonUtil;
import top.ilovemyhome.dag.server.domain.FooUser;

import static top.ilovemyhome.commons.common.lang.StringConvertUtils.toStr;
import static top.ilovemyhome.dag.server.event.SharedResource.*;

public class Producer implements Callback {
    private static final Random RND = new Random(0);
    private static final long PROCESSING_DELAY_MS = 20L;
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    protected AtomicLong messageCount = new AtomicLong(0);

    public static void main(String[] args) {
        new Producer().run();
    }

    @Override
    public void onCompletion(RecordMetadata metadata, Exception e) {
        if (e != null) {
            logger.error("Error while producing message", e);
            if (!retriable(e)) {
                System.exit(1);
            }
        } else {
            logger.info("Record sent to [[{}/{}] with offset {}.",
                metadata.topic(), metadata.partition(), metadata.offset());
        }
    }

    public void run() {
        logger.info("Running producer..........");
        try (var producer = createKafkaProducer()) {
            String value = JacksonUtil.toJson(FooUser.randomFooUser());
            while (messageCount.get() < NUM_MESSAGES) {
                String key = toStr(messageCount.get() % 100 );
                sleep(PROCESSING_DELAY_MS);
                producer.send(new ProducerRecord<>(TOPIC_NAME, key, value), this);
                messageCount.incrementAndGet();
            }
        }
    }

    private KafkaProducer<String, String> createKafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "client-" + UUID.randomUUID());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new KafkaProducer<>(props);
    }

    private void sleep(long ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] randomBytes(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Record size must be greater than zero");
        }
        byte[] payload = new byte[size];
        for (int i = 0; i < payload.length; ++i) {
            payload[i] = (byte) (RND.nextInt(26) + 65);
        }
        return payload;
    }

    private boolean retriable(Exception e) {
        if (e == null) {
            return false;
        } else if (e instanceof IllegalArgumentException
            || e instanceof UnsupportedOperationException
            || !(e instanceof RetriableException)) {
            return false;
        } else {
            return true;
        }
    }


}
