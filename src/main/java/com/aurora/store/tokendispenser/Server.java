package com.aurora.store.tokendispenser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class Server {

    public static final Logger LOG = LoggerFactory.getLogger(Server.class.getName());

    static Map<String, List<String>> authMap;

    public static void main(String[] args) {
        try {
            authMap = new HashMap<String, List<String>>();
            List<String> lines = Files.readAllLines(Paths.get(System.getProperty("td.tokens", "tokens.txt")));
            for (String line: lines) {
                int split = line.indexOf(" ");
                String email = line.substring(0, split),
                       token = line.substring(split + 1);
                if (!authMap.containsKey(email)) {
                    authMap.put(email, new ArrayList<String>());
                }
                authMap.get(email).add(token);
            }
        } catch(IOException e) {
            e.printStackTrace();
            return;
        }
        String host = System.getProperty("td.host", "0.0.0.0");
        // Google auth requests are not fast, so lets limit max simultaneous threads
        Spark.threadPool(32, 2, 5000);
        Spark.ipAddress(host);
        Spark.port(Integer.parseInt(System.getProperty("td.port", "8443")));
        if (System.getProperty("td.keystore") != null) {
            Spark.secure(System.getProperty("td.keystore"), System.getProperty("td.keystore_password", "changeit"), null, null);
        }

        Spark.before((req, res) -> {
            LOG.info(req.requestMethod() + " " + req.url());
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Request-Method", "GET");
        });

        Spark.after((req, res) -> res.type("text/plain"));
        Spark.get("/", (req, res) -> "Aurora Token Dispenser");
        Spark.get("/status", (req, res) -> "Token dispenser is alive !");
        Spark.get("/token/email/:email", (req, res) -> new TokenResource().handle(req, res));
        Spark.get("/email", (req, res) -> getRandomEmail(req, res));
        Spark.notFound((req, res) -> "You are lost !");
    }

    private static Response getRandomEmail(Request request, Response response) {
        Object[] keyArray = authMap.keySet().toArray();
        Object key = keyArray[new Random().nextInt(keyArray.length)];
        response.body(key.toString());
        return response;
    }

    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567;
    }
}
