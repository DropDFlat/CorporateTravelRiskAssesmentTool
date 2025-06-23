package hr.java.corporatetravelriskassessmenttool.utils;

import hr.java.corporatetravelriskassessmenttool.changelog.ChangelogEntry;
import hr.java.corporatetravelriskassessmenttool.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.changelogRepository;

public class ChangelogUtil {
    private static final String ARROW =" → ";
    private static final String DESCRIPTION = "description: ";
    private static final String RISK_LEVEL = "risk_level: ";
    private ChangelogUtil() {}
    public static void logCreation(User user, String action, String detail) {
        changelogRepository.logChange(new ChangelogEntry(
                user.username(), user.role(), action, detail, LocalDateTime.now()));
    }
    public static void logTripUpdate(User user, Trip<Person> oldTrip, Trip<Person> newTrip) {
        List<String> changes = new ArrayList<>();
        if (!oldTrip.getName().equals(newTrip.getName())) {
            changes.add("name: '" + oldTrip.getName() + ARROW + newTrip.getName() + "'\n");
        }
        if (!oldTrip.getStartDate().equals(newTrip.getStartDate())) {
            changes.add("start date: '" + oldTrip.getStartDate() + ARROW + newTrip.getStartDate() + "'\n");
        }
        if (!oldTrip.getEndDate().equals(newTrip.getEndDate())) {
            changes.add("end date: '" + oldTrip.getEndDate() + ARROW + newTrip.getEndDate() + "'\n");
        }
        if (!oldTrip.getEmployees().equals(newTrip.getEmployees())) {
            changes.add("employees: " + oldTrip.getEmployees() + ARROW + newTrip.getEmployees() + "\n");
        }
        if (!oldTrip.getDestinations().equals(newTrip.getDestinations())) {
            changes.add("destinations: " + oldTrip.getDestinations() + ARROW + newTrip.getDestinations());
        }
        if(!changes.isEmpty()) {
            changelogRepository.logChange(new ChangelogEntry(user.username(), user.role(), "Trip updated",
                    "Id: " + newTrip.getId() + " " + changes, LocalDateTime.now()));
        }
    }
    public static void logDestinationUpdate(User user, Destination oldDestination, Destination newDestination) {
        List<String> changes = new ArrayList<>();
        if (!oldDestination.getCity().equals(newDestination.getCity())) {
            changes.add("city: '" + oldDestination.getCity() + ARROW + newDestination.getCity() + "'\n");
        }
        if (!oldDestination.getCountry().equals(newDestination.getCountry())) {
            changes.add("country: '" + oldDestination.getCountry() + ARROW + newDestination.getCountry() + "'\n");
        }
        if (!oldDestination.getRisks().equals(newDestination.getRisks())) {
            changes.add("Risks: " + oldDestination.getRisks() + ARROW + newDestination.getRisks() + "'");
        }
        if(!changes.isEmpty()) {
            changelogRepository.logChange(new ChangelogEntry(user.username(), user.role(), "Destination updated",
                    "Id: " + newDestination.getId() + " " + changes, LocalDateTime.now()));
        }
    }
    public static void logAssessmentUpdate(User user, RiskAssessment<Person, Risk> oldAssessment, RiskAssessment<Person, Risk> newAssessment) {
        List<String> changes = new ArrayList<>();
        if (!oldAssessment.getAssessmentDate().equals(newAssessment.getAssessmentDate())) {
            changes.add("assessment date: '" + oldAssessment.getAssessmentDate() + ARROW + newAssessment.getAssessmentDate() + "'\n");
        }
        if (!oldAssessment.generateReport().equals(newAssessment.generateReport())) {
            changes.add("report: '" + oldAssessment.generateReport() + ARROW + newAssessment.generateReport() + "'\n");
        }
        if(!changes.isEmpty()) {
            changelogRepository.logChange(new ChangelogEntry(user.username(), user.role(), "Destination updated",
                    "Id: " + newAssessment.getId() + " " + changes, LocalDateTime.now()));
        }
    }
    public static void logPoliticalRiskUpdate(User user, PoliticalRisk oldRisk, PoliticalRisk newRisk){
        List<String> changes = new ArrayList<>();
        if (!oldRisk.getDescription().equals(newRisk.getDescription())) {
            changes.add(DESCRIPTION + oldRisk.getDescription() + ARROW + newRisk.getDescription() + "'\n");
        }
        if (!oldRisk.getRiskLevel().equals(newRisk.getRiskLevel())) {
            changes.add(RISK_LEVEL + oldRisk.getRiskLevel() + ARROW + newRisk.getRiskLevel() + "'\n");
        }
        if (oldRisk.getStabilityIndex().compareTo(newRisk.getStabilityIndex()) != 0) {
            changes.add("stability index: " + oldRisk.getStabilityIndex() + ARROW + newRisk.getStabilityIndex() + "\n");
        }
        if(oldRisk.getUnrestIndex().compareTo(newRisk.getUnrestIndex()) != 0) {
            changes.add("unrest index: " + oldRisk.getUnrestIndex() + ARROW + newRisk.getUnrestIndex());
        }
        if(!changes.isEmpty()) {
            changelogRepository.logChange(new ChangelogEntry(user.username(), user.role(), "Political risk updated",
                    "Id: " + newRisk.getId() + " " + changes, LocalDateTime.now()));
        }
    }
    public static void logHealthRiskUpdate(User user, HealthRisk oldRisk, HealthRisk newRisk){
        List<String> changes = new ArrayList<>();
        if (!oldRisk.getDescription().equals(newRisk.getDescription())) {
            changes.add(DESCRIPTION + oldRisk.getDescription() + ARROW + newRisk.getDescription() + "'\n");
        }
        if (!oldRisk.getRiskLevel().equals(newRisk.getRiskLevel())) {
            changes.add(RISK_LEVEL + oldRisk.getRiskLevel() + ARROW + newRisk.getRiskLevel() + "'\n");
        }
        if (oldRisk.getSeverity().compareTo(newRisk.getSeverity()) != 0) {
            changes.add("severity: " + oldRisk.getSeverity() + ARROW + newRisk.getSeverity());
        }
        if(!changes.isEmpty()) {
            changelogRepository.logChange(new ChangelogEntry(user.username(), user.role(), "Health risk updated",
                    "Id: " + newRisk.getId() + " " + changes, LocalDateTime.now()));
        }
    }
    public static void logEnvironmentalRiskUpdate(User user, EnvironmentalRisk oldRisk, EnvironmentalRisk newRisk){
        List<String> changes = new ArrayList<>();
        if (!oldRisk.getDescription().equals(newRisk.getDescription())) {
            changes.add(DESCRIPTION + oldRisk.getDescription() + ARROW + newRisk.getDescription() + "'\n");
        }
        if (!oldRisk.getRiskLevel().equals(newRisk.getRiskLevel())) {
            changes.add(RISK_LEVEL + oldRisk.getRiskLevel() + ARROW + newRisk.getRiskLevel() + "'\n");
        }
        if (oldRisk.getDamageIndex().compareTo(newRisk.getDamageIndex()) != 0) {
            changes.add("damage index: " + oldRisk.getDamageIndex() + ARROW + newRisk.getDamageIndex() + "\n");
        }
        if(oldRisk.getDisasterProbability().compareTo(newRisk.getDisasterProbability()) != 0) {
            changes.add("disaster probability: " + oldRisk.getDisasterProbability() + " → " + newRisk.getDisasterProbability());
        }
        if(!changes.isEmpty()) {
            changelogRepository.logChange(new ChangelogEntry(user.username(), user.role(), "Environmental risk updated",
                    "Id: " + newRisk.getId() + " " + changes, LocalDateTime.now()));
        }
    }
    public static void logEmployeeUpdate(User user, Employee oldEmployee, Employee newEmployee){
        List<String> changes = new ArrayList<>();
        if (!oldEmployee.getName().equals(newEmployee.getName())) {
            changes.add("name: '" + oldEmployee.getName() + ARROW + newEmployee.getName() + "'\n");
        }
        if (!oldEmployee.getJobTitle().equals(newEmployee.getJobTitle())) {
            changes.add("job title: '" + oldEmployee.getJobTitle() + ARROW + newEmployee.getJobTitle() + "'\n");
        }
        if (!oldEmployee.getDepartment().equals(newEmployee.getDepartment())) {
            changes.add("Department: " + oldEmployee.getDepartment() + ARROW + newEmployee.getDepartment() + "'\n");
        }
        if (!oldEmployee.getDateOfBirth().equals(newEmployee.getDateOfBirth())) {
            changes.add("Date of birth: " + oldEmployee.getDateOfBirth() + ARROW + newEmployee.getDateOfBirth() + "\n");
        }
        if (!oldEmployee.getSalary().equals(newEmployee.getSalary())) {
            changes.add("Salary: " + oldEmployee.getSalary() + ARROW + newEmployee.getSalary());
        }
        if(!changes.isEmpty()) {
            changelogRepository.logChange(new ChangelogEntry(user.username(), user.role(), "Employee updated",
                    "Id: " + newEmployee.getId() + " " + changes, LocalDateTime.now()));
        }
    }
}
