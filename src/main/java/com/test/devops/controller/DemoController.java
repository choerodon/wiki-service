package com.test.devops.controller;

import java.util.HashMap;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/v1")
public class DemoController {


    private static String PAGE_GET_URl = "http://xwiki.saas.hand-china.com/rest/wikis/xwiki/spaces/TestApp/spaces/yslPage/pages/WebHome?objects=true";

    private RestTemplate restTemplate = new RestTemplate();


    @GetMapping("/wiki")
    public ResponseEntity<String> hello() {

        HttpHeaders requestHeaders = new HttpHeaders();
        String key = "Authorization";
        String value = "Basic YWRtaW46aGFuZGhhbmQ=";
        requestHeaders.add(key, value);

        HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);

        ResponseEntity<String> responseEntity = restTemplate.exchange(PAGE_GET_URl, HttpMethod.GET, requestEntity, String.class, new HashMap());
        String body = responseEntity.getBody();
        HttpStatus statusCode = responseEntity.getStatusCode();
        if (statusCode.is2xxSuccessful()) {
            System.out.println(body);
        } else {
            System.out.println(statusCode);
        }
        return new ResponseEntity<String>(body, HttpStatus.OK);
    }
}
