package cz.sinko.moneymanager.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class FormatUtil {

	public static final String CZK = " CZK";
	public static final String PERCENT = "%";

	public static String formatBigDecimal(BigDecimal amount) {
		return getDecimalFormat().format(amount) + CZK;
	}

	public static String formatPercentage(BigDecimal amount) {
		return getDecimalFormat().format(amount) + PERCENT;
	}

	private static DecimalFormat getDecimalFormat() {
		DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
		DecimalFormatSymbols customSymbol = new DecimalFormatSymbols();
		customSymbol.setGroupingSeparator(' ');
		decimalFormat.setDecimalFormatSymbols(customSymbol);
		return decimalFormat;
	}

}
