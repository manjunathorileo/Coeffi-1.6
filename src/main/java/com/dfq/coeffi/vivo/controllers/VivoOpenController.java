package com.dfq.coeffi.vivo.controllers;

import com.dfq.coeffi.vivo.SimpleMessagingTemplate1;
import com.dfq.coeffi.vivo.entity.VivoInfo;
import com.dfq.coeffi.vivo.entity.VivoInfoDto;
import com.dfq.coeffi.vivo.service.VivoInfoService;
import com.github.sarxos.webcam.Webcam;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


@RestController
@Slf4j
public class VivoOpenController {

    @Autowired
    private VivoInfoService vivoInfoService;

    @PostMapping("capture/data")
    public ResponseEntity<VivoInfo> captureAndShowVehicleDetails(@RequestBody VivoInfoDto vivoInfoDto) {
        VivoInfo vivoInfo = new VivoInfo();
        vivoInfo.setVehicleNumber(vivoInfoDto.getVehicleNumber());
        Date date=new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
        String s = sdf.format(date);
        vivoInfo.setEntryTime(s);
        vivoInfo.setExtraTime(String.valueOf(0));
        vivoInfo.setWorkedHours(String.valueOf(0));
        vivoInfo.setStayTime(4);
        vivoInfo.setTypeOfVehicle(vivoInfoDto.getTypeOfVehicle());
        vivoInfo.setActive(true);
//        VivoInfo vivoInfo1 = vivoInfoService.save(vivoInfo);
//        this.template.convertAndSend("/topic/c", vivoInfo);
        System.out.println(("*******" + vivoInfo.getVehicleType()));
        return new ResponseEntity<>(vivoInfo, HttpStatus.CREATED);
    }

    @GetMapping("car")
    public String testTesseract() throws IOException {

        Webcam webcam = Webcam.getDefault();
        webcam.open();
        BufferedImage image = webcam.getImage();
        String imag = "img-" + new Date();
        ImageIO.write(image, "PNG", new File(imag + ".png"));
        webcam.close();

        Tesseract tesseract = new Tesseract();
        String str = null;
        try {
            tesseract.setDatapath("/home/orileo/orileo/COEFFI_BE/COEFFI_1.3_BE_MANJU/E:\\data\\photos");
            tesseract.setLanguage("eng");
            // the path of your tess data folder
            // inside the extracted file
            String text = tesseract.doOCR(new File(imag+ ".png"));

           str = StringEscapeUtils.unescapeJava(text);
            // path of your image file
            System.out.print(text);
            System.out.println(str);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return str;
    }


    @GetMapping("capture")
    public void test() throws IOException {
        Webcam webcam = Webcam.getDefault();
        webcam.open();
        BufferedImage image = webcam.getImage();
        String imag = "img-" + new Date();
        ImageIO.write(image, "PNG", new File(imag + ".png"));
        webcam.close();

    }
}
