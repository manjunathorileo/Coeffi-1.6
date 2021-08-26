package com.dfq.coeffi.util;

import com.dfq.coeffi.dto.MonthlyEmployeeAttendanceDto;
import com.dfq.coeffi.dto.MonthlyStatusDto;
import com.dfq.coeffi.entity.finance.expense.Asset;
import com.dfq.coeffi.entity.finance.expense.Expense;
import com.dfq.coeffi.entity.finance.expense.Liability;
import com.dfq.coeffi.entity.finance.income.Income;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeneratePdfReport extends PdfPageEventHelper {

    public static ByteArrayInputStream incomePdfReport(List<Income> incomes) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "Description", "Amount", "Created On", "Category Name", "Approved By");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 3, 3, 5, 3});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            List<String> numbers = Arrays.asList("No", "Description", "Amount", "Created On", "Category Name", "Approved By", "No", "Description", "Amount", "Created On", "Category Name", "Approved By", "No", "Description", "Amount", "Created On", "Category Name", "Approved By", "No", "Description", "Amount", "Created On", "Category Name", "Approved By", "No", "Description", "Amount", "Created On", "Category Name", "Approved By", "No", "Description", "Amount", "Created On", "Category Name", "Approved By", "No", "Description", "Amount", "Created On", "Category Name", "Approved By", "No", "Description", "Amount", "Created On", "Category Name", "Approved By", "No", "Description", "Amount", "Created On", "Category Name", "Approved By");
            for (String string : numbers) {
                for (Income income2 : incomes) {
                    Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                    PdfPCell cell;
                    cell = new PdfPCell(new Phrase(String.valueOf(income2.getId()), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(income2.getDescription(), font));
                    cell.setPaddingLeft(2);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(String.valueOf(income2.getAmount()), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setPaddingRight(2);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(String.valueOf(income2.getCreatedOn()), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setPaddingRight(2);
                    table.addCell(cell);


                    cell = new PdfPCell(new Phrase(income2.getIncomeCategory().getName(), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setPaddingRight(2);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(String.valueOf(income2.getApprovedBy()), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setPaddingRight(2);
                    table.addCell(cell);
                }
            }
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream liabilityPdfReport1(List<Liability> liability) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "Title", "Descriptiom", "amount", "Created On");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 3, 3, 5});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            List<String> numbers = Arrays.asList("No", "Title", "Descriptiom", "amount", "Created On", "No", "Title", "Descriptiom", "amount", "Created On", "No", "Title", "Descriptiom", "amount", "Created On");
            for (String string : numbers) {
                for (Liability liability2 : liability) {
                    Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                    PdfPCell cell;
                    cell = new PdfPCell(new Phrase(String.valueOf(liability2.getId()), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(liability2.getTitle(), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setPaddingRight(2);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(liability2.getDescription(), font));
                    cell.setPaddingLeft(2);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(String.valueOf(liability2.getAmount()), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setPaddingRight(2);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(String.valueOf(liability2.getCreatedOn()), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setPaddingRight(2);
                    table.addCell(cell);


                }
            }
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream expensePdfReport(List<Expense> liabilty) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "amount", "title", "description", "Created On");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 3, 3, 5});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            List<String> numbers = Arrays.asList("No", "amount", "Title", "description", "Created On", "No", "amount", "Title", "description", "Created On", "No", "amount", "Title", "description", "Created On");
            for (String string : numbers) {
                for (Expense expense2 : liabilty) {
                    Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                    PdfPCell cell;
                    cell = new PdfPCell(new Phrase(String.valueOf(expense2.getId()), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(String.valueOf(expense2.getAmount()), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setPaddingRight(2);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(expense2.getTitle(), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setPaddingRight(2);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(expense2.getDescription(), font));
                    cell.setPaddingLeft(2);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);


                    cell = new PdfPCell(new Phrase(String.valueOf(expense2.getCreatedOn()), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setPaddingRight(2);
                    table.addCell(cell);


                }
            }
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public void onStartPage(PdfWriter writer, Document document) {
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Top Left"), 30, 800, 0);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Top Right"), 550, 800, 0);
    }

    public void onEndPage(PdfWriter writer, Document document) {
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("http://www.xxxx-your_example.com/"), 110, 30, 0);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("page " + document.getPageNumber()), 550, 30, 0);
    }

    public static ByteArrayInputStream assetPdfReport(List<Asset> asset) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "Title", "Description", "amount", "createdOn");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 3, 3, 5});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            List<String> numbers = Arrays.asList("No", "Description", "Amount", "Created On", "Category Name", "Approved By", "No", "Description", "Amount", "Created On", "Category Name", "Approved By", "No", "Description", "Amount", "Created On", "Category Name", "Approved By", "No", "Description", "Amount", "Created On", "Category Name", "Approved By", "No", "Description", "Amount", "Created On", "Category Name", "Approved By", "No", "Description", "Amount", "Created On", "Category Name", "Approved By", "No", "Description", "Amount", "Created On", "Category Name", "Approved By", "No", "Description", "Amount", "Created On", "Category Name", "Approved By", "No", "Description", "Amount", "Created On", "Category Name", "Approved By");
            for (String string : numbers) {
                for (Asset asset2 : asset) {
                    Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                    PdfPCell cell;
                    cell = new PdfPCell(new Phrase(String.valueOf(asset2.getId()), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    System.out.println(asset2.getId());

                    cell = new PdfPCell(new Phrase(asset2.getTitle(), font));
                    cell.setPaddingLeft(2);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);
                    System.out.println("--------------------" + asset2.getTitle());
                    cell = new PdfPCell(new Phrase(String.valueOf(asset2.getDescription()), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setPaddingRight(2);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(String.valueOf(asset2.getAmount()), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setPaddingRight(2);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(String.valueOf(asset2.getCreatedOn()), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setPaddingRight(2);
                    table.addCell(cell);


                }
            }
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream attendanceReportByInputDate(List<EmployeeAttendance> employeeAttendances) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            List<String> headers = Arrays.asList("No", "Date", "EmployeeId", "EmployeeName", "Department", "Designation","Time In");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(90);
            table.setWidths(new int[]{1, 3, 3, 4, 3, 5,3});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
                int i = 1;
                for (EmployeeAttendance employeeAttendance : employeeAttendances) {
                    Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                    PdfPCell cell;
                    cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                    i++;
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(DateUtil.convertToDateString(employeeAttendance.getMarkedOn()), font));
                    cell.setPaddingLeft(2);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(String.valueOf(employeeAttendance.getEmployee().getEmployeeCode()), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setPaddingRight(2);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(String.valueOf(employeeAttendance.getEmployee().getFirstName()), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setPaddingRight(2);
                    table.addCell(cell);


                    cell = new PdfPCell(new Phrase(employeeAttendance.getEmployee().getDepartment().getName(), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setPaddingRight(2);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(String.valueOf(employeeAttendance.getEmployee().getDesignation().getName()), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setPaddingRight(2);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(String.valueOf(employeeAttendance.getInTime()), font));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setPaddingRight(2);
                    table.addCell(cell);
                }
            PdfWriter.getInstance(document, out);
            document.open();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Daily Attedance Report", fontHeader);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream attendanceReportByMonth(List<MonthlyEmployeeAttendanceDto> employeeAttendances) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            //List<String> headers = Arrays.asList("No","EmployeeId","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31");
            List<String> headers = Arrays.asList("No","Employee Name","1","2","3","4","5","6");
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(60);
            table.setWidths(new int[]{1,2,2,2,2,2,2,2});
            for (String string : headers) {
                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase(string, headFont));
                hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hcell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(hcell);
            }
            int i = 1;
            for (MonthlyEmployeeAttendanceDto attendanceDto : employeeAttendances) {
                Font font = FontFactory.getFont(FontFactory.COURIER, 8);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(String.valueOf(i), font));
                i++;
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(attendanceDto.getEmployeeName()), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingRight(2);
                table.addCell(cell);
                List<MonthlyStatusDto> monthlyStatusDto=attendanceDto.getMonthlyStatus();
             for (MonthlyStatusDto attendanceDto1:attendanceDto.getMonthlyStatus()) {
                    cell = new PdfPCell(new Phrase(String.valueOf(attendanceDto1.getInTime()), font));
                    cell.setPaddingLeft(2);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);

                }



            }
            PdfWriter.getInstance(document, out);
            document.open();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font f = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
            Chunk c = new Chunk("Daily Attedance Report", fontHeader);
            Paragraph p = new Paragraph(c);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph para = new Paragraph();
            para = new Paragraph(" ", fontHeader);
            document.add(para);

            document.add(table);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

}


