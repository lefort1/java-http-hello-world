package mike706574;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.function.Consumer;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

public class MessagingTest {
    private static final String URL = "tcp://localhost:61616";

    private static List<String> receivedMessages =
        Collections.synchronizedList( new ArrayList<String>() );

    private JMSConsumer consumer;
    private JMSProducer producer;

    @Before
    public void setUp() {
        ConnectionFactory connFactory = new ActiveMQConnectionFactory( URL );
        Consumer<String> handler = message -> receivedMessages.add( message );
        consumer = new JMSConsumer( "test",
                                    connFactory,
                                    handler );
        consumer.start( "test" );
        producer = new JMSProducer( "test",
                                    connFactory );
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
        producer.sendTextMessage( "test", "foo" );
        sleep( 200 );
        assertEquals( receivedMessages.toString(),
                      1,
                      receivedMessages.size() );
        assertEquals( "foo", receivedMessages.get( 0 ) );
    }

    private static void sleep( Integer ms ) {
        try {
            Thread.sleep( ms );
        }
        catch( InterruptedException ex ) {
            throw new RuntimeException( ex );
        }
    }
}
