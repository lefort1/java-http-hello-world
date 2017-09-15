package mike706574;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.jms.ConnectionFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

public class MessagingTest {
    private static final String URL = "tcp://localhost:61616";

    private static List<String> receivedMessages =
            Collections.synchronizedList(new ArrayList<String>());

    private JMSConsumer consumer;
    private JMSProducer producer;

    private static void sleep(Integer ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Before
    public void setUp() {
        ConnectionFactory connFactory = new ActiveMQConnectionFactory(URL);
        Consumer<String> handler = message -> receivedMessages.add(message);
        consumer = new JMSConsumer("test",
                connFactory,
                handler);
        consumer.start("test");
        producer = new JMSProducer("test",
                connFactory);
        receivedMessages.clear();
    }

    @After
    public void tearDown() {
        consumer.stop();
        receivedMessages.clear();
    }

    @Ignore
    @Test
    public void textMessage() {
        producer.sendTextMessage("test", "foo");
        sleep(200);
        assertEquals(receivedMessages.toString(),
                1,
                receivedMessages.size());
        assertEquals("foo", receivedMessages.get(0));
    }
}
