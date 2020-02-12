package com.revolut.test.init;

import com.revolut.test.httphandler.AccountHandler;
import com.revolut.test.httphandler.GenericHandler;
import com.revolut.test.httphandler.TransactionHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Init {


    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public static void main(String args[])  {

        InetSocketAddress socket = new InetSocketAddress("localhost", 9950);
        try {
            HttpServer server = HttpServer.create(socket,0);
            server.createContext("/accounts", new AccountHandler());
            server.createContext("/transactions", new TransactionHandler());
            server.start();

            /* The executor service is used to poll for the futures from the queue. The queue holds
             * the asynchronous db insert jobs. The queue is then read at an interval of every 1s and
             * pushes the data into in memory db (a Simple HashMap).
             */
            executorService.submit(() -> {
                while(true) {
                    Future<Boolean> future = GenericHandler.getQueueForInsertOperations().poll();
                    if(future != null)
                        Thread.sleep(1000);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
