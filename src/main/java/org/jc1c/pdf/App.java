package org.jc1c.pdf;

import org.jc1c.JServer;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {

        JServer.builder()
                .withArgs(args)
                .withBacklog(3)
                .withThreadPool(3)
                .withHandlersController(ConverterHandlers.class)
                .build()
                .start();

    }
}
