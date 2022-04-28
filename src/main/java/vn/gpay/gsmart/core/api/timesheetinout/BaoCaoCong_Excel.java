package vn.gpay.gsmart.core.api.timesheetinout;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.gpay.gsmart.core.timesheet_absence.TimesheetAbsence_Binding;
import vn.gpay.gsmart.core.timesheetinout.TimeSheetMonth;
import vn.gpay.gsmart.core.utils.GPAYDateFormat;

import javax.swing.border.Border;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaoCaoCong_Excel {
    public static File createBaoCaoCong(String filePath, List<?> listOfData, int month, int year, String orgName, FileInputStream file) throws IOException, IllegalAccessException, ParseException {
        double unitPriceValue = 20000;
        File excelFile = new File(filePath);

//        System.out.println("oke 1");
        //// Setup
        XSSFWorkbook workbook = new XSSFWorkbook(file);
//        var sheet = workbook.createSheet("Báo Cáo Công");
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFFormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        IndexedColorMap colorMap = workbook.getStylesSource().getIndexedColors();

        // Setup margin for print
//        var printSetup = sheet.getPrintSetup();
//        printSetup.setTopMargin(0.43);
//        printSetup.setBottomMargin(0.75);
//        printSetup.setLeftMargin(0.59);
//        printSetup.setRightMargin(0.2);
//        printSetup.setHeaderMargin(0.3);
//        printSetup.setFooterMargin(0.3);

        //// Setup width of column
//        sheet.setColumnWidth(0, 1425);
//        sheet.setColumnWidth(1, 6000);
//        sheet.setColumnWidth(2, 1699);
//        sheet.setColumnWidth(3, 3000);
//        sheet.setColumnWidth(4, 4000);
//        sheet.setColumnWidth(5, 2500);
//        sheet.setColumnWidth(6, 2500);
//        sheet.setColumnWidth(7, 2500);
//        sheet.setColumnWidth(8, 2500);
//        sheet.setColumnWidth(9, 4000);
//        sheet.setColumnWidth(10, 2500);
//        sheet.setColumnWidth(11, 2500);
//        sheet.setColumnWidth(12, 2500);
//        sheet.setColumnWidth(13, 2500);
//        sheet.setColumnWidth(14, 2500);

        //// Title part
        int rowNum = 3;
//        Row firstTitleRow = sheet.createRow(rowNum);
//        CellStyle firstTitleStyle = workbook.createCellStyle();
//        XSSFFont firstTitleFont = workbook.createFont();
//        firstTitleFont.setFontName("Times New Roman");
//        firstTitleFont.setFontHeightInPoints((short) 9);
//        firstTitleFont.setItalic(true);
//        firstTitleFont.setBold(true);
//        firstTitleStyle.setFont(firstTitleFont);
//        firstTitleStyle.setAlignment(HorizontalAlignment.CENTER);
//        firstTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//
//        Cell firstTitleCell = firstTitleRow.createCell(0);
//        firstTitleCell.setCellValue("BÁO CÁO CÔNG");
//        firstTitleCell.setCellStyle(firstTitleStyle);
//        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 14));
//
//
//        rowNum++;
//        Row thirdTitleRow = sheet.createRow(rowNum);
//        CellStyle thirdTitleStyle = workbook.createCellStyle();
//        DataFormat thirdTitleFormat = workbook.createDataFormat();
//        XSSFFont thirdTitleFont = workbook.createFont();
//        thirdTitleFont.setFontName("Times New Roman");
//        thirdTitleFont.setFontHeightInPoints((short) 9);
//        thirdTitleFont.setItalic(true);
//        thirdTitleFont.setBold(true);
////        thirdTitleStyle.setBorderTop(BorderStyle.THIN);
////        thirdTitleStyle.setBorderBottom(BorderStyle.THIN);
////        thirdTitleStyle.setBorderLeft(BorderStyle.THIN);
////        thirdTitleStyle.setBorderRight(BorderStyle.THIN);
////        thirdTitleStyle.setDataFormat(thirdTitleFormat.getFormat("tháng\" mm \"năm\" yyyy"));
//        thirdTitleStyle.setFont(thirdTitleFont);
//        thirdTitleStyle.setAlignment(HorizontalAlignment.CENTER);
//        thirdTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//
//        Cell thirdTitleCell = thirdTitleRow.createCell(0);
//        thirdTitleCell.setCellValue(orgName+"-"+"Tháng "+month+" Năm "+year);
//        thirdTitleCell.setCellStyle(thirdTitleStyle);
//        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 14));



        //// Header table
//        rowNum++;
//        System.out.println(rowNum);
//        Row headerTableRow = sheet.createRow(rowNum);
//
//        CellStyle headerTableStyle = workbook.createCellStyle();
//        XSSFFont headerTableFont = workbook.createFont();
//        headerTableFont.setFontName("Times New Roman");
//        headerTableFont.setFontHeightInPoints((short) 9);
//        headerTableFont.setBold(true);
//        headerTableStyle.setFont(headerTableFont);
//        headerTableStyle.setAlignment(HorizontalAlignment.CENTER);
//        headerTableStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        headerTableStyle.setWrapText(true);
//        headerTableStyle.setBorderTop(BorderStyle.THIN);
//        headerTableStyle.setBorderBottom(BorderStyle.THIN);
//        headerTableStyle.setBorderLeft(BorderStyle.THIN);
//        headerTableStyle.setBorderRight(BorderStyle.THIN);
//
//        Cell idNV = headerTableRow.createCell(0);
//        idNV.setCellValue("Mã NV");
//        idNV.setCellStyle(headerTableStyle);
//        sheet.addMergedRegion(new CellRangeAddress(2, 3, 0, 0));
//        Cell name = headerTableRow.createCell(1);
//        name.setCellValue("Tên Nhân Viên");
//        name.setCellStyle(headerTableStyle);
//        sheet.addMergedRegion(new CellRangeAddress(2, 3, 1, 1));
//        Cell chucVu = headerTableRow.createCell(2);
//        chucVu.setCellValue("Chức Vụ");
//        chucVu.setCellStyle(headerTableStyle);
//        sheet.addMergedRegion(new CellRangeAddress(2, 3, 2, 2));
//        Cell ngayVaoCT = headerTableRow.createCell(3);
//        ngayVaoCT.setCellValue("Ngày vào CT");
//        ngayVaoCT.setCellStyle(headerTableStyle);
//        sheet.addMergedRegion(new CellRangeAddress(2, 3, 3, 3));
//        Cell gioCong = headerTableRow.createCell(4);
//        gioCong.setCellValue("Giờ công (h)");
//        gioCong.setCellStyle(headerTableStyle);
//        sheet.addMergedRegion(new CellRangeAddress(2, 3, 4, 4));
//        Cell lamThem = headerTableRow.createCell(5);
//        lamThem.setCellValue("Làm thêm (h)");
//        lamThem.setCellStyle(headerTableStyle);
//        sheet.addMergedRegion(new CellRangeAddress(2, 3, 5, 5));
//        Cell boSung = headerTableRow.createCell(6);
//        boSung.setCellValue("Bổ sung (h)");
//        boSung.setCellStyle(headerTableStyle);
//        sheet.addMergedRegion(new CellRangeAddress(2, 3, 6, 6));
//        Cell chuNhat = headerTableRow.createCell(7);
//        chuNhat.setCellValue("Chủ nhật (h)");
//        chuNhat.setCellStyle(headerTableStyle);
//        sheet.addMergedRegion(new CellRangeAddress(2, 3, 7, 7));
//        Cell buaAn = headerTableRow.createCell(8);
//        buaAn.setCellValue("Bữa ăn");
//        buaAn.setCellStyle(headerTableStyle);
//        sheet.addMergedRegion(new CellRangeAddress(2, 3, 8, 8));
//        Cell tong = headerTableRow.createCell(9);
//        tong.setCellValue("Tổng");
//        tong.setCellStyle(headerTableStyle);
//        sheet.addMergedRegion(new CellRangeAddress(2, 3, 9, 9));
//        Cell tongCong = headerTableRow.createCell(10);
//        tongCong.setCellValue("Tổng công");
//        tongCong.setCellStyle(headerTableStyle);
//        sheet.addMergedRegion(new CellRangeAddress(2, 3, 10, 10));
//        Cell nghi = headerTableRow.createCell(11);
////        Row nullHeaderRow = sheet.createRow(rowNum);
////        for (int i = 12; i < 15; i++) nullHeaderRow.createCell(i).setCellStyle(headerTableStyle);
//        CellStyle nullStyle1 = workbook.createCellStyle();
//        nullStyle1.setBorderTop(BorderStyle.THIN);
//
//        CellStyle nullStyle2 = workbook.createCellStyle();
//        nullStyle2.setBorderTop(BorderStyle.THIN);
//        nullStyle2.setBorderRight(BorderStyle.THIN);
//
//        Cell null12 = headerTableRow.createCell(12);
//        null12.setCellStyle(nullStyle1);
//        Cell null13 = headerTableRow.createCell(13);
//        null13.setCellStyle(nullStyle1);
//        Cell null14 = headerTableRow.createCell(14);
//        null14.setCellStyle(nullStyle2);
//        nghi.setCellValue("Nghỉ");
//        nghi.setCellStyle(headerTableStyle);
//        sheet.addMergedRegion(new CellRangeAddress(2, 2, 11, 14));
//
//        rowNum++;
//        Row header2TableRow =sheet.createRow(rowNum);
//        CellStyle nullStyle = workbook.createCellStyle();
//        nullStyle.setBorderLeft(BorderStyle.THIN);
//        nullStyle.setBorderRight(BorderStyle.THIN);
//
//        Cell null0 = header2TableRow.createCell(0);
//        null0.setCellStyle(nullStyle);
//        Cell null1 = header2TableRow.createCell(1);
//        null1.setCellStyle(nullStyle);
//        Cell null2 = header2TableRow.createCell(2);
//        null2.setCellStyle(nullStyle);
//        Cell null3 = header2TableRow.createCell(3);
//        null3.setCellStyle(nullStyle);
//        Cell null4 = header2TableRow.createCell(4);
//        null4.setCellStyle(nullStyle);
//        Cell null5 = header2TableRow.createCell(5);
//        null5.setCellStyle(nullStyle);
//        Cell null6 = header2TableRow.createCell(6);
//        null6.setCellStyle(nullStyle);
//        Cell null7 = header2TableRow.createCell(7);
//        null7.setCellStyle(nullStyle);
//        Cell null8 = header2TableRow.createCell(8);
//        null8.setCellStyle(nullStyle);
//        Cell null9 = header2TableRow.createCell(9);
//        null9.setCellStyle(nullStyle);
//        Cell null10 = header2TableRow.createCell(10);
//        null10.setCellStyle(nullStyle);
//
//
//
//        Cell om= header2TableRow.createCell(11);
//        om.setCellValue("Ốm");
//        om.setCellStyle(headerTableStyle);
//////        sheet.addMergedRegion(new CellRangeAddress(2, 3, 11, 11));
//        Cell khongLuong = header2TableRow.createCell(12);
//        khongLuong.setCellValue("Không lương");
//        khongLuong.setCellStyle(headerTableStyle);
//////        sheet.addMergedRegion(new CellRangeAddress(2, 3, 12, 12));
//        Cell phep = header2TableRow.createCell(13);
//        phep.setCellValue("Phép");
//        phep.setCellStyle(headerTableStyle);
//////        sheet.addMergedRegion(new CellRangeAddress(2, 3, 13, 13));
//        Cell khongPhep = header2TableRow.createCell(14);
//        khongPhep.setCellValue("Không phép");
//        khongPhep.setCellStyle(headerTableStyle);
//
//
//
//

//        rowNum++;
//        Row nullHeaderRow = sheet.createRow(rowNum);
//        for (int i = 0; i < 8; i++) nullHeaderRow.createCell(i).setCellStyle(headerTableStyle);
//        System.out.print("return 111");

        //// Table data
        CellStyle dateStyle = workbook.createCellStyle();
        DataFormat dateFormat = workbook.createDataFormat();
        XSSFFont dateFont = workbook.createFont();
        dateFont.setFontName("Times New Roman");
        dateFont.setFontHeightInPoints((short) 9);
        dateStyle.setDataFormat(dateFormat.getFormat("m/d/yyyy"));
        dateStyle.setFont(dateFont);
        dateStyle.setAlignment(HorizontalAlignment.CENTER);
        dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dateStyle.setBorderTop(BorderStyle.THIN);
        dateStyle.setBorderBottom(BorderStyle.THIN);
        dateStyle.setBorderLeft(BorderStyle.THIN);
        dateStyle.setBorderRight(BorderStyle.THIN);



        CellStyle nameStyle = workbook.createCellStyle();
        DataFormat nameFormat = workbook.createDataFormat();
        XSSFFont nameFont = workbook.createFont();
        nameFont.setFontName("Times New Roman");
        nameFont.setFontHeightInPoints((short) 9);
        nameStyle.setDataFormat(nameFormat.getFormat("0.00"));
        nameStyle.setFont(nameFont);
        nameStyle.setAlignment(HorizontalAlignment.LEFT);
        nameStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        nameStyle.setBorderTop(BorderStyle.THIN);
        nameStyle.setBorderBottom(BorderStyle.THIN);
        nameStyle.setBorderLeft(BorderStyle.THIN);
        nameStyle.setBorderRight(BorderStyle.THIN);


        CellStyle normalDayStyle = workbook.createCellStyle();
        DataFormat normalDayFormat = workbook.createDataFormat();
        XSSFFont normalDayFont = workbook.createFont();
        normalDayFont.setFontName("Times New Roman");
        normalDayFont.setFontHeightInPoints((short) 9);
        normalDayStyle.setDataFormat(normalDayFormat.getFormat("0.00"));
        normalDayStyle.setFont(normalDayFont);
        normalDayStyle.setAlignment(HorizontalAlignment.CENTER);
        normalDayStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        normalDayStyle.setBorderTop(BorderStyle.THIN);
        normalDayStyle.setBorderBottom(BorderStyle.THIN);
        normalDayStyle.setBorderLeft(BorderStyle.THIN);
        normalDayStyle.setBorderRight(BorderStyle.THIN);



        int firstRowDataTable = rowNum + 1;
        for (int i = 0; i < listOfData.size(); i++) {
            rowNum++;
            Row newRow = sheet.createRow(rowNum);

            TimeSheetMonth data = (TimeSheetMonth) listOfData.get(i);


            Cell idNVData = newRow.createCell(0);
            idNVData.setCellStyle(normalDayStyle);
            Cell nameData = newRow.createCell(1);
            nameData.setCellStyle(nameStyle);
            Cell chucVuData = newRow.createCell(2);
            chucVuData.setCellStyle(normalDayStyle);
            Cell ngayVaoCTData = newRow.createCell(3);
            Cell gioCongData = newRow.createCell(4);
            gioCongData.setCellStyle(normalDayStyle);
            Cell lamThemData = newRow.createCell(5);
            lamThemData.setCellStyle(normalDayStyle);
            Cell boSungData = newRow.createCell(6);
            boSungData.setCellStyle(normalDayStyle);
            Cell chuNhatData = newRow.createCell(7);
            chuNhatData.setCellStyle(normalDayStyle);
            Cell buaAnData = newRow.createCell(8);
            buaAnData.setCellStyle(normalDayStyle);
            Cell tongData = newRow.createCell(9);
            tongData.setCellStyle(normalDayStyle);
            Cell tongCongData = newRow.createCell(10);
            tongCongData.setCellStyle(normalDayStyle);
            Cell omData = newRow.createCell(11);
            omData.setCellStyle(normalDayStyle);
            Cell khongLuongData = newRow.createCell(12);
            khongLuongData.setCellStyle(normalDayStyle);
            Cell phepData = newRow.createCell(13);
            phepData.setCellStyle(normalDayStyle);
            Cell khongPhepData = newRow.createCell(14);
            khongPhepData.setCellStyle(normalDayStyle);

//   truyền data vào cell
//            cardinalNumberData.setCellValue(i + 1);

//            String dateData = GPAYDateFormat.DateToString(data.getNgayvao_congty(),"m/d/yyyy");
//            System.out.println(data.getNgayvao_congty());
//
            SimpleDateFormat dt = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");
//            Date date = dt.parse(data.getNgayvao_congty());
            Date date = null;
            if(data.getNgayvao_congty() != null) date = dt.parse(data.getNgayvao_congty());
            SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yyyy");

            idNVData.setCellValue(data.getPersonnel_code());
            nameData.setCellValue(data.getFullname());

            if(date == null  ) {
                ngayVaoCTData.setCellValue("-");
                ngayVaoCTData.setCellStyle(dateStyle);
            }
            else {
                ngayVaoCTData.setCellValue(dt1.format(date));
                ngayVaoCTData.setCellStyle(dateStyle);
            }
            if(data.getPosition_code() == null) chucVuData.setCellValue("-");
            else chucVuData.setCellValue(data.getPosition_code());

            if(data.getTime_working() == 0) gioCongData.setCellValue("-");
            else gioCongData.setCellValue((data.getTime_working()));
            
            //
            if(data.getTime_over() == 0) lamThemData.setCellValue("-");
            else lamThemData.setCellValue((data.getTime_over()));

            if(data.getTime_plus() == 0) boSungData.setCellValue("-");
            else boSungData.setCellValue((data.getTime_plus()));
            //

            if(data.getTime_sunday() == 0 ) chuNhatData.setCellValue("-");
            else chuNhatData.setCellValue((data.getTime_sunday()));

            if(data.getLunch() == 0) buaAnData.setCellValue("-");
            else buaAnData.setCellValue((data.getLunch()));

            if(data.getTotal() == 0) tongData.setCellValue("-");
            else tongData.setCellValue((data.getTotal()));

            if(data.getTotal() == 0) tongCongData.setCellValue("-");
            else tongCongData.setCellValue((data.getTotal()/8));

            if(data.getNghi_om() == 0) omData.setCellValue("-");
            else omData.setCellValue(data.getNghi_om());

            if(data.getNghi_khongluong() == 0) khongLuongData.setCellValue("-");
            else khongLuongData.setCellValue(data.getNghi_khongluong());

            if(data.getNghi_phep() == 0) phepData.setCellValue("-");
            else phepData.setCellValue((data.getNghi_phep()));

            if(data.getNghi_khongphep() == 0) khongPhepData.setCellValue("-");
            else khongPhepData.setCellValue(data.getNghi_khongphep());



     }
//        System.out.print("return 2");
        int lastRowDataTable = rowNum;

        //// Footer table
        rowNum++;
        Row footerRow = sheet.createRow(rowNum);
        DataFormat footerFormat = workbook.createDataFormat();
        CellStyle footerStyle = workbook.createCellStyle();
        XSSFFont footerFont = workbook.createFont();
        footerFont.setFontName("Times New Roman");
        footerFont.setFontHeightInPoints((short) 9);
        footerFont.setBold(true);
        footerStyle.setFont(footerFont);
        footerStyle.setDataFormat(footerFormat.getFormat("0.00"));
        footerStyle.setAlignment(HorizontalAlignment.CENTER);
        footerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        footerStyle.setBorderTop(BorderStyle.THIN);
        footerStyle.setBorderBottom(BorderStyle.THIN);
        footerStyle.setBorderLeft(BorderStyle.THIN);
        footerStyle.setBorderRight(BorderStyle.THIN);

        List<Cell> nullCell = new ArrayList<Cell>(){{
            add(footerRow.createCell(0));
            add(footerRow.createCell(14));
        }};

        nullCell.forEach(cell -> cell.setCellStyle(footerStyle));

        Cell sumText = footerRow.createCell(1);
        sumText.setCellValue("Tổng:"+listOfData.size()+" NV");
        sumText.setCellStyle(footerStyle);

        CellStyle nullStyle1 = workbook.createCellStyle();
        nullStyle1.setBorderBottom(BorderStyle.THIN);
        nullStyle1.setBorderRight(BorderStyle.THIN);
        Cell nullFooter1 = footerRow.createCell(2);
        nullFooter1.setCellStyle(nullStyle1);
        Cell nullFooter2 = footerRow.createCell(3);
        nullFooter2.setCellStyle(nullStyle1);


        Cell sumGioCong = footerRow.createCell(4);
        sumGioCong.setCellFormula(String.format("SUM(E%d:E%d)", firstRowDataTable + 1, lastRowDataTable + 1));
        formulaEvaluator.evaluateFormulaCell(sumGioCong);
        sumGioCong.setCellStyle(footerStyle);

        Cell sumLamThem = footerRow.createCell(5);
        sumLamThem.setCellFormula(String.format("SUM(F%d:F%d)", firstRowDataTable + 1, lastRowDataTable + 1));
        formulaEvaluator.evaluateFormulaCell(sumLamThem);
        sumLamThem.setCellStyle(footerStyle);

        Cell sumBoSung = footerRow.createCell(6);
        sumBoSung.setCellFormula(String.format("SUM(G%d:G%d)", firstRowDataTable + 1, lastRowDataTable + 1));
        formulaEvaluator.evaluateFormulaCell(sumBoSung);
        sumBoSung.setCellStyle(footerStyle);
        
        Cell sumChuNhat = footerRow.createCell(7);
        sumChuNhat.setCellFormula(String.format("SUM(H%d:H%d)", firstRowDataTable + 1, lastRowDataTable + 1));
        formulaEvaluator.evaluateFormulaCell(sumChuNhat);
        sumChuNhat.setCellStyle(footerStyle);

        Cell sumBuaAn = footerRow.createCell(8);
        sumBuaAn.setCellFormula(String.format("SUM(I%d:I%d)", firstRowDataTable + 1, lastRowDataTable + 1));
        formulaEvaluator.evaluateFormulaCell(sumBuaAn);
        sumBuaAn.setCellStyle(footerStyle);

        Cell sumTong = footerRow.createCell(9);
        sumTong.setCellFormula(String.format("SUM(J%d:J%d)", firstRowDataTable + 1, lastRowDataTable + 1));
        formulaEvaluator.evaluateFormulaCell(sumTong);
        sumTong.setCellStyle(footerStyle);

        Cell sumTongCong = footerRow.createCell(10);
        sumTongCong.setCellFormula(String.format("SUM(K%d:K%d)", firstRowDataTable + 1, lastRowDataTable + 1));
        formulaEvaluator.evaluateFormulaCell(sumTongCong);
        sumTongCong.setCellStyle(footerStyle);

        Cell sumOm = footerRow.createCell(11);
        sumOm.setCellFormula(String.format("SUM(L%d:L%d)", firstRowDataTable + 1, lastRowDataTable + 1));
        formulaEvaluator.evaluateFormulaCell(sumOm);
        sumOm.setCellStyle(footerStyle);

        Cell sumKhongLuong = footerRow.createCell(12);
        sumKhongLuong.setCellFormula(String.format("SUM(M%d:M%d)", firstRowDataTable + 1, lastRowDataTable + 1));
        formulaEvaluator.evaluateFormulaCell(sumKhongLuong);
        sumKhongLuong.setCellStyle(footerStyle);

        Cell sumPhep = footerRow.createCell(13);
        sumPhep.setCellFormula(String.format("SUM(N%d:N%d)", firstRowDataTable + 1, lastRowDataTable + 1));
        formulaEvaluator.evaluateFormulaCell(sumPhep);
        sumPhep.setCellStyle(footerStyle);

        Cell sumKhongPhep = footerRow.createCell(14);
        sumKhongPhep.setCellFormula(String.format("SUM(O%d:O%d)", firstRowDataTable + 1, lastRowDataTable + 1));
        formulaEvaluator.evaluateFormulaCell(sumKhongPhep);
        sumKhongPhep.setCellStyle(footerStyle);

        //// Signature
        rowNum++;
        Row billToTextRow = sheet.createRow(rowNum);

        CellStyle billToTextStyle = workbook.createCellStyle();
        XSSFFont billToTextFont = workbook.createFont();
        billToTextFont.setFontName("Times New Roman");
        billToTextFont.setFontHeightInPoints((short) 9);
        billToTextFont.setBold(true);
        billToTextStyle.setFont(billToTextFont);
        billToTextStyle.setAlignment(HorizontalAlignment.LEFT);
        billToTextStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell billToTextCell = billToTextRow.createCell(0);
        billToTextCell.setCellValue("Ghi chú ");
        billToTextCell.setCellStyle(billToTextStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 14));

        rowNum += 5;
        Row signatureDayRow = sheet.createRow(rowNum);

        CellStyle signatureDayStyle = workbook.createCellStyle();
        XSSFFont signatureDayFont = workbook.createFont();
        signatureDayFont.setFontName("Times New Roman");
        signatureDayFont.setFontHeightInPoints((short) 9);
        signatureDayFont.setItalic(true);
        signatureDayStyle.setFont(signatureDayFont);
        signatureDayStyle.setAlignment(HorizontalAlignment.RIGHT);
        signatureDayStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell signatureDayCell = signatureDayRow.createCell(3);
        signatureDayCell.setCellValue("Bắc Ninh, ngày   tháng   năm      ");
        signatureDayCell.setCellStyle(signatureDayStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 3, 14));

        rowNum++;
        Row signatureRow = sheet.createRow(rowNum);

        CellStyle signatureStyle = workbook.createCellStyle();
        XSSFFont signatureFont = workbook.createFont();
        signatureFont.setFontName("Times New Roman");
        signatureFont.setFontHeightInPoints((short) 9);
        signatureFont.setBold(true);
        signatureStyle.setFont(signatureFont);
        signatureStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell signatureCell = signatureRow.createCell(0);
        signatureCell.setCellValue("   Giám đốc                      Kế toán trưởng                    Quản đốc                    Lập biểu            ");
        signatureCell.setCellStyle(signatureStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 14));
//        System.out.println("oke oke");
        //// Write listOfData to workbook
        FileOutputStream fileOutputStream = new FileOutputStream(excelFile);
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        workbook.close();
//        System.out.print("return return");
        return excelFile;
    }
}
