package mike706574;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TradesMainTest {
    private static final String URL = "tcp://localhost:61616";

    private static List<String> receivedMessages =
            Collections.synchronizedList(new ArrayList<String>());

    private JMSConsumer consumer;
    private JMSProducer producer;

}
