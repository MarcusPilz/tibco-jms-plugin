package com.dbsystel.maven.plugins.tibco.cli;

/**
 * {@code Usage: tibemsadmin [<arguments>]
 * 
 * where <arguments> are:
 * 
 * -help - print this help screen -server <server-url> - connect to specified server -user <user-name> - use this user
 * name to connect to server -password <password> - use this password to connect to server -pwdfile <passwd file> - use
 * the password in the specified file -script <script-file> - execute specified script file and quit -ignore - ignore
 * errors when executing script file
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
 * If no trusted certificates were specified in the parameters, then tibemsadmin will trust any server. If it is not
 * desirable then at least one ssl_trusted parameter must be specified.
 * 
 * Parameter -script may be combined with -server, -user, -password and -pwdfile. Parameter -ignore instructs to ignore
 * errors while executing the script file. This only affects errors in command execution but not syntax errors in the
 * script.
 * 
 * Examples: tibemsadmin -script config.scr tibemsadmin -server "tcp://myhost:7222" tibemsadmin -server
 * "tcp://myhost:7222" -user admin -password secret
 * 
 * }
 * 
 */
public class CreateTopic {

}
