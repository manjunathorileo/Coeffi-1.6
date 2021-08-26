package com.dfq.coeffi.vivo.controllers;


import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.vivo.entity.Site;
import com.dfq.coeffi.vivo.service.SiteService;
import jxl.write.*;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jxl.format.Alignment.CENTRE;

@RestController
public class SiteController extends BaseController {
    @Autowired
    SiteService siteService;

    @PostMapping("site/save")
    public ResponseEntity<List<Site>> saveSite(@RequestBody List<Site> siteList) {
        for (Site site : siteList) {
            site.setAvailable(true);
            Site site1 = siteService.saveSite(site);
        }
        return new ResponseEntity<>(siteList, HttpStatus.OK);
    }

    @GetMapping("sites")
    public ResponseEntity<List<Site>> getAllSite() {
        List<Site> site2 = siteService.getActiveSites();
        return new ResponseEntity<>(site2, HttpStatus.OK);
    }

    @GetMapping("site/get/{id}")
    public ResponseEntity<Optional<Site>> getSiteById(@PathVariable long id) {
        Optional<Site> site3 = siteService.getSiteById(id);
        return new ResponseEntity<>(site3, HttpStatus.OK);
    }

    @GetMapping("site-delete/{id}")
    public void deleteSiteByid(@PathVariable long id) {
        Optional<Site> site = siteService.getSiteById(id);
        site.get().setAvailable(false);
        siteService.saveSite(site.get());
    }

    @PostMapping("site/import")
    public ResponseEntity<List<Site>> SiteExcel(@RequestParam("file") MultipartFile file) throws Exception {
        List<Site> siteList = new ArrayList<>();
        HSSFWorkbook wb = new HSSFWorkbook(file.getInputStream());
        HSSFSheet sheet = wb.getSheetAt(0);
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Site site = new Site();
            HSSFRow row = sheet.getRow(i);
            site.setId((int) row.getCell(0).getNumericCellValue());
            site.setSiteName(row.getCell(1).getStringCellValue());
            site.setRemarks(row.getCell(2).getStringCellValue());
            site.setNoOfEmp((long) row.getCell(3).getNumericCellValue());
            site.setAvailable(true);
            siteService.saveSite(site);
            siteList.add(site);
        }
        return new ResponseEntity<>(siteList, HttpStatus.OK);

    }


    @GetMapping("site/export")
    private void createCustomersDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Site> siteList = siteService.getActiveSites();
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= site.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet(workbook, siteList, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheet(WritableWorkbook workbook, List<Site> sites, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Data Input", index);
        s.getSettings().setPrintGridLines(false);

        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(Colour.GRAY_25);

        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 10);
        s.setColumnView(7, 15);
        s.setColumnView(8, 15);
        s.addCell(new Label(0, 0, "ID", headerFormat));
        s.addCell(new Label(1, 0, "SiteName", headerFormat));
        s.addCell(new Label(2, 0, "Remarks", headerFormat));
        s.addCell(new Label(3, 0, "NoOfEmployees", headerFormat));
        int rownum = 1;
        for (Site sit : sites) {
            s.addCell(new Label(0, rownum, "" + sit.getId()));
            s.addCell(new Label(1, rownum, "" + sit.getSiteName()));
            s.addCell(new Label(2, rownum, "" + sit.getRemarks()));
            s.addCell(new Label(3, rownum, "" + sit.getNoOfEmp()));

            rownum++;
        }
        return workbook;
    }


}
