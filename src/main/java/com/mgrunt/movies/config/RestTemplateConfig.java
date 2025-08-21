package com.mgrunt.movies.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

//        restTemplate.setErrorHandler(new ResponseErrorHandler() {
//            @Override
//            public boolean hasError(ClientHttpResponse response) throws IOException {
//                return response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR ||
//                        response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR;
//            }
//
//            @Override
//            public void handleError(ClientHttpResponse response) throws IOException {
//                if (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
//                    throw new ExternalApiException("TMDB API server error: " + response.getStatusCode());
//                } else if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
//                    throw new ExternalApiException("TMDB API client error: " + response.getStatusCode());
//                }
//            }
//        });

        // Ustaw timeout
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 sekund
        factory.setReadTimeout(10000);   // 10 sekund
        restTemplate.setRequestFactory(factory);

        return restTemplate;
    }
}
