package com.revolut.test.httphandler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.test.entity.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.revolut.test.constants.WebConstants.RESPONSE_CODE_OK;
import static com.revolut.test.constants.WebConstants.SUCCESS;

public abstract class GenericHandler<T> {

    private static final Queue<Future<Boolean>> queueForInsertOperations = new ConcurrentLinkedQueue<>();

    protected static final ExecutorService pool = Executors.newFixedThreadPool(10);

    public static Queue<Future<Boolean>> getQueueForInsertOperations() {
        return queueForInsertOperations;
    }

    public static final String ID = "?id=";
    protected BiFunction<InputStream, T, Object> jsonToObject = (InputStream in, T t) -> {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Object obj = null;
        try {
            obj = objectMapper.readValue(in, t.getClass());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    };

    protected Function<T, byte[]> objectToJson = (T t) -> {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        byte[] obj = null;
        try {
            obj = objectMapper.writeValueAsBytes(t);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    };

    protected Function<Response<List<T>>, byte[]> responseObjectToJson = (Response<List<T>> response) -> {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        byte[] obj = null;
        try {
            obj = objectMapper.writeValueAsBytes(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    };

    protected Consumer<Response<List<T>>> successFunction = (Response<List<T>> response) -> {
        response.setCode(RESPONSE_CODE_OK);
        response.setMessage(SUCCESS);
    };

    protected Integer getId(String query){
        return Integer.parseInt(query.substring(query.indexOf("=")+1));
    }

}
