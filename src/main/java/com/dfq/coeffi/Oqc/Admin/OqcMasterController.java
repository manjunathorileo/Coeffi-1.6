package com.dfq.coeffi.Oqc.Admin;

import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMasterService;
import com.dfq.coeffi.StoreManagement.Entity.ProductName;
import com.dfq.coeffi.StoreManagement.Entity.ProductionLineMasters;
import com.dfq.coeffi.StoreManagement.Repository.ProductNameRepository;
import com.dfq.coeffi.StoreManagement.Repository.ProductionLineMastersRepository;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.EmployeeService;
import jxl.write.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
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
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jxl.format.Alignment.CENTRE;

@RestController
public class OqcMasterController extends BaseController {

    private final CheckListMasterService checkListMasterService;
    private final OqcMasterService oqcMasterService;
    private final ProductNameRepository productNameRepository;
    //private final ProductionLineMasterService productionLineMasterService;
    private final EmployeeService employeeService;
    private final ProductionLineMastersRepository productionLineMastersRepository;

    @Autowired
    public OqcMasterController(CheckListMasterService checkListMasterService, OqcMasterService oqcMasterService, ProductNameRepository productNameRepository, ProductionLineMasterService productionLineMasterService, EmployeeService employeeService, ProductionLineMastersRepository productionLineMastersRepository) {
        this.checkListMasterService = checkListMasterService;
        this.oqcMasterService = oqcMasterService;
        this.productNameRepository = productNameRepository;
        //this.productionLineMasterService = productionLineMasterService;
        this.employeeService = employeeService;
        this.productionLineMastersRepository = productionLineMastersRepository;
    }


    @GetMapping("parameter/template-download")
    private void createItemDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= CheckList.xlsx");
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

        s.addCell(new Label(0, 0, "#", headerFormat));
        s.addCell(new Label(1, 0, "CheckListMaster", headerFormat));
        s.addCell(new Label(2, 0, "CheckListMaster Value", headerFormat));
        s.addCell(new Label(3, 0, "Description", headerFormat));

        return workbook;
    }

    @PostMapping("parameter-bulk-upload")
    public ResponseEntity<List<CheckListMaster>> checkListMasterBulkUpload(@RequestParam("file") MultipartFile file) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = wb.getSheetAt(0);
        List<CheckListMaster> parameterList = new ArrayList<>();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            CheckListMaster parameter = new CheckListMaster();
            XSSFRow row = sheet.getRow(i);
            parameter.setParameter(row.getCell(1).getStringCellValue());
            parameter.setParameterValue(row.getCell(2).getNumericCellValue());
            parameter.setDescription(row.getCell(3).getStringCellValue());
            parameter = checkListMasterService.createCheckListMaster(parameter);
            parameterList.add(parameter);

        }
        return new ResponseEntity<>(parameterList, HttpStatus.OK);
    }

    @PostMapping("admin-oqc-save")
    public ResponseEntity<OqcMaster> saveOqc(@RequestBody OqcMaster oqc) {
        OqcMaster oqcNew = new OqcMaster();
        ProductName productName = productNameRepository.findOne(oqc.getProductId());
        ProductionLineMasters productionLineMasters = productionLineMastersRepository.findOne(oqc.getProductionLineId());
        oqcNew.setProductId(productName.getId());
        oqcNew.setProduct(productName.getProductName());
        oqcNew.setProductionLineId(productionLineMasters.getId());
        oqcNew.setProductionLine(productionLineMasters.getProductionLineName());
        oqcNew.setSupervisor(oqc.getSupervisor());
        oqcNew.setNoOfParameter(oqc.getNoOfParameter());
        oqcNew.setCheckListMasters(oqc.getCheckListMasters());

        OqcMaster oqcMaster = oqcMasterService.createOqcMaster(oqcNew);
        return new ResponseEntity<>(oqcMaster, HttpStatus.CREATED);
    }

    @GetMapping("admin-get-oqc")
    public ResponseEntity<List<OqcMaster>> getOqc() {
        List<OqcMaster> oqcList = oqcMasterService.getOqcMaster();
        if (oqcList.isEmpty()){
            throw new EntityNotFoundException("There is no data for oqc");
        }
        return new ResponseEntity<>(oqcList, HttpStatus.OK);
    }

    @PostMapping("admin-oqc-save/{productId}/{productionLineId}/{supervisorId}")
    public ResponseEntity<OqcMaster> saveOqc(@RequestParam("file") MultipartFile file, @PathVariable long productId, @PathVariable long productionLineId, @PathVariable long supervisorId) throws Exception {
        List<CheckListMaster> checkListMasterList = parameterBulkUpload(file);
        ProductName productName = productNameRepository.findOne(productId);
        ProductionLineMasters productionLineMaster = productionLineMastersRepository.findOne(productionLineId);
        Optional<Employee> employee = employeeService.getEmployee(supervisorId);

        OqcMaster oqcMaster = new OqcMaster();
        oqcMaster = oqcMasterService.getOqcMasterByProductAndProductionLine(productId,productionLineId);
        if (oqcMaster.getId() > 0){
            oqcMaster.setSupervisorId(employee.get().getId());
            oqcMaster.setSupervisor(employee.get().getFirstName());
            oqcMaster.setNoOfParameter(checkListMasterList.size());
            oqcMaster.setCheckListMasters(checkListMasterList);
        } else {
            oqcMaster.setProductId(productName.getId());
            oqcMaster.setProduct(productName.getProductName());
            oqcMaster.setProductionLineId(productionLineMaster.getId());
            oqcMaster.setProductionLine(productionLineMaster.getProductionLineName());
            oqcMaster.setSupervisorId(employee.get().getId());
            oqcMaster.setSupervisor(employee.get().getFirstName());
            oqcMaster.setNoOfParameter(checkListMasterList.size());
            oqcMaster.setCheckListMasters(checkListMasterList);
        }
        OqcMaster oqcMasterNew = oqcMasterService.createOqcMaster(oqcMaster);
        return new ResponseEntity<>(oqcMasterNew, HttpStatus.CREATED);
    }

    public List<CheckListMaster> parameterBulkUpload(MultipartFile file) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = wb.getSheetAt(0);
        List<CheckListMaster> parameterList = new ArrayList<>();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            CheckListMaster parameter = new CheckListMaster();
            XSSFRow row = sheet.getRow(i);
            parameter.setParameter(row.getCell(1).getStringCellValue());
            parameter.setParameterValue(row.getCell(2).getNumericCellValue());
            parameter.setDescription(row.getCell(3).getStringCellValue());
            parameter = checkListMasterService.createCheckListMaster(parameter);
            parameterList.add(parameter);
        }
        return parameterList;
    }

}