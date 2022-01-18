package edu.iu.uits.lms.iuonly.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.util.Scanner;

@Slf4j
public class IuOnlyErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        //conversion logic for decoding conversion
        Scanner scanner = new Scanner(response.getBody());
        scanner.useDelimiter("\\Z");
        String data = "";
        if (scanner.hasNext()) {
            data = scanner.next();
        }
        log.error(data);
    }

}
