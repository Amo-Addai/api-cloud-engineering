package com.example.demo.configurations.third.parties;

//import com.twilio.Twilio;
//import com.twilio.rest.api.v2024.account.Message;
//import com.twilio.rest.api.v2024.account.Call;
//import com.twilio.type.PhoneNumber;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.net.URI;


public class TwilioConfiguration {

    private final static String ACCOUNT_SID = "account-id"; // todo: config.env
    private final static String AUTH_ID = "auth-id";

    /*

    static {
        Twilio.init(
                ACCOUNT_SID,
                AUTH_ID
        );
    }

    // todo: should be in a TwilioService which uses TwilioConfiguration
    public static void sendMessage(String msg, String from, String to) {
        Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(from),
                msg
        ).create();
    }

    public static void makeVoiceCall(String uri, String from, String to) {
        try { // TODO: Build an Exception Handling Factory - Wrapper (try & execute interface) & ExceptionHandler (all noted Exceptions)
            Call.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(from),
                    new URI(uri)
            ).create();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    */

    /*

    // TODO: Sample main SpringBootApplication class file implementing ApplicationRunner
    // implementing run method to call SpringKafkaTemplate.sendMessage & consume the message with SpringKafkaTemplate.listen

    public class SmsDemoApp implements ApplicationRunner {

        @Override
        public void run(ApplicationArguments args) throws Exception {
            TwilioConfiguration.sendMessage(
                    "message-content", "from-number", "to-number"
            );
        }

    }

    */

}
