package hr.java.corporatetravelriskassessmenttool.utils;

import hr.java.corporatetravelriskassessmenttool.model.Risk;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ValidationUtils {
    private static final String EMPTY_STRING_ERROR = " cannot be empty!\n";
    private ValidationUtils() {}
    public static String validateString(StringBuilder errors, TextField field, String name) {
        String fieldValue = field.getText();
        if(fieldValue.isEmpty()) {
            errors.append(name).append(EMPTY_STRING_ERROR);
        }
        return fieldValue;
    }
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
    public static Integer validateIntegerAgeValue(StringBuilder errors, TextField integerTextField, String name){
        String ageString = integerTextField.getText();
        if(ageString.isEmpty()) {
            errors.append(name).append(EMPTY_STRING_ERROR);
        }else {
            if (!ageString.matches("\\d+$")) {
                errors.append("Age must be a whole number!\n");
            }else {
                Integer age = Integer.valueOf(ageString);

                if (age < 18 || age > 80) {
                    errors.append("Employee age must be between 18 and 80");
                }
                return age;
            }

        }
        return 0;
    }
    public static LocalDate validateDate(StringBuilder errors, DatePicker datePicker,  String name){
        Optional<LocalDate> date = Optional.ofNullable(datePicker.getValue());
        if(date.isEmpty()){
            errors.append("No valid ").append(name).append(" selected\n");
        }else{
            return date.get();
        }
        return LocalDate.MIN;
    }
    public static Set<Risk> validateRisks(StringBuilder errors, ListView<Risk> listView, String name){
        Set<Risk> risks = new HashSet<>(listView.getSelectionModel().getSelectedItems());
        if(risks.isEmpty()) {
            errors.append("No ").append(name).append(" selected!\n");
        }
        return risks;
    }
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
    public static Optional<ButtonType> showConfirmation(String header, String content){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }
    public static void showError(String header, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
