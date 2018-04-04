package test.ex1;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws IOException {
        URL site = new URL("https://yandex.ru");

        HttpURLConnection httpURLConnection = (HttpURLConnection) site.openConnection();
        int response = httpURLConnection.getResponseCode();
        System.out.println(response);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

        StringBuilder result = new StringBuilder();
        String input;
        while ((input = bufferedReader.readLine()) != null) {
            result.append(input);
        }

        System.out.println(result);
    }
}
