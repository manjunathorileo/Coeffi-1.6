package com.dfq.coeffi.LossAnalysis.LossSubCategory;

import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategory;
import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategoryService;
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
public class LossSubCategoryResource extends BaseController {

    private final LossSubCategoryService lossSubCategoryService;
    private final LossCategoryService lossCategoryService;
    private String fileName;

    @Autowired
    public LossSubCategoryResource(LossSubCategoryService lossSubCategoryService, LossCategoryService lossCategoryService) {
        this.lossSubCategoryService = lossSubCategoryService;
        this.lossCategoryService = lossCategoryService;
    }

    @PostMapping("/loss-sub-category")
    public ResponseEntity<LossSubCategory> createLossSubCategory(@Valid @RequestBody LossSubCategory lossSubCategory){
        lossSubCategory.setStatus(true);
        LossSubCategory lossSubCategoryObj = lossSubCategoryService.createLossSubCategory(lossSubCategory);
        return new ResponseEntity<>(lossSubCategoryObj, HttpStatus.CREATED);
    }

    @GetMapping("/loss-sub-category")
    public ResponseEntity<LossSubCategory> getAllLossSubCategory(){
        List<LossSubCategory> lossSubCategories = lossSubCategoryService.getAllLossSubCategory();
        if (lossSubCategories.isEmpty()){
            throw new EntityNotFoundException("There is no Loss SubCategories");
        }
        return new ResponseEntity(lossSubCategories, HttpStatus.OK);
    }

    @GetMapping("/loss-sub-category/{id}")
    public ResponseEntity<LossSubCategory> getLossSubCategoryById(@PathVariable long id){
        Optional<LossSubCategory> lossSubCategoryOptional = lossSubCategoryService.getLossSubCategory(id);
        return new ResponseEntity(lossSubCategoryOptional, HttpStatus.OK);
    }

    @GetMapping("/loss-sub-category-by-loss-category/{lossCategoryId}")
    public ResponseEntity<LossSubCategory> getLossSubCategoryByLossCategory(@PathVariable long lossCategoryId){
        Optional<LossCategory> lossCategoryOptional = lossCategoryService.getLossCategory(lossCategoryId);
        List<LossSubCategory> lossSubCategories = lossSubCategoryService.getLossSubCategoryByLosscategory(lossCategoryOptional.get());
        if (lossSubCategories.isEmpty()){
            throw new EntityNotFoundException("There is no Sub Category for "+ lossCategoryOptional.get().getLossCategory());
        }
        return new ResponseEntity(lossSubCategories, HttpStatus.OK);
    }

    @DeleteMapping("/loss-sub-category/{id}")
    public ResponseEntity<LossSubCategory> deleteLossSubCategory(@PathVariable long id){
        LossSubCategory lossSubCategory = lossSubCategoryService.deleteLossSubCategory(id);
        return new ResponseEntity<>(lossSubCategory, HttpStatus.OK);
    }

    @GetMapping("/loss-subcategory-excel-export")
    private void createAdminLossSubCategoryDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, WriteException {
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        String fileName = "Loss SubCategory";
        response.setHeader("Content-Disposition", "attachment; filename="+fileName+".xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheetLossSubCategory(workbook, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheetLossSubCategory(WritableWorkbook workbook,   int index) throws WriteException {
        WritableSheet s = workbook.createSheet("Data Input", index);
        s.getSettings().setPrintGridLines(false);

        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(Colour.BLUE);

        s.addCell(new Label(0, 0, "Loss SubCategory", headerFormat));
        s.addCell(new Label(1, 0, "Description", headerFormat));

        s.setColumnView(0, 20);
        s.setColumnView(1, 20);
        return workbook;
    }

    @PostMapping("/loss-subcategory-import/{lossCategoryId}")
    public ResponseEntity<List<LossSubCategory>> importLossCategory(@RequestParam("file") MultipartFile file, @PathVariable long lossCategoryId) {
        fileName = file.getOriginalFilename();
        List<LossSubCategory> lossSubCategories = lossSubCategoryService.getAllLossSubCategory();
        List<LossSubCategory> dto = importLossSubCategory(file, lossSubCategories, lossCategoryId);
        for (LossSubCategory lossSubCategory:dto) {
            lossSubCategoryService.createLossSubCategory(lossSubCategory);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    public List<LossSubCategory> importLossSubCategory(MultipartFile file, List<LossSubCategory> lossSubCategories, long lossCategoryId) {
        List<LossSubCategory> dtos = new ArrayList<>();
        ArrayList arrayList = new ArrayList();
        Optional<LossCategory> lossCategory = lossCategoryService.getLossCategory(lossCategoryId);
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            int rowNumber = sheet.getPhysicalNumberOfRows();
            if (rowNumber > 1) {
                for (int i = 1; i < rowNumber; i++) {
                    Row row = sheet.getRow(i);
                    LossSubCategory dto = new LossSubCategory();
                    List<LossSubCategory> lossSubCategoriesObj = new ArrayList<>();
                    //dto.setIndex(i);
                    dto.setLossCategory(lossCategory.get());
                    dto.setLossSubCategory(row.getCell(0).getStringCellValue());
                    dto.setDescription(row.getCell(1).getStringCellValue());
                    dto.setStatus(true);
                    dtos.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return dtos;
    }

    /*public List<LossSubCategory> saveImportedLossSubCategory(List<LossSubCategory> dto) {
        List<LossSubCategory> lossSubCategories = toLossSubCategoryEntity(dto);
        if (lossSubCategories != null) {
            for (LossSubCategory lossSubCategory:lossSubCategories) {
                lossSubCategoryService.createLossSubCategory(lossSubCategory);
            }
        }
        return dto;
    }

    private List<LossSubCategory> toLossSubCategoryEntity(List<LossSubCategory> dtos) {
        List<LossSubCategory> lossSubCategories = new ArrayList<>();
        if (dtos != null && dtos.size() > 0) {
            for (LossSubCategory dto : dtos) {
                Optional<LossCategory> lossCategory = lossCategoryService.getLossCategory(dto.getId());
                LossSubCategory lossSubCategory = new LossSubCategory();
                lossSubCategory.setLossCategory(lossCategory.get());
                lossSubCategory.setLossCategory(dto.getLossCategory());
                lossSubCategory.setDescription(dto.getDescription());
                lossSubCategory.setStatus(true);
                lossSubCategories.add(lossSubCategory);
            }
        }
        return lossSubCategories;
    }*/
}