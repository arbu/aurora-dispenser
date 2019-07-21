package com.aurora.tokenizer;


import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;
import com.dragons.aurora.playstoreapiv2.GooglePlayException;
import com.dragons.aurora.playstoreapiv2.PropertiesDeviceInfoProvider;
import com.wizzardo.http.request.Request;
import com.wizzardo.http.response.Response;
import com.wizzardo.http.response.Status;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public class TokenAc2dmResource {

    protected Response response;
    protected Request request;

    public Response handle(Request request, Response response) {
        this.response = response;
        this.request = request;

        String email = request.param("email");
        String password = Tokenizer.authMap.get(email);

        try {
            response = getToken(email, password);
            response.setStatus(Status._200);
        } catch (GooglePlayException e) {
            response.setStatus(Status._404);
            response.setBody(e.getMessage());
        } catch (IOException e) {
            response.setBody(e.getMessage());
        }
        return response;
    }

    protected GooglePlayAPI getApi() {
        final Properties properties = new Properties();

        try {
            final ClassLoader classLoader = getClass().getClassLoader();
            final InputStream inputStream = classLoader.getResourceAsStream("device-oneplus3.properties");
            if (inputStream != null)
                properties.load(inputStream);
        } catch (IOException e) {
            Tokenizer.logger.severe(e.getMessage());
        }

        final PropertiesDeviceInfoProvider deviceInfoProvider = new PropertiesDeviceInfoProvider();
        deviceInfoProvider.setProperties(properties);
        deviceInfoProvider.setLocaleString(Locale.ENGLISH.toString());

        final GooglePlayAPI api = new GooglePlayAPI();
        api.setClient(new OkHttpClientAdapter());
        api.setDeviceInfoProvider(deviceInfoProvider);
        api.setLocale(Locale.US);
        return api;
    }

    protected Response getToken(String email, String password) throws IOException {
        String token = getApi().generateAC2DMToken(email, password);
        response.setBody(token);
        return response;
    }
}