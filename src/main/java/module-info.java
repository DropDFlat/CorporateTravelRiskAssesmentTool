module hr.java.corporate_travel_risk_assesment_tool {
    requires javafx.controls;
    requires javafx.fxml;


    opens hr.java.corporate_travel_risk_assessment_tool.main to javafx.fxml;
    exports hr.java.corporate_travel_risk_assessment_tool;
    opens hr.java.corporate_travel_risk_assessment_tool.controller to javafx.fxml;
}