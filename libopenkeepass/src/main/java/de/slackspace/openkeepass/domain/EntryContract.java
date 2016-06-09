package de.slackspace.openkeepass.domain;

import java.util.List;

public interface EntryContract {

    String getUuid();

    byte[] getIconData();

    int getIconId();

    String getCustomIconUUID();

    String getTitle();

    String getUsername();

    String getPassword();

    String getNotes();

    String getUrl();

    List<Property> getCustomPropertyList();

    History getHistory();

    Times getTimes();
}
