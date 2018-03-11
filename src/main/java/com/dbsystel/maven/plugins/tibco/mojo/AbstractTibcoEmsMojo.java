package com.dbsystel.maven.plugins.tibco.mojo;

import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;

public class AbstractTibcoEmsMojo extends AbstractMojo {
    protected final static String TIBCO_UTILITY_BINARY_NOTFOUND = "The TIBCO tibemsadmin binary can't be found.";

    /**
     * The Maven project.
     * 
     * 
     */
    @Parameter(property = "project", defaultValue = "${project}", required = true, readonly = true)
    protected MavenProject project;

    /**
     * The current Maven session.
     * 
     * 
     *
     */
    @Parameter(property = "session", required = true, readonly = true)
    protected MavenSession session;

    /**
     * @since 1.0
     */
    @Parameter(defaultValue = "${settings}", readonly = true, required = true)
    protected Settings settings;

    /**
     * Server's <code>id</code> in <code>settings.xml</code> to look up username and password. Defaults to
     * <code>${url}</code> if not given.
     *
     * @since 1.0
     */
    @Parameter(property = "settingsKey")
    protected String settingsKey;

    /**
     * EMS user. If not given, it will be looked up through <code>settings.xml</code>'s server with
     * <code>${settingsKey}</code> as key.
     *
     * @since 1.0
     */
    @Parameter(property = "user")
    private String userName;

    public void execute() throws MojoExecutionException, MojoFailureException {
        initialize();

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    protected void initialize() {
        List<String> activeProfiles = settings.getActiveProfiles();

        for (String profile : activeProfiles) {

        }
    }
}
