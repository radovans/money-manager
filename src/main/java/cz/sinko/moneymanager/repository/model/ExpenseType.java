package cz.sinko.moneymanager.repository.model;

public enum ExpenseType {

	//payments that I have to make (payments that I can't save on) / mortgage, loan, installments, insurance, ...
	MUST,
	//payments that I need to make (payments that are necessary for survival, but it is possible to partially save on them) / food, housing, fuel, ...
	NEED,
	//payments that I want to make (payments that are not necessary for survival and can be omitted) / restaurants, travel, ...
	WANT

}