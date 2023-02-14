package cz.sinko.moneymanager.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import cz.sinko.moneymanager.model.Transaction;

public interface ImportService {

	List<Transaction> parseTransactions(MultipartFile file);

}
