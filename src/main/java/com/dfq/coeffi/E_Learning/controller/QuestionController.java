package com.dfq.coeffi.E_Learning.controller;


import com.dfq.coeffi.E_Learning.modules.*;
import com.dfq.coeffi.E_Learning.repository.TestMasterRepository;
import com.dfq.coeffi.E_Learning.service.DocumentUploadService;
import com.dfq.coeffi.E_Learning.service.ProductService;
import com.dfq.coeffi.E_Learning.service.QuestionService;
import com.dfq.coeffi.E_Learning.service.TestMasterService;
import com.dfq.coeffi.controller.BaseController;
import com.google.common.annotations.GwtCompatible;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
public class QuestionController extends BaseController {

    @Autowired
    private QuestionService questionservice;
    @Autowired
    TestMasterService testMasterService;
    @Autowired
    TestMasterRepository testMasterRepository;
    @Autowired
    ProductService productService;
    @Autowired
    DocumentUploadService documentUploadService;

    @PostMapping("/question")
    public ResponseEntity<Question> createQuestion(@RequestBody Question question) {
        Question persistedObject = questionservice.saveUpdateQuestion(question);
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getQuestions() {
        List<Question> getQuestion = questionservice.getQuestions();
        return new ResponseEntity<>(getQuestion, HttpStatus.OK);
    }

    @GetMapping("/question-productId/{productId}")
    public List<Question> getQuestionByProductId(@PathVariable long productId) {
        return questionservice.getQuestionByProductId(productId);
    }

    @DeleteMapping("/question/{id}")
    public void deleteById(@PathVariable long id) {
        questionservice.deleteById(id);
    }

    @GetMapping("/shuffled-question/{productId}")
    public ResponseEntity<List<Question>> getShuffledQuestions(@PathVariable long productId) {
        List<Question> questions = questionservice.getQuestionByProductId(productId);
        Collections.shuffle(questions);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    @PostMapping("question/create-test/{productId}")
    public ResponseEntity<TestMaster> createTest(@RequestBody TestMaster testMaster, @PathVariable long productId) throws Exception {
        List<Question> questions = questionservice.getQuestionByProductId(productId);
        if (questions.isEmpty()) {
            throw new Exception("Add questions for this Product");
        }
        List<Question> questionsLevel = new ArrayList<>();
        for (Question question : questions) {
            if (question.getLevel() == testMaster.getTestLevel()) {
                questionsLevel.add(question);
            }
        }
        if (questionsLevel.isEmpty()) {
            throw new Exception("Add questions for this level");
        }
        TestMaster testMaster1 = testMasterService.getTestMasterById(testMaster.getId());
        if (testMaster1 == null) {
            TestMaster testMaster2 = new TestMaster();
            Optional<Product> product = productService.getProductById(productId);
            testMaster2.setProduct(product.get());
            testMaster2.setNumberOfQuestions(testMaster.getNumberOfQuestions());
            testMaster2.setTimingInMinutes(testMaster.getTimingInMinutes());
            testMaster2.setTestLevel(testMaster.getTestLevel());
            testMasterRepository.save(testMaster2);
        } else {
            Optional<Product> product = productService.getProductById(productId);
            testMaster1.setProduct(product.get());
            testMaster1.setNumberOfQuestions(testMaster.getNumberOfQuestions());
            testMaster1.setTimingInMinutes(testMaster.getTimingInMinutes());
            testMaster1.setTestLevel(testMaster.getTestLevel());
            testMasterRepository.save(testMaster1);
        }
        return new ResponseEntity<>(testMaster, HttpStatus.CREATED);
    }

    @GetMapping("/shuffled-question-20/{productId}/{noOfQuestions}/{level}")
    public ResponseEntity<List<Question>> get20ShuffledQuestions(@PathVariable long noOfQuestions, @PathVariable long productId, @PathVariable long level) {
        List<Question> questions = questionservice.getQuestionByProductId(productId);
        List<Question> questionsLevel = new ArrayList<>();
        for (Question question : questions) {
            if (question.getLevel() == level) {
                questionsLevel.add(question);
            }

        }
        Collections.shuffle(questionsLevel);
        List<Question> limitedQuestions = questionsLevel.subList(0, (int) noOfQuestions);

        return new ResponseEntity<>(limitedQuestions, HttpStatus.OK);
    }

    @PostMapping("/question-import/{pid}/{title}/{level}")
    public ResponseEntity<List<QuestionDto>> importQuestion(@RequestParam("file") MultipartFile file, @PathVariable("pid") long pid, @PathVariable("title") String title, @PathVariable("level") long description) {
        List<QuestionDto> dto = QuestionImport.questionImport(file, pid, title, description);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @GetMapping("dd")
    public void check(long id) throws Exception {
        DocumentUpload documentUpload = documentUploadService.getFile(1);
        final BufferedImage image = ImageIO.read(new URL(documentUpload.getUrl()));
//        new URL("https://homepages.cae.wisc.edu/~ece533/images/airplane.png");
        Graphics g = image.getGraphics();
        g.setFont(g.getFont().deriveFont(30f));
        g.drawString("Hello Spy!", 100, 100);
        g.dispose();
        ImageIO.write(image, "png", new File("test.png"));

    }
}
