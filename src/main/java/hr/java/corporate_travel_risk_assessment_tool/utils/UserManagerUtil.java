package hr.java.corporate_travel_risk_assessment_tool.utils;

import hr.java.corporate_travel_risk_assessment_tool.model.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static hr.java.corporate_travel_risk_assessment_tool.main.CorporateTravelRiskAssessmentApplication.log;

public class UserManagerUtil {
    private static final String USER_FILE = "dat/users.txt";
    public static String hashPassword(String password) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            log.error("Error hashing password", e);
            throw new RuntimeException(e);
        }
    }
    public static Map<String, User> loadUserData(){
        Map<String, User> userMap = new HashMap<>();
        try(Stream<String> stream = Files.lines(Paths.get(USER_FILE))){
            stream.map(line -> line.split(":"))
                    .forEach(parts -> userMap.put(parts[0], new User(parts[0], parts[1], parts[2])));

        }catch (IOException e){
            log.error(e.getMessage());
        }
        return userMap;
    }
}
