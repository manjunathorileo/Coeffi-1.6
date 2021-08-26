package com.dfq.coeffi.resource;

import com.dfq.coeffi.entity.finance.expense.Category;
import com.dfq.coeffi.entity.finance.expense.Expense;
import com.dfq.coeffi.entity.finance.expense.SubCategory;
import com.dfq.coeffi.service.finance.ExpenseService;
import com.dfq.coeffi.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Component
public class ExpenseResourceConverter {

    @Autowired
    private ExpenseService expenseService;

    public Expense toExpenseEntity(ExpenseResource expenseResource){
        Expense expense = new Expense();
        if(expenseResource.getId() > 0){
            expense.setId(expenseResource.getId());
        }
        expense.setApproval(expenseResource.isApproval());
        expense.setTitle(expenseResource.getTitle());
        expense.setAmount(expenseResource.getAmount());
        expense.setDescription(expenseResource.getDescription());
        expense.setCreatedOn(DateUtil.getTodayDate());

        setCategory(expense, expenseResource);
        setSubCategory(expense, expenseResource);
        return  expense;
    }

    private void setCategory(Expense expense, ExpenseResource expenseResource){
        if(expenseResource.getCategoryId() > 0){
            Optional<Category> categoryObj =   expenseService.getExpenseCategory(expenseResource.getCategoryId());
            if(!categoryObj.isPresent()){
                throw new EntityNotFoundException("category");
            }
            Category category = categoryObj.get();
            expense.setCategory(category);
        }
    }

    private void setSubCategory(Expense expense, ExpenseResource expenseResource){
        if(expenseResource.getSubCategoryId() > 0){
            Optional<SubCategory> subCategoryObj = expenseService.getSubCategory(expenseResource.getSubCategoryId());

            if(!subCategoryObj.isPresent()){
                throw new EntityNotFoundException("subcategory");
            }
            expense.setSubCategory(subCategoryObj.get());
        }

    }
}
