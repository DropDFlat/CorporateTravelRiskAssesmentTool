package hr.java.corporatetravelriskassessmenttool.changelog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles storage and retrieval of changelog entries.
 * The changelog is saved to file {@code dat/changelog.dat} using java serialization.
 */
public class ChangelogRepository {
    private static final String LOG_FILE = "dat/changelog.dat";
    private static final Logger log = LoggerFactory.getLogger(ChangelogRepository.class);

    /**
     * Logs a new change and appends it to the existing changelog file by reading all existing entries and adding the new
     * one before writing the entire list back to the file.
     *
     * @param entry the entry to log
     */
    public synchronized void logChange(ChangelogEntry entry) {
        List<ChangelogEntry> entries = readAll();
        entries.add(entry);
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LOG_FILE))){
            oos.writeObject(entries);
        }catch(IOException e){
            log.error("Error writing change to log file", e);
        }
    }

    /**
     * Reads and returns all changelog entries from the changelog file.
     *
     * @return list of all {@link ChangelogEntry} objects from the log file.
     */
    public synchronized List<ChangelogEntry> readAll() {
        File file = new File(LOG_FILE);
        if(!file.exists()) return new ArrayList<>();
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<ChangelogEntry>) ois.readObject();
        }catch(IOException | ClassNotFoundException e) {
            log.error("Error reading log file", e);
            return new ArrayList<>();
        }
    }

    /**
     * Reads and returns the last entry founds in the changelog file.
     *
     * @return the latest Changelog entry
     */
    public synchronized Optional<ChangelogEntry> readLastEntry(){
        List<ChangelogEntry> entries = readAll();
        if(!entries.isEmpty()) return Optional.ofNullable(entries.get(entries.size() - 1));
        return Optional.empty();
    }
}

