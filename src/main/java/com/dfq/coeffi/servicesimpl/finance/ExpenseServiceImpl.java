package com.dfq.coeffi.servicesimpl.finance;

import com.dfq.coeffi.auditlog.log.ApplicationLogService;
import com.dfq.coeffi.entity.finance.expense.*;
import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.repository.finance.*;
import com.dfq.coeffi.service.finance.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.ofNullable;

/**
 * @Auther H Kapil Kumar on 20/3/18.
 * @Company Orileo Technologies
 */

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService
{
    @Autowired
    private ApplicationLogService applicationLogService;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseSubCategoryRepository expenseSubCategoryRepository;

    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepository;

    @Autowired
    private SubCategoryOneRepository subCategoryOneRepository;

    @Autowired
    private SubCategoryTwoRepository subCategoryTwoRepository;

    @Override
    public Expense createExpense(Expense expense, User loggedUser) {
        Expense expense1 = expenseRepository.save(expense);
        if(expense1 != null){
           applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "expense entry created "+expense1.getId(),
                   "POST", loggedUser.getId());
        }
        return expenseRepository.save(expense);
    }

    @Override
    public List<Expense> getExpenses() {
        return newArrayList(ofNullable(expenseRepository.findAll()).orElse(Collections.emptyList()));
    }

    @Override
    public List<Expense> getExpenseByYear(long id) {
        return newArrayList(ofNullable(expenseRepository.findAll()).orElse(Collections.emptyList()));
    }

    @Override
    public Optional<Expense> getExpense(long id) {
        return ofNullable(expenseRepository.getOne(id));
    }

    @Override
    public boolean isExpenseExists(long id) {
        return expenseRepository.exists(id);
    }

    @Override
    public void deleteExpense(long id) {
        expenseRepository.delete(id);
    }

    @Override
    public Category createExpenseCategory(Category category, User loggedUser) {

        Category persistedCategory = expenseCategoryRepository.save(category);

        if(persistedCategory != null){
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "expense category created "+persistedCategory.getId(),
                    "POST", loggedUser.getId());
        }
        return persistedCategory;
    }

    @Override
    public List<Category> getExpenseCategories() {
        return newArrayList(ofNullable(expenseCategoryRepository.findAll()).orElse(Collections.emptyList()));
    }

    @Override
    public List<Category> getExpenseCategoryByYear(long year) {
        return newArrayList(ofNullable(expenseCategoryRepository.findAll()).orElse(Collections.emptyList()));
    }

    @Override
    public Optional<Category> getExpenseCategory(long id) {

        return ofNullable(expenseCategoryRepository.findOne(id));
    }

    @Override
    public boolean isExpenseCategoryExists(long id) {
        return expenseCategoryRepository.exists(id);
    }

    @Override
    public void deleteExpenseCategory(long id) {
        expenseCategoryRepository.delete(id );
    }

    @Override
    public SubCategory createExpenseSubCategory(SubCategory subCategory) {
        return expenseSubCategoryRepository.save(subCategory);
    }

    @Override
    public List<SubCategory> getSubExpenseCategories(long categoryId) {
        return expenseSubCategoryRepository.findSubCategoryByCategoryId(categoryId);
    }

    @Override
    public Optional<SubCategory> getSubCategory(long id) {
        return ofNullable(expenseSubCategoryRepository.findOne(id));
    }

    @Override
    public SubCategoryOne createSubCategoryOne(SubCategoryOne subCategoryOne) {
        return subCategoryOneRepository.save(subCategoryOne);
    }

    @Override
    public List<SubCategoryOne> getSubCategoriesOne(long subCategoryId) {
        return subCategoryOneRepository.findSubCategoryOneBySubCategoryId(subCategoryId);
    }

    @Override
    public Optional<SubCategoryOne> getSubCategoryOne(long id) {
        return ofNullable(subCategoryOneRepository.findOne(id));
    }

    @Override
    public SubCategoryTwo createSubCategoryTwo(SubCategoryTwo subCategoryTwo) {
        return subCategoryTwoRepository.save(subCategoryTwo);
    }

    @Override
    public List<SubCategoryTwo> getSubCategoriesTwo(long subCategoryOneId) {
        return subCategoryTwoRepository.findSubCategoryTwoBySubCategoryOneId(subCategoryOneId);
    }

    @Override
    public Optional<SubCategoryTwo> getSubCategoryTwo(long id) {
        return ofNullable(subCategoryTwoRepository.findOne(id));
    }

	@Override
	public List<Expense> getExpenseBetweenDate(Date startDate, Date endDate) {
		return expenseRepository.getExpenseBetweenDate(startDate,endDate);
	}
}