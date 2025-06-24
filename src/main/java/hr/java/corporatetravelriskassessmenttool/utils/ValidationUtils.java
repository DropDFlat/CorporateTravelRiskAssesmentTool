package hr.java.corporatetravelriskassessmenttool.utils;

import hr.java.corporatetravelriskassessmenttool.model.Risk;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
/**
 * Utility class providing validation methods for various JavaFX controls and input data types.
 * This class contains static methods to validate strings, numbers, dates, and UI selections,
 * as well as utility methods to show confirmation and error dialogs.
 * It is not instantiable.
 */
public class ValidationUtils {
    private static final String EMPTY_STRING_ERROR = " cannot be empty!\n";

    /**
     * Private constructor to prevent instantiation.
     */
    private ValidationUtils() {}
    /**
     * Validates that the text content of a TextField is not empty.
     *
     * @param errors StringBuilder to append error messages
     * @param field TextField to validate
     * @param name Name of the field for error reporting
     * @return the text value of the field (empty string if invalid)
     */
    public static String validateString(StringBuilder errors, TextField field, String name) {
        String fieldValue = field.getText();
        if(fieldValue.isEmpty()) {
            errors.append(name).append(EMPTY_STRING_ERROR);
        }
        return fieldValue;
    }

    /**
     * Validates that the TextField contains a valid percentage value (0 to 100) with up to 4 decimal places,
     * and returns it as a decimal fraction (e.g., 50% -> 0.5).
     *
     * @param errors StringBuilder to append error messages
     * @param bigDecimalTextField TextField containing the percentage value
     * @param name Name of the field for error reporting
     * @return BigDecimal representing the fraction value (0 if invalid)
     */
    public static BigDecimal validateBigDecimalPercentageValue(StringBuilder errors, TextField bigDecimalTextField, String name){
        String bigDecimalString = bigDecimalTextField.getText();
        if (bigDecimalString.isEmpty()) {
            errors.append(name).append(EMPTY_STRING_ERROR);
            return BigDecimal.ZERO;
        } else {
            if (!bigDecimalString.matches("^\\d{1,12}(\\.\\d{1,4})?$")) {
                errors.append(name).append(" must be a positive number\n");
                return BigDecimal.ZERO;
            }
            BigDecimal bigDecimal = new BigDecimal(bigDecimalString);
            if(bigDecimal.compareTo(new BigDecimal(0)) < 0){
                errors.append(name).append(" cannot be negative\n");
                return BigDecimal.ZERO;
            }else if(bigDecimal.compareTo(new BigDecimal(100)) > 0){
                errors.append(name).append(" cannot be greater than 100%\n");
                return BigDecimal.ZERO;
            }else{
                return bigDecimal.divide(BigDecimal.valueOf(100));
            }
        }
    }
    /**
     * Validates that a DatePicker contains a selected date.
     *
     * @param errors StringBuilder to append error messages
     * @param datePicker DatePicker control to validate
     * @param name Name of the field for error reporting
     * @return the selected LocalDate, or LocalDate.MIN if invalid
     */
    public static LocalDate validateDate(StringBuilder errors, DatePicker datePicker,  String name){
        Optional<LocalDate> date = Optional.ofNullable(datePicker.getValue());
        if(date.isEmpty()){
            errors.append("No valid ").append(name).append(" selected\n");
        }else{
            return date.get();
        }
        return LocalDate.MIN;
    }
    /**
     * Validates that at least one Risk is selected in a ListView.
     *
     * @param errors StringBuilder to append error messages
     * @param listView ListView containing Risk items
     * @param name Name of the field for error reporting
     * @return a Set of selected Risks, possibly empty
     */
    public static Set<Risk> validateRisks(StringBuilder errors, ListView<Risk> listView, String name){
        Set<Risk> risks = new HashSet<>(listView.getSelectionModel().getSelectedItems());
        if(risks.isEmpty()) {
            errors.append("No ").append(name).append(" selected!\n");
        }
        return risks;
    }

    /**
     * Validates that the TextField contains a valid non-negative decimal number.
     *
     * @param errors StringBuilder to append error messages
     * @param textField TextField containing the decimal number
     * @param name Name of the field for error reporting
     * @return the parsed BigDecimal value, or BigDecimal.ZERO if invalid
     */
    public static BigDecimal validateBigDecimalValue(StringBuilder errors, TextField textField, String name){
        String textFieldValue = textField.getText();
        if(textFieldValue.isEmpty()) {
            errors.append(name).append(EMPTY_STRING_ERROR);
            return BigDecimal.ZERO;
        }else {
            if (!textFieldValue.matches("^\\d{1,12}(\\.\\d{1,4})?$")) {
                errors.append(name).append(" must be a valid number!(e.g 1000.00)\n");
                return BigDecimal.ZERO;
            }
            BigDecimal salary = new BigDecimal(textFieldValue);
            if (salary.compareTo(BigDecimal.ZERO) < 0) {
                errors.append(name).append(" must be a positive number!\n");
            }
            return salary;
        }

    }
    /**
     * Validates that the TextField contains a valid non-negative integer.
     *
     * @param errors StringBuilder to append error messages
     * @param integerTextField TextField containing the integer value
     * @param name Name of the field for error reporting
     * @return the parsed Integer value, or 0 if invalid
     */
    public static Integer validateIntegerValue(StringBuilder errors, TextField integerTextField, String name){
        String valueString = integerTextField.getText();
        if(valueString.isEmpty()) {
            errors.append(name).append(EMPTY_STRING_ERROR);
            return 0;
        }else {
            if (!valueString.matches("^\\d{1,12}$")) {
                errors.append(name).append(" must be a positive number\n");
                return 0;
            }
            Integer value = Integer.parseInt(valueString);
            if(value < 0){
                errors.append(name).append(" cannot be negative\n");
                return 0;
            }
            return value;
        }
    }

    /**
     * Validates that the DatePicker contains a valid birthdate corresponding to an age between 18 and 80.
     *
     * @param errors StringBuilder to append error messages
     * @param datePicker DatePicker control containing the birthdate
     * @param name Name of the field for error reporting
     * @return the selected birthdate, or LocalDate.MIN if invalid
     */
    public static LocalDate validateBirthDate(StringBuilder errors, DatePicker datePicker, String name){
        Optional<LocalDate> date = Optional.ofNullable(datePicker.getValue());
        if(date.isEmpty()){
            errors.append("No valid ").append(name).append(" selected\n");
        }else{
            LocalDate today = LocalDate.now();
            Period age = Period.between(date.get(), today);
            if(age.getYears() < 18 || age.getYears() > 80) {
                errors.append(name).append(" must be between 18 and 80");
            }else{
                return date.get();
            }
        }
        return LocalDate.MIN;
    }
    /**
     * Shows a confirmation dialog with the specified header and content text.
     *
     * @param header the header text for the dialog
     * @param content the content text for the dialog
     * @return an Optional containing the button clicked by the user
     */
    public static Optional<ButtonType> showConfirmation(String header, String content){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }
    /**
     * Shows an error dialog with the specified header and content text.
     *
     * @param header the header text for the dialog
     * @param content the content text for the dialog
     */
    public static void showError(String header, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
