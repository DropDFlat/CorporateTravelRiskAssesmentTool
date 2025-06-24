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
/**
 * Utility class for managing user-related operations such as password hashing and loading user data from a file.
 * This class is not instantiable.
 */
public class UserManagerUtil {
    private static final String USER_FILE = "dat/users.txt";

    /**
     * Private constructor to prevent instantiation.
     */
    private UserManagerUtil() {
    }

    /**
     * Hashes a plain-text password using the SHA-256 algorithm.
     *
     * @param password the plain-text password to hash
     * @return the hashed password as a hexadecimal string
     * @throws PasswordHashingException if the SHA-256 algorithm is not available
     */
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
    /**
     * Loads user data from the user data file into a map keyed by username.
     * Each line in the file is expected to have the format: username:hashedPassword:role.
     *
     * @return a map of usernames to {@link User} objects
     * @throws MalformedUserFileException if any line in the user data file is malformed
     */
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
