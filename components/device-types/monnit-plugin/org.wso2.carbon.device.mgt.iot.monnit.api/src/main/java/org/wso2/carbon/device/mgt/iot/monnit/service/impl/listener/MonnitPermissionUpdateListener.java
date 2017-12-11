package org.wso2.carbon.device.mgt.iot.monnit.service.impl.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.constants.Constants;
import org.wso2.carbon.user.api.AuthorizationManager;
import org.wso2.carbon.user.api.Permission;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

public class MonnitPermissionUpdateListener implements ServletContextListener {

    private static Log log = LogFactory.getLog(MonnitPermissionUpdateListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        UserStoreManager userStoreManager = getUserStoreManager();
        try {
            if (userStoreManager != null) {
                if (!userStoreManager.isExistingRole(Constants.ROLE_NAME)) {
                    userStoreManager.addRole(Constants.ROLE_NAME, null, getPermissions());
                } else {
                    getAuthorizationManager().authorizeRole(Constants.ROLE_NAME,
                            Constants.PERM_ENROLL_MONNIT, CarbonConstants.UI_PERMISSION_ACTION);
                    getAuthorizationManager().authorizeRole(Constants.ROLE_NAME,
                            Constants.PERM_OWNING_DEVICE_VIEW, CarbonConstants.UI_PERMISSION_ACTION);
                }
            }
        } catch (UserStoreException e) {
            log.error("Error while creating a role and adding a user for Arduino.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }


    public static UserStoreManager getUserStoreManager() {
        RealmService realmService;
        UserStoreManager userStoreManager;
        try {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);
            if (realmService == null) {
                String msg = "Realm service has not initialized.";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
            int tenantId = ctx.getTenantId();
            userStoreManager = realmService.getTenantUserRealm(tenantId).getUserStoreManager();
            realmService.getTenantUserRealm(tenantId).getAuthorizationManager();
        } catch (UserStoreException e) {
            String msg = "Error occurred while retrieving current user store manager";
            log.error(msg, e);
            throw new IllegalStateException(msg);
        }
        return userStoreManager;
    }

    public static AuthorizationManager getAuthorizationManager() {
        RealmService realmService;
        AuthorizationManager authorizationManager;
        try {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);
            if (realmService == null) {
                String msg = "Realm service has not initialized.";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
            int tenantId = ctx.getTenantId();
            authorizationManager = realmService.getTenantUserRealm(tenantId).getAuthorizationManager();
        } catch (UserStoreException e) {
            String msg = "Error occurred while retrieving current user store manager";
            log.error(msg, e);
            throw new IllegalStateException(msg);
        }
        return authorizationManager;
    }

    private Permission[] getPermissions() {

        Permission monnit = new Permission(Constants.PERM_ENROLL_MONNIT,
                CarbonConstants.UI_PERMISSION_ACTION);
        Permission view = new Permission(Constants.PERM_OWNING_DEVICE_VIEW, CarbonConstants
                .UI_PERMISSION_ACTION);

        return new Permission[]{monnit, view};
    }

}
