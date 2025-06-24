package hr.java.corporatetravelriskassessmenttool.model;
/**
 * Interface defining a contract for generating reports.
 * Classes implementing this interface should provide
 * a textual representation of their data or state.
 */
public interface Reportable {
    /**
     * Generates a report as a string.
     *
     * @return the generated report
     */
    String generateReport();
}
