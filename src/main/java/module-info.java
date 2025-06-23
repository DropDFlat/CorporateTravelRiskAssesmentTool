module hr.java.corporatetravelriskassesmenttool {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires java.sql;
    requires java.management;


    opens hr.java.RiskAssessmentTool to javafx.fxml;
    exports hr.java.corporatetravelriskassessmenttool.controller;
    opens hr.java.corporatetravelriskassessmenttool.controller to javafx.fxml;
    exports hr.java.corporatetravelriskassessmenttool.main;
    opens hr.java.corporatetravelriskassessmenttool.main to javafx.fxml;
    exports hr.java.corporatetravelriskassessmenttool.changelog to javafx.fxml;
}