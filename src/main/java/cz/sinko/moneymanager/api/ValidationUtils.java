package cz.sinko.moneymanager.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import org.springframework.validation.ObjectError;

import cz.sinko.moneymanager.api.RequestValidationException;

public class ValidationUtils {

	public static void checkErrors(List<ObjectError> errors) throws RequestValidationException {
		if (!errors.isEmpty()) {
			throw RequestValidationException.createWith(errors);
		}
	}

	public static void validateLocalDateFormat(String input, List<ObjectError> errors, String field) {
		try {
			LocalDate.parse(input);
		} catch (DateTimeParseException e) {
			errors.add(new ObjectError(field, "must be a date in format yyyy-MM-dd."));
		}
	}

	public static void validateOffsetDateTimeFormat(String input, List<ObjectError> errors, String field) {
		try {
			OffsetDateTime.parse(input);
		} catch (DateTimeParseException e) {
			errors.add(new ObjectError(field, "must be a date in format yyyy-MM-dd'T'HH:mm:ss.SSSz."));
		}
	}

	public static void validateYearMonthFormat(String input, List<ObjectError> errors, String field) {
		try {
			YearMonth.parse(input);
		} catch (DateTimeParseException e) {
			errors.add(new ObjectError(field, "must be in format yyyy-MM."));
		}
	}

	public static void validateIntegerGreaterThan(String input, Integer value, List<ObjectError> errors, String field) {
		try {
			int intValue = Integer.parseInt(input);
			if (intValue < value) {
				errors.add(new ObjectError(field, "must be greater than " + value + "."));
			}
		} catch (NumberFormatException e) {
			errors.add(new ObjectError(field, "must be a number."));
		}
	}

	public static void validateLongGreaterThan(String input, Long value, List<ObjectError> errors, String field) {
		try {
			long longValue = Long.parseLong(input);
			if (longValue <= value) {
				errors.add(new ObjectError(field, "must be greater than " + value + "."));
			}
		} catch (NumberFormatException e) {
			errors.add(new ObjectError(field, "must be a number."));
		}
	}

	public static <T extends Enum<T>> void validateEnumValue(String input, List<ObjectError> errors, Class<T> sortFields) {
		try {
			Arrays.stream(sortFields.getEnumConstants()).filter(s -> s.name().equalsIgnoreCase(input)).findFirst().orElseThrow(IllegalArgumentException::new);
		} catch (IllegalArgumentException e) {
			errors.add(new ObjectError("sort", "must be in " + Arrays.toString(sortFields.getEnumConstants()) + " (case insensitive)."));
		}
	}
}