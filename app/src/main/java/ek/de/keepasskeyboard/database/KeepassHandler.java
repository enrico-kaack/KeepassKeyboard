package ek.de.keepasskeyboard.database;


import java.util.List;

import de.slackspace.openkeepass.KeePassDatabase;
import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.KeePassFile;

/**
 * Created by Enrico on 07.06.2016.
 */
public class KeepassHandler {


    private KeePassFile database;


    public KeepassHandler() {

    }

    public void unlockDatabase(String file, String password){
        database = KeePassDatabase.getInstance(file).openDatabase(password);

    }

    public List<Entry> getAllEntries(){
        // Retrieve all entries
        List<Entry> entries = database.getEntries();
        for (Entry entry : entries) {
            System.out.println("Title: " + entry.getTitle() + " Password: " + entry.getPassword() + "Group " + entry.getGroupName());
        }
        return entries;
    }

    public List<Entry> searchByString(String search){

        // Search for all entries that contain 'Sample' in title
        List<Entry> entriesByTitle = database.getEntriesByTitle("Sample", false);
        for (Entry entry : entriesByTitle) {
            System.out.println("Title: " + entry.getTitle() + " Password: " + entry.getPassword());
        }
        return entriesByTitle;
    }
}
