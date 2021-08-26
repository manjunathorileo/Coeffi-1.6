package com.dfq.coeffi.preventiveMaintenance.user.controler;

import com.dfq.coeffi.SOPDetails.Event.eventCompletion.EventCompletion;
import com.dfq.coeffi.SOPDetails.Event.eventCompletion.EventCompletionService;
import com.dfq.coeffi.SOPDetails.Event.eventMaster.EventMaster;
import com.dfq.coeffi.SOPDetails.Event.eventMaster.EventMasterService;
import com.dfq.coeffi.SOPDetails.Event.eventWiseSopStepsAssigned.EventWiseSopStepsAssigned;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategoryService;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import com.dfq.coeffi.SOPDetails.SOPType.SopTypeService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.preventiveMaintenance.durationType.DurationType;
import com.dfq.coeffi.preventiveMaintenance.durationType.DurationTypeService;
import com.dfq.coeffi.preventiveMaintenance.user.entity.PreventiveMaintenance;
import com.dfq.coeffi.preventiveMaintenance.user.entity.SopStepsAssigned;
import com.dfq.coeffi.preventiveMaintenance.user.service.PreventiveMaintenanceService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jxl.write.WriteException;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.itextpdf.text.BaseColor.BLACK;

@RestController
@Slf4j
public class PreventiveMaintenancePdfResource extends BaseController {

    private final SopTypeService sopTypeService;
    private final SopCategoryService SOPCategoryService;
    private final DurationTypeService durationTypeService;
    private final PreventiveMaintenanceService preventiveMaintenanceService;
    private final EventMasterService eventMasterService;
    private final EventCompletionService eventCompletionService;

    @Autowired
    public PreventiveMaintenancePdfResource(SopTypeService sopTypeService, SopCategoryService SOPCategoryService, DurationTypeService durationTypeService, PreventiveMaintenanceService preventiveMaintenanceService, EventMasterService eventMasterService, EventCompletionService eventCompletionService) {
        this.sopTypeService = sopTypeService;
        this.SOPCategoryService = SOPCategoryService;
        this.durationTypeService = durationTypeService;
        this.preventiveMaintenanceService = preventiveMaintenanceService;
        this.eventMasterService = eventMasterService;
        this.eventCompletionService = eventCompletionService;
    }

    /*@Value("${file.uploadDir}")
    private String uploadDir;*/

    @PostMapping("/preventive-maintenance-pdf")
    public void preventiveMaintenancePdf(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody PreventiveMaintenance preventiveMaintenances, Principal principal) throws ServletException, IOException, DocumentException, com.itextpdf.text.DocumentException {
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        String yearString = dateFormat.format(today);
        long year = Long.parseLong(yearString);
        Optional<SopType> sopTypeOptional = sopTypeService.getSopTypeById(preventiveMaintenances.getSopType().getId());
        Optional<SopCategory> SOPCategoryOptional = SOPCategoryService.getSopCategory(preventiveMaintenances.getSopCategory().getId());
        SopCategory SOPCategory = SOPCategoryOptional.get();
        Optional<DurationType> durationTypeOptional = durationTypeService.getDurationById(preventiveMaintenances.getDurationType().getId());
        List<PreventiveMaintenance> preventiveMaintenanceList = preventiveMaintenanceService.getPreventiveMaintenanceByAssemblyLineByStagesByDurationTypeByDurationValue(sopTypeOptional.get().getId(), SOPCategory.getId(), durationTypeOptional.get().getId(), preventiveMaintenances.getDurationValue());
        PreventiveMaintenance preventiveMaintenance = new PreventiveMaintenance();
        for (PreventiveMaintenance preventiveMaintenanceObj : preventiveMaintenanceList) {
            preventiveMaintenance = preventiveMaintenanceObj;
        }
        OutputStream out = null;
        //String url = "/home/orileo/Enterprise/NKC/BackEnd/nkc-server/pdf/Preventive_Maintenance_"+ duration +"_"+year+".pdf";
        String pdfName = "Preventive_Maintenance" + "_" + sopTypeOptional.get().getId() + "_" + SOPCategory.getId() + "_" + year + ".pdf";
        Document document = new Document();
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= " + pdfName);
        PdfWriter.getInstance(document, response.getOutputStream());
        try {
            convertToPDF(document, response, preventiveMaintenance, year, principal, 0);
        } catch (Exception e) {
            throw new ServletException("Exception in Pdf download", e);
        } finally {
            if (out != null)
                out.close();
        }
        document.close();
    }

    private Document convertToPDF(Document document, HttpServletResponse response, PreventiveMaintenance preventiveMaintenances, long year, Principal principal, int index) throws IOException, WriteException, DocumentException, com.itextpdf.text.DocumentException {

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
        String sopType = "";
        String digitalSop = "";
        String durationType = "";
        String submitedBy = "";

        if (preventiveMaintenances.getSubmitedOn() != null) {
            checkListDate = sdf.format(preventiveMaintenances.getSubmitedOn());
        }
        sopType = preventiveMaintenances.getSopType().getSopTypeName();
        digitalSop = preventiveMaintenances.getSopCategory().getName();
        durationType = preventiveMaintenances.getDurationType().getDurationType();
        if (preventiveMaintenances.getIsAutoSubmit() != null) {
            if (preventiveMaintenances.getIsAutoSubmit().equals(true)) {
                submitedBy = "Auto Submited";
            } else {
                submitedBy = preventiveMaintenances.getSubmitedBy();
            }
        }

        PdfPTable pdfHeaderMaster = new PdfPTable(3);
        pdfHeaderMaster.setWidthPercentage(100f);
        pdfHeaderMaster.getDefaultCell().setBorder(0);
        pdfHeaderMaster.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfHeaderMaster.addCell(new Phrase("Date : " + checkListDate, tableHeader));
        pdfHeaderMaster.addCell(new Phrase("SOP Type : " + sopType, tableHeader));
        pdfHeaderMaster.addCell(new Phrase("Submited By : " + submitedBy, tableHeader));
        pdfHeaderMaster.addCell(new Phrase("Duration Type : " + durationType + " Wise", tableHeader));
        pdfHeaderMaster.addCell(new Phrase("SOP Category : " + digitalSop, tableHeader));
        pdfHeaderMaster.addCell(new Phrase(" "));
        para.add(pdfHeaderMaster);
        para.add(Chunk.NEWLINE);

        PdfPTable pdfHeaderBody = new PdfPTable(2);
        pdfHeaderBody.setWidthPercentage(100f);
        pdfHeaderBody.getDefaultCell().setBorder(0);
        pdfHeaderBody.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfHeaderBody.addCell(new Phrase("Check Points ", tableHeader));
        pdfHeaderBody.addCell(new Phrase("Remarks ", tableHeader));
        List<SopStepsAssigned> checkLists = preventiveMaintenances.getSopStepsAssigned();
        for (SopStepsAssigned checkListObj : checkLists) {
            pdfHeaderBody.addCell(new Phrase("" + checkListObj.getCheckPoint(), tableBody));
            pdfHeaderBody.addCell(new Phrase("" + checkListObj.getRemark(), tableBody));
        }
        para.add(pdfHeaderBody);
        para.add(Chunk.NEWLINE);

        document.add(para);

        return document;
    }

    @PostMapping("/event-completion-pdf")
    public void preventiveMaintenancePdf(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody EventCompletion eventCompletion) throws ServletException, IOException, DocumentException, com.itextpdf.text.DocumentException {
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        String yearString = dateFormat.format(today);
        long year = Long.parseLong(yearString);
        Optional<SopType> sopTypeOptional = sopTypeService.getSopTypeById(eventCompletion.getSopType().getId());
        Optional<SopCategory> SOPCategoryOptional = SOPCategoryService.getSopCategory(eventCompletion.getSopCategory().getId());
        SopCategory SOPCategory = SOPCategoryOptional.get();
        EventMaster eventMaster = eventMasterService.getEventById(eventCompletion.getEventMaster().getId());
        List<EventCompletion> eventCompletions = eventCompletionService.getEventCompletionBySopTypeByDigitalSopByEvent(sopTypeOptional.get().getId(), SOPCategory.getId(), eventMaster.getId());
        EventCompletion eventCompletionNew = new EventCompletion();
        for (EventCompletion eventCompletionObj : eventCompletions) {
            eventCompletionNew = eventCompletionObj;
        }
        OutputStream out = null;
        String pdfName = "Preventive_Maintenance" + "_" + sopTypeOptional.get().getId() + "_" + SOPCategory.getId() + "_" + eventMaster.getId() + "_" + year + ".pdf";
        Document document = new Document();
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= " + pdfName);
        PdfWriter.getInstance(document, response.getOutputStream());
        try {
            convertEventToPDF(document, response, eventCompletionNew, year, 0);
        } catch (Exception e) {
            throw new ServletException("Exception in Pdf download", e);
        } finally {
            if (out != null)
                out.close();
        }
        document.close();
    }

    private Document convertEventToPDF(Document document, HttpServletResponse response, EventCompletion eventCompletion, long year, int index) throws IOException, WriteException, DocumentException, com.itextpdf.text.DocumentException {

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
        String sopType = "";
        String digitalSop = "";
        String durationType = "";
        String submitedBy = "";

        if (eventCompletion.getSubmitedOn() != null) {
            checkListDate = sdf.format(eventCompletion.getSubmitedOn());
        }
        sopType = eventCompletion.getSopType().getSopTypeName();
        digitalSop = eventCompletion.getSopCategory().getName();

        if (eventCompletion.getSubmitedBy() != null) {
            submitedBy = eventCompletion.getSubmitedBy().getFirstName();
        }

        PdfPTable pdfHeaderMaster = new PdfPTable(3);
        pdfHeaderMaster.setWidthPercentage(100f);
        pdfHeaderMaster.getDefaultCell().setBorder(0);
        pdfHeaderMaster.setHorizontalAlignment(Element.ALIGN_LEFT);
        pdfHeaderMaster.addCell(new Phrase("Date : " + checkListDate, tableHeader));
        pdfHeaderMaster.addCell(new Phrase("SOP Type : " + sopType, tableHeader));
        pdfHeaderMaster.addCell(new Phrase("Submited By : " + submitedBy, tableHeader));
        pdfHeaderMaster.addCell(new Phrase("Duration Type : " + durationType + " Wise", tableHeader));
        pdfHeaderMaster.addCell(new Phrase("SOP Category: " + digitalSop, tableHeader));
        pdfHeaderMaster.addCell(new Phrase(" "));
        para.add(pdfHeaderMaster);
        para.add(Chunk.NEWLINE);

        PdfPTable pdfHeaderBody = new PdfPTable(2);
        pdfHeaderBody.setWidthPercentage(100f);
        pdfHeaderBody.getDefaultCell().setBorder(0);
        pdfHeaderBody.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfHeaderBody.addCell(new Phrase("Check Points ", tableHeader));
        pdfHeaderBody.addCell(new Phrase("Remarks ", tableHeader));
        List<EventWiseSopStepsAssigned> eventWiseSopStepsAssigneds = eventCompletion.getEventWiseSopStepsAssigneds();
        for (EventWiseSopStepsAssigned eventWiseSopStepsAssigned : eventWiseSopStepsAssigneds) {
            pdfHeaderBody.addCell(new Phrase("" + eventWiseSopStepsAssigned.getCheckPoint(), tableBody));
            pdfHeaderBody.addCell(new Phrase("" + eventWiseSopStepsAssigned.getRemark(), tableBody));
        }
        para.add(pdfHeaderBody);
        para.add(Chunk.NEWLINE);

        document.add(para);

        return document;
    }
}