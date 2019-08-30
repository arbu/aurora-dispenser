package com.aurora.tokenizer;

import com.wizzardo.http.HttpConnection;
import com.wizzardo.http.HttpServer;
import com.wizzardo.http.request.Request;
import com.wizzardo.http.response.Response;

import java.util.LinkedHashMap;
import java.util.Random;
import java.util.logging.Logger;

public class Tokenizer {

    static LinkedHashMap<String, String> authMap = new LinkedHashMap<>();
    static Logger logger = Logger.getGlobal();

    //Put your username:password pair here..
    static {
        authMap.put("username@gmail.com", "password");
    }

    public static void main(String[] args) {
        HttpServer<HttpConnection> server = new HttpServer<>(8080);
        server.setWorkersCount(32);
        server.setIoThreadsCount(8);
        server.setTTL(5 * 60 * 1000);
        server.notFoundHandler((request, response) -> {
            response.setBody("You are lost !");
            return response;
        });
        server.getUrlMapping()
                .append("/", (request, response) -> {
                    response.setBody("Aurora Token Dispenser");
                    return response;
                })
                .append("/status", (request, response) -> {
                    response.setBody("I'm alive !");
                    return response;
                })
                .append("/email", (request, response) -> getRandomEmail(request, response))
                .append("/token/email/$email/", (request, response) -> new TokenResource().handle(request, response))
                .append("/token-ac2dm/email/$email/", (request, response) -> new TokenAc2dmResource().handle(request, response));
        server.start();
    }

    private static Response getRandomEmail(Request request, Response response) {
        Object[] keyArray = authMap.keySet().toArray();
        Object key = keyArray[new Random().nextInt(keyArray.length)];
        response.setBody(key.toString());
        return response;
    }
}
