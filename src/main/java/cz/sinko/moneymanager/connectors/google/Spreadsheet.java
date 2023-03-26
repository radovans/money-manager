package cz.sinko.moneymanager.connectors.google;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Spreadsheet {

	private String range;
	private List<List<String>> values;

}
