package com.example.simple;

import com.example.simple.Simple.SimpleMessage;

public class SimpleMain {

    public static void main(String[] args) {
        System.out.println("Hello world!");
        SimpleMessage.Builder builder = SimpleMessage.newBuilder();
        builder.setId(42).setName("Simple Message Name").setIsSimple(true);
        // builder.addSampleList(1).addAllSampleList(Arrays.asList(2, 3, 4));
        // builder.setSampleList(3, 42);

        System.out.println(builder.toString());
        SimpleMessage message = builder.build();

        try { // write the protocol buffers to a binary file
            FileOutputStream outputStream = new FileOutputStream("simple_message.bin");
            message.writeTo(outputStream); outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // can also be sent as a byte array
        // byte[] bytes = message.toByteArray();

        try { // read the protocol buffers from a binary file
            FileInputStream inputStream = new FileInputStream("simple_message.bin");
            SimpleMessage simpleMessage = SimpleMessage.parseFrom(inputStream);
            System.out.println(simpleMessage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
}