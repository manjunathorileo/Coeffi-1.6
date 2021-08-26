package com.dfq.coeffi.StoreManagement.Controller;

import com.dfq.coeffi.StoreManagement.Entity.Items;
import com.dfq.coeffi.StoreManagement.Repository.ItemsRepository;
import com.dfq.coeffi.controller.BaseController;
import jxl.write.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import static jxl.format.Alignment.CENTRE;
@RestController
public class ItemQuantityUploadController extends BaseController  {

    @Autowired
    ItemsRepository itemsRepository;

    @GetMapping("item/template-download")
    private void createItemDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Item.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeVehicleToSheet(workbook, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }


    private WritableWorkbook writeVehicleToSheet(WritableWorkbook workbook, HttpServletResponse response, int index) throws IOException, WriteException {
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
        s.setColumnView(6, 20);
        s.setColumnView(7, 20);
        s.setColumnView(8, 20);
        s.setColumnView(9, 20);

        s.addCell(new Label(0, 0, "#", headerFormat));
        s.addCell(new Label(1, 0, "Item Category", headerFormat));
        s.addCell(new Label(2, 0, "Item Number", headerFormat));
        s.addCell(new Label(3, 0, "Item Name", headerFormat));
        s.addCell(new Label(4, 0, "Item Description", headerFormat));
        s.addCell(new Label(5, 0, "Quantity", headerFormat));
        s.addCell(new Label(6, 0, "Item Price", headerFormat));
        s.addCell(new Label(7, 0, "Min Quantity", headerFormat));
        s.addCell(new Label(8, 0, "Max Quantity", headerFormat));
        s.addCell(new Label(9, 0, "Reorder Quantity", headerFormat));

        return workbook;
    }

    @PostMapping("items-bulk-upload")
    public ResponseEntity<List<Items>> ItemsBulkUpload(@RequestParam("file") MultipartFile file) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = wb.getSheetAt(0);
        List<Items> itemsList = new ArrayList<>();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Items items= new Items();
            XSSFRow row = sheet.getRow(i);
            items.setItemCategory(row.getCell(1).getStringCellValue()); ;
            items.setItemNumber((long)row.getCell(2).getNumericCellValue()) ;
            items.setItemName(row.getCell(3).getStringCellValue()) ;
            items.setDescription(row.getCell(4).getStringCellValue()); ;
            items.setQuantity((long)row.getCell(5).getNumericCellValue()) ;
            items.setItemPrice((long)row.getCell(6).getNumericCellValue()) ;
            items.setMinQuantity((long)row.getCell(7).getNumericCellValue()) ;
            items.setMaxQuantity((long)row.getCell(8).getNumericCellValue()) ;
            items.setReOrderQuantity((long)row.getCell(9).getNumericCellValue()) ;
            itemsRepository.save(items);
            itemsList.add(items);
        }
        return new ResponseEntity<>(itemsList, HttpStatus.OK);
    }

    @GetMapping("get-item-number")
    public ResponseEntity<List<Items>> getItemNumber(){
        List<Items> itemsList=itemsRepository.findAll();
        return new ResponseEntity<>(itemsList,HttpStatus.OK );
    }

    @GetMapping("get-item-details/{id}")
    public ResponseEntity<Items> getItemName(@PathVariable("id") long itemId){
        Items items=itemsRepository.findOne(itemId);
        return new ResponseEntity<>(items,HttpStatus.OK);
    }

    @DeleteMapping("item-delete/{id}")
    public void deleteItem(@PathVariable("id") long itemId ){
        itemsRepository.delete(itemId);
    }

    @PostMapping("item-edit/{id}")
    public ResponseEntity<Items> editItem(@PathVariable("id") long itemId,@RequestBody Items items){
        Items items1=itemsRepository.findOne(itemId);
        items1.setItemName(items.getItemName());
        items1.setDescription(items.getDescription());
        items1.setQuantity(items.getQuantity());
        itemsRepository.save(items1);
        return new ResponseEntity<>(items1,HttpStatus.CREATED);
    }
}
