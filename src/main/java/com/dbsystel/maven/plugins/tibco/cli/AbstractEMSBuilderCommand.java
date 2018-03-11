package com.dbsystel.maven.plugins.tibco.cli;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

public abstract class AbstractEMSBuilderCommand {
    /**
     * Commad String - tibemsadmin
     */
    protected String command;

    /**
     * List of arguments
     */
    private List<String> arguments;

    public AbstractEMSBuilderCommand(String command) {
        this.command = command;
        arguments = new ArrayList<String>();
    }

    public void addArgument(String key) {
        arguments.add(key);
    }

    public abstract String arguments();

    /**
     * @TODO CHECK IF THIS PARAMETERS SHOULD BE CALLED EVERY APPMANAGE CMD
     * @return a list with common arguments for appmanage
     */
    public StringBuffer defaultArguments() {
        StringBuffer buf = new StringBuffer();
        buf.append(command);
        return buf;
    }

    /**
     * 
     * @return
     * @throws ExecuteException
     * @throws IOException
     */
    public int execute() throws ExecuteException {
        int retCode = 0;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String command = arguments();

        CommandLine cmdLine = CommandLine.parse(command);

        Executor executor = new DefaultExecutor();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        executor.setStreamHandler(streamHandler);

        try {
            //System.out.println(cmdLine);

            retCode = executor.execute(cmdLine);
            System.out.print(outputStream.toString());
        } catch (ExecuteException e) {
            throw new ExecuteException(e.getMessage(), retCode, e);
        } catch (IOException e) {
            throw new ExecuteException(e.getMessage(), retCode, e);
        }

        return retCode;
    }

    /**
     * 
     * @return
     * @throws ExecuteException
     * @throws IOException
     */
    public int execute(ExecuteStreamHandler sh) throws ExecuteException {
        int retCode = 0;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String command = arguments();

        CommandLine cmdLine = CommandLine.parse(command);

        Executor executor = new DefaultExecutor();

        executor.setStreamHandler(sh);

        try {
            //System.out.println(cmdLine);

            retCode = executor.execute(cmdLine);
            //System.out.print(outputStream.toString());
        } catch (ExecuteException e) {
            throw new ExecuteException(e.getMessage(), retCode, e);
        } catch (IOException e) {
            throw new ExecuteException(e.getMessage(), retCode, e);
        }

        return retCode;
    }
}
