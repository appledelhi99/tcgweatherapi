package com.tcg.tcgweatherapi.validator;

import org.apache.commons.validator.routines.RegexValidator;

public class ZipCodeValidator {

    private static final RegexValidator US_ZIP_CODE_VALIDATOR =
            new RegexValidator("^(\\d{5})(?:[-\\s]\\d{4})?$");

    /**
     * Validates whether the given ZIP code is a valid US ZIP code.
     *
     * @param zipCode the ZIP code to validate.
     * @return true if the ZIP code is valid; false otherwise.
     */
    public static boolean isValidUSZipCode(String zipCode) {
        return US_ZIP_CODE_VALIDATOR.isValid(zipCode);
    }
}
