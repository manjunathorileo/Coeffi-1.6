package com.dfq.coeffi.service.finance;

import com.dfq.coeffi.entity.finance.income.Income;
import com.dfq.coeffi.entity.finance.income.IncomeCategory;
import com.dfq.coeffi.entity.user.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface IncomeService {

    Income createIncome(Income income, User loggedUser);

    List<Income> listAllIncome();

    Optional<Income> getIncome(long id);

    List<Income> getIncomeByYear(long year);

    void deleteIncome(long id);

    boolean isIncomeExists(long id);

    List<Income> getIncomeBetweenDate(Date startDate, Date endDate);

    /***
     * Income Categories
     */

    IncomeCategory createIncomeCategory(IncomeCategory incomeCategory, User loggedUser);

    List<IncomeCategory> listAllIncomeCategory();

    Optional<IncomeCategory> getIncomeCategory(long id);

    List<IncomeCategory> getIncomeCategoryByYear(long year);

    void deleteIncomeCategory(long id);

    boolean isIncomeCategoryExists(long id);

}
