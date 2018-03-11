package com.dbsystel.maven.plugins.tibco.mojo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Server;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;

import com.dbsystel.maven.plugins.tibco.cli.TibEmsCommand;
import com.dbsystel.maven.plugins.tibco.common.PropertyNames;
import com.dbsystel.maven.plugins.tibco.common.Topic;

/**
 * Executes JMS against Tibco EMS Server
 * 
 * @author MarcusPilz
 *
 */
@Mojo(name = "execute", requiresProject = true, threadSafe = true)
public class TibcoEmsAdminExecMojo extends AbstractTibcoEmsMojo {

    /**
     * Call  with this value to abort JMS command execution if an error is found.
     */
    public static final String ON_ERROR_ABORT = "abort";

    /**
     * Call  with this value to continue JMS command execution until all commands have been
     * attempted, then abort the build if an SQL error occurred in any of the commands.
     */
    public static final String ON_ERROR_ABORT_AFTER = "abortAfter";

    /**
     * Call  with this value to continue JMS command execution if an error is found.
     */
    public static final String ON_ERROR_CONTINUE = "continue";

    /**
     * Call with this value to sort in ascendant order the sql files.
     */
    public static final String FILE_SORTING_ASC = "ascending";

    /**
     * Call with this value to sort in descendant order the sql files.
     */
    public static final String FILE_SORTING_DSC = "descending";

    /**
     * The default SQL delimiter which is used to separate statements.
     */
    public static final String DEFAULT_DELIMITER = ";";

    //////////////////////////// User Info ///////////////////////////////////

    /**
     * ems password. If not given, it will be looked up through <code>settings.xml</code>'s server with
     * <code>${settingsKey}</code> as key.
     *
     * @since 1.0
     */
    @Parameter(property = "password")
    private String password;

    /**
     * Database password. If not given, it will be looked up through <code>settings.xml</code>'s server with
     * <code>${settingsKey}</code> as key.
     *
     * @since 1.0
     */
    @Parameter(property = "pwdFile")
    private File pwdFile;

    /**
     * -ignore - ignore errors when executing script file
     */
    @Parameter(property = "ignore", defaultValue = "false")
    private boolean ignore;

    /**
     * Ignore the password and use anonymous access. This may be useful for databases like MySQL which do not allow
     * empty password parameters in the connection initialization.
     *
     * @since 1.0
     */
    @Parameter(defaultValue = "false")
    private boolean enableAnonymousPassword;

    /**
     * 
     * 
     * @since 1.0
     */
    @Component(role = org.sonatype.plexus.components.sec.dispatcher.SecDispatcher.class, hint = "default")
    private SecDispatcher securityDispatcher;

    /**
     * Skip missing files defined by {@link #setSrcFiles(File[])}. This behavior allows to define a full fledged list of
     * all jms files in a single {@code pom.xml} without failing if used by modules for which some jms files are not
     * available on the classpath.
     */
    @Parameter(defaultValue = "false", property = "skipMissingFiles")
    private boolean skipMissingFiles;

    /**
     * Setting this parameter to <code>true</code> will force the execution of this mojo, even if it would get skipped
     * usually.
     */
    @Parameter(defaultValue = "false", property = "forceExecution", required = true)
    private boolean forceMojoExecution;

    //////////////////////////////// Source info /////////////////////////////
    /**
     * JMS input commands separated by <code>${delimiter}</code>.
     *
     * @since 1.0
     */
    @Parameter(property = "jmsCommand")
    private String jmsCommand;

    /**
     * List of files containing JMS statements to execute.
     *
     */
    @Parameter
    private File[] srcFiles;

    /**
     * File(s) containing JSM statements to execute. Only use a Fileset if you want to use ant-like filepatterns,
     * otherwise use srcFiles. The order is based on a matching occurrence while scanning the directory (not the order
     * of includes!).
     * 
     * @since 1.0
     * @see #srcFiles
     */
    @Parameter
    private FileSet fileset;

    /**
     * 
     */
    @Parameter
    private Topic[] topic;

    /**
     * When <code>true</code>, skip the execution.
     *
     * @since 1.0
     */
    @Parameter(defaultValue = "false")
    private boolean skip;

    //////////////////////////////////JMS Server info /////////////////////////
    /**
     * Server URL.
     *
     * @since 1.0
     */
    @Parameter(property = "serverUrl", required = true)
    private String serverUrl;

    ///////////////////////////////// TIBCO Specific /////////////////////////

    /**
     * Path to the TIBCO home directory.
     * 
     * 
     */
    @Parameter(property = PropertyNames.EMS_HOME_DIR)
    protected File emsHome;

    /**
     * Name of tibemsadmin
     * 
     * 
     */
    private String EMS_ADMIN_UTILITY = "tibemsadmin";

    @Parameter(property = PropertyNames.EMS_ADMIN_UTILITY_PATH)
    protected File emsAdmin;

    /**
     * Used Tibco Version
     * 
     *
     */
    @Parameter(property = PropertyNames.TIBCO_EMS_VERSION, required = false, defaultValue = "7.0")
    protected String emsVersion;

    //
    // helper accessors
    //
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerUrl() {
        return this.serverUrl;
    }

    public void setServerUrl(String url) {
        this.serverUrl = url;
    }

    /**
     * <p>
     * Determine if the mojo execution should get skipped.
     * 
     * This is the case if:
     * <ul>
     * <li>{@link #skip} is <code>true</code></li>
     * <li>if the mojo gets executed on a project with packaging type 'pom' and {@link #forceMojoExecution} is
     * <code>false</code></li>
     * </ul>
     * 
     * @return <code>true</code> if the mojo execution should be skipped.
     */
    protected boolean skipMojo() {
        if (skip) {
            getLog().info(PropertyNames.SKIPPING);
            return true;
        }

        if (!forceMojoExecution && project != null && "pom".equals(project.getPackaging())) {
            getLog().info("Skipping jms execution for project with packaging type 'pom'");
            return true;
        }

        return false;
    }

    /**
     * Load username password from settings if user has not set them in JVM properties
     * 
     * @throws MojoExecutionException if error occure
     */
    private void loadUserInfoFromSettings() throws MojoExecutionException {
        if (settingsKey == null) {
            this.getLog().info("There is no settingsKey defined. Credentials must be set in POM");
            settingsKey = getServerUrl();
        }

        if ((getUserName() == null || getPassword() == null) && (settings != null)) {
            Server server = this.settings.getServer(this.settingsKey);

            if (server != null) {
                if (getUserName() == null) {
                    setUserName(server.getUsername());
                    this.getLog().debug(" set user to " + server.getUsername());
                }

                if (getPassword() == null && server.getPassword() != null) {
                    try {
                        this.getLog().debug("... getting passwd..." + server.getPassword());
                        setPassword(securityDispatcher.decrypt(server.getPassword()));
                    } catch (SecDispatcherException e) {
                        throw new MojoExecutionException(e.getMessage());
                    }
                }
            }
        }

    }

    /**
     * Check Property Setting to AppManage Binary is correct
     * 
     * @throws MojoExecutionException if error occur
     * @throws MojoFailureException if error occur
     */
    protected void checkUtilities() throws MojoExecutionException, MojoFailureException {
        this.getLog().debug("Check " + PropertyNames.EMS_ADMIN_UTILITY + " Utility in... " + emsHome);
        String fileExtension = "";
        if (SystemUtils.IS_OS_WINDOWS) {
            fileExtension = ".exe";
        } else {
            fileExtension = "";
        }

        if (emsHome != null && emsAdmin == null) {
            emsAdmin = new File(emsHome + System.getProperty("file.separator") + emsVersion
                    + System.getProperty("file.separator") + "bin" + System.getProperty("file.separator")
                    + PropertyNames.EMS_ADMIN_UTILITY + fileExtension);
        }

        if (emsAdmin == null || !emsAdmin.exists() || !emsAdmin.canExecute()) {
            throw new MojoExecutionException(TIBCO_UTILITY_BINARY_NOTFOUND);
        }
        this.getLog().info("Successfully Checked " + PropertyNames.EMS_ADMIN_UTILITY);
    }

    /**
     * @param script the File which contains statements to execute
     * @return the return value of the binary 
     * @throws MojoExecutionException if error occur
     */
    protected int launchTIBCOBinary(File script) throws MojoExecutionException {
        this.getLog().info("launch..." + emsAdmin.getAbsolutePath());
        int retCode = 0;

        try {
            /*
             * if (pwdFile != null) cmd.setPwdFile(pwdFile);
             */
            if (this.getUserName() == null || this.getPassword() == null) {
                throw new MojoExecutionException("Credentials for tibemsadmin not set!");
            }

            List<String> connectionUrl = null;
            if (this.getServerUrl() != null) {
                connectionUrl = parseServerUrl(serverUrl);
            }
            this.getLog().debug("... execute..." + connectionUrl.size());
            TibEmsCommand cmd = new TibEmsCommand(emsAdmin.getAbsolutePath());
            for (String url : connectionUrl) {
                this.getLog().info("...execute for " + url);
                this.getLog().info("...execute with user :" + getUserName());
                cmd.setUser(getUserName());
                cmd.setPasswd(password);
                cmd.setIgnore(ignore);
                cmd.setScript(script);
                cmd.setServerURL(url);
                this.getLog().debug(cmd.arguments());
                retCode = cmd.execute();
            }

            return retCode;
        } catch (ExecuteException e) {
            throw new MojoExecutionException("Failure ..." + script, e);
        }

    }

    /**
     * The serverUrl might be a comma separated List of several serverUrls. The Method extract each to connect to and execute the scripts. 
     * @param serverUrl value of URL connect to
     * @return a list of serverUrl as String
     */
    protected List<String> parseServerUrl(String serverUrl) {
        List<String> retVal = new ArrayList<String>();
        String delim = ",";
        StringTokenizer st = new StringTokenizer(serverUrl, delim);
        while (st.hasMoreElements()) {
            retVal.add((String) st.nextElement());
        }
        return retVal;
    }

    /**
     * 
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipMojo()) {
            return;
        }
        this.initialize();
        checkUtilities();
        loadUserInfoFromSettings();
        this.getLog().debug("Number of Files to execute..." + this.srcFiles.length);
        for (File srcFile : srcFiles) {
            this.getLog().debug("Execute tibemsadmin with ..." + srcFile);
            if (!srcFile.exists())
                throw new MojoExecutionException(srcFile + " does not exist!");
            launchTIBCOBinary(srcFile);
        }

    }

}
