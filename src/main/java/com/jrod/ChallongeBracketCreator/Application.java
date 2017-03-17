package com.jrod.ChallongeBracketCreator;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Jared Win Eaton on 3/6/2016.
 */
public class Application {
    private static final String API_KEY = "3FORtKTjULZcmaXGEdbCd2qDYRiBfK2PJoVx2MHn";
    private static final String USERNAME = "txtkd2015";
    private static final String BASE_URL = "https://api.challonge.com/v1/";

    public static void main(String[] args) throws Exception {
        System.out.println("Checking arguments.");
        if (args.length < 1) {
            throw new IOException("Not enough arguments!");
        }

        String excelPath = args[0];
        File excelFile = new File(excelPath);

        if (!excelFile.exists()) {
            throw new IOException("Given excel file does not exist!");
        }
        if (!excelFile.getName().contains(".xlsx")) {
            throw new IOException("File is not an excel spreadsheet!");
        }

        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(excelPath)));
        XSSFSheet sheet = workbook.getSheetAt(0);

        String tournamentName = sheet.getRow(1).getCell(0).toString();
        tournamentName = tournamentName.replaceAll("/", "_");
        tournamentName = tournamentName.replaceAll("\\\\", "_");
        System.out.println("Tournament Name: " + tournamentName);

        String tournamentID = UUID.randomUUID().toString().replaceAll("-", "_");
        createTournament(tournamentName, tournamentID);
        System.out.println("Tournament created!");

        System.out.println("Adding Participants:");
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = sheet.getRow(i);
            addParticipant(tournamentID, row.getCell(1) + " " + row.getCell(2));
            System.out.println(row.getCell(1) + " " + row.getCell(2));
        }
        System.out.println("All Participants have been added to the tournament.");
    }

    private static String createTournament(String name, String tournamentID) {
        System.out.println("got to create torunament");
        String url = BASE_URL + "tournaments.json";
        PostParameters pp = new PostParameters();
//        pp.add("api_key", API_KEY);
        if(name.length() > 60) {
            pp.add("tournament[name]", name.substring(0, 59));
        }
        else {
            pp.add("tournament[name]", name);
        }
        pp.add("tournament[url]", tournamentID);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;
        try {
            response = restTemplate.postForEntity(url, new HttpEntity<>(pp.getParameterString(), getHeader()), String.class);
        } catch (RestClientException e) {
            throw new RestClientException(((HttpStatusCodeException) e).getResponseBodyAsString());
        }
        return response.getBody();
    }

    private static String addParticipant(String tournamentID, String name) {
        String url = BASE_URL + "tournaments/" + tournamentID + "/participants.json";
        PostParameters pp = new PostParameters();
        pp.add("participant[name]", name);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;
        try {
            response = restTemplate.postForEntity(url, new HttpEntity<>(pp.getParameterString(), getHeader()), String.class);
        } catch (RestClientException e) {
            System.out.println(((HttpStatusCodeException) e).getResponseBodyAsString());
            return null;
        }
        return response.getBody();
    }

    private static HttpHeaders getHeader() {
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

    private static class PostParameters {
        private Map<String, Object> map;

        public PostParameters() {
            this.map = new LinkedHashMap<>();
        }

        public void add(String key, Object value) {
            this.map.put(key, value);
        }

        public void remove(String key) {
            this.map.remove(key);
        }

        public String getParameterString() {
            StringBuilder paramBuilder = new StringBuilder();
            try {
                for (Map.Entry<String, Object> param : map.entrySet()) {
                    if (paramBuilder.length() != 0) {
                        paramBuilder.append("&");
                    }
                    paramBuilder.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    paramBuilder.append('=');
                    paramBuilder.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return paramBuilder.toString();
        }
    }
}
