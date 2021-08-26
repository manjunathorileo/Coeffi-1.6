package com.dfq.coeffi.E_Learning.modules;

import com.dfq.coeffi.E_Learning.service.ProductService;
import com.dfq.coeffi.E_Learning.service.QuestionService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class QuestionImport {

    private static QuestionService questionService;
    private static ProductService productService;
    // Product service declare
    private static QuestionDto questiondto;

    @Autowired
    QuestionImport(QuestionService questionService, ProductService productService) {
        this.questionService = questionService;
        this.productService = productService;
    }

    public static List<QuestionDto> questionImport(MultipartFile file, long pid, String qtitle, long level) {
        List<QuestionDto> dto = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = (Sheet) workbook.getSheetAt(0);

            int rowNumber = sheet.getPhysicalNumberOfRows();
            if (rowNumber > 1) {
                for (int i = 1; i < rowNumber; i++) {
                    Row row = sheet.getRow(i);
                    QuestionDto questionDto = new QuestionDto();
                    questionDto.setId(i);
                    questionDto.setQuestionTitle(row.getCell(0).getStringCellValue());
                    questionDto.setQuestionDescription(row.getCell(1).getStringCellValue());
                    questionDto.setQuestionOption1(row.getCell(2).getStringCellValue());
                    questionDto.setQuestionOption2(row.getCell(3).getStringCellValue());
                    questionDto.setQuestionOption3(row.getCell(4).getStringCellValue());
                    questionDto.setQuestionOption4(row.getCell(5).getStringCellValue());
                    questionDto.setRightOption(row.getCell(6).getStringCellValue());
                    Question question = new Question();
//                    question.setId(questionDto.getId());
                    question.setQuestionTitle(questionDto.getQuestionTitle());
                    question.setQuestionDescription(questionDto.getQuestionDescription());
                    question.setQuestionOption1(questionDto.getQuestionOption1());
                    question.setQuestionOption2(questionDto.getQuestionOption2());
                    question.setQuestionOption3(questionDto.getQuestionOption3());
                    question.setQuestionOption4(questionDto.getQuestionOption4());
                    question.setRightOption(questionDto.getRightOption());
                    Optional<Product> product = productService.getProductById(pid);

                    question.setProduct(product.get());
                    question.setQTitle(qtitle);
                    question.setLevel(level);

                    questionService.saveUpdateQuestion(question);

                    System.out.println(question.toString());
                    dto.add(questionDto);
                }
            } else {
                throw new NoSuchFieldException("No data in excel sheet\", \"Question-Import");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }
}
