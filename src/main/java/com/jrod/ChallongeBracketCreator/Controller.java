package com.jrod.ChallongeBracketCreator;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Jared Win Eaton on 3/10/2016.
 */
@Component("challongeController")
public class Controller
{
    @Autowired
    private Connection connection;

    public void createBracketSilent(String excelPath) throws IOException
    {
        if(excelPath.isEmpty())
        {
            throw new IOException("Excel path is empty!");
        }
        if(!new File(excelPath).exists())
        {
            throw new IOException("Given excel file does not exist!");
        }

        File excelFile = new File(excelPath);
        String excelFileName = excelFile.getName().split("\\.xlsx")[0];
        System.out.println(excelFileName);
        System.out.println("got to silent bracket");
        String tournamentID = UUID.randomUUID().toString().replaceAll("-", "_");
        connection.createTournament(excelFileName, tournamentID);
        System.out.println("Tournament created!");
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(excelPath)));
        XSSFSheet sheet = workbook.getSheetAt(0);

        System.out.println("Adding Participants:");
        for(int i=1;i<sheet.getPhysicalNumberOfRows();i++)
        {
            XSSFRow row = sheet.getRow(i);
            connection.addParticipant(tournamentID, row.getCell(5) + " " + row.getCell(6));
            System.out.println(row.getCell(5) + " " + row.getCell(6));
        }
        System.out.println("All Participants have been added to the tournament.");
    }


}
