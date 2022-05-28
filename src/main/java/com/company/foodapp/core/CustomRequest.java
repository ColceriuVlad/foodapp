package com.company.foodapp.core;

import com.company.foodapp.mappers.MethodMapper;
import com.company.foodapp.models.Header;
import org.springframework.http.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class CustomRequest {
    private HttpURLConnection urlConnection;

    public CustomRequest(String urlString) {
        try {
            var url = new URL(urlString);
            this.urlConnection = (HttpURLConnection) url.openConnection();
            setMethod(HttpMethod.GET);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMethod(HttpMethod method) {
        try {
            urlConnection.setRequestMethod(MethodMapper.map(method));
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    public void setHeader(Header header) {
        urlConnection.setRequestProperty(header.headerName, header.headerValue);
    }

    public String getResponse() {
        try {
            var bodyInputStream = urlConnection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(bodyInputStream));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void closeConnection() {
        urlConnection.disconnect();
    }
}
