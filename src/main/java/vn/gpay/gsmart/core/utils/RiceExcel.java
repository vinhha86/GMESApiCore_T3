package vn.gpay.gsmart.core.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.*;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.*;

public final class RiceExcel {

    public static File createGuestRice(String filePath, List<?> listOfData) throws IOException, IllegalAccessException {
        double unitPriceValue = 20000;

        File excelFile = new File(filePath);

        //// Setup
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Cơm Khách");
        XSSFFormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        IndexedColorMap colorMap = workbook.getStylesSource().getIndexedColors();

        // Setup margin for print
        XSSFPrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setTopMargin(0.43);
        printSetup.setBottomMargin(0.75);
        printSetup.setLeftMargin(0.59);
        printSetup.setRightMargin(0.2);
        printSetup.setHeaderMargin(0.3);
        printSetup.setFooterMargin(0.3);

        //// Setup width of column
        sheet.setColumnWidth(0, 1425);
        sheet.setColumnWidth(1, 4640);
        sheet.setColumnWidth(2, 3160);
        sheet.setColumnWidth(3, 2925);
        sheet.setColumnWidth(4, 4000);
        sheet.setColumnWidth(5, 8200);

        //// Title part
        int rowNum = 0;
        Row firstTitleRow = sheet.createRow(rowNum);
        CellStyle firstTitleStyle = workbook.createCellStyle();
        XSSFFont firstTitleFont = workbook.createFont();
        firstTitleFont.setFontName("Times New Roman");
        firstTitleFont.setFontHeightInPoints((short) 12);
        firstTitleFont.setItalic(true);
        firstTitleFont.setBold(true);
        firstTitleStyle.setFont(firstTitleFont);
        firstTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell firstTitleCell = firstTitleRow.createCell(0);
        firstTitleCell.setCellValue("CÔNG TY TNHH MTV DHA BẮC NINH");
        firstTitleCell.setCellStyle(firstTitleStyle);

        rowNum++;
        Row secTitleRow = sheet.createRow(rowNum);
        CellStyle secTitleStyle = workbook.createCellStyle();
        XSSFFont secTitleFont = workbook.createFont();
        secTitleFont.setFontName("Times New Roman");
        secTitleFont.setFontHeightInPoints((short) 16);
        secTitleFont.setBold(true);
        secTitleStyle.setFont(secTitleFont);
        secTitleStyle.setAlignment(HorizontalAlignment.CENTER);
        secTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell secTitleCell = secTitleRow.createCell(0);
        secTitleCell.setCellValue("BẢNG TỔNG HỢP CƠM KHÁCH");
        secTitleCell.setCellStyle(secTitleStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));

        rowNum++;
        Row thirdTitleRow = sheet.createRow(rowNum);
        CellStyle thirdTitleStyle = workbook.createCellStyle();
        DataFormat thirdTitleFormat = workbook.createDataFormat();
        XSSFFont thirdTitleFont = workbook.createFont();
        thirdTitleFont.setFontName("Times New Roman");
        thirdTitleFont.setFontHeightInPoints((short) 16);
        thirdTitleFont.setItalic(true);
        thirdTitleFont.setBold(true);
        thirdTitleStyle.setDataFormat(thirdTitleFormat.getFormat("\"Tháng\" mm \"năm\" yyyy"));
        thirdTitleStyle.setFont(thirdTitleFont);
        thirdTitleStyle.setAlignment(HorizontalAlignment.CENTER);
        thirdTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell thirdTitleCell = thirdTitleRow.createCell(0);
        thirdTitleCell.setCellValue("Tháng " + " năm ");
        thirdTitleCell.setCellStyle(thirdTitleStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 5));

        rowNum++;
        Row forthTitleRow = sheet.createRow(rowNum);
        CellStyle forthTitleStyle = workbook.createCellStyle();
        XSSFFont forthTitleFont = workbook.createFont();
        forthTitleFont.setFontName("Times New Roman");
        forthTitleFont.setFontHeightInPoints((short) 10);
        forthTitleFont.setItalic(true);
        forthTitleStyle.setFont(forthTitleFont);
        forthTitleStyle.setAlignment(HorizontalAlignment.CENTER);
        forthTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell forthTitleCell = forthTitleRow.createCell(4);
        forthTitleCell.setCellValue("ĐVT: Đồng");
        forthTitleCell.setCellStyle(forthTitleStyle);

        //// Header table
        rowNum++;
        Row headerTableRow = sheet.createRow(rowNum);

        CellStyle headerTableStyle = workbook.createCellStyle();
        XSSFFont headerTableFont = workbook.createFont();
        headerTableFont.setFontName("Times New Roman");
        headerTableFont.setFontHeightInPoints((short) 12);
        headerTableFont.setBold(true);
        headerTableStyle.setFont(headerTableFont);
        headerTableStyle.setAlignment(HorizontalAlignment.CENTER);
        headerTableStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerTableStyle.setWrapText(true);
        headerTableStyle.setBorderTop(BorderStyle.THIN);
        headerTableStyle.setBorderBottom(BorderStyle.THIN);
        headerTableStyle.setBorderLeft(BorderStyle.THIN);
        headerTableStyle.setBorderRight(BorderStyle.THIN);

        Cell cardinalNumber = headerTableRow.createCell(0);
        cardinalNumber.setCellValue("STT");
        cardinalNumber.setCellStyle(headerTableStyle);
        Cell date = headerTableRow.createCell(1);
        date.setCellValue("NGÀY TRONG THÁNG");
        date.setCellStyle(headerTableStyle);
        Cell numberOfMeals = headerTableRow.createCell(2);
        numberOfMeals.setCellValue("SỐ SUẤT ĂN");
        numberOfMeals.setCellStyle(headerTableStyle);
        Cell unitPrice = headerTableRow.createCell(3);
        unitPrice.setCellValue("ĐƠN GIÁ");
        unitPrice.setCellStyle(headerTableStyle);
        Cell bill = headerTableRow.createCell(4);
        bill.setCellValue("THÀNH TIỀN");
        bill.setCellStyle(headerTableStyle);
        Cell note = headerTableRow.createCell(5);
        note.setCellValue("GHI CHÚ");
        note.setCellStyle(headerTableStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, 3, 3));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, 4, 4));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, 5, 5));

        rowNum++;
        Row nullHeaderRow = sheet.createRow(rowNum);
        for (int i = 0; i < 6; i++) nullHeaderRow.createCell(i).setCellStyle(headerTableStyle);

        //// Table data
        CellStyle normalDayStyle = workbook.createCellStyle();
        DataFormat normalDayFormat = workbook.createDataFormat();
        XSSFFont normalDayFont = workbook.createFont();
        normalDayFont.setFontName("Times New Roman");
        normalDayFont.setFontHeightInPoints((short) 14);
        normalDayStyle.setDataFormat(normalDayFormat.getFormat("#,###"));
        normalDayStyle.setFont(normalDayFont);
        normalDayStyle.setAlignment(HorizontalAlignment.CENTER);
        normalDayStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        normalDayStyle.setBorderTop(BorderStyle.THIN);
        normalDayStyle.setBorderBottom(BorderStyle.THIN);
        normalDayStyle.setBorderLeft(BorderStyle.THIN);
        normalDayStyle.setBorderRight(BorderStyle.THIN);

        CellStyle sundayStyle = workbook.createCellStyle();
        DataFormat sundayFormat = workbook.createDataFormat();
        XSSFFont sundayFont = workbook.createFont();
        sundayFont.setColor(new XSSFColor(Color.BLUE, colorMap));
        sundayFont.setFontName("Times New Roman");
        sundayFont.setFontHeightInPoints((short) 14);
        sundayStyle.setDataFormat(sundayFormat.getFormat("#,###"));
        sundayStyle.setFont(sundayFont);
        sundayStyle.setAlignment(HorizontalAlignment.CENTER);
        sundayStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        sundayStyle.setFillBackgroundColor(IndexedColors.CORAL.index);
        sundayStyle.setFillForegroundColor(IndexedColors.CORAL.index);
        sundayStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        sundayStyle.setBorderTop(BorderStyle.THIN);
        sundayStyle.setBorderBottom(BorderStyle.THIN);
        sundayStyle.setBorderLeft(BorderStyle.THIN);
        sundayStyle.setBorderRight(BorderStyle.THIN);

        int firstRowDataTable = rowNum + 1;
        for (int i = 0; i < listOfData.size(); i++) {
            rowNum++;
            Row newRow = sheet.createRow(rowNum);

            List<String> data = getObjectData(listOfData.get(i));

            Cell cardinalNumberData = newRow.createCell(0);
            Cell dateData = newRow.createCell(1);
            Cell numberOfMealsData = newRow.createCell(2);
            Cell unitPriceData = newRow.createCell(3);
            Cell billData = newRow.createCell(4);
            Cell noteData = newRow.createCell(5);

            cardinalNumberData.setCellValue(i + 1);
            dateData.setCellValue(data.get(0));
            numberOfMealsData.setCellValue(Double.parseDouble(data.get(1)));
            unitPriceData.setCellValue(unitPriceValue);
            billData.setCellFormula(String.format("C%d*D%d", rowNum + 1, rowNum + 1));
            formulaEvaluator.evaluateFormulaCell(billData);

            if (isSunday(data.get(0))) {
                cardinalNumberData.setCellStyle(sundayStyle);
                dateData.setCellStyle(sundayStyle);
                numberOfMealsData.setCellStyle(sundayStyle);
                unitPriceData.setCellStyle(sundayStyle);
                billData.setCellStyle(sundayStyle);
                noteData.setCellStyle(sundayStyle);
            } else {
                cardinalNumberData.setCellStyle(normalDayStyle);
                dateData.setCellStyle(normalDayStyle);
                numberOfMealsData.setCellStyle(normalDayStyle);
                unitPriceData.setCellStyle(normalDayStyle);
                billData.setCellStyle(normalDayStyle);
                noteData.setCellStyle(normalDayStyle);
            }
        }
        int lastRowDataTable = rowNum;

        //// Footer table
        rowNum++;
        Row footerRow = sheet.createRow(rowNum);

        CellStyle footerStyle = workbook.createCellStyle();
        XSSFFont footerFont = workbook.createFont();
        footerFont.setFontName("Times New Roman");
        footerFont.setFontHeightInPoints((short) 14);
        footerFont.setBold(true);
        footerStyle.setFont(footerFont);
        footerStyle.setDataFormat(sundayFormat.getFormat("#,###"));
        footerStyle.setAlignment(HorizontalAlignment.CENTER);
        footerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        footerStyle.setBorderTop(BorderStyle.THIN);
        footerStyle.setBorderBottom(BorderStyle.THIN);
        footerStyle.setBorderLeft(BorderStyle.THIN);
        footerStyle.setBorderRight(BorderStyle.THIN);

        List<Cell> nullCell = new ArrayList<Cell>(){{
            add(footerRow.createCell(0));
            add(footerRow.createCell(5));
        }};

        nullCell.forEach(cell -> cell.setCellStyle(footerStyle));

        Cell sumText = footerRow.createCell(1);
        sumText.setCellValue("Tổng");
        sumText.setCellStyle(footerStyle);

        Cell sumNOM = footerRow.createCell(2);
        sumNOM.setCellFormula(String.format("SUM(C%d:C%d)", firstRowDataTable + 1, lastRowDataTable + 1));
        formulaEvaluator.evaluateFormulaCell(sumNOM);
        sumNOM.setCellStyle(footerStyle);

        Cell unitPriceFooter = footerRow.createCell(3);
        unitPriceFooter.setCellValue(unitPriceValue);
        unitPriceFooter.setCellStyle(footerStyle);

        Cell sumBill = footerRow.createCell(4);
        sumBill.setCellFormula(String.format("SUM(E%d:E%d)", firstRowDataTable + 1, lastRowDataTable + 1));
        formulaEvaluator.evaluateFormulaCell(sumBill);
        sumBill.setCellStyle(footerStyle);

        //// Signature
        rowNum++;
        Row billToTextRow = sheet.createRow(rowNum);

        CellStyle billToTextStyle = workbook.createCellStyle();
        XSSFFont billToTextFont = workbook.createFont();
        billToTextFont.setFontName("Times New Roman");
        billToTextFont.setFontHeightInPoints((short) 14);
        billToTextFont.setBold(true);
        billToTextStyle.setFont(billToTextFont);
        billToTextStyle.setAlignment(HorizontalAlignment.LEFT);
        billToTextStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell billToTextCell = billToTextRow.createCell(0);
        billToTextCell.setCellValue("Bằng chữ: ");
        billToTextCell.setCellStyle(billToTextStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 5));

        rowNum += 2;
        Row signatureDayRow = sheet.createRow(rowNum);

        CellStyle signatureDayStyle = workbook.createCellStyle();
        XSSFFont signatureDayFont = workbook.createFont();
        signatureDayFont.setFontName("Times New Roman");
        signatureDayFont.setFontHeightInPoints((short) 12);
        signatureDayFont.setItalic(true);
        signatureDayStyle.setFont(signatureDayFont);
        signatureDayStyle.setAlignment(HorizontalAlignment.CENTER);
        signatureDayStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell signatureDayCell = signatureDayRow.createCell(3);
        signatureDayCell.setCellValue("Bắc Ninh, ngày  tháng  năm");
        signatureDayCell.setCellStyle(signatureDayStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 3, 5));

        rowNum++;
        Row signatureRow = sheet.createRow(rowNum);

        CellStyle signatureStyle = workbook.createCellStyle();
        XSSFFont signatureFont = workbook.createFont();
        signatureFont.setFontName("Times New Roman");
        signatureFont.setFontHeightInPoints((short) 14);
        signatureFont.setBold(true);
        signatureStyle.setFont(signatureFont);
        signatureStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell signatureCell = signatureRow.createCell(0);
        signatureCell.setCellValue("   Giám đốc                      Kế toán trưởng                    Quản đốc                    Lập biểu            ");
        signatureCell.setCellStyle(signatureStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 5));

        //// Write listOfData to workbook
        FileOutputStream fileOutputStream = new FileOutputStream(excelFile);
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        workbook.close();
        return excelFile;
    }

    public static File createTotalRice(String filePath, List<?> listOfData) throws IllegalAccessException, IOException {
        double unitPriceValue = 20000;
        File excelFile = new File(filePath);

        //// Setup
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Tổng hợp cơm ca");
        XSSFFormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        IndexedColorMap colorMap = workbook.getStylesSource().getIndexedColors();

        XSSFPrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setTopMargin(0.43);
        printSetup.setBottomMargin(0.75);
        printSetup.setLeftMargin(0.59);
        printSetup.setRightMargin(0.2);
        printSetup.setHeaderMargin(0.3);
        printSetup.setFooterMargin(0.3);

        //// Chỉnh sửa độ rộng các cột
        sheet.setColumnWidth(0, 1425);
        sheet.setColumnWidth(1, 4640);
        sheet.setColumnWidth(2, 3160);
        sheet.setColumnWidth(3, 2925);
        sheet.setColumnWidth(4, 4000);

        //// Phần tiêu đề của phiếu
        int rowNum = 0;
        Row firstTitleRow = sheet.createRow(rowNum);
        CellStyle firstTitleStyle = workbook.createCellStyle();
        XSSFFont firstTitleFont = workbook.createFont();
        firstTitleFont.setFontName("Times New Roman");
        firstTitleFont.setFontHeightInPoints((short) 12);
        firstTitleFont.setItalic(true);
        firstTitleFont.setBold(true);
        firstTitleStyle.setFont(firstTitleFont);
        firstTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell firstTitleCell = firstTitleRow.createCell(0);
        firstTitleCell.setCellValue("CÔNG TY TNHH MTV DHA BẮC NINH");
        firstTitleCell.setCellStyle(firstTitleStyle);

        rowNum++;
        Row secTitleRow = sheet.createRow(rowNum);
        CellStyle secTitleStyle = workbook.createCellStyle();
        XSSFFont secTitleFont = workbook.createFont();
        secTitleFont.setFontName("Times New Roman");
        secTitleFont.setFontHeightInPoints((short) 16);
        secTitleFont.setBold(true);
        secTitleStyle.setFont(secTitleFont);
        secTitleStyle.setAlignment(HorizontalAlignment.CENTER);
        secTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell secTitleCell = secTitleRow.createCell(0);
        secTitleCell.setCellValue("BẢNG TỔNG HỢP CƠM CA");
        secTitleCell.setCellStyle(secTitleStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));

        rowNum++;
        Row thirdTitleRow = sheet.createRow(rowNum);
        CellStyle thirdTitleStyle = workbook.createCellStyle();
        DataFormat thirdTitleFormat = workbook.createDataFormat();
        XSSFFont thirdTitleFont = workbook.createFont();
        thirdTitleFont.setFontName("Times New Roman");
        thirdTitleFont.setFontHeightInPoints((short) 16);
        thirdTitleFont.setItalic(true);
        thirdTitleFont.setBold(true);
        thirdTitleStyle.setDataFormat(thirdTitleFormat.getFormat("\"Tháng\" mm \"năm\" yyyy"));
        thirdTitleStyle.setFont(thirdTitleFont);
        thirdTitleStyle.setAlignment(HorizontalAlignment.CENTER);
        thirdTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell thirdTitleCell = thirdTitleRow.createCell(0);
        thirdTitleCell.setCellValue("Tháng " + " năm ");
        thirdTitleCell.setCellStyle(thirdTitleStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 4));

        rowNum++;
        Row forthTitleRow = sheet.createRow(rowNum);
        CellStyle forthTitleStyle = workbook.createCellStyle();
        XSSFFont forthTitleFont = workbook.createFont();
        forthTitleFont.setFontName("Times New Roman");
        forthTitleFont.setFontHeightInPoints((short) 10);
        forthTitleFont.setItalic(true);
        forthTitleStyle.setFont(forthTitleFont);
        forthTitleStyle.setAlignment(HorizontalAlignment.CENTER);
        forthTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell forthTitleCell = forthTitleRow.createCell(4);
        forthTitleCell.setCellValue("ĐVT: Đồng");
        forthTitleCell.setCellStyle(forthTitleStyle);

        //// Tiêu đề cho bảng
        rowNum++;
        Row headerTableRow = sheet.createRow(rowNum);

        CellStyle headerTableStyle = workbook.createCellStyle();
        XSSFFont headerTableFont = workbook.createFont();
        headerTableFont.setFontName("Times New Roman");
        headerTableFont.setFontHeightInPoints((short) 12);
        headerTableFont.setBold(true);
        headerTableStyle.setFont(headerTableFont);
        headerTableStyle.setAlignment(HorizontalAlignment.CENTER);
        headerTableStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerTableStyle.setWrapText(true);
        headerTableStyle.setBorderTop(BorderStyle.THIN);
        headerTableStyle.setBorderBottom(BorderStyle.THIN);
        headerTableStyle.setBorderLeft(BorderStyle.THIN);
        headerTableStyle.setBorderRight(BorderStyle.THIN);

        Cell cardinalNumber = headerTableRow.createCell(0);
        cardinalNumber.setCellValue("STT");
        cardinalNumber.setCellStyle(headerTableStyle);
        Cell date = headerTableRow.createCell(1);
        date.setCellValue("NGÀY TRONG THÁNG");
        date.setCellStyle(headerTableStyle);
        Cell numberOfMeals = headerTableRow.createCell(2);
        numberOfMeals.setCellValue("SỐ SUẤT ĂN");
        numberOfMeals.setCellStyle(headerTableStyle);
        Cell unitPrice = headerTableRow.createCell(3);
        unitPrice.setCellValue("ĐƠN GIÁ");
        unitPrice.setCellStyle(headerTableStyle);
        Cell bill = headerTableRow.createCell(4);
        bill.setCellValue("THÀNH TIỀN");
        bill.setCellStyle(headerTableStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, 3, 3));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, 4, 4));

        rowNum++;
        Row nullHeaderRow = sheet.createRow(rowNum);
        for (int i = 0; i < 5; i++) nullHeaderRow.createCell(i).setCellStyle(headerTableStyle);

        //// Vẽ bảng
        CellStyle normalDayStyle = workbook.createCellStyle();
        DataFormat normalDayFormat = workbook.createDataFormat();
        XSSFFont normalDayFont = workbook.createFont();
        normalDayFont.setFontName("Times New Roman");
        normalDayFont.setFontHeightInPoints((short) 14);
        normalDayStyle.setDataFormat(normalDayFormat.getFormat("#,###"));
        normalDayStyle.setFont(normalDayFont);
        normalDayStyle.setAlignment(HorizontalAlignment.CENTER);
        normalDayStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        normalDayStyle.setBorderTop(BorderStyle.THIN);
        normalDayStyle.setBorderBottom(BorderStyle.THIN);
        normalDayStyle.setBorderLeft(BorderStyle.THIN);
        normalDayStyle.setBorderRight(BorderStyle.THIN);

        CellStyle sundayStyle = workbook.createCellStyle();
        DataFormat sundayFormat = workbook.createDataFormat();
        XSSFFont sundayFont = workbook.createFont();
        sundayFont.setColor(new XSSFColor(Color.BLUE, colorMap));
        sundayFont.setFontName("Times New Roman");
        sundayFont.setFontHeightInPoints((short) 14);
        sundayStyle.setDataFormat(sundayFormat.getFormat("#,###"));
        sundayStyle.setFont(sundayFont);
        sundayStyle.setAlignment(HorizontalAlignment.CENTER);
        sundayStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        sundayStyle.setFillBackgroundColor(IndexedColors.CORAL.index);
        sundayStyle.setFillForegroundColor(IndexedColors.CORAL.index);
        sundayStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        sundayStyle.setBorderTop(BorderStyle.THIN);
        sundayStyle.setBorderBottom(BorderStyle.THIN);
        sundayStyle.setBorderLeft(BorderStyle.THIN);
        sundayStyle.setBorderRight(BorderStyle.THIN);

        int firstRowDataTable = rowNum + 1;
        for (int i = 0; i < listOfData.size(); i++) {
            rowNum++;
            Row newRow = sheet.createRow(rowNum);

            List<String> data = getObjectData(listOfData.get(i));

            Cell cardinalNumberData = newRow.createCell(0);
            Cell dateData = newRow.createCell(1);
            Cell numberOfMealsData = newRow.createCell(2);
            Cell unitPriceData = newRow.createCell(3);
            Cell billData = newRow.createCell(4);

            cardinalNumberData.setCellValue(i + 1);
            dateData.setCellValue(data.get(0));
            numberOfMealsData.setCellValue(Double.parseDouble(data.get(1)));
            unitPriceData.setCellValue(unitPriceValue);
            billData.setCellFormula(String.format("C%d*D%d", rowNum + 1, rowNum + 1));
            formulaEvaluator.evaluateFormulaCell(billData);

            if (isSunday(data.get(0))) {
                cardinalNumberData.setCellStyle(sundayStyle);
                dateData.setCellStyle(sundayStyle);
                numberOfMealsData.setCellStyle(sundayStyle);
                unitPriceData.setCellStyle(sundayStyle);
                billData.setCellStyle(sundayStyle);
            } else {
                cardinalNumberData.setCellStyle(normalDayStyle);
                dateData.setCellStyle(normalDayStyle);
                numberOfMealsData.setCellStyle(normalDayStyle);
                unitPriceData.setCellStyle(normalDayStyle);
                billData.setCellStyle(normalDayStyle);
            }
        }
        int lastRowDataTable = rowNum;

        //// Đuôi bảng
        rowNum++;
        Row footerRow = sheet.createRow(rowNum);

        CellStyle footerStyle = workbook.createCellStyle();
        XSSFFont footerFont = workbook.createFont();
        footerFont.setFontName("Times New Roman");
        footerFont.setFontHeightInPoints((short) 14);
        footerFont.setBold(true);
        footerStyle.setFont(footerFont);
        footerStyle.setDataFormat(sundayFormat.getFormat("#,###"));
        footerStyle.setAlignment(HorizontalAlignment.CENTER);
        footerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        footerStyle.setBorderTop(BorderStyle.THIN);
        footerStyle.setBorderBottom(BorderStyle.THIN);
        footerStyle.setBorderLeft(BorderStyle.THIN);
        footerStyle.setBorderRight(BorderStyle.THIN);

        List<Cell> nullCell = new ArrayList<Cell>(){{
            add(footerRow.createCell(0));
        }};

        nullCell.forEach(cell -> cell.setCellStyle(footerStyle));

        Cell sumText = footerRow.createCell(1);
        sumText.setCellValue("Tổng");
        sumText.setCellStyle(footerStyle);

        Cell sumNOM = footerRow.createCell(2);
        sumNOM.setCellFormula(String.format("SUM(C%d:C%d)", firstRowDataTable + 1, lastRowDataTable + 1));
        formulaEvaluator.evaluateFormulaCell(sumNOM);
        sumNOM.setCellStyle(footerStyle);

        Cell unitPriceFooter = footerRow.createCell(3);
        unitPriceFooter.setCellValue(unitPriceValue);
        unitPriceFooter.setCellStyle(footerStyle);

        Cell sumBill = footerRow.createCell(4);
        sumBill.setCellFormula(String.format("SUM(E%d:E%d)", firstRowDataTable + 1, lastRowDataTable + 1));
        formulaEvaluator.evaluateFormulaCell(sumBill);
        sumBill.setCellStyle(footerStyle);

        //// Chữ ký
        rowNum++;
        Row billToTextRow = sheet.createRow(rowNum);

        CellStyle billToTextStyle = workbook.createCellStyle();
        XSSFFont billToTextFont = workbook.createFont();
        billToTextFont.setFontName("Times New Roman");
        billToTextFont.setFontHeightInPoints((short) 14);
        billToTextFont.setBold(true);
        billToTextStyle.setFont(billToTextFont);
        billToTextStyle.setAlignment(HorizontalAlignment.LEFT);
        billToTextStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell billToTextCell = billToTextRow.createCell(0);
        billToTextCell.setCellValue("Bằng chữ: ");
        billToTextCell.setCellStyle(billToTextStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 4));

        rowNum += 2;
        Row signatureDayRow = sheet.createRow(rowNum);

        CellStyle signatureDayStyle = workbook.createCellStyle();
        XSSFFont signatureDayFont = workbook.createFont();
        signatureDayFont.setFontName("Times New Roman");
        signatureDayFont.setFontHeightInPoints((short) 12);
        signatureDayFont.setItalic(true);
        signatureDayStyle.setFont(signatureDayFont);
        signatureDayStyle.setAlignment(HorizontalAlignment.CENTER);
        signatureDayStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell signatureDayCell = signatureDayRow.createCell(2);
        signatureDayCell.setCellValue("Bắc Ninh, ngày  tháng  năm");
        signatureDayCell.setCellStyle(signatureDayStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 2, 4));

        rowNum++;
        Row signatureRow = sheet.createRow(rowNum);

        CellStyle signatureStyle = workbook.createCellStyle();
        XSSFFont signatureFont = workbook.createFont();
        signatureFont.setFontName("Times New Roman");
        signatureFont.setFontHeightInPoints((short) 14);
        signatureFont.setBold(true);
        signatureStyle.setFont(signatureFont);
        signatureStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell signatureCell = signatureRow.createCell(0);
        signatureCell.setCellValue("   Giám đốc                             Kế toán trưởng                               Lập biểu            ");
        signatureCell.setCellStyle(signatureStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 4));

        //// Write listOfData to workbook
        FileOutputStream fileOutputStream = new FileOutputStream(excelFile);
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        workbook.close();
        return excelFile;
    }

    public static File createTotalExtraFile(String filePath, List<?> listOfData) throws IOException, IllegalAccessException {
        double unitPriceValue = 20000;
        File excelFile = new File(filePath);

        //// Setup
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Tổng hợp cơm tăng ca");
        XSSFFormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        IndexedColorMap colorMap = workbook.getStylesSource().getIndexedColors();

        XSSFPrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setTopMargin(0.43);
        printSetup.setBottomMargin(0.75);
        printSetup.setLeftMargin(0.59);
        printSetup.setRightMargin(0.2);
        printSetup.setHeaderMargin(0.3);
        printSetup.setFooterMargin(0.3);

        //// Setup width of column
        sheet.setColumnWidth(0, 1425);
        sheet.setColumnWidth(1, 4640);
        sheet.setColumnWidth(2, 3160);
        sheet.setColumnWidth(3, 2925);
        sheet.setColumnWidth(4, 4000);

        //// Title part
        int rowNum = 0;
        Row firstTitleRow = sheet.createRow(rowNum);
        CellStyle firstTitleStyle = workbook.createCellStyle();
        XSSFFont firstTitleFont = workbook.createFont();
        firstTitleFont.setFontName("Times New Roman");
        firstTitleFont.setFontHeightInPoints((short) 12);
        firstTitleFont.setItalic(true);
        firstTitleFont.setBold(true);
        firstTitleStyle.setFont(firstTitleFont);
        firstTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell firstTitleCell = firstTitleRow.createCell(0);
        firstTitleCell.setCellValue("CÔNG TY TNHH MTV DHA BẮC NINH");
        firstTitleCell.setCellStyle(firstTitleStyle);

        rowNum++;
        Row secTitleRow = sheet.createRow(rowNum);
        CellStyle secTitleStyle = workbook.createCellStyle();
        XSSFFont secTitleFont = workbook.createFont();
        secTitleFont.setFontName("Times New Roman");
        secTitleFont.setFontHeightInPoints((short) 16);
        secTitleFont.setBold(true);
        secTitleStyle.setFont(secTitleFont);
        secTitleStyle.setAlignment(HorizontalAlignment.CENTER);
        secTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell secTitleCell = secTitleRow.createCell(0);
        secTitleCell.setCellValue("BẢNG TỔNG HỢP CƠM TĂNG CA");
        secTitleCell.setCellStyle(secTitleStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));

        rowNum++;
        Row thirdTitleRow = sheet.createRow(rowNum);
        CellStyle thirdTitleStyle = workbook.createCellStyle();
        DataFormat thirdTitleFormat = workbook.createDataFormat();
        XSSFFont thirdTitleFont = workbook.createFont();
        thirdTitleFont.setFontName("Times New Roman");
        thirdTitleFont.setFontHeightInPoints((short) 16);
        thirdTitleFont.setItalic(true);
        thirdTitleFont.setBold(true);
        thirdTitleStyle.setDataFormat(thirdTitleFormat.getFormat("\"Tháng\" mm \"năm\" yyyy"));
        thirdTitleStyle.setFont(thirdTitleFont);
        thirdTitleStyle.setAlignment(HorizontalAlignment.CENTER);
        thirdTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell thirdTitleCell = thirdTitleRow.createCell(0);
        thirdTitleCell.setCellValue("Tháng " + " năm ");
        thirdTitleCell.setCellStyle(thirdTitleStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 4));

        rowNum++;
        Row forthTitleRow = sheet.createRow(rowNum);
        CellStyle forthTitleStyle = workbook.createCellStyle();
        XSSFFont forthTitleFont = workbook.createFont();
        forthTitleFont.setFontName("Times New Roman");
        forthTitleFont.setFontHeightInPoints((short) 10);
        forthTitleFont.setItalic(true);
        forthTitleStyle.setFont(forthTitleFont);
        forthTitleStyle.setAlignment(HorizontalAlignment.CENTER);
        forthTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell forthTitleCell = forthTitleRow.createCell(4);
        forthTitleCell.setCellValue("ĐVT: Đồng");
        forthTitleCell.setCellStyle(forthTitleStyle);

        //// Header table
        rowNum++;
        Row headerTableRow = sheet.createRow(rowNum);

        CellStyle headerTableStyle = workbook.createCellStyle();
        XSSFFont headerTableFont = workbook.createFont();
        headerTableFont.setFontName("Times New Roman");
        headerTableFont.setFontHeightInPoints((short) 12);
        headerTableFont.setBold(true);
        headerTableStyle.setFont(headerTableFont);
        headerTableStyle.setAlignment(HorizontalAlignment.CENTER);
        headerTableStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerTableStyle.setWrapText(true);
        headerTableStyle.setBorderTop(BorderStyle.THIN);
        headerTableStyle.setBorderBottom(BorderStyle.THIN);
        headerTableStyle.setBorderLeft(BorderStyle.THIN);
        headerTableStyle.setBorderRight(BorderStyle.THIN);

        Cell cardinalNumber = headerTableRow.createCell(0);
        cardinalNumber.setCellValue("STT");
        cardinalNumber.setCellStyle(headerTableStyle);
        Cell date = headerTableRow.createCell(1);
        date.setCellValue("NGÀY TRONG THÁNG");
        date.setCellStyle(headerTableStyle);
        Cell numberOfMeals = headerTableRow.createCell(2);
        numberOfMeals.setCellValue("SỐ SUẤT ĂN");
        numberOfMeals.setCellStyle(headerTableStyle);
        Cell unitPrice = headerTableRow.createCell(3);
        unitPrice.setCellValue("ĐƠN GIÁ");
        unitPrice.setCellStyle(headerTableStyle);
        Cell bill = headerTableRow.createCell(4);
        bill.setCellValue("THÀNH TIỀN");
        bill.setCellStyle(headerTableStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, 3, 3));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, 4, 4));

        rowNum++;
        Row nullHeaderRow = sheet.createRow(rowNum);
        for (int i = 0; i < 5; i++) nullHeaderRow.createCell(i).setCellStyle(headerTableStyle);

        //// Table data
        CellStyle normalDayStyle = workbook.createCellStyle();
        DataFormat normalDayFormat = workbook.createDataFormat();
        XSSFFont normalDayFont = workbook.createFont();
        normalDayFont.setFontName("Times New Roman");
        normalDayFont.setFontHeightInPoints((short) 14);
        normalDayStyle.setDataFormat(normalDayFormat.getFormat("#,###"));
        normalDayStyle.setFont(normalDayFont);
        normalDayStyle.setAlignment(HorizontalAlignment.CENTER);
        normalDayStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        normalDayStyle.setBorderTop(BorderStyle.THIN);
        normalDayStyle.setBorderBottom(BorderStyle.THIN);
        normalDayStyle.setBorderLeft(BorderStyle.THIN);
        normalDayStyle.setBorderRight(BorderStyle.THIN);

        CellStyle sundayStyle = workbook.createCellStyle();
        DataFormat sundayFormat = workbook.createDataFormat();
        XSSFFont sundayFont = workbook.createFont();
        sundayFont.setColor(new XSSFColor(Color.BLUE, colorMap));
        sundayFont.setFontName("Times New Roman");
        sundayFont.setFontHeightInPoints((short) 14);
        sundayStyle.setDataFormat(sundayFormat.getFormat("#,###"));
        sundayStyle.setFont(sundayFont);
        sundayStyle.setAlignment(HorizontalAlignment.CENTER);
        sundayStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        sundayStyle.setFillBackgroundColor(IndexedColors.CORAL.index);
        sundayStyle.setFillForegroundColor(IndexedColors.CORAL.index);
        sundayStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        sundayStyle.setBorderTop(BorderStyle.THIN);
        sundayStyle.setBorderBottom(BorderStyle.THIN);
        sundayStyle.setBorderLeft(BorderStyle.THIN);
        sundayStyle.setBorderRight(BorderStyle.THIN);

        int firstRowDataTable = rowNum + 1;
        for (int i = 0; i < listOfData.size(); i++) {
            rowNum++;
            Row newRow = sheet.createRow(rowNum);

            List<String> data = getObjectData(listOfData.get(i));

            Cell cardinalNumberData = newRow.createCell(0);
            Cell dateData = newRow.createCell(1);
            Cell numberOfMealsData = newRow.createCell(2);
            Cell unitPriceData = newRow.createCell(3);
            Cell billData = newRow.createCell(4);

            cardinalNumberData.setCellValue(i + 1);
            dateData.setCellValue(data.get(0));
            numberOfMealsData.setCellValue(Double.parseDouble(data.get(1)));
            unitPriceData.setCellValue(unitPriceValue);
            billData.setCellFormula(String.format("C%d*D%d", rowNum + 1, rowNum + 1));
            formulaEvaluator.evaluateFormulaCell(billData);

            if (isSunday(data.get(0))) {
                cardinalNumberData.setCellStyle(sundayStyle);
                dateData.setCellStyle(sundayStyle);
                numberOfMealsData.setCellStyle(sundayStyle);
                unitPriceData.setCellStyle(sundayStyle);
                billData.setCellStyle(sundayStyle);
            } else {
                cardinalNumberData.setCellStyle(normalDayStyle);
                dateData.setCellStyle(normalDayStyle);
                numberOfMealsData.setCellStyle(normalDayStyle);
                unitPriceData.setCellStyle(normalDayStyle);
                billData.setCellStyle(normalDayStyle);
            }
        }
        int lastRowDataTable = rowNum;

        //// Footer table
        rowNum++;
        Row footerRow = sheet.createRow(rowNum);

        CellStyle footerStyle = workbook.createCellStyle();
        XSSFFont footerFont = workbook.createFont();
        footerFont.setFontName("Times New Roman");
        footerFont.setFontHeightInPoints((short) 14);
        footerFont.setBold(true);
        footerStyle.setFont(footerFont);
        footerStyle.setDataFormat(sundayFormat.getFormat("#,###"));
        footerStyle.setAlignment(HorizontalAlignment.CENTER);
        footerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        footerStyle.setBorderTop(BorderStyle.THIN);
        footerStyle.setBorderBottom(BorderStyle.THIN);
        footerStyle.setBorderLeft(BorderStyle.THIN);
        footerStyle.setBorderRight(BorderStyle.THIN);

        List<Cell> nullCell = new ArrayList<Cell>(){{
            add(footerRow.createCell(0));
        }};

        nullCell.forEach(cell -> cell.setCellStyle(footerStyle));

        Cell sumText = footerRow.createCell(1);
        sumText.setCellValue("Tổng");
        sumText.setCellStyle(footerStyle);

        Cell sumNOM = footerRow.createCell(2);
        sumNOM.setCellFormula(String.format("SUM(C%d:C%d)", firstRowDataTable + 1, lastRowDataTable + 1));
        formulaEvaluator.evaluateFormulaCell(sumNOM);
        sumNOM.setCellStyle(footerStyle);

        Cell unitPriceFooter = footerRow.createCell(3);
        unitPriceFooter.setCellValue(unitPriceValue);
        unitPriceFooter.setCellStyle(footerStyle);

        Cell sumBill = footerRow.createCell(4);
        sumBill.setCellFormula(String.format("SUM(E%d:E%d)", firstRowDataTable + 1, lastRowDataTable + 1));
        formulaEvaluator.evaluateFormulaCell(sumBill);
        sumBill.setCellStyle(footerStyle);

        //// Chữ ký
        rowNum++;
        Row billToTextRow = sheet.createRow(rowNum);

        CellStyle billToTextStyle = workbook.createCellStyle();
        XSSFFont billToTextFont = workbook.createFont();
        billToTextFont.setFontName("Times New Roman");
        billToTextFont.setFontHeightInPoints((short) 14);
        billToTextFont.setBold(true);
        billToTextStyle.setFont(billToTextFont);
        billToTextStyle.setAlignment(HorizontalAlignment.LEFT);
        billToTextStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell billToTextCell = billToTextRow.createCell(0);
        billToTextCell.setCellValue("Bằng chữ: ");
        billToTextCell.setCellStyle(billToTextStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 4));

        rowNum += 2;
        Row signatureDayRow = sheet.createRow(rowNum);

        CellStyle signatureDayStyle = workbook.createCellStyle();
        XSSFFont signatureDayFont = workbook.createFont();
        signatureDayFont.setFontName("Times New Roman");
        signatureDayFont.setFontHeightInPoints((short) 12);
        signatureDayFont.setItalic(true);
        signatureDayStyle.setFont(signatureDayFont);
        signatureDayStyle.setAlignment(HorizontalAlignment.CENTER);
        signatureDayStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell signatureDayCell = signatureDayRow.createCell(2);
        signatureDayCell.setCellValue("Bắc Ninh, ngày  tháng  năm");
        signatureDayCell.setCellStyle(signatureDayStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 2, 4));

        rowNum++;
        Row signatureRow = sheet.createRow(rowNum);

        CellStyle signatureStyle = workbook.createCellStyle();
        XSSFFont signatureFont = workbook.createFont();
        signatureFont.setFontName("Times New Roman");
        signatureFont.setFontHeightInPoints((short) 14);
        signatureFont.setBold(true);
        signatureStyle.setFont(signatureFont);
        signatureStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell signatureCell = signatureRow.createCell(0);
        signatureCell.setCellValue("   Giám đốc                             Kế toán trưởng                               Lập biểu            ");
        signatureCell.setCellStyle(signatureStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 4));

        //// Write listOfData to workbook
        FileOutputStream fileOutputStream = new FileOutputStream(excelFile);
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        workbook.close();
        return excelFile;
    }

    public static File exportComCa(String filePath, String factoryName, String time, HashMap<Date, HashMap<String, Integer>> allData) throws IOException {
        File excelFile = new File(filePath);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Cơm ca");

        // Lấy dữ liệu mẫu và tính dòng cho bảng
        Map.Entry<Date, HashMap<String, Integer>> tempMap = allData.entrySet().iterator().next();
        HashMap<String, Integer> tempData = tempMap.getValue();
        int tableRow = (int) Math.ceil(tempData.size() * 1.0 / 2);
        int allFieldRow = tableRow + 11;

        // Tạo sẵn các dòng cho file
        for (int i = 0; i < allFieldRow * 2; i++) sheet.createRow(i);

        // Tạo các bảng trong file
        int counter = 0;
        for (Date date : allData.keySet()) {
            int startRow = (counter % 2 == 0) ? 0 : allFieldRow;

            createTable(workbook, sheet, startRow, 6 * (counter / 2),
                    factoryName, time,
                    date, allData.get(date));

            counter++;
        }

        // Đưa dữ liệu vào trong file excel
        FileOutputStream fos = new FileOutputStream(excelFile);
        workbook.write(fos);

        // Đóng các dữ liệu đã tạo
        workbook.close();
        fos.close();

        return excelFile;
    }

    public static File exportComTangCa(String filePath, String factoryName, HashMap<Date, HashMap<String, Integer>> allData) throws IOException {
        File excelFile = new File(filePath);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Cơm tăng ca");

        // Lấy dữ liệu mẫu và tính dòng cho bảng
        Map.Entry<Date, HashMap<String, Integer>> tempMap = allData.entrySet().iterator().next();
        HashMap<String, Integer> tempData = tempMap.getValue();
        int tableRow = (int) Math.ceil(tempData.size() * 1.0 / 2);
        int allFieldRow = tableRow + 11;

        // Tạo sẵn các dòng cho file
        for (int i = 0; i < allFieldRow * 2; i++) sheet.createRow(i);

        // Tạo các bảng trong file
        int counter = 0;
        for (Date date : allData.keySet()) {
            int startRow = (counter % 2 == 0) ? 0 : allFieldRow;

            createTable(workbook, sheet, startRow, 6 * (counter / 2),
                    factoryName, "",
                    date, allData.get(date));

            counter++;
        }

        // Đưa dữ liệu vào trong file excel
        FileOutputStream fos = new FileOutputStream(excelFile);
        workbook.write(fos);

        // Đóng các dữ liệu đã tạo
        workbook.close();
        fos.close();

        return excelFile;
    }

    private static void createTable(XSSFWorkbook workbook, XSSFSheet sheet, int startRow, int startCol,
                                    String factoryName, String time,
                                    Date date, HashMap<String, Integer> listOfData) {
        int row = startRow;

        //// Thiết lập một số phần cần thiết cho bảng
        XSSFFormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

        //// Chỉnh sửa căn lề
        XSSFPrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setTopMargin(0.25);
        printSetup.setRightMargin(0.17);
        printSetup.setBottomMargin(0.25);
        printSetup.setLeftMargin(0.55);

        //// Tạo bảng
        // Tạo tiêu đề
        XSSFRow firstTitleRow = sheet.getRow(row);

        CellStyle firstTitleStyle = workbook.createCellStyle();
        XSSFFont firstTitleFont = workbook.createFont();

        firstTitleFont.setFontName("Times New Roman");
        firstTitleFont.setFontHeightInPoints((short) 12);
        firstTitleFont.setBold(true);

        firstTitleStyle.setFont(firstTitleFont);
        firstTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        firstTitleStyle.setAlignment(HorizontalAlignment.CENTER);

        sheet.addMergedRegion(new CellRangeAddress(row, row, startCol, startCol + 4));

        Cell firstTitleCell = firstTitleRow.createCell(startCol);
        firstTitleCell.setCellValue("CÔNG TY TNHH MTV DHA - BẮC NINH");
        firstTitleCell.setCellStyle(firstTitleStyle);

        row++;
        XSSFRow secondTitleRow = sheet.getRow(row);

        CellStyle secondTitleStyle = workbook.createCellStyle();
        XSSFFont secondTitleFont = workbook.createFont();

        secondTitleFont.setFontName("Times New Roman");
        secondTitleFont.setFontHeightInPoints((short) 16);
        secondTitleFont.setBold(true);

        secondTitleStyle.setFont(secondTitleFont);
        secondTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        secondTitleStyle.setAlignment(HorizontalAlignment.CENTER);

        sheet.addMergedRegion(new CellRangeAddress(row, row, startCol, startCol + 4));

        Cell secondTitleCell = secondTitleRow.createCell(startCol);
        secondTitleCell.setCellValue("ĂN " + time.toUpperCase());
        secondTitleCell.setCellStyle(secondTitleStyle);

        row++;
        XSSFRow thirdTitleRow = sheet.getRow(row);

        CellStyle thirdTitleStyle = workbook.createCellStyle();
        XSSFFont thirdTitleFont = workbook.createFont();

        thirdTitleFont.setFontName("Times New Roman");
        thirdTitleFont.setFontHeightInPoints((short) 12);
        thirdTitleFont.setBold(true);

        thirdTitleStyle.setFont(thirdTitleFont);
        thirdTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        thirdTitleStyle.setAlignment(HorizontalAlignment.CENTER);

        sheet.addMergedRegion(new CellRangeAddress(row, row, startCol, startCol + 4));

        Cell thirdTitleCell = thirdTitleRow.createCell(startCol);
        thirdTitleCell.setCellValue("PHIẾU BÁO CƠM " + factoryName.toUpperCase());
        thirdTitleCell.setCellStyle(thirdTitleStyle);

        row++;
        XSSFRow dateRow = sheet.getRow(row);

        CellStyle dateStyle = workbook.createCellStyle();
        XSSFFont dateFont = workbook.createFont();
        DataFormat dateFormat = workbook.createDataFormat();

        dateFont.setFontName("Times New Roman");
        dateFont.setFontHeightInPoints((short) 12);

        dateStyle.setFont(dateFont);
        dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dateStyle.setAlignment(HorizontalAlignment.CENTER);
        dateStyle.setDataFormat(dateFormat.getFormat("\"Ngày\" dd \"tháng\" mm \"năm\" yyyy"));

        sheet.addMergedRegion(new CellRangeAddress(row, row, startCol, startCol + 4));

        Cell dateCell = dateRow.createCell(startCol);
        dateCell.setCellValue(date);
        dateCell.setCellStyle(dateStyle);

        // Vẽ bảng
        int startTableRow = ++row;
        int endTableRow = 0;

        CellStyle tableGroupStyle = workbook.createCellStyle();
        XSSFFont tableGroupFont = workbook.createFont();

        tableGroupFont.setFontName("Times New Roman");
        tableGroupFont.setFontHeightInPoints((short) 12);

        tableGroupStyle.setFont(tableGroupFont);
        tableGroupStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        tableGroupStyle.setBorderTop(BorderStyle.THIN);
        tableGroupStyle.setBorderRight(BorderStyle.THIN);
        tableGroupStyle.setBorderBottom(BorderStyle.THIN);
        tableGroupStyle.setBorderLeft(BorderStyle.THIN);

        CellStyle tableValStyle = workbook.createCellStyle();
        XSSFFont tableValFont = workbook.createFont();

        tableValFont.setFontName("Times New Roman");
        tableValFont.setFontHeightInPoints((short) 12);

        tableValStyle.setFont(tableValFont);
        tableValStyle.setAlignment(HorizontalAlignment.CENTER);
        tableValStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        tableValStyle.setBorderTop(BorderStyle.THIN);
        tableValStyle.setBorderRight(BorderStyle.THIN);
        tableValStyle.setBorderBottom(BorderStyle.THIN);
        tableValStyle.setBorderLeft(BorderStyle.THIN);

        // Thêm 1 dữ liệu trống để đều bảng nếu số lượng nhóm lẻ
        if (listOfData.size() % 2 != 0)
            listOfData.put("", 0);

        int numberOfTableRow = listOfData.size() / 2;
        int startCell = startCol;
        int counter = 0;

        for (String group : listOfData.keySet()) {
            XSSFRow tableRow = sheet.getRow(row + counter);
            Cell keyCell = tableRow.createCell(startCell);
            Cell valCell = tableRow.createCell(startCell + 1);

            keyCell.setCellValue(group);
            if (listOfData.get(group) == 0)
                valCell.setCellValue("");
            else
                valCell.setCellValue(listOfData.get(group));

            keyCell.setCellStyle(tableGroupStyle);
            valCell.setCellStyle(tableValStyle);

            counter++;
            if (counter == numberOfTableRow) {
                endTableRow = startTableRow + counter - 1;
                counter = 0;
                startCell = startCol + 2;
            }
        }
        row = endTableRow + 1;

        // Dòng tổng hợp
        XSSFRow totalRow = sheet.getRow(row);

        CellStyle totalStyle = workbook.createCellStyle();
        XSSFFont totalFont = workbook.createFont();

        totalFont.setFontName("Times New Roman");
        totalFont.setFontHeightInPoints((short) 12);
        totalFont.setBold(true);

        totalStyle.setFont(totalFont);
        totalStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        totalStyle.setAlignment(HorizontalAlignment.LEFT);

        String firstValCol = indexToColumnLetter(startCol + 1);
        String priceValCol = indexToColumnLetter(startCol + 2);
        String secValCol = indexToColumnLetter(startCol + 3);

        String numberFormula = String.format("SUM(%s%d:%s%d, %s%d:%s%d)",
                firstValCol, startTableRow + 1,
                firstValCol, endTableRow + 1,
                secValCol, startTableRow + 1,
                secValCol, endTableRow + 1);

        String cellPriceAt = String.format("%s%d", priceValCol, row + 1);
        String totalFormula = String.format("%s%d*" + getNumberFromTextExcel(cellPriceAt), firstValCol, row + 1);

        Cell sumCell = totalRow.createCell(startCol);
        Cell numberCell = totalRow.createCell(startCol + 1);
        Cell priceCell = totalRow.createCell(startCol + 2);
        Cell totalCell = totalRow.createCell(startCol + 3);
        Cell unitCell = totalRow.createCell(startCol + 4);

        sumCell.setCellValue("Tổng:");

        numberCell.setCellFormula(numberFormula);
        formulaEvaluator.evaluateFormulaCell(numberCell);

        priceCell.setCellValue("x15000=");

        totalCell.setCellFormula(totalFormula);
        formulaEvaluator.evaluateFormulaCell(totalCell);

        unitCell.setCellValue("đồng");

        sumCell.setCellStyle(totalStyle);
        numberCell.setCellStyle(totalStyle);
        priceCell.setCellStyle(totalStyle);
        totalCell.setCellStyle(totalStyle);
        unitCell.setCellStyle(totalStyle);

        row++;
        XSSFRow textRow = sheet.getRow(row);

        CellStyle textStyle = workbook.createCellStyle();
        XSSFFont textFont = workbook.createFont();

        textFont.setFontName("Times New Roman");
        textFont.setFontHeightInPoints((short) 12);
        textFont.setBold(true);

        textStyle.setFont(textFont);
        textStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        textStyle.setAlignment(HorizontalAlignment.LEFT);

        Cell textCell = textRow.createCell(startCol);
        textCell.setCellValue("Bằng chữ:");
        textCell.setCellStyle(textStyle);

        // Thiết lập phần ký tên
        row++;
        XSSFRow firstNullRow = sheet.getRow(row);
        firstNullRow.setHeightInPoints(3.8f);

        row++;
        XSSFRow signatureRow = sheet.getRow(row);

        CellStyle signatureStyle = workbook.createCellStyle();
        XSSFFont signatureFont = workbook.createFont();

        signatureFont.setFontName("Times New Roman");
        signatureFont.setFontHeightInPoints((short) 12);

        signatureStyle.setFont(signatureFont);
        signatureStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        signatureStyle.setAlignment(HorizontalAlignment.CENTER);

        Cell reportRicerCell = signatureRow.createCell(startCol);
        reportRicerCell.setCellValue("Người báo cơm");
        reportRicerCell.setCellStyle(signatureStyle);

        Cell signatureCell = signatureRow.createCell(startCol + 3);
        signatureCell.setCellValue("Ký nhận");
        signatureCell.setCellStyle(signatureStyle);

        sheet.addMergedRegion(new CellRangeAddress(row, row, startCol, startCol + 1));
        sheet.addMergedRegion(new CellRangeAddress(row, row, startCol + 3, startCol + 4));

        row++;
        XSSFRow secondNullRow = sheet.getRow(row);
        secondNullRow.setHeightInPoints(6f);

        // Kẻ đường bao quanh phiếu
        row++;
        CellRangeAddress reportArea = new CellRangeAddress(startRow, row, startCol, startCol + 4);
        RegionUtil.setBorderTop(BorderStyle.THIN, reportArea, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, reportArea, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, reportArea, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, reportArea, sheet);

        // Chỉnh sửa độ rộng của các hàng (cuối)
        sheet.setColumnWidth(startCol, 2650);
        sheet.setColumnWidth(startCol + 1, 3072);
        sheet.setColumnWidth(startCol + 2, 2816);
        sheet.setColumnWidth(startCol + 3, 3290); // 12.11
        sheet.setColumnWidth(startCol + 4, 1520);
        sheet.setColumnWidth(startCol + 5, 400);
    }

    private static String getNumberFromTextExcel(String cellAt) {
        return "IF(SUM(LEN(" + cellAt + ")-LEN(SUBSTITUTE(" + cellAt + ", {\"0\",\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\"}, \"\")))>0, SUMPRODUCT(MID(0&" + cellAt + ", LARGE(INDEX(ISNUMBER(--MID(" + cellAt + ",ROW(INDIRECT(\"$1:$\"&LEN(" + cellAt + "))),1))* ROW(INDIRECT(\"$1:$\"&LEN(" + cellAt + "))),0), ROW(INDIRECT(\"$1:$\"&LEN(" + cellAt + "))))+1,1) * 10^ROW(INDIRECT(\"$1:$\"&LEN(" + cellAt + ")))/10),\"\")";
    }

    private static String indexToColumnLetter(int columnIndex) {
        StringBuilder columnName = new StringBuilder();
        int columnNumber = columnIndex + 1;

        while (columnNumber > 0) {
            int rem = columnNumber % 26;

            if (rem == 0) {
                columnName.append("Z");
                columnNumber = (columnNumber / 26) - 1;
            } else {
                columnName.append((char) ((rem - 1) + 'A'));
                columnNumber = columnNumber / 26;
            }
        }

        return columnName.reverse().toString();
    }

    private static List<String> getObjectData(Object obj) throws IllegalAccessException {
        Field[] allFields = obj.getClass().getDeclaredFields();
        List<String> data = new ArrayList<>();
        for (Field field : allFields) {
            field.setAccessible(true);
            data.add(field.get(obj).toString());
        }
        return data;
    }

    private static boolean isSunday(String date) {
        String[] dateArray = date.split("/");
        LocalDate localDate = LocalDate.of(Integer.parseInt(dateArray[2]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[0]));
        DayOfWeek day = DayOfWeek.of(localDate.get(ChronoField.DAY_OF_WEEK));

        return day == DayOfWeek.SUNDAY;
    }

}
