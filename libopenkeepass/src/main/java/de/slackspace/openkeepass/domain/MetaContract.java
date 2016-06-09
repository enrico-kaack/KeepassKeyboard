package de.slackspace.openkeepass.domain;

public interface MetaContract {

    String getGenerator();

    String getDatabaseName();

    String getDatabaseDescription();

    String getDatabaseNameChanged();

    String getDatabaseDescriptionChanged();

    int getMaintenanceHistoryDays();

    String getRecycleBinUuid();

    String getRecycleBinChanged();

    boolean getRecycleBinEnabled();

    long getHistoryMaxItems();

    long getHistoryMaxSize();

    CustomIcons getCustomIcons();
}
