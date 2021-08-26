package com.dfq.coeffi.entity.payroll;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InterestCalculation {
	
	
    public static double calcMonthlyPayment(double loanInterest, double amount, int loanDuration)
    {
		    double monthlyPayment;
		    double calcMonthlyPayment;
	        calcMonthlyPayment = (loanInterest*amount)/(1-(1+loanInterest)-loanDuration);
	        return monthlyPayment = calcMonthlyPayment;
	}//end method CalcMonthlyPayment

    public static void loanStatment (double amount,double annualInterestRate,int loanDuration,double monthlyPayment)
    {
        log.info("sjkcbsb guyudw wd");
    }


}


