package com.example.demo.configurations.templates;

import com.example.demo.configurations.KafkaProducerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.SpringBootApplication;

public class SpringKafkaTemplate {

    /*

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
            // = KafkaProducerConfiguration.getKafkaTemplate();
            // * no need to get Autowired kafkaTemplate from KafkaProducerConfiguration

    public static void sendMessage(String topic, String msg) {
        kafkaTemplate.send(topic, msg);
    }

    @KafkaListner(
            topics="auto",
            groupId="group-id"
    )
    public static void listen(String msg) {
        System.out.println(
                "Received Message in group (id): "
                + msg
        );
    }

    */

    /*

    // TODO: Sample main SpringBootApplication class file implementing ApplicationRunner
    // implementing run method to call SpringKafkaTemplate.sendMessage & consume the message with SpringKafkaTemplate.listen

    @SpringBootApplication
    public class KafkaDemoApplication implements AppicationRunner {

        // import SpringKafkaTemplate when class as main entrypoint class

        @Override
        public void run(ApplicationArguments args) throws Exception {
            SpringKafkaTemplate.listen("message content"); // test-listen 1st, then send message
            SpringKafkaTemplate.sendMessage("message content");
        }

    }

    */

}
