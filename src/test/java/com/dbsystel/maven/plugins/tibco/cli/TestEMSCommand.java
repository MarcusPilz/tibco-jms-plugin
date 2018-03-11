package com.dbsystel.maven.plugins.tibco.cli;

import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.junit.Assert;
import org.junit.Test;

public class TestEMSCommand {
    @Test
    public void testScriptEMSCommand() {

        try {
            TibEmsCommand cmd = new TibEmsCommand("D:\\tibco\\ems\\ems\\7.0\\bin\\tibemsadmin.exe");
            cmd.setServerURL("tcp://twix16-228v.linux.rz.db.de:7111");
            cmd.setUser("admin");
            cmd.setPasswd("9ed705e1d9");
            cmd.setScript(new File("src/main/resources/create_queues.ems"));
            Assert.assertTrue(cmd.execute() != 0);
        } catch (ExecuteException e) {
            //Assert.fail(e.getMessage());
        } catch (IOException e) {
            //Assert.fail(e.getMessage());
        }
    }
}
