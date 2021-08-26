package com.dfq.coeffi.SOPDetails.adherence;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jxl.write.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static jxl.format.Alignment.CENTRE;

@RestController
public class AdherenceController {

    @Autowired
    AdherenceService adherenceService;

    /**
     * save user adherence object to database
     *
     * @param : adherence object
     * @return adherence object
     */

    @ApiOperation(value = "save adherence", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully saved adherence"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @PostMapping("User/Adherence/Save")
    public ResponseEntity<Adherence> saveAdherence(@RequestBody Adherence adherence) {

        Adherence adherence1 = adherenceService.saveAdherence(adherence);
        return new ResponseEntity<>(adherence1, HttpStatus.OK);
    }

    /**
     * get all adherence of user from database
     *
     * @return all adherences from database
     */

    @ApiOperation(value = "get all adherence", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully get adherence"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @GetMapping("Adherence/all")
    public ResponseEntity<List<Adherence>> getAdherence() {
        List<Adherence> adherence2 = adherenceService.getAdherence();
        return new ResponseEntity<>(adherence2, HttpStatus.OK);
    }

    /**
     * get adherence of user by id from database
     *
     * @param : adherence id
     * @return adherence attached to id
     */

    @ApiOperation(value = "get adherence from database by adherence id", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrived adherence"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @GetMapping("Adherence/{id}")
    public ResponseEntity<Optional<Adherence>> getAdherenceById(@PathVariable long id) {
        Optional<Adherence> adherence3 = Optional.ofNullable(adherenceService.getAdherenceById(id));
        return new ResponseEntity<>(adherence3, HttpStatus.OK);
    }

    /**
     * delete adherence of user by id form database
     *
     * @param : adherence id
     */

    @ApiOperation(value = "delete adherence from database by adherence id", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted adherence"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @DeleteMapping("Adherence/{id}")
    public void deleteAdherenceByid(@PathVariable long id) {
        adherenceService.deleteAdherenceByid(id);
    }

    //user complied reports by user id

    /**
     * get all user complied reports by user if from database
     *
     * @param : userid
     * @return adherence attached to userid
     */

    @ApiOperation(value = "get adherence from database by user id", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrived adherence"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @GetMapping("User/Reports/View/{uid}")
    public ResponseEntity<List<Adherence>> getByUserId(@PathVariable("uid") long uid) {
        List<Adherence> a = adherenceService.getByUserId(uid);
        return new ResponseEntity<>(a, HttpStatus.OK);

    }

    //Admin reports by soplist and userid

    /**
     * Admin get reports by sopid and userid from database
     *
     * @param : digital sop id
     * @param : user id
     * @return adherence attached to digital sop id and user id
     */

    @ApiOperation(value = "get adherence from database by digital sop id and user id", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrived adherence"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @GetMapping("Admin/Reports/View/{did}/{uid}")
    public ResponseEntity<List<Adherence>> getbyDigitalSopIdAndUserId(@PathVariable("did") long did, @PathVariable("uid") long uid) {
        List<Adherence> m = adherenceService.getbyDigitalSopIdAndUserId(did, uid);
        return new ResponseEntity<>(m, HttpStatus.OK);

    }

    @PostMapping("admin/reports/filter")
    public ResponseEntity<List<Adherence>> getByFilter(@RequestBody AdherenceDto adherenceDto) {
        System.out.println("Start Date: " + adherenceDto.getStartDate() + " " + adherenceDto.getEndDate());
        List<Adherence> m = adherenceService.getByFilter(adherenceDto.getSopId(), convertDateToFormat(adherenceDto.getStartDate()), convertDateToFormat(adherenceDto.getEndDate()), adherenceDto.getUserId());
        return new ResponseEntity<>(m, HttpStatus.OK);

    }

    //**********************************************************************


    //*******************************************************************************8

    private SimpleDateFormat dformat = new SimpleDateFormat();

    public Date convertDateToFormat(Date date) {
        try {
            date = dformat.parse(dformat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

//    @PostMapping("reports/download/filter")
//    //@RequestMapping(value = "reports/download/filter", method = RequestMethod.POST, headers = {"content-type=multipart/mixed","content-type=multipart/form-data"})
//    private ResponseEntity<List<Adherence>> createExcel(@RequestBody AdherenceDto adherenceDto,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
//    {
//
//        List<Adherence> adherenceList6 = adherenceService.getByFilter(adherenceDto.getSopId(), convertDateToFormat(adherenceDto.getStartDate()), convertDateToFormat(adherenceDto.getEndDate()), adherenceDto.getUserId());
//
//        OutputStream out = null;
//        response.setContentType("application/vnd.ms-excel");
//        response.setHeader("Content-Disposition", "attachment; filename= Reports.xlsx");
//        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
//        try
//        {
//            writeToSheets(workbook, adherenceList6, response, 0);
//            workbook.write();
//            workbook.close();
//        }
//        catch (Exception e)
//        {
//            throw new ServletException("Exception in Excel download", e);
//        }
//        finally
//        {
//            if (out != null)
//                out.close();
//        }
//        return new ResponseEntity<>(adherenceList6,HttpStatus.OK);
//    }
//
//    private WritableWorkbook writeToSheets(WritableWorkbook workbook, List<Adherence> adherences, HttpServletResponse response, int index) throws IOException, WriteException
//    {
//        WritableSheet s = workbook.createSheet("Data Input", index);
//        s.getSettings().setPrintGridLines(false);
//
//        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
//        headerFont.setBoldStyle(WritableFont.BOLD);
//        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
//        headerFormat.setAlignment(CENTRE);
//        headerFormat.setBackground(Colour.GRAY_25);
//
//        s.setColumnView(0, 10);
//        s.setColumnView(1, 20);
//        s.setColumnView(2, 20);
//        s.setColumnView(3, 20);
//        s.setColumnView(4, 20);
//        s.setColumnView(5, 20);
//        s.setColumnView(6, 10);
//        s.setColumnView(7, 15);
//        s.setColumnView(8, 15);
//        s.addCell(new Label(0, 0, "Digital SOP ID", headerFormat));
//        s.addCell(new Label(1, 0, "Digital SOP Name", headerFormat));
//        s.addCell(new Label(2, 0, "Date", headerFormat));
//        s.addCell(new Label(3, 0, "Remarks", headerFormat));
//
//
//        int rownum = 1;
//        for (Adherence usr : adherences) {
//            s.addCell(new Label(0, rownum, "" + usr.getDigitalSopId()));
//            s.addCell(new Label(1, rownum, "" + usr.getSopName()));
//            s.addCell(new Label(2, rownum, "" + usr.getDate()));
//            s.addCell(new Label(3, rownum, "" + usr.getRemarks()));
//
//
//            rownum++;
//        }
//        return workbook;
//    }

    @ApiOperation(value = "get adherence from database", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrived adherence"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @PostMapping("reports/download/all")
    private void createCustomersDetails(@RequestBody AdherenceDto adherenceDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Adherence> adherenceList2 = adherenceService.getByFilter(adherenceDto.getSopId(), convertDateToFormat(adherenceDto.getStartDate()), convertDateToFormat(adherenceDto.getEndDate()), adherenceDto.getUserId());
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Reports.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet(workbook, adherenceList2, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    /**
     * admin reports download to as excel file
     *
     * @param workbook
     * @param adherences
     * @param response
     * @param index
     * @return excel file
     * @throws IOException
     * @throws WriteException
     */

    @ApiOperation(value = "admin download reports as excel", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully downloaded report"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    private WritableWorkbook writeToSheet(WritableWorkbook workbook, List<Adherence> adherences, HttpServletResponse response, int index) throws IOException, WriteException {
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
        s.addCell(new Label(0, 0, "Digital SOP ID", headerFormat));
        s.addCell(new Label(1, 0, "Digital SOP Name", headerFormat));
        s.addCell(new Label(2, 0, "Date", headerFormat));
        s.addCell(new Label(3, 0, "Remarks", headerFormat));


        int rownum = 1;
        for (Adherence usr : adherences) {
            s.addCell(new Label(0, rownum, "" + usr.getDigitalSopId()));
            s.addCell(new Label(1, rownum, "" + usr.getSopName()));
            s.addCell(new Label(2, rownum, "" + usr.getDate()));
            s.addCell(new Label(3, rownum, "" + usr.getRemarks()));


            rownum++;
        }
        return workbook;
    }

}
