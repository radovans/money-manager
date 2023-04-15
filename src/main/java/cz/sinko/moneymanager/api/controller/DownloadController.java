package cz.sinko.moneymanager.api.controller;

import cz.sinko.moneymanager.facade.DownloadFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/download")
@AllArgsConstructor
@Slf4j
public class DownloadController {

	@Autowired
	private DownloadFacade downloadFacade;

	@GetMapping(value = "/transactions/csv")
	public ResponseEntity<Void> downloadTransactions() {
		log.info("Creating csv file with transactions.");
		downloadFacade.downloadTransactions();
		return ResponseEntity.ok().build();
	}

}
