package com.revolut.test.httphandler;

import com.revolut.test.constants.TransactionStatus;
import com.revolut.test.entity.Account;
import com.revolut.test.entity.Response;
import com.revolut.test.exception.InvalidAccountException;
import com.revolut.test.exception.InvalidRequestException;
import com.revolut.test.exception.InvalidRequestMethodException;
import com.revolut.test.service.AccountService;
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


public class AccountHandler extends GenericHandler<Account> implements HttpHandler {


    private final AccountService accountService = AccountService.getInstance();

    private static final AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        Response<List<Account>> response = new Response<>();

        List<Account> accountList = new ArrayList<>();

        Account account = null;

        StringBuffer uri = new StringBuffer(httpExchange.getRequestURI().getPath());

        try {
            switch (httpExchange.getRequestMethod()) {
                case GET:
                    account = doGet(httpExchange.getRequestURI().getQuery());
                    break;
                case POST:
                    account = doPost(httpExchange.getRequestBody());
                    break;
                case PUT:
                    account = doPut(httpExchange.getRequestBody());
                    break;
                default:
                    throw new InvalidRequestMethodException("Unknown Request Method:" + httpExchange.getRequestMethod() );
            }
            accountList.add(account);
            uri.append(ID).append(account.getId());
            response.setBody(accountList);
            successFunction.accept(response);
        } catch(Exception exception) {
            response.setCode(RESPONSE_CODE_ERROR);
            response.setMessage(exception.getMessage());
        }


        //we are adding the uri to response entity. All the insert transactions are executed asyncronously
        //In case of any failures this uri can be used for retry operation.
        response.setUri(uri.toString());


        httpExchange.getResponseHeaders().set(CONTENT_TYPE, APPLICATION_JSON);
        httpExchange.sendResponseHeaders(response.getCode(),0);
        OutputStream os = httpExchange.getResponseBody();
        os.write(super.responseObjectToJson.apply(response));
        os.close();
        httpExchange.close();
    }

    private Account doPut(InputStream in) throws InvalidAccountException {
        Account accountUpdate =(Account) super.jsonToObject.apply(in,new Account());
        accountService.update(accountUpdate);
        return accountUpdate;
    }

    private Account doPost(InputStream in) throws  Exception{
        Account accountCreate =(Account) super.jsonToObject.apply(in,new Account());
        accountCreate.setStatus(TransactionStatus.PENDING);
        accountCreate.setId(atomicInteger.incrementAndGet());

        //Inserting into the database is an expensive operation. Hence, dealing it asynchronously.
        //Though the return status is not explicitly used here. This can be used in the future to resubmit the failed jobs.

        Future<Boolean> future = pool.submit(() -> {
            Boolean status  = false;
            try {
                accountService.create(accountCreate);
                status = true;
                accountCreate.setStatus(TransactionStatus.ACCEPTED);
            }catch (Exception exception){
                accountCreate.setStatus(TransactionStatus.FAILED);
            }
            return status;
        });

        GenericHandler.getQueueForInsertOperations().add(future);

        return accountCreate;
    }

    private Account doGet(String query) throws InvalidAccountException, InvalidRequestException {
        if(query != null)
            return accountService.findById(super.getId(query));
        else
            throw new InvalidRequestException("Request needs to be queried with: " + ID);
    }


}
