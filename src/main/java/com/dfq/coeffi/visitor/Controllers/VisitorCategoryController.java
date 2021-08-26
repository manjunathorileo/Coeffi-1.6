package com.dfq.coeffi.visitor.Controllers;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.visitor.Entities.VisitorCategory;
import com.dfq.coeffi.visitor.Services.VisitorCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@RestController
public class VisitorCategoryController extends BaseController
{
    @Autowired
    VisitorCategoryService visitorCategoryService;

    @PostMapping("visitor/category-save")
    public ResponseEntity<VisitorCategory> saveVisitor(@RequestBody VisitorCategory  visitorCategory)
    {
        VisitorCategory visitorCategory1 = visitorCategoryService.saveVisitor(visitorCategory) ;
        return new ResponseEntity<>(visitorCategory1, HttpStatus.OK);

    }

    @GetMapping("visitor/category-view")
    public ResponseEntity<List<VisitorCategory>> getAllVisitor()
    {
        List<VisitorCategory> visitorCategory2= visitorCategoryService.getAllVisitor();
        return new ResponseEntity<>(visitorCategory2,HttpStatus.OK);

    }

    @GetMapping("visitor/category/viewbyid/{id}")
    public ResponseEntity<Optional<VisitorCategory>> getVisitor(@PathVariable long id)
    {
        Optional<VisitorCategory> visitorCategory3= Optional.ofNullable(visitorCategoryService.getVisitor(id));
        return new ResponseEntity<>(visitorCategory3,HttpStatus.OK);

    }

    @DeleteMapping("visitor/category-delete/{id}")
    public void deleteDepartmentByid(@PathVariable long id)
    {
        visitorCategoryService.deleteVisitorById(id);

    }
}
