package mike706574;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.function.Consumer;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

public class TradesMainTest {
    private static final String URL = "tcp://localhost:61616";

    private static List<String> receivedMessages =
        Collections.synchronizedList( new ArrayList<String>() );

    private JMSConsumer consumer;
    private JMSProducer producer;

}
