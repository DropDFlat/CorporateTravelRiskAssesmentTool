module hr.java.corporate_travel_risk_assesment_tool {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires java.sql;


    opens hr.java.RiskAssessmentTool to javafx.fxml;
    exports hr.java.corporate_travel_risk_assessment_tool.controller;
    opens hr.java.corporate_travel_risk_assessment_tool.controller to javafx.fxml;
    exports hr.java.corporate_travel_risk_assessment_tool.main;
    opens hr.java.corporate_travel_risk_assessment_tool.main to javafx.fxml;
}