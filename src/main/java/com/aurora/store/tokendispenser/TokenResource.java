package com.aurora.store.tokendispenser;

import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;
import com.dragons.aurora.playstoreapiv2.GooglePlayException;
import com.dragons.aurora.playstoreapiv2.PropertiesDeviceInfoProvider;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;

public class TokenResource {

    public String handle(Request request, Response response) {
        String email = request.params("email");
        List<String> tokens = Server.authMap.get(email);
        String aasToken = tokens.get(new Random().nextInt(tokens.size()));
        int code = 500;
        String message;
        try {
            String token = getToken(email, aasToken);
            return token;
        } catch (GooglePlayException e) {
            if (e.getCode() >= 400) {
                code = e.getCode();
            }
            message = e.getMessage();
            Spark.halt(code, "Google responded with: " + message + e.getCode());
        } catch (IOException e) {
            message = e.getMessage();
            Spark.halt(code, message);
        }
        return "";
    }

    GooglePlayAPI getApi() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getSystemResourceAsStream("device-gemini.properties"));
        } catch (IOException e) {
            Spark.halt(500, "device-gemini.properties not found");
        }

        PropertiesDeviceInfoProvider deviceInfoProvider = new PropertiesDeviceInfoProvider();
        deviceInfoProvider.setProperties(properties);
        deviceInfoProvider.setLocaleString(Locale.ENGLISH.toString());

        GooglePlayAPI api = new GooglePlayAPI();
        api.setClient(new OkHttpClientAdapter());
        api.setDeviceInfoProvider(deviceInfoProvider);
        api.setLocale(Locale.US);
        return api;
    }

    protected String getToken(String email, String aasToken) throws IOException {
        return getApi().generateToken(email, aasToken);
    }
}
