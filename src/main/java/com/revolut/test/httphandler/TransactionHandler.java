package com.revolut.test.httphandler;

import com.revolut.test.constants.TransactionStatus;
import com.revolut.test.entity.Response;
import com.revolut.test.entity.Transaction;
import com.revolut.test.exception.InvalidAccountException;
import com.revolut.test.exception.InvalidRequestException;
import com.revolut.test.exception.InvalidRequestMethodException;
import com.revolut.test.service.TransactionService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static com.revolut.test.constants.WebConstants.*;

public class TransactionHandler extends GenericHandler<Transaction> implements HttpHandler {

    private final TransactionService transactionService = TransactionService.getInstance();

    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        Response<List<Transaction>> response = new Response<>();

        List<Transaction> transactionList = new ArrayList<>();

        Transaction transaction = null;

        StringBuffer uri = new StringBuffer(httpExchange.getRequestURI().getPath());

        try {
            switch (httpExchange.getRequestMethod()) {
                case GET:
                    transaction = doGet(httpExchange.getRequestURI().getQuery());
                    break;
                case POST:
                    transaction = doPost(httpExchange.getRequestBody());
                    break;
                default:
                    throw new InvalidRequestMethodException("Unknown Request Method:" + httpExchange.getRequestMethod());
            }
            uri.append(ID).append(transaction.getId());
            transactionList.add(transaction);
            response.setBody(transactionList);
            super.successFunction.accept(response);
        } catch (Exception exception) {
            exception.printStackTrace();
            response.setCode(RESPONSE_CODE_ERROR);
            response.setMessage(exception.getMessage());
        }

        response.setUri(uri.toString());
        httpExchange.getResponseHeaders().set(CONTENT_TYPE, APPLICATION_JSON);
        httpExchange.sendResponseHeaders(response.getCode(), 0);
        OutputStream os = httpExchange.getResponseBody();
        os.write(super.responseObjectToJson.apply(response));
        os.close();
        httpExchange.close();
    }

    private Transaction doPost(InputStream in) throws Exception {
        Transaction transactionCreate = (Transaction) super.jsonToObject.apply(in, new Transaction());
        transactionCreate.setStatus(TransactionStatus.PENDING);
        transactionCreate.setId(atomicInteger.incrementAndGet());

        Future<Boolean> future = pool.submit(() -> {
            Boolean status  = false;
            try {
                transactionService.create(transactionCreate);
                status = true;
                transactionCreate.setStatus(TransactionStatus.ACCEPTED);
            }catch (Exception exception){
            }
            return status;
        });

        GenericHandler.getQueueForInsertOperations().add(future);

        return transactionCreate;
    }

    private Transaction doGet(String query) throws InvalidAccountException, InvalidRequestException {
        if (query != null)
            return transactionService.findById(super.getId(query));
        else
            throw new InvalidRequestException("Request needs to be queried with: " + ID);
    }

}
