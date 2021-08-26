package com.dfq.coeffi.Oqc.User;

import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMasterService;
import com.dfq.coeffi.Oqc.Admin.*;
import com.dfq.coeffi.StoreManagement.Repository.ProductNameRepository;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jxl.write.WriteException;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static com.itextpdf.text.BaseColor.BLACK;

@RestController
public class OqcUserController extends BaseController {

    private final OqcMasterService oqcMasterService;
    private final OqcUserService oqcUserService;
    private final CheckListAssignedService checkListAssignedService;
    private final EmployeeService employeeService;

    @Autowired
    public OqcUserController(OqcMasterService oqcMasterService, OqcUserService oqcUserService, CheckListAssignedService checkListAssignedService, EmployeeService employeeService) {
        this.oqcMasterService = oqcMasterService;
        this.oqcUserService = oqcUserService;
        this.checkListAssignedService = checkListAssignedService;
        this.employeeService = employeeService;
    }

   /* @GetMapping("get-parameter-details/{product}/{line}")
    public ResponseEntity<List<CheckListMaster>> getParamaterDetails(@PathVariable("product") String product, @PathVariable("line") String line){
        List<OqcMaster> oqcList=oqcRepository.findAll();
        List<CheckListMaster> parameterList=new ArrayList<>();
        for (OqcMaster o:oqcList) {
            if(o.getProduct().equals(product)&&o.getProductionLine().equals(line)){
                parameterList=o.getParameterList();
            }
        }
        return new ResponseEntity<>(parameterList, HttpStatus.OK);
    }

    @PostMapping("save-parameter-grading")
    public ResponseEntity<OqcUser> saveGrading(@RequestBody OqcUser userOqc){
        OqcUser userOqc1=userOqc;
        Date date=new Date();
        userOqc1.setSubmittedOn(date);
        userOqcRepository.save(userOqc1);

        return new ResponseEntity<>(userOqc,HttpStatus.CREATED);
    }

    @GetMapping("get-parameter-grading/{empId}")
    public ResponseEntity<List<OqcUser>> getGrading(@PathVariable("empId") long empId){
        List<OqcUser> userOqcList=userOqcRepository.findAll();
        List<OqcUser> userOqcList1=new ArrayList<>();
        for (OqcUser u:userOqcList) {
            if(u.getEmployeeId()==empId){
                userOqcList1.add(u);
            }

        }
        return new ResponseEntity<>(userOqcList1,HttpStatus.OK);
    }
*/

    @PostMapping("oqc-user-view")
    public ResponseEntity<OqcUser> viewOqc(@RequestBody OqcUser oqcUser){
        OqcUser oqcUserFinal = new OqcUser();
        List<OqcUser> oldOqcUsers = oqcUserService.getOqcUserByProductByProductionLine(oqcUser.getProductName().getId(), oqcUser.getProductionLineMaster().getId());
        for (OqcUser oqcUserObj:oldOqcUsers) {
            if (oqcUserObj.getIsSubmitted().equals(false)){
                oqcUserFinal = oqcUserObj;
            }
        }

        if (oqcUserFinal.getId() < 1){
            OqcMaster oqcMaster = oqcMasterService.getOqcMasterByProductAndProductionLine(oqcUser.getProductName().getId(), oqcUser.getProductionLineMaster().getId());
            if (oqcMaster == null){
                throw new EntityNotFoundException("There is no oqc present.");
            }
            if (oqcMaster.getCheckListMasters().isEmpty()){
                throw new EntityNotFoundException("There is no check list present.");
            }
            List<CheckListAssigned> checkListAssigneds = assignCheckList(oqcMaster.getCheckListMasters());

            OqcUser oqcUserNew = oqcUser;
            oqcUserNew.setCheckListAssigneds(checkListAssigneds);
            oqcUserNew.setNoOfParameter(checkListAssigneds.size());
            oqcUserNew.setIsSubmitted(false);
            oqcUserFinal = oqcUserService.createOqcUser(oqcUserNew);
        }
        return new ResponseEntity<>(oqcUserFinal,HttpStatus.OK);
    }

    public List<CheckListAssigned> assignCheckList(List<CheckListMaster> checkListMasters){
        List<CheckListAssigned> checkListAssigneds = new ArrayList<>();
        for (CheckListMaster checkListMaster:checkListMasters) {
            CheckListAssigned checkListAssigned = new CheckListAssigned();
            checkListAssigned.setParameter(checkListMaster.getParameter());
            checkListAssigned.setParameterValue(checkListMaster.getParameterValue());
            checkListAssigned.setDescription(checkListMaster.getDescription());
            CheckListAssigned checkListAssignedObj = checkListAssignedService.createCheckListAssigned(checkListAssigned);
            checkListAssigneds.add(checkListAssignedObj);
        }
        return checkListAssigneds;
    }

    @PostMapping("oqc-user-submit")
    public ResponseEntity<OqcUser> saveOqc(@RequestBody OqcUser oqcUser){
        Date date = new Date();
        OqcUser oqcUserObj = oqcUserService.getOqcUser(oqcUser.getId());
        List<CheckListAssigned> checkListAssigneds = oqcUserObj.getCheckListAssigneds();

        for (CheckListAssigned checkListAssignedOld:checkListAssigneds) {
            for (CheckListAssigned checkListAssignedNew:oqcUser.getCheckListAssigneds()) {
                if (checkListAssignedNew.getId() == checkListAssignedOld.getId()){
                    checkListAssignedOld.setGrading(checkListAssignedNew.getGrading());
                    checkListAssignedOld.setGradingValue(checkListAssignedNew.getGradingValue());
                    checkListAssignedOld.setRemarks(checkListAssignedNew.getRemarks());
                    checkListAssignedOld.setIsApplicable(checkListAssignedNew.getIsApplicable());
                    checkListAssignedService.createCheckListAssigned(checkListAssignedOld);
                }
            }
        }

        oqcUserObj.setIsSubmitted(true);
        oqcUserObj.setSubmittedOn(date);
        oqcUserObj.setSubmittedById(oqcUser.getSubmittedById());
        OqcUser oqcUserFinal = oqcUserService.createOqcUser(oqcUserObj);
        return new ResponseEntity<>(oqcUserFinal,HttpStatus.OK);
    }

    /*@PostMapping("oqc-user-check-list-submit")
    public ResponseEntity<CheckListAssigned> saveCheckList(@RequestBody CheckListAssigned checkListAssigned){
        CheckListAssigned checkListAssignedObj = checkListAssignedService.getCheckListAssigned(checkListAssigned.getId());
        checkListAssignedObj.setGrading(checkListAssigned.getGrading());
        checkListAssignedObj.setGradingValue(checkListAssigned.getGradingValue());
        checkListAssignedObj.setRemarks(checkListAssigned.getRemarks());
        //checkListAssignedObj.setIsSubmitted(true);
        return new ResponseEntity<>(checkListAssignedObj,HttpStatus.OK);
    }*/

    @PostMapping("oqc-user-pdf/{id}")
    public void oqcUserPdf(HttpServletRequest request, HttpServletResponse response, @PathVariable long id, Principal principal) throws ServletException, IOException, DocumentException, com.itextpdf.text.DocumentException {
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        String yearString = dateFormat.format(today);
        long year = Long.parseLong(yearString);
        OqcUser oqcUser = oqcUserService.getOqcUser(id);

        OutputStream out = null;
        String pdfName = "OQC" + "_" + oqcUser.getProductName().getId() +"_"+ oqcUser.getProductionLineMaster().getId() + "_" + year + ".pdf";
        Document document = new Document();
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= " + pdfName);
        PdfWriter.getInstance(document, response.getOutputStream());
        try {
            convertToPDF(document, response, oqcUser, year, principal, 0);
        } catch (Exception e) {
            throw new ServletException("Exception in Pdf download", e);
        } finally {
            if (out != null)
                out.close();
        }
        document.close();
    }

    private Document convertToPDF(Document document, HttpServletResponse response, OqcUser oqcUser, long year, Principal principal, int index) throws IOException, WriteException, DocumentException, com.itextpdf.text.DocumentException {

        Paragraph para = new Paragraph();
        PdfWriter.getInstance(document, new FileOutputStream("SetAttributeExample.pdf"));
        document.open();

        String str1 = "dd/MM/yyyy";
        String str2 = "MMM";
        Date todayDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(str1, Locale.ENGLISH);
        SimpleDateFormat month = new SimpleDateFormat(str2, Locale.ENGLISH);
        String date = sdf.format(todayDate);

        Font tableHeader = new Font(Font.FontFamily.COURIER, 9, Font.BOLD, BLACK);
        Font tableBody = new Font(Font.FontFamily.COURIER, 9, Font.NORMAL, BLACK);

        String checkListDate = "";
        String product = "";
        String productionLine = "";
        String loggedBy = "";
        String submittedBy = "";

        if (oqcUser.getSubmittedOn() != null){
            checkListDate = sdf.format(oqcUser.getSubmittedOn());
        }

        Optional<Employee> loggedByEmp = employeeService.getEmployee(oqcUser.getLoggedById());
        Optional<Employee> submittedByEmp = employeeService.getEmployee(oqcUser.getLoggedById());

        product = oqcUser.getProductName().getProductName();
        productionLine = oqcUser.getProductionLineMaster().getProductionLineName();
        loggedBy = loggedByEmp.get().getFirstName();
        submittedBy = submittedByEmp.get().getFirstName();

        PdfPTable pdfHeaderMaster = new PdfPTable(3);
        pdfHeaderMaster.setWidthPercentage(100f);
        pdfHeaderMaster.getDefaultCell().setBorder(0);
        pdfHeaderMaster.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfHeaderMaster.addCell(new Phrase("Date : " + checkListDate, tableHeader));
        pdfHeaderMaster.addCell(new Phrase("Product: " + product, tableHeader));
        pdfHeaderMaster.addCell(new Phrase("Submitted By : " + submittedBy, tableHeader));
        pdfHeaderMaster.addCell(new Phrase("Logged By : " + loggedBy , tableHeader));
        pdfHeaderMaster.addCell(new Phrase("Production Line : " + productionLine, tableHeader));
        pdfHeaderMaster.addCell(new Phrase(" "));
        para.add(pdfHeaderMaster);
        para.add(Chunk.NEWLINE);

        PdfPTable pdfHeaderBody = new PdfPTable(3);
        pdfHeaderBody.setWidthPercentage(100f);
        pdfHeaderBody.getDefaultCell().setBorder(0);
        pdfHeaderBody.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfHeaderBody.addCell(new Phrase("Check Points ", tableHeader));
        pdfHeaderBody.addCell(new Phrase("Grade ", tableHeader));
        pdfHeaderBody.addCell(new Phrase("Remarks ", tableHeader));
        List<CheckListAssigned> checkLists = oqcUser.getCheckListAssigneds();
        for (CheckListAssigned checkListObj : checkLists) {
            pdfHeaderBody.addCell(new Phrase("" + checkListObj.getParameter(), tableBody));
            if (checkListObj.getGrading() == null){
                pdfHeaderBody.addCell(new Phrase("", tableBody));
            } else {
                pdfHeaderBody.addCell(new Phrase("" + checkListObj.getGrading(), tableBody));
            }
            if (checkListObj.getRemarks() == null){
                pdfHeaderBody.addCell(new Phrase("", tableBody));
            } else {
                pdfHeaderBody.addCell(new Phrase("" + checkListObj.getRemarks(), tableBody));
            }
        }
        para.add(pdfHeaderBody);
        para.add(Chunk.NEWLINE);

        document.add(para);

        return document;
    }

    @GetMapping("oqc-user")
    public ResponseEntity<OqcUser> getAllOqc(){
        List<OqcUser> oqcUserList = new ArrayList<>();
        List<OqcUser> oqcUser = oqcUserService.getAllOqcUser();
        for (OqcUser oqcUserObj:oqcUser) {
            if (oqcUserObj.getIsSubmitted().equals(true)){
                oqcUserList.add(oqcUserObj);
            }
        }
        if (oqcUserList.isEmpty()){
            throw new EntityNotFoundException("No OQC submitted.");
        }
        return new ResponseEntity(oqcUserList,HttpStatus.OK);
    }
}