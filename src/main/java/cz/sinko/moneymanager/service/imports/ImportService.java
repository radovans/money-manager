package cz.sinko.moneymanager.service.imports;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import cz.sinko.moneymanager.repository.model.Transaction;

public interface ImportService {

	List<Transaction> parseTransactions(MultipartFile file);

}
