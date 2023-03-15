package cz.sinko.moneymanager.api.dto;

public enum SortTransactionFields {

	id, date, formattedAmountInCzk, amountInCzk, amount;

	public static SortTransactionFields fromString(String value) {
		try {
			return valueOf(value);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException(String.format("Invalid value '%s' for orders given! Has to be either 'id' or 'date' (case insensitive).", value), ex);
		}
	}

}
