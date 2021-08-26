package com.dfq.coeffi.E_Learning.modules;

import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.service.UserService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserImport {
    //1
    private static UserService userService;

    @Autowired
        // private static UserRepository userRepository;2
    UserImport(UserService userService) {
        this.userService = userService;
    }

    public static List<UserDto> userImport(MultipartFile file) {

        List<UserDto> dto = new ArrayList<>();
        List<User> users = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            int rowNumber = sheet.getPhysicalNumberOfRows();
            if (rowNumber > 1) {
                for (int i = 1; i < rowNumber; i++) {

                    Row row = sheet.getRow(i);

                    UserDto userDto = new UserDto();
                    userDto.setId(i);
                    userDto.setFirstName(row.getCell(0).toString());
                    userDto.setLastName(row.getCell(1).getStringCellValue());
                    userDto.setEmail(row.getCell(2).getStringCellValue());
                    userDto.setPassword(row.getCell(3).getStringCellValue());
                    //  userDto.setCreatedOn(row.getCell(4).getStringCellValue());
                    dto.add(userDto);
                    User user = convertToEntity(userDto);
                    users.add(user);

                }

             /*3   if (!CollectionUtils.isEmpty(users)) {
                    userService.saveAll(users);
                    // Save to database
                }*/
            } else {
                throw new EntityNotFoundException("No data in excel sheet : User Import");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dto;
    }

    public static User convertToEntity(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        // todo need to set createdOn flag
        // date util class required
        userService.saveUser(user);
        return user;
    }
}
