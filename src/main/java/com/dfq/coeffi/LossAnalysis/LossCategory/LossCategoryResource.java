package com.dfq.coeffi.LossAnalysis.LossCategory;

import com.dfq.coeffi.controller.BaseController;
import jxl.format.Colour;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jxl.format.Alignment.CENTRE;


@RestController
@Slf4j
public class LossCategoryResource extends BaseController {

    private final LossCategoryService lossCategoryService;
    private String fileName;

    @Autowired
    public LossCategoryResource(LossCategoryService lossCategoryService) {
        this.lossCategoryService = lossCategoryService;
    }

    @PostMapping("/loss-category")
    public ResponseEntity<LossCategory> createLossCategory(@Valid @RequestBody LossCategory lossCategory){
        lossCategory.setStatus(true);
        LossCategory lossCategoryObj = lossCategoryService.createLossCategory(lossCategory);
        return new ResponseEntity<>(lossCategoryObj, HttpStatus.CREATED);
    }

    @GetMapping("/loss-category")
    public ResponseEntity<LossCategory> getAllLossCategory(){
        List<LossCategory> lossCategoryList = lossCategoryService.getAllLossCategory();
        if (lossCategoryList.isEmpty()){
            throw new EntityNotFoundException("There is no Loss Category");
        }
        return new ResponseEntity(lossCategoryList, HttpStatus.OK);
    }

    @GetMapping("/loss-category/{id}")
    public ResponseEntity<LossCategory> getLossCategoryById(@PathVariable long id){
        Optional<LossCategory> lossCategory = lossCategoryService.getLossCategory(id);
        return new ResponseEntity(lossCategory, HttpStatus.OK);
    }

    @DeleteMapping("/loss-category/{id}")
    public ResponseEntity<LossCategory> deleteLossCategory(@PathVariable long id){
        LossCategory lossCategory = lossCategoryService.deleteLossCategory(id);
        return new ResponseEntity<>(lossCategory, HttpStatus.OK);
    }

    @GetMapping("/loss-category-excel-export")
    private void createAdminLossCategoryDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, WriteException {
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        String fileName = "Loss Category";
        response.setHeader("Content-Disposition", "attachment; filename="+fileName+".xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheetLossCategory(workbook, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheetLossCategory(WritableWorkbook workbook,   int index) throws WriteException {
        WritableSheet s = workbook.createSheet("Data Input", index);
        s.getSettings().setPrintGridLines(false);

        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(Colour.BLUE);

        s.addCell(new Label(0, 0, "Loss Category", headerFormat));
        s.addCell(new Label(1, 0, "Description", headerFormat));

        s.setColumnView(0, 20);
        s.setColumnView(1, 20);
        return workbook;
    }

    @PostMapping("/loss-category-import")
    public ResponseEntity<List<LossCategory>> importLossCategory(@RequestParam("file") MultipartFile file) {
        fileName = file.getOriginalFilename();
        List<LossCategory> lossCategories = lossCategoryService.getAllLossCategory();
        List<LossCategory> dto = importLossCategory(file, lossCategories);
        List<LossCategory> lossCategories1 = saveImportedCustomers(dto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    public static List<LossCategory> importLossCategory(MultipartFile file, List<LossCategory> lossCategories) {
        List<LossCategory> dtos = new ArrayList<>();
        ArrayList arrayList = new ArrayList();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            int rowNumber = sheet.getPhysicalNumberOfRows();
            if (rowNumber > 1) {
                for (int i = 1; i < rowNumber; i++) {
                    Row row = sheet.getRow(i);
                    LossCategory dto = new LossCategory();
                    //dto.setIndex(i);
                    dto.setLossCategory(row.getCell(0).getStringCellValue());
                    dto.setDescription(row.getCell(1).getStringCellValue());
                    dtos.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return dtos;
    }


    public List<LossCategory> saveImportedCustomers(List<LossCategory> dto) {
        List<LossCategory> lossCategories = toLossCategoryEntity(dto);
        if (lossCategories != null) {
            for (LossCategory lossCategory:lossCategories) {
                lossCategoryService.createLossCategory(lossCategory);
            }
        }
        return dto;
    }

    private List<LossCategory> toLossCategoryEntity(List<LossCategory> dtos) {
        List<LossCategory> lossCategories = new ArrayList<>();
        if (dtos != null && dtos.size() > 0) {
            for (LossCategory dto : dtos) {
                LossCategory lossCategory = new LossCategory();
                lossCategory.setLossCategory(dto.getLossCategory());
                lossCategory.setDescription(dto.getDescription());
                lossCategory.setStatus(true);
                lossCategories.add(lossCategory);
            }
        }
        return lossCategories;
    }
}