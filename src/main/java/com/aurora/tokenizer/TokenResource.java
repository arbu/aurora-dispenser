package com.aurora.tokenizer;

import com.wizzardo.http.response.Response;

import java.io.IOException;

public class TokenResource extends TokenAc2dmResource {

    @Override
    protected Response getToken(String email, String password) throws IOException {
        String token = getApi().generateToken(email, password);
        response.setBody(token);
        return response;
    }
}
