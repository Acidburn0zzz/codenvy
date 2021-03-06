/*
 *  [2012] - [2017] Codenvy, S.A.
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
package com.codenvy.api.user.server;

import com.codenvy.api.license.server.SystemLicenseManager;
import com.codenvy.api.permission.server.SystemDomain;
import org.eclipse.che.api.core.ApiException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.user.server.UserService;
import org.eclipse.che.commons.env.EnvironmentContext;
import org.eclipse.che.commons.subject.Subject;
import org.eclipse.che.everrest.CheMethodInvokerFilter;
import org.everrest.core.Filter;
import org.everrest.core.resource.GenericResourceMethod;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Path;

import static com.codenvy.api.license.shared.model.Constants.UNABLE_TO_ADD_ACCOUNT_BECAUSE_OF_LICENSE;
import static java.lang.String.format;
import static org.eclipse.che.api.user.server.UserService.USER_SELF_CREATION_ALLOWED;

/**
 * Filter that covers calls to {@link UserService} with authorization
 *
 * @author Sergii Leschenko
 */
@Filter
@Path("/user{path:.*}")
public class UserServicePermissionsFilter extends CheMethodInvokerFilter {
    public static final String MANAGE_USERS_ACTION = "manageUsers";

    private final boolean              userSelfCreationAllowed;
    private final SystemLicenseManager licenseManager;

    @Inject
    public UserServicePermissionsFilter(@Named(USER_SELF_CREATION_ALLOWED) boolean userSelfCreationAllowed,
                                        SystemLicenseManager licenseManager) {
        this.userSelfCreationAllowed = userSelfCreationAllowed;
        this.licenseManager = licenseManager;
    }

    @Override
    protected void filter(GenericResourceMethod genericResourceMethod, Object[] arguments) throws ApiException {
        final String methodName = genericResourceMethod.getMethod().getName();
        final Subject subject = EnvironmentContext.getCurrent().getSubject();
        switch (methodName) {
            case "getCurrent":
            case "updatePassword":
            case "getById":
            case "find":
            case "getSettings":
                //public methods
                return;
            case "create":
                final String token = (String)arguments[1];
                if (token != null) {
                    //it is available to create user from token without permissions
                    if (!userSelfCreationAllowed && !subject.hasPermission(SystemDomain.DOMAIN_ID, null, MANAGE_USERS_ACTION)) {
                        throw new ForbiddenException(
                                "Currently only admins can create accounts. Please contact our Admin Team for further info.");
                    }

                    checkIfUserCanBeAdded(subject);

                    return;
                }

                checkIfUserCanBeAdded(subject);

                subject.checkPermission(SystemDomain.DOMAIN_ID, null, MANAGE_USERS_ACTION);
                break;
            case "remove":
                final String userToRemove = (String)arguments[0];
                if (subject.getUserId().equals(userToRemove)) {
                    //everybody should be able to remove himself
                    return;
                }
                subject.checkPermission(SystemDomain.DOMAIN_ID, null, MANAGE_USERS_ACTION);
                break;
            default:
                //unknown method
                throw new ForbiddenException("User is not authorized to perform this operation");
        }
    }

    private void checkIfUserCanBeAdded(Subject subject) throws ServerException, ForbiddenException {
        if (!licenseManager.canUserBeAdded()) {
            if (subject.hasPermission(SystemDomain.DOMAIN_ID, null, MANAGE_USERS_ACTION)) {
                throw new ForbiddenException(
                        format("The user cannot be added. You have %s users in Codenvy which is the maximum allowed by your current license.",
                               licenseManager.getAllowedUserNumber()));
            } else {
                throw new ForbiddenException(UNABLE_TO_ADD_ACCOUNT_BECAUSE_OF_LICENSE);
            }
        }
    }
}
