package com.example.complex;

import com.example.complex.Complex.*;

public class ComplexMain {

    public static void main(String[] args) {
        System.out.println("Hello world!");

        SampleMessage sample = newSampleMessage(55, "Sample Message Name");

        ComplexMessage.Builder builder = ComplexMessage.newBuilder();
        builder.setOneSample(sample);

        builder.setMultipleSamples(newSampleMessage(66, "2nd Sample Message Name"));
        builder.addMultipleSamples(newSampleMessage(66, "Next Sample Message Name"))
            .addAllMultipleSamples(Arrays.asList(
                newSampleMessage(66, "Next Sample Message Name"),
                newSampleMessage(66, "Next Sample Message Name"),
                newSampleMessage(66, "Next Sample Message Name")
            ));
        builder.setMultipleSamples(
            newSampleMessage(66, "Next Sample Message Name"),
            newSampleMessage(66, "Next Sample Message Name")
        );

        ComplexMessage message = builder.build();
        System.out.println(message.toString());

        // todo: get samples
        // message.getMultipleSamples();

        try { // write the protocol buffers to a binary file
            FileOutputStream outputStream = new FileOutputStream("complex_message.bin");
            message.writeTo(outputStream); outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // can also be sent as a byte array
        // byte[] bytes = message.toByteArray();

        try { // read the protocol buffers from a binary file
            FileInputStream inputStream = new FileInputStream("complex_message.bin");
            ComplexMessage complexMessage = ComplexMessage.parseFrom(inputStream);
            System.out.println(complexMessage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static SampleMessage newSampleMessage(Integer id, String name) {
        SampleMessage.Builder builder = SampleMessage.newBuilder();
        builder.setId(id).setName(name).setIsComplex(true);

        System.out.println(builder.toString());
        SampleMessage message = builder.build();
        return message
    }
    
}