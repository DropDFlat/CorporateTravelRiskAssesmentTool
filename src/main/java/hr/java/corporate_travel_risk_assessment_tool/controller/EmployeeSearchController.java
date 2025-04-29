package hr.java.corporate_travel_risk_assessment_tool.controller;


import hr.java.corporate_travel_risk_assessment_tool.model.RoleAware;
import hr.java.corporate_travel_risk_assessment_tool.model.User;

public class EmployeeSearchController implements RoleAware {

    private User loggedUser;
    public void initialize(){
    }

    @Override
    public void setUser(User user) {
        this.loggedUser = user;
    }
}
