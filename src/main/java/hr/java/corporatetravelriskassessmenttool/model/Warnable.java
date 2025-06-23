package hr.java.corporatetravelriskassessmenttool.model;

public interface Warnable {
    boolean hasWarning();
    String getWarningMessage();
    void setWarningMessage(String warningMessage);
    void noWarning();
}
