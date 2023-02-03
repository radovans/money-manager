package cz.sinko.moneymanager;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ConfigurationJson {

	private List<String> accounts;
	private List<String> mainCategories;
	private Map<String, String> categories;

}
