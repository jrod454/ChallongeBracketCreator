package com.jrod.ChallongeBracketCreator;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by Jared Win Eaton on 3/6/2016.
 */
@Service("challongeConnection")
public class Connection
{

    private static final String API_KEY = "3FORtKTjULZcmaXGEdbCd2qDYRiBfK2PJoVx2MHn";
    private static final String USERNAME = "txtkd2015";
    private static final String BASE_URL = "https://api.challonge.com/v1/";

    public String createTournament(String name, String tournamentID)
    {
        System.out.println("got to create torunament");
        String url = BASE_URL + "tournaments.json";
        PostParameters pp = new PostParameters();
//        pp.add("api_key", API_KEY);
        pp.add("tournament[name]", name);
        pp.add("tournament[url]", tournamentID);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;
        try
        {
            response = restTemplate.postForEntity(url, new HttpEntity<>(pp.getParameterString(), getHeader()), String.class);
        } catch (RestClientException e)
        {
            throw new RestClientException(((HttpStatusCodeException)e).getResponseBodyAsString());
        }
        return response.getBody();
    }

    public String addParticipant(String tournamentID, String name)
    {
        String url = BASE_URL + "tournaments/" + tournamentID + "/participants.json";
        PostParameters pp = new PostParameters();
        pp.add("participant[name]", name);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;
        try
        {
            response = restTemplate.postForEntity(url, new HttpEntity<>(pp.getParameterString(), getHeader()), String.class);
        } catch (RestClientException e)
        {
            System.out.println(((HttpStatusCodeException)e).getResponseBodyAsString());
            return null;
        }
        return response.getBody();
    }

    private HttpHeaders getHeader()
    {
        String plainCreds = USERNAME + ":" + API_KEY;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        //The value for user agent is just a dummy value. It has no real meaning.  Its simply to get past challonge security.
        headers.add("User-Agent", "my-integration/1.2.3");

        return headers;
    }

    private class PostParameters
    {
        private Map<String, Object> map;

        public PostParameters()
        {
            this.map = new LinkedHashMap<>();
        }

        public void add(String key, Object value)
        {
            this.map.put(key, value);
        }

        public void remove(String key)
        {
            this.map.remove(key);
        }

        public String getParameterString()
        {
            StringBuilder paramBuilder = new StringBuilder();
            try
            {
                for(Map.Entry<String, Object> param : map.entrySet())
                {
                    if(paramBuilder.length() != 0)
                    {
                        paramBuilder.append("&");
                    }
                    paramBuilder.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    paramBuilder.append('=');
                    paramBuilder.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
            } catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            return paramBuilder.toString();
        }
    }
}
