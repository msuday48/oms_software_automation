package com.oms.utilities;

import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.util.List;

public class DataProviders {

    @DataProvider(name = "LoginData")
    public String[][] getData() throws IOException {

        String path = "C:\\Users\\Hemanth\\IdeaProjects\\OMS_SOFTWARE_AUTOMATION\\src\\main\\resources\\OmsData\\OmsSoftwareData.xlsx";
        ExcelReaderUtility xlutil = new ExcelReaderUtility(path);
        String sheetName = "LoginData";

        int totalRows = xlutil.getRowCount(sheetName);
        int totalCols = xlutil.getCellCount(sheetName, 1);

        System.out.println("Sheet has total rows (raw): " + totalRows);
        System.out.println("Sheet has total columns: " + totalCols);

        // Count actual data rows (skip blanks)
        int validRowCount = 0;
        for (int i = 1; i <= totalRows; i++) {
            boolean rowEmpty = true;
            for (int j = 0; j < totalCols; j++) {
                String cellValue = xlutil.getCellData(sheetName, i, j);
                if (cellValue != null && !cellValue.trim().isEmpty()) {
                    rowEmpty = false;
                    break;
                }
            }
            if (!rowEmpty) validRowCount++;
        }

        System.out.println("✅ Actual non-empty rows found: " + validRowCount);

        // Create array based on actual data rows
        String[][] loginData = new String[validRowCount][totalCols];

        int dataRowIndex = 0;
        for (int i = 1; i <= totalRows; i++) {
            boolean rowEmpty = true;
            for (int j = 0; j < totalCols; j++) {
                String cellValue = xlutil.getCellData(sheetName, i, j);
                if (cellValue != null && !cellValue.trim().isEmpty()) {
                    rowEmpty = false;
                    break;
                }
            }
            if (!rowEmpty) {
                for (int j = 0; j < totalCols; j++) {
                    loginData[dataRowIndex][j] = xlutil.getCellData(sheetName, i, j);
                }
                dataRowIndex++;
            }
        }

        return loginData;
    }
/*
    @DataProvider(name = "LoginData")
    public String[][] getData() throws IOException {

        String path = "C:\\Users\\Hemanth\\IdeaProjects\\OMS_SOFTWARE_AUTOMATION\\src\\main\\resources\\OmsData\\OmsSoftwareData.xlsx";
        ExcelReaderUtility xlutil = new ExcelReaderUtility(path);
        String sheetName = "LoginData";

        int totalRows = xlutil.getRowCount(sheetName);
        int totalCols = xlutil.getCellCount(sheetName, 1);

        System.out.println("Sheet has total rows (raw): " + totalRows);
        System.out.println("Sheet has total columns: " + totalCols);

        // Collect non-empty rows directly
        java.util.List<String[]> rows = new java.util.ArrayList<>();

        for (int i = 1; i <= totalRows; i++) {
            String[] rowData = new String[totalCols];
            boolean rowEmpty = true;

            for (int j = 0; j < totalCols; j++) {
                String cellValue = xlutil.getCellData(sheetName, i, j);
                rowData[j] = cellValue;
                if (cellValue != null && !cellValue.trim().isEmpty()) {
                    rowEmpty = false;
                }
            }

            if (!rowEmpty) rows.add(rowData);
        }

        System.out.println("✅ Actual non-empty rows found: " + rows.size());

        return rows.toArray(new String[0][0]);
    }*/
}




