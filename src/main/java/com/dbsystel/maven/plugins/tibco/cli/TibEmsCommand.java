package com.dbsystel.maven.plugins.tibco.cli;

import java.io.File;

/**
 * Examples: tibemsadmin -script config.scr tibemsadmin -server "tcp://myhost:7222" tibemsadmin -server
 * "tcp://myhost:7222" -user admin -password secret Usage: tibemsadmin [<arguments>]
 * 
 * where <arguments> are:
 * 
 * -help - print this help screen -ignore - ignore errors when executing script file
 * 
 * -mangle [password] - mangle the password and quit. Mangled string in the output can be set as a value of server
 * password or server SSL password in the server configuration file. If the password is not entered it is prompted for.
 * 
 * SSL parameters (for SSL connection only):
 * 
 * -ssl_trusted <filename> - file containing trusted certificate(s). This parameter may be entered more than once if
 * required. -ssl_identity <filename> - file containing client certificate and optionally extra issuer certificate(s)
 * and private key. -ssl_issuer <filename> - file containing extra issuer certificate(s) for client-side identity.
 * -ssl_password <password> - private key or PKCS12 password. If not specified the password is prompted for if it is
 * required. -ssl_pwdfile <pwd file> - use private key or PKCS12 password in this file -ssl_key <filename> - file
 * containing private key. -ssl_noverifyhostname - do not verify host name against the name in the certificate.
 * -ssl_hostname <name> - name expected in the certificate sent by host. -ssl_trace - show loaded certificates and
 * certificates sent by the host. -ssl_debug_trace - show additional tracing, useful for debugging.
 * 
 * @author MarcusPilz
 *
 */

public class TibEmsCommand extends AbstractEMSBuilderCommand {
    /**
     * -server <server-url> - connect to specified server
     */
    private String serverUrl = null;

    /**
     * -script <script-file> - execute specified script file and quit
     */
    private File script = null;

    /**
     * -user <user-name> - use this user name to connect to server
     */
    private String userName = null;

    /**
     * -password <password> - use this password to connect to server
     */
    private String passwd = null;

    /**
     * -pwdfile <passwd file> - use the password in the specified file
     */
    private File pwdFile = null;

    private boolean ignore = false;

    public TibEmsCommand(String command) {
        super(command);
    }

    /**
     * 
     * @param command
     * @param serverUrl
     * @param userName
     * @param password
     * @param script
     */
    public TibEmsCommand(String command, String serverUrl, String userName, String password, File script) {
        super(command);
        super.addArgument(serverUrl);
        super.addArgument(userName);
        super.addArgument(password);
        super.addArgument(script.getAbsolutePath());
    }

    /**
     * 
     * @return
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * 
     * @param serverUrl
     */
    public void setServerURL(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * 
     * @return
     */
    public File getScript() {
        return script;
    }

    /**
     * 
     * @param script
     */
    public void setScript(File script) {
        this.script = script;
    }

    /**
     * 
     * @return
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 
     * @param userName
     */
    public void setUser(String userName) {
        this.userName = userName;
    }

    /**
     * 
     * @return
     */
    public String getPasswd() {
        return passwd;
    }

    /**
     * 
     * @param passwd
     */
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public File getPwdFile() {
        return pwdFile;
    }

    public void setPwdFile(File pwdFile) {
        this.pwdFile = pwdFile;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    @Override
    public String arguments() throws IllegalArgumentException {
        StringBuffer buf = this.defaultArguments();
        buf.append(" -server ");
        buf.append(this.getServerUrl());
        if (this.getPwdFile() == null) {
            buf.append(" -user ");
            if (this.getUserName() == null) {
                throw new IllegalArgumentException("Password or Username are not set correctly!");
            }
            buf.append(this.getUserName());
            if (this.getPasswd() != null & this.getPasswd().length() > 0) {
                buf.append(" -password ");
                buf.append(this.getPasswd());
            }
        } else {
            buf.append(" -pwdfile ");
            buf.append(this.getPwdFile().getAbsolutePath());
        }
        if (script != null) {
            buf.append(" -script ");
            buf.append(script.getAbsolutePath());
        }
        if (ignore) {
            buf.append(" -ignore");
        }
        return buf.toString();
    }
}
