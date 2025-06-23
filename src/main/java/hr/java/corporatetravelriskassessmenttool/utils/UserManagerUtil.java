package hr.java.corporatetravelriskassessmenttool.utils;

import hr.java.corporatetravelriskassessmenttool.exception.MalformedUserFileException;
import hr.java.corporatetravelriskassessmenttool.exception.PasswordHashingException;
import hr.java.corporatetravelriskassessmenttool.model.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static hr.java.corporatetravelriskassessmenttool.main.CorporateTravelRiskAssessmentApplication.log;

public class UserManagerUtil {
    private UserManagerUtil() {
    }

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
            throw new PasswordHashingException("SHA-256 algorithm not available", e);
        }
    }
    public static Map<String, User> loadUserData() throws MalformedUserFileException {
        Map<String, User> userMap = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(Path.of(USER_FILE));
            for(String line : lines) {
                String[] parts = line.split(":");
                if(parts.length != 3) {
                    throw new MalformedUserFileException("Invalid format in line: " + line);
                }
                userMap.put(parts[0], new User(parts[0], parts[1], parts[2]));
            }
        } catch (IOException e) {
            log.error("Error reading user data", e);
        }
        return userMap;
    }
}
