package com.dbsystel.maven.plugins.tibco.common;

public interface PropertyNames {
    final static String SKIPPING = "Skipping...";

    /**
     * Path to the TIBCO home directory.
     */
    String EMS_HOME_DIR = "ems.home";

    /**
     * Version of the Tibco EMS Software
     */
    String TIBCO_EMS_VERSION = "ems.version";

    String EMS_ADMIN_UTILITY_PATH = "tibco.emsadmin.path";

    /**
     * Name of validate utility
     */
    String EMS_ADMIN_UTILITY = "tibemsadmin";

    /**
     * Property for user used to execute tibemsadmin
     */
    final static String userName = "user";

}
