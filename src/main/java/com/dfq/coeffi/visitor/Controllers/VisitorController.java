package com.dfq.coeffi.visitor.Controllers;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.policy.document.Document;
import com.dfq.coeffi.policy.document.FileStorageService;
import com.dfq.coeffi.superadmin.Entity.CompanyConfigure;
import com.dfq.coeffi.superadmin.Services.CompanyConfigureService;
import com.dfq.coeffi.util.DateUtil;
import com.dfq.coeffi.util.notification.messages.email.EmailConfig;
import com.dfq.coeffi.util.notification.messages.email.Mail;
import com.dfq.coeffi.util.notification.messages.email.MailService;
import com.dfq.coeffi.visitor.Entities.*;
import com.dfq.coeffi.visitor.Repositories.VisitorDocRepo;
import com.dfq.coeffi.visitor.Repositories.VisitorRepository;
import com.dfq.coeffi.visitor.Services.VisitorImageService;
import com.dfq.coeffi.visitor.Services.VisitorPassService;
import com.dfq.coeffi.visitor.Services.VisitorService;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDevice;
import com.github.sarxos.webcam.ds.dummy.WebcamDummyDevice;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bouncycastle.util.encoders.Base64;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.persistence.EntityNotFoundException;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Sides;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static jxl.format.Alignment.CENTRE;

@RestController
@EnableAutoConfiguration
@Slf4j
public class VisitorController extends BaseController {
    @Autowired
    VisitorService visitorService;
    @Autowired
    FileStorageService fileStorageService;
    @Autowired
    VisitorRepository visitorRepository;
    @Autowired
    MailService mailService;
    @Autowired
    VisitorDocRepo visitorDocumentRepository;
    @Autowired
    VisitorImageService visitorImageService;
    @Autowired
    VisitorPassService visitorPassService;
    @Autowired
    CompanyConfigureService companyConfigureService;

    //Save Details of Employee When Clicked On Save Button
    @PostMapping(value = "visitor/security-save")
    public ResponseEntity<Visitor> createVisitor(@RequestBody Visitor visitor) {
        Date date = new Date();
        visitor.setDateOfVisit(date);
        SimpleDateFormat sdfHr = new SimpleDateFormat("HH.mm");
        String checkInTime = sdfHr.format(date);
        visitor.setCheckInTime(checkInTime);
        Visitor persistedObject = visitorService.saveVisitor(visitor);
        if (persistedObject == null) {
            throw new EntityNotFoundException("no visitor is present");
        }
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    @GetMapping("visitor/get-all-visitor")
    public ResponseEntity<List<Visitor>> getAllVisitors() {
        List<Visitor> getVisitors = visitorService.getAll();
        return new ResponseEntity<>(getVisitors, HttpStatus.OK);
    }

    //Get details Of Visitor by Entering MobileNumber
    @GetMapping("visitor/security-get/{mobileNumber}")
    public ResponseEntity<Visitor> getVisitorByMobilrNumber(@PathVariable String mobileNumber) {
        Visitor getVisitor = visitorService.getVisitorByMobileNo(mobileNumber);
        Date date = new Date();
        SimpleDateFormat sdfHr = new SimpleDateFormat("HH.mm");
        String checkOutTime = sdfHr.format(date);
        getVisitor.setCheckOutTime(checkOutTime);
        return new ResponseEntity(getVisitor, HttpStatus.OK);
    }

    @GetMapping("visitor/checkin-time-for-multi-visitors/{mobileNumber}")
    public ResponseEntity<Visitor> setVisitorCheckInTimeForMulti(@PathVariable("mobileNumber") String mobileNumber) {
        Visitor visitorBymobNo = visitorService.getVisitorByMobileNoforMultivisit(mobileNumber);
        //  if (visitorBymobNo.getMobileNumber()==mobileNumber) {
        Date date = new Date();
        SimpleDateFormat sdfHr = new SimpleDateFormat("HH.mm");
        String checkInTime = sdfHr.format(date);
        visitorBymobNo.setCheckInTime(checkInTime);
        visitorService.saveVisitor(visitorBymobNo);
        // }
        return new ResponseEntity<>(visitorBymobNo, HttpStatus.OK);
    }

    //Save Details of Employee When Clicked On Save Button
    @PostMapping(value = "visitor/multivisit-save")
    public ResponseEntity<Visitor> saveVisitors(@RequestBody Visitor visitor) {
       /* Date date = new Date();
        SimpleDateFormat sdfHr = new SimpleDateFormat("HH.mm");
        String checkInTime = sdfHr.format(date);
        visitor.setCheckInTime(checkInTime);*/
        Visitor persistedObject = visitorService.saveVisitor(visitor);
        if (persistedObject == null) {
            throw new EntityNotFoundException("no visitor is present");
        }
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }


//    @GetMapping("visitor/visitors/{departmentId}")
//        public ResponseEntity<List<Visitor>> getVisitorsByDepartmentName(@PathVariable long departmentId)
//    {
//        List<Visitor> VisitorsBydeptId=visitorService.getVisitors(departmentId);
//        return new ResponseEntity<>(VisitorsBydeptId,HttpStatus.OK);
//    }*/


    //List of visitor with sme number
    //get details of pre regd visitor(CheckBox)
    @GetMapping("visitor/pre-registered-visitor/{mobileNumber}")
    public ResponseEntity<Visitor> getVisitorByMobileNumber(@PathVariable String mobileNumber) {
        Visitor visitorByMobNo = visitorService.getVisitorByMobileNo(mobileNumber);
        return new ResponseEntity(visitorByMobNo, HttpStatus.OK);
    }

    // CheckinTime will be set here for emp regd visitor (Generate/view pass button)
    @GetMapping("visitor/pre-reg-checkin-time-setting/{mobileNumber}")
    public ResponseEntity<Visitor> setVisitorCheckInTime(@PathVariable String mobileNumber) {
        Visitor visitorBymobNo = visitorService.getVisitorByMobileNo(mobileNumber);
        Date date = new Date();
        SimpleDateFormat sdfHr = new SimpleDateFormat("HH.mm");
        String checkInTime = sdfHr.format(date);
        visitorBymobNo.setCheckInTime(checkInTime);
        visitorService.saveVisitor(visitorBymobNo);
        return new ResponseEntity<>(visitorBymobNo, HttpStatus.OK);
    }

    @GetMapping("capture-image")
    public ResponseEntity imageCapture() throws IOException, InterruptedException {
        WebcamDevice webcamDevice = new WebcamDummyDevice(1);
        Webcam webcam = Webcam.getDefault();
        webcam.open();
        BufferedImage image = webcam.getImage();
        String imag = "img-" + new Date();
        ImageIO.write(image, "PNG", new File(imag + ".png"));
        saveVisitorDoc((MultipartFile) image);
        webcam.close();
        return new ResponseEntity<>(HttpStatus.OK);
    }

   /* @GetMapping("capture-image")
    public ResponseEntity imageCaptureds() throws IOException ,InterruptedException{
        WebcamDevice webcamDevice = new WebcamDummyDevice(1);
        webcamDevice.open();
        BufferedImage image = webcamDevice.getImage();
        String imag = "img-" + new Date();0
        .
        ImageIO.write(image, "PNG", new File(imag +".png"));
        webcamDevice.wait(2000);
        webcamDevice.close();
        return new ResponseEntity<>(HttpStatus.CREATED);
    }*/

    @GetMapping("find-connected-printer")
    public void uploadNoDues(@RequestParam("file") MultipartFile file) throws IOException, PrintException {
        FileInputStream textStream = new FileInputStream("test.png");
        DocFlavor myFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
        Doc myDoc = new SimpleDoc(textStream, myFormat, null);
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(new Copies(1));
        aset.add(Sides.ONE_SIDED);
        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
        System.out.println("Printing to default printer: " + printService.getName());
        DocPrintJob job = printService.createPrintJob();
        job.print(myDoc, aset);
    }

    @PostMapping("visitor/upload-document")
    public ResponseEntity<Document> uploadNoDue(@RequestParam("file") MultipartFile file) throws IOException {
        Document document = null;
        if (file.isEmpty()) {
            throw new FileNotFoundException();
        } else {
            document = fileStorageService.storeFile(file);
        }
        return new ResponseEntity<>(document, HttpStatus.OK);
    }

    //Employee pre registration for visitor form
    @PostMapping("visitor/visitors-pre-registration-by-employee")
    public ResponseEntity<Visitor> employeeRegistration(@RequestBody Visitor visitor) {

      /*  Date date = new Date();
        SimpleDateFormat sdfHr = new SimpleDateFormat("HH.mm");
        String checkInTime = sdfHr.format(date);
        visitor.setCheckInTime(checkInTime);*/
        visitor.setRegisteredBy("1");
        Visitor persistedObject = visitorService.saveVisitor(visitor);
        if (persistedObject == null) {
            throw new EntityNotFoundException("no visitor is present");
        }
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    //Visitor who registered through kiosk will be saved
    @PostMapping("visitor/visitor-by-kiosk-save")
    public ResponseEntity<Visitor> kioskVisitorSave(@RequestBody Visitor visitor) {
        Visitor persistedObject = visitorService.saveVisitor(visitor);
        visitor.setRegisteredBy("1");
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }


    //get kiosk registered visitor by mobNo
    @GetMapping("visitor/kiosk-registered-emp/{mobileNumber}")
    public ResponseEntity<Visitor> getKioskRegisteredEmpByMobileNumber(@PathVariable String mobileNumber) {
        Visitor visitorByMobNo = visitorService.getVisitorByMobileNo(mobileNumber);
        return new ResponseEntity(visitorByMobNo, HttpStatus.OK);
    }

    //setCheckinTime here
    @PostMapping("visitor/kiosk-registered-emp")
    public ResponseEntity<Visitor> getKioskEmpAndSetCheckIn(@PathVariable VisitorDto visitorDto) {
        Visitor visitorByMobNo = visitorService.getVisitorByMobileNo(visitorDto.getMobileNumber());
        visitorByMobNo.setTimeSlot(visitorDto.getTimeSlot());
        visitorByMobNo.setItemCarried(visitorDto.getItemCarried());
        return new ResponseEntity(visitorByMobNo, HttpStatus.OK);
    }

    //We have considered standard rate as 50/-
    @GetMapping("visitor/checkout/{mobile}")
    private Visitor calculateTotalStayTime(@PathVariable("mobile") String mobile) throws Exception {
        Visitor visitorByMobNo = visitorService.getVisitorByMobileNo(mobile);
        DateFormat timeFormat = new SimpleDateFormat("HH.mm");
        String checkInTime = (visitorByMobNo.getCheckInTime());
        Date date = new Date();
        SimpleDateFormat sdfHr = new SimpleDateFormat("HH.mm");
        String checkOutTime = sdfHr.format(date);
        long visitorInTime = timeFormat.parse(checkInTime).getTime();
        long visitorOutTime = timeFormat.parse(checkOutTime).getTime();
        visitorByMobNo.setCheckOutTime(checkOutTime);
        double stayedMinutes;
        double stayedMillis = visitorOutTime - visitorInTime;
        stayedMinutes = TimeUnit.MILLISECONDS.toMinutes((long) stayedMillis);
        double stayedHrs = stayedMinutes / 60;
        if (stayedHrs > visitorByMobNo.getTimeSlot()) {
            double extraHour = stayedHrs - visitorByMobNo.getTimeSlot();
            double payableAmount;
            long rate = 50;
            payableAmount = extraHour * rate;
            visitorByMobNo.setPaymentAmt((payableAmount));
            visitorByMobNo.setExtraTime(String.valueOf(extraHour));
        } else {
            visitorByMobNo.setPaymentAmt(0);
            visitorByMobNo.setExtraTime("0");
        }
        visitorService.saveVisitor(visitorByMobNo);
        return visitorByMobNo;
    }


    public ResponseEntity<VisitorDocument> saveVisitorDoc(@RequestParam("file") MultipartFile file) {
        VisitorDocument visitorDocument = visitorImageService.saveImage(file);
        if (visitorDocument == null) {
            throw new EntityNotFoundException("No image");
        }
        return new ResponseEntity<>(visitorDocument, HttpStatus.OK);
    }

    @PostMapping("Capture-and-save")
    public ResponseEntity<VisitorDocument> captureDoc(@RequestBody DocumentDto documentDto) {
        VisitorDocument visitorDocument = new VisitorDocument();
        visitorDocument.setData(documentDto.getData());
        visitorDocumentRepository.save(visitorDocument);
        return new ResponseEntity<>(visitorDocument, HttpStatus.OK);
    }


    @GetMapping("visitor/view-image/{number}")
    public ResponseEntity<DocumentDto> viewImage(@PathVariable("number") String number) throws IOException {
        Visitor visitor = visitorService.getVisitorByMobileNo(number);
        VisitorDocument visitorDocument = visitorDocumentRepository.findOne(visitor.getCaptureId());
        DocumentDto documentDto = new DocumentDto();
        documentDto.setData1(visitorDocument.getData());
        documentDto.setA(String.valueOf(visitorDocument.getData()));
        Resource r = new ByteArrayResource(visitorDocument.getData());
        System.out.println("** " + visitorDocument.getData());
        byte[] encoded = Base64.encode((visitorDocument.getData()));
        documentDto.setData(encoded);
        documentDto.setResource(r);
        return new ResponseEntity<>(documentDto, HttpStatus.OK);
    }

    /**
     * -Manjunath kuruba-
     *
     * @return
     */

    @GetMapping("visitor/barcode/{barcodeText}")
    public static BufferedImage generateEAN13BarcodeImage(@PathVariable String barcodeText) {
        EAN13Bean barcodeGenerator = new EAN13Bean();
        BitmapCanvasProvider canvas =
                new BitmapCanvasProvider(160, BufferedImage.TYPE_BYTE_BINARY, false, 0);

        barcodeGenerator.generateBarcode(canvas, barcodeText);
        return canvas.getBufferedImage();
    }

    @PostMapping("visitor/register")
    public void saveAndRegisterVisitor(@RequestBody VisitorPass visitorPass) throws Exception {
        VisitorPass visitorPass1 = visitorPassService.getByMobileNumber(visitorPass.getMobileNumber());
        if (visitorPass1 != null) {
            throw new Exception("Already registered");
        }
        visitorPassService.save(visitorPass);
    }

    @PostMapping("visitor/generate-pass")
    public ResponseEntity<VisitorPass> generatePass(@RequestBody VisitorDto visitorDto) {
        VisitorPass visitorPass = visitorPassService.getByMobileNumber(visitorDto.getMobileNumber());
        if (visitorPass != null) {
            visitorPass.setStartDate(visitorDto.getPassStartDate());
            visitorPass.setEndDate(visitorDto.getPassEndDate());
            visitorPass.setRfid(visitorDto.getRfid());
            visitorPass.setAllowedOrDenied(true);
            if (visitorPass.getEmail() != null) {
                sendEmai(visitorPass.getEmail(), "Visitor_Pass", "Your pass for the visit from: " + visitorPass.getStartDate() + " to: " + visitorPass.getEndDate() + " please carry copy of this email before you visit");
            }
            visitorPassService.save(visitorPass);
        }
        return new ResponseEntity<>(visitorPass, HttpStatus.OK);
    }

    public void sendEmai(String email, String title, String content) {
        Date todayDate = new Date();
        EmailConfig emailConfignew = new EmailConfig();
        String emailTitle = title + " on " + todayDate;
        Mail mailnew = emailConfignew.setMailCredentials(email, emailTitle, content, null);
        mailService.sendEmail(mailnew, "****");
    }

    @PostMapping("visitor/check-in-visitor")
    public ResponseEntity<Visitor> checkInVisitor(@RequestBody VisitorDto visitorDto) {
        VisitorPass visitorPass = visitorPassService.getByMobileNumber(visitorDto.getMobileNumber());
        Date d = new Date();

        Visitor visitorBymobNo = visitorService.getByMobileNumberAndDate(d, visitorDto.getMobileNumber());
        if (d.after(visitorPass.getEndDate()) || d.before(visitorPass.getStartDate())) {
            throw new EntityNotFoundException("No valid pass for today");
        }
        if (visitorBymobNo != null) {
            throw new EntityNotFoundException("Already checked in");
        } else {
            Visitor visitor = new Visitor();
            SimpleDateFormat sdfHr = new SimpleDateFormat("HH.mm");
            String checkInTime = sdfHr.format(d);
            visitor.setLoggedOn(d);
            visitor.setCheckInTime(checkInTime);
            visitor.setMobileNumber(visitorDto.getMobileNumber());
            visitor.setFirstName(visitorPass.getFirstName());
            visitor.setEmail(visitorPass.getEmail());
            visitor.setVisitType(visitorPass.getVisitType());
            visitor.setPersonToVisit(visitorPass.getPersonToVisit());
            visitor.setEntryBodyTemperature(visitorDto.getEntryBodyTemperature());
            visitor.setMaskWearing(visitorDto.isMaskWearing());
            visitor.setTimeSlot(visitorDto.getTimeSlot());
            visitor.setImgId(visitorDto.getImgId());
            visitor.setEntryGateNumber(visitorDto.getEntryGateNumber());
            visitor.setVisitorPass(visitorPass);
            visitorService.saveVisitor(visitor);
            return new ResponseEntity<>(visitorBymobNo, HttpStatus.OK);
        }
    }

    @PostMapping("visitor/check-out-visitor")
    public ResponseEntity<Visitor> checkOutVisitor(@RequestBody VisitorDto visitorDto) {
        VisitorPass visitorPass = visitorPassService.getByMobileNumber(visitorDto.getMobileNumber());
        Date d = new Date();
        Visitor visitorBymobNo = visitorService.getByMobileNumberAndDate(d, visitorDto.getMobileNumber());
        if (visitorBymobNo == null) {
            throw new NullPointerException("Check-In first");
        }
        if (visitorBymobNo != null && visitorBymobNo.getCheckOutTime() == null) {
            Visitor visitor = visitorBymobNo;
            SimpleDateFormat sdfHr = new SimpleDateFormat("HH.mm");
            String checkInTime = sdfHr.format(d);
            visitor.setCheckOutTime(checkInTime);
            visitor.setFirstName(visitorPass.getFirstName());
            visitor.setEmail(visitorPass.getEmail());
            visitor.setVisitType(visitorPass.getVisitType());
            visitor.setPersonToVisit(visitorPass.getPersonToVisit());
            visitor.setExitBodyTemperature(visitorDto.getExitBodyTemperature());
            visitor.setMaskWearing(visitorDto.isMaskWearing());
            visitor.setExitGateNumber(visitorDto.getExitGateNumber());
            visitor.setVisitorPass(visitorPass);
            visitorService.saveVisitor(visitor);
        } else {
            throw new EntityNotFoundException("Checked Already first");
        }
        return new ResponseEntity<>(visitorBymobNo, HttpStatus.OK);
    }

    @GetMapping("visitor/view-registered/{mobileNumber}")
    public ResponseEntity<VisitorPass> viewTodaysVisitedDetails(@PathVariable String mobileNumber) {
        CompanyConfigure companyConfigure = null;
        VisitorPass visitorPass = visitorPassService.getByMobileNumber(mobileNumber);
        List<CompanyConfigure> companyConfigures = companyConfigureService.getCompany();
        if (!companyConfigures.isEmpty()) {
            companyConfigure = companyConfigures.get(0);
            if (companyConfigure.isDenialList()) {
                if (visitorPass.isAllowedOrDenied() == false) {
                    throw new EntityNotFoundException("Denied user not allowed");
                }
            }
        } else {
            throw new EntityNotFoundException("Configuration in super admin not done");
        }


        Visitor visitorAtt = visitorService.getByMobileNumberAndDate(new Date(), mobileNumber);
        if (visitorAtt != null) {
            if (visitorAtt.getCheckInTime() != null) {
                visitorPass.setCheckInTime(visitorAtt.getCheckInTime());
            }
            if (visitorAtt.getCheckOutTime() != null) {
                visitorPass.setCheckOutTime(visitorAtt.getCheckOutTime());
            }
            if (visitorAtt.getTimeSlot() != 0) {
                visitorPass.setTimeSlot(visitorAtt.getTimeSlot());
            }
            visitorAtt.setVisitorPass(visitorPass);
            visitorPass.setTimeSlot(visitorAtt.getTimeSlot());
            visitorPass.setEntryGateNumber(visitorAtt.getEntryGateNumber());
            visitorPass.setExitGateNumber(visitorAtt.getExitGateNumber());
            visitorPass.setEntryBodyTemperature(visitorAtt.getEntryBodyTemperature());
            visitorPass.setExitBodyTemperature(visitorAtt.getExitBodyTemperature());
            visitorPass.setMaskWearing(visitorAtt.isMaskWearing());
        }
        return new ResponseEntity<>(visitorPass, HttpStatus.OK);
    }

    @GetMapping("visitor/view-all-registred")
    public ResponseEntity<List<VisitorPass>> viewVisitorsList() {
        List<VisitorPass> visitorPassList = visitorPassService.getAllVisitors();
        return new ResponseEntity<>(visitorPassList, HttpStatus.OK);
    }

    @GetMapping("visitor/in-out-today")
    public ResponseEntity<List<Visitor>> viewToday() {
        List<Visitor> visitorAtt = visitorService.getByMobileDate(new Date());

        return new ResponseEntity(visitorAtt, HttpStatus.OK);
    }


    @PostMapping("visitor-denail/{id}")
    public ResponseEntity<VisitorPass> allowOrDenyCompany(@PathVariable long id) {
        VisitorPass visitorPass = visitorPassService.getVisitor(id);
        if (visitorPass.isAllowedOrDenied()) {
            visitorPass.setAllowedOrDenied(false);
            visitorPassService.save(visitorPass);
        } else if (visitorPass.isAllowedOrDenied() == false) {
            visitorPass.setAllowedOrDenied(true);
            visitorPassService.save(visitorPass);
        }
        return new ResponseEntity<>(visitorPass, HttpStatus.OK);
    }

    /**
     * Arun
     */

    public ResponseEntity<Visitor> securityScreenView() throws ParseException {
        Visitor visitor = null;
        List<Visitor> visitorList = visitorService.getAll();
        if (!visitorList.isEmpty()) {
            Collections.reverse(visitorList);
            Date timeNow = new Date();
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH.mm");
            if (visitorList.get(0).getCheckInTime() != null) {
                String inTime = visitorList.get(0).getCheckInTime();
                long time = timeFormat.parse(inTime).getTime();

                long seconds = (timeNow.getTime() - time) / 1000;
                if (seconds < 60) {
                    visitor = visitorList.get(0);
                }
            }
            if (visitorList.get(0).getCheckOutTime() != null) {
                String outTime = visitorList.get(0).getCheckOutTime();
                long out = timeFormat.parse(outTime).getTime();
                long secondsOut = (timeNow.getTime() - out) / 1000;
                if (secondsOut < 60) {
                    visitor = visitorList.get(0);
                }
            }
        }
        return new ResponseEntity(visitor, HttpStatus.OK);
    }

    @GetMapping("visitor/security-screen")
    public ResponseEntity<Visitor> getVivoLastInfo() {
        List<Visitor> vivoInfo = visitorService.getAll();
        Visitor v = new Visitor();
        if (vivoInfo.isEmpty()) {
            return new ResponseEntity(v, HttpStatus.OK);
        }
        Collections.reverse(vivoInfo);
        Visitor vivoInfo1 = vivoInfo.get(0);
        if (vivoInfo1.getVisitorPass() == null) {
            VisitorPass visitorPass = new VisitorPass();
            visitorPass.setFirstName(vivoInfo1.getFirstName());
            vivoInfo1.setVisitorPass(visitorPass);
        }

//        if (vivoInfo1.getShoww() <= 35) {
//            vivoInfo1.setShoww(vivoInfo1.getShoww() + 1);
//            visitorService.saveVisitor(vivoInfo1);
//            return new ResponseEntity<>(vivoInfo1, HttpStatus.OK);
//        }
        return new ResponseEntity(vivoInfo1, HttpStatus.OK);
    }


    /**
     * Visitor Mobile check-in and check-out
     *
     * @param visitorDto
     * @return
     */

    @PostMapping("visitor-mobile/check-in-visitor")
    public ResponseEntity<Visitor> checkInVisitorUsingMobile(@RequestBody VisitorDto visitorDto) {
        VisitorPass visitorPass = visitorPassService.getByMobileNumber(visitorDto.getMobileNumber());
        Date d = new Date();
        Visitor visitorBymobNo = visitorService.getByMobileNumberAndDate(d, visitorDto.getMobileNumber());
        if (d.after(visitorPass.getEndDate()) || d.before(visitorPass.getStartDate())) {
            throw new EntityNotFoundException("No valid pass for today");
        }
        if (visitorBymobNo != null) {
            throw new EntityNotFoundException("Already checked in");
        } else {
            Visitor visitor = new Visitor();
            SimpleDateFormat sdfHr = new SimpleDateFormat("HH.mm");
            String checkInTime = sdfHr.format(d);
            visitor.setLoggedOn(d);
            visitor.setCheckInTime(checkInTime);
            visitor.setMobileNumber(visitorDto.getMobileNumber());
            visitor.setFirstName(visitorPass.getFirstName());
            visitor.setEmail(visitorPass.getEmail());
            visitor.setVisitType(visitorPass.getVisitType());
            visitor.setPersonToVisit(visitorPass.getPersonToVisit());
            visitor.setEntryBodyTemperature(visitorDto.getEntryBodyTemperature());
            visitor.setMaskWearing(visitorDto.isMaskWearing());
            visitor.setTimeSlot(visitorDto.getTimeSlot());
            visitor.setEntryGateNumber(visitorDto.getEntryGateNumber());
            visitor.setVisitorPass(visitorPass);
            visitorService.saveVisitor(visitor);
            return new ResponseEntity<>(visitorBymobNo, HttpStatus.OK);
        }
    }


    @PostMapping("visitor-mobile/check-out-visitor")
    public ResponseEntity<Visitor> checkOutVisitorUsingMobile(@RequestBody VisitorDto visitorDto) {
        VisitorPass visitorPass = visitorPassService.getByMobileNumber(visitorDto.getMobileNumber());
        Date d = new Date();
        Visitor visitorBymobNo = visitorService.getByMobileNumberAndDate(d, visitorDto.getMobileNumber());
        if (visitorBymobNo == null) {
            throw new NullPointerException("Check-In first");
        }
        if (visitorBymobNo != null && visitorBymobNo.getCheckOutTime() == null) {
            Visitor visitor = visitorBymobNo;
            SimpleDateFormat sdfHr = new SimpleDateFormat("HH.mm");
            String checkOutTime = sdfHr.format(d);
            visitor.setCheckOutTime(checkOutTime);
            visitor.setFirstName(visitorPass.getFirstName());
            visitor.setEmail(visitorPass.getEmail());
            visitor.setVisitType(visitorPass.getVisitType());
            visitor.setPersonToVisit(visitorPass.getPersonToVisit());
            visitor.setExitBodyTemperature(visitorDto.getExitBodyTemperature());
            visitor.setMaskWearing(visitorDto.isMaskWearing());
            visitor.setExitGateNumber(visitorDto.getExitGateNumber());
            visitor.setVisitorPass(visitorPass);
            visitorService.saveVisitor(visitor);
        } else {
            throw new EntityNotFoundException("Checked Already first");
        }
        return new ResponseEntity<>(visitorBymobNo, HttpStatus.OK);
    }

    @GetMapping("visitor/check-duplicate-mobile/{mobileNumber}")
    public void checkMobileNumberRegisteredOrNot(@PathVariable String mobileNumber) {
        VisitorPass visitorPass = visitorPassService.getByMobileNumber(mobileNumber);
        if (visitorPass != null) {
            throw new EntityNotFoundException("Mobile number already registered,Enter another number");
        }
    }

    @GetMapping("visitor/pass/{id}")
    public ResponseEntity<VisitorPass> getVisitorPass(@PathVariable long id) {
        VisitorPass visitorPass = new VisitorPass();
        return new ResponseEntity<>(visitorPass, HttpStatus.OK);
    }


    /**
     * BULK UPLOAD
     *
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("indian-visitor-bulk-upload")
    public ResponseEntity<List<VisitorPass>> indianVisitorBulkUpload(@RequestParam("file") MultipartFile file) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());

        XSSFSheet sheet = wb.getSheetAt(0);
        List<VisitorPass> vivoPassList = new ArrayList<>();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            VisitorPass visitorPass = new VisitorPass();
            XSSFRow row = sheet.getRow(i);
            VisitorPass visitorPassCheck = visitorPassService.getByMobileNumber(row.getCell(0).getStringCellValue());
            if (visitorPassCheck == null) {
                visitorPass.setMobileNumber(row.getCell(0).getStringCellValue());
                visitorPass.setFirstName(row.getCell(1).getStringCellValue());
                visitorPass.setRfid(row.getCell(2).getStringCellValue());
                visitorPass.setDepartmentName(row.getCell(3).getStringCellValue());
                visitorPass.setVisitType(row.getCell(4).getStringCellValue());
                visitorPass.setCompanyName(row.getCell(5).getStringCellValue());
                visitorPass.setVisitorType("INDIAN");
                visitorPassService.save(visitorPass);
                vivoPassList.add(visitorPass);
            }else {
                visitorPass = visitorPassCheck;
                visitorPass.setMobileNumber(row.getCell(0).getStringCellValue());
                visitorPass.setFirstName(row.getCell(1).getStringCellValue());
                visitorPass.setRfid(row.getCell(2).getStringCellValue());
                visitorPass.setDepartmentName(row.getCell(3).getStringCellValue());
                visitorPass.setVisitType(row.getCell(4).getStringCellValue());
                visitorPass.setCompanyName(row.getCell(5).getStringCellValue());
                visitorPass.setVisitorType("INDIAN");
                visitorPassService.save(visitorPass);
                vivoPassList.add(visitorPass);
            }

        }
        return new ResponseEntity<>(vivoPassList, HttpStatus.OK);
    }

    /**
     * BULK UPLOAD
     *
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("foreign-visitor-bulk-upload")
    public ResponseEntity<List<VisitorPass>> foreignVisitorBulkUpload(@RequestParam("file") MultipartFile file) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = wb.getSheetAt(0);
        List<VisitorPass> vivoPassList = new ArrayList<>();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            VisitorPass visitorPass = new VisitorPass();
            XSSFRow row = sheet.getRow(i);
            visitorPass.setFirstName(row.getCell(1).getStringCellValue());
            visitorPass.setVisitorOrganization(row.getCell(2).getStringCellValue());
            visitorPass.setMobileNumber(row.getCell(3).getStringCellValue());
            visitorPass.setEmail(row.getCell(4).getStringCellValue());
            visitorPass.setPassPortNumber(row.getCell(5).getStringCellValue());
            visitorPass.setValidTill(row.getCell(6).getStringCellValue());
            visitorPass.setVisaNumber(row.getCell(7).getStringCellValue());
            visitorPass.setVisaValidTill(row.getCell(8).getStringCellValue());
            visitorPass.setVisitorType("FOREIGN");
            visitorPassService.save(visitorPass);
            vivoPassList.add(visitorPass);
        }
        return new ResponseEntity<>(vivoPassList, HttpStatus.OK);
    }


    /**
     * TEMPLATE
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @GetMapping("indian-visitor/template-download")
    private void createVisitorDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Indian_Visitor.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeIndianVistorToSheet(workbook, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }


    private WritableWorkbook writeIndianVistorToSheet(WritableWorkbook workbook, HttpServletResponse response, int index) throws IOException, WriteException {
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
        s.addCell(new Label(0, 0, "#", headerFormat));
        s.addCell(new Label(1, 0, "Name", headerFormat));
        s.addCell(new Label(2, 0, "Visitor Organization", headerFormat));
        s.addCell(new Label(3, 0, "Mobile Number", headerFormat));
        s.addCell(new Label(4, 0, "Email", headerFormat));
        s.addCell(new Label(5, 0, "Id Proof Number", headerFormat));


        return workbook;
    }


    /**
     * TEMPLATE
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @GetMapping("Foreign-visitor/template-download")
    private void createForeignVisitorDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Foreign_Visitor.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeForeignVistorToSheet(workbook, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }


    private WritableWorkbook writeForeignVistorToSheet(WritableWorkbook workbook, HttpServletResponse response, int index) throws IOException, WriteException {
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

        s.addCell(new Label(0, 0, "#", headerFormat));
        s.addCell(new Label(1, 0, "Name", headerFormat));
        s.addCell(new Label(2, 0, "Visitor Organization", headerFormat));
        s.addCell(new Label(3, 0, "Mobile Number", headerFormat));
        s.addCell(new Label(4, 0, "Email", headerFormat));
        s.addCell(new Label(5, 0, "Passport No", headerFormat));
        s.addCell(new Label(6, 0, "Passport Validity", headerFormat));
        s.addCell(new Label(7, 0, "VISA Number", headerFormat));
        s.addCell(new Label(8, 0, "VISA Validity", headerFormat));
        return workbook;
    }


    @DeleteMapping("visitor-pass/{id}")
    public void deleteVisitor(@PathVariable long id) {
        visitorPassService.deleteVisitor(id);
    }

    /**
     * Date wise view
     *
     * @param dateDto
     * @return
     */
    @PostMapping("visitor/in-out-today")
    public ResponseEntity<List<Visitor>> viewToday(@RequestBody DateDto dateDto) {
        List<Visitor> visitorAtt = visitorService.getByMobileDate(dateDto.startDate);
        return new ResponseEntity(visitorAtt, HttpStatus.OK);
    }

    /**
     * Direct checkout
     *
     * @param id
     * @return
     */
    @PostMapping("visitor/check-out-visitor/{id}")
    public ResponseEntity<Visitor> checkOutVisitor(@PathVariable long id) {
        Date d = new Date();
        Visitor visitorBymobNo = visitorRepository.findOne(id);
        VisitorPass visitorPass = visitorPassService.getByMobileNumber(visitorBymobNo.getMobileNumber());
        if (visitorBymobNo == null) {
            throw new NullPointerException("Check-In first");
        }
        if (visitorBymobNo != null && visitorBymobNo.getCheckOutTime() == null) {
            Visitor visitor = visitorBymobNo;
            SimpleDateFormat sdfHr = new SimpleDateFormat("HH.mm");
            String checkInTime = sdfHr.format(d);
            visitor.setCheckOutTime(checkInTime);
            if (visitorPass != null) {
                visitor.setFirstName(visitorPass.getFirstName());
                visitor.setEmail(visitorPass.getEmail());
                visitor.setVisitType(visitorPass.getVisitType());
                visitor.setPersonToVisit(visitorPass.getPersonToVisit());
                visitor.setVisitorPass(visitorPass);
            }
            visitorService.saveVisitor(visitor);
        } else {
            throw new EntityNotFoundException("Checked Already first");
        }
        return new ResponseEntity<>(visitorBymobNo, HttpStatus.OK);
    }


}