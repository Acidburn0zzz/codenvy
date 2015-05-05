/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2015] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.im.cli.command;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;

/**
 * @author Anatoliy Bazko
 */
@Command(scope = "codenvy", name = "im-password", description = "Change Codenvy admin password")
public class ChangeAdminPasswordCommand extends AbstractIMCommand {

    @Argument(name = "password", description = "new admin password", required = true, multiValued = false, index = 0)
    private String password;

    @Override
    protected void doExecuteCommand() throws Exception {
        console.printResponse(facade.changeAdminPassword(password.getBytes("UTF-8")));
    }
}
