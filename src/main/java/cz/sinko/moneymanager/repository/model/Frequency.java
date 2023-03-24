package cz.sinko.moneymanager.repository.model;

import java.time.Period;

public enum Frequency {

	MONTHLY(Period.ofMonths(1)),
	YEARLY(Period.ofYears(1));

	private Period value;

	Frequency(Period value) {
		this.value = value;
	}

	public static Frequency fromValue(String input) {
		for (Frequency frequency : Frequency.values()) {
			if (String.valueOf(frequency.value).equals(input)) {
				return frequency;
			}
		}
		return null;
	}

	public Period getValue() {
		return value;
	}
}
