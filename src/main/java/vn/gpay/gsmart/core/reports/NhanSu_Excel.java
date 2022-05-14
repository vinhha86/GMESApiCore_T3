package vn.gpay.gsmart.core.reports;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.gpay.gsmart.core.org.IOrgService;
import vn.gpay.gsmart.core.personel.Personel;
import vn.gpay.gsmart.core.personnel_position.IPersonnel_Position_Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

@Service
public final class NhanSu_Excel {
    @Autowired
    IOrgService orgService;

    @Autowired
    IPersonnel_Position_Service personnelPositionService;

    public NhanSu_Excel() {};
    public static File createBaoCaoNhanSu(String filePath, List<Personel> persons) throws IOException, IllegalAccessException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date current_time = new Date();
        String date = formatter.format(current_time);
        File fileExport = new File(filePath + "/Template_NhanSu.xlsx");
        File file = new File(filePath + "/BaoCaoNhanSu" + date + ".xlsx");
        FileInputStream is_fileExport = new FileInputStream(fileExport);

        //Setup
        Workbook workbook = new XSSFWorkbook(is_fileExport);
        Sheet sheet = workbook.getSheetAt(0);
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        int rowIndex = 1;

        //Style Title
        CellStyle titleCell = workbook.createCellStyle();
        XSSFFont titleFont = ((XSSFWorkbook) workbook).createFont();
        titleFont.setFontName("Times New Roman");
        titleFont.setFontHeightInPoints((short) 10);
        titleFont.setBold(true);
        titleCell.setFont(titleFont);
        titleCell.setVerticalAlignment(VerticalAlignment.CENTER);
        titleCell.setAlignment(HorizontalAlignment.CENTER);
        titleCell.setBorderLeft(BorderStyle.THIN);
        titleCell.setBorderRight(BorderStyle.THIN);
        titleCell.setBorderTop(BorderStyle.THIN);
        titleCell.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        titleCell.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        //Style Normal
        CellStyle normalCell = workbook.createCellStyle();
        XSSFFont normalFont = ((XSSFWorkbook) workbook).createFont();
        normalCell.setFont(normalFont);
        normalFont.setFontName("Times New Roman");
        normalFont.setFontHeightInPoints((short) 11);

//        Write data
        int TT = 1;
        for (Personel person : persons) {
            Row row = sheet.createRow(rowIndex++);

            Cell c1 = row.createCell(0);
            c1.setCellStyle(normalCell);
            c1.setCellValue(TT++);

            Cell c2 = row.createCell(1);
            c2.setCellStyle(normalCell);
            c2.setCellValue(person.getCode());

            Cell c3 = row.createCell(2);
            c3.setCellStyle(normalCell);
            if(person.getStatus() == 0) {
                c3.setCellValue("L");
            } else {
                c3.setCellValue("N");
            }

            String fullName = person.getFullname().trim();
            int lastIndex = fullName.lastIndexOf(" ");
            String ho = fullName.substring(0, lastIndex).trim();
            Cell c4 = row.createCell(3);
            c4.setCellStyle(normalCell);
            c4.setCellValue(ho);

            Cell c5 = row.createCell(4);
            c5.setCellStyle(normalCell);
            String ten = fullName.substring(lastIndex).trim();
            c5.setCellValue(ten);

            Cell c6 = row.createCell(5);
            c6.setCellStyle(normalCell);
            if(person.getGender() == 0) {
                c6.setCellValue("Nữ");
            } else {
                c6.setCellValue("Nam");
            }

            Cell c7 = row.createCell(6);
            String orgManageName = person.getOrgCode();
            c7.setCellStyle(normalCell);
            c7.setCellValue(orgManageName);

            Cell c8 = row.createCell(7);
            String positionCode = person.getPositionCode();
            c8.setCellStyle(normalCell);
            c8.setCellValue(positionCode);

            Cell c9 = row.createCell(8);
            c9.setCellStyle(normalCell);
            String positionName = person.getPositionName();
            c9.setCellValue(positionName);

            Cell c10 = row.createCell(9);
            c10.setCellStyle(normalCell);
            if(person.getSalaryLevel() == null) {
                c10.setCellValue("");
            } else {
                c10.setCellValue(person.getSalaryLevel());
            }

            Cell c11 = row.createCell(10);
            c11.setCellStyle(normalCell);
            if(person.getSalaryCode() == null) {
                c11.setCellValue("");
            } else {
                c11.setCellValue(person.getSalaryCode());
            }

            Cell c12 = row.createCell(11);
            c12.setCellStyle(normalCell);
            if(person.getFactorSalaryBH() == null) {
                c12.setCellValue("");
            } else {
                c12.setCellValue(String.format("%.4f", person.getFactorSalaryBH()));
            }

            Cell c13 = row.createCell(12);
            c13.setCellStyle(normalCell);
            if(person.getSalaryBH() == null) {
                c13.setCellValue("");
            } else {
                c13.setCellValue(String.format("%,d", person.getSalaryBH()));
            }

            Cell c14 = row.createCell(13);
            c14.setCellStyle(normalCell);
            if(person.getSalaryCurrent() == null) {
                c14.setCellValue("");
            } else {
                c14.setCellValue(String.format("%,d", person.getSalaryCurrent()));
            }

            Cell c15 = row.createCell(14);
            c15.setCellStyle(normalCell);
            if(person.getFactorCV() == null) {
                c15.setCellValue("");
            } else {
                c15.setCellValue(String.format("%.3f", person.getFactorCV()));
            }

            Cell c16 = row.createCell(15);
            c16.setCellStyle(normalCell);
            if(person.getDate_startworking() == null) {
                c16.setCellValue("");
            } else {
                String startWorking = formatter.format(person.getDate_startworking());
                c16.setCellValue(startWorking);
            }

            Cell c17 = row.createCell(16);
            c17.setCellStyle(normalCell);
            if(person.getDate_endworking() == null) {
                c17.setCellValue("");
            } else {
                String endWorking = formatter.format(person.getDate_startworking());
                c17.setCellValue(endWorking);
            }

            Cell c18 = row.createCell(17);
            c18.setCellStyle(normalCell);
            c18.setCellValue(person.getReason());

            Cell c19 = row.createCell(18);
            c19.setCellStyle(normalCell);
            c19.setCellValue(person.getBankname());

            Cell c20 = row.createCell(19);
            c20.setCellStyle(normalCell);
            c20.setCellValue(person.getAccount_number());
        }

        sheet.createFreezePane( 0, 1);

        //Return File Excel
        FileOutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

        return file;
    }
}
