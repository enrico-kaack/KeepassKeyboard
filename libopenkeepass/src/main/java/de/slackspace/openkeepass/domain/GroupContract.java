package de.slackspace.openkeepass.domain;

import java.util.List;
import java.util.UUID;

public interface GroupContract {

    String getUuid();

    String getName();

    int getIconId();

    Times getTimes();

    boolean isExpanded();

    byte[] getIconData();

    String getCustomIconUuid();

    List<Entry> getEntries();

    List<Group> getGroups();
}
