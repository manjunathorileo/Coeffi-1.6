package com.dfq.coeffi.service.finance;



import com.dfq.coeffi.entity.finance.expense.Expense;
import com.dfq.coeffi.entity.finance.expense.SubCategory;
import com.dfq.coeffi.entity.finance.expense.SubCategoryOne;
import com.dfq.coeffi.entity.finance.expense.SubCategoryTwo;
import com.dfq.coeffi.entity.finance.expense.Category;
import com.dfq.coeffi.entity.user.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
 * @Auther H Kapil Kumar on 20/3/18.
 * @Company Orileo Technologies
 */
public interface ExpenseService
{
    Expense createExpense(Expense expense, User loggedBy);
    List<Expense> getExpenses();
    List<Expense> getExpenseByYear(long id);
    Optional<Expense> getExpense(long id);
    boolean isExpenseExists(long id);
    void deleteExpense(long id);
    List<Expense> getExpenseBetweenDate(Date startDate, Date endDate);

    Category createExpenseCategory(Category category, User loggedUser);
    List<Category> getExpenseCategories();
    List<Category> getExpenseCategoryByYear(long year);
    Optional<Category> getExpenseCategory(long id);
    boolean isExpenseCategoryExists(long id);
    void deleteExpenseCategory(long id);

    // Subcategories

    SubCategory createExpenseSubCategory(SubCategory subCategory);
    List<SubCategory> getSubExpenseCategories(long categoryId);
    Optional<SubCategory> getSubCategory(long id);

    SubCategoryOne createSubCategoryOne(SubCategoryOne subCategoryOne);
    List<SubCategoryOne> getSubCategoriesOne(long subCategoryId);
    Optional<SubCategoryOne> getSubCategoryOne(long id);

    SubCategoryTwo createSubCategoryTwo(SubCategoryTwo subCategoryTwo);
    List<SubCategoryTwo> getSubCategoriesTwo(long subCategoryOneId);
    Optional<SubCategoryTwo> getSubCategoryTwo(long id);
}
