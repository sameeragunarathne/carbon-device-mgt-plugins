package org.wso2.carbon.device.mgt.iot.droneanalyzer.plugin.impl.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.iot.droneanalyzer.plugin.constants.DroneConstants;
import org.wso2.carbon.device.mgt.iot.droneanalyzer.plugin.impl.dao.DroneAnalyzerDAO;
import org.wso2.carbon.device.mgt.iot.util.iotdevice.dao.IotDeviceDAO;
import org.wso2.carbon.device.mgt.iot.util.iotdevice.dao.IotDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.iot.util.iotdevice.dao.util.IotDeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.iot.util.iotdevice.dto.IotDevice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by geesara on 12/9/15.
 */
public class DroneAnalyzerDeviceDAOImpl implements IotDeviceDAO {

    private static final Log log = LogFactory.getLog(DroneAnalyzerDeviceDAOImpl.class);

    @Override
    public IotDevice getIotDevice(String iotDeviceId) throws IotDeviceManagementDAOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        IotDevice iotDevice = null;
        ResultSet resultSet = null;
        try {
            conn = DroneAnalyzerDAO.getConnection();
            String selectDBQuery =
                    "SELECT VIRTUAL_FIREALARM_DEVICE_ID, DEVICE_NAME" +
                            " FROM VIRTUAL_FIREALARM_DEVICE WHERE VIRTUAL_FIREALARM_DEVICE_ID = ?";
            stmt = conn.prepareStatement(selectDBQuery);
            stmt.setString(1, iotDeviceId);
            resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                iotDevice = new IotDevice();
                iotDevice.setIotDeviceName(resultSet.getString(
                        DroneConstants.DEVICE_PLUGIN_DEVICE_NAME));
                Map<String, String> propertyMap = new HashMap<String, String>();
                iotDevice.setDeviceProperties(propertyMap);
                if (log.isDebugEnabled()) {
                    log.debug("Drone device " + iotDeviceId + " data has been fetched from " +
                            "Drone database.");
                }
            }
        } catch (SQLException e) {
            String msg = "Error occurred while fetching Drone device : '" + iotDeviceId + "'";
            log.error(msg, e);
            throw new IotDeviceManagementDAOException(msg, e);
        } finally {
            IotDeviceManagementDAOUtil.cleanupResources(stmt, resultSet);
            DroneAnalyzerDAO.closeConnection();
        }
        return iotDevice;
    }

    @Override
    public boolean addIotDevice(IotDevice iotDevice) throws IotDeviceManagementDAOException {
        boolean status = false;
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DroneAnalyzerDAO.getConnection();
            String createDBQuery =
                    "INSERT INTO VIRTUAL_FIREALARM_DEVICE(VIRTUAL_FIREALARM_DEVICE_ID, DEVICE_NAME) VALUES (?, ?)";

            stmt = conn.prepareStatement(createDBQuery);
            stmt.setString(1, iotDevice.getIotDeviceId());
            stmt.setString(2,iotDevice.getIotDeviceName());
            if (iotDevice.getDeviceProperties() == null) {
                iotDevice.setDeviceProperties(new HashMap<String, String>());
            }


            int rows = stmt.executeUpdate();
            if (rows > 0) {
                status = true;
                if (log.isDebugEnabled()) {
                    log.debug("Virtual Firealarm device " + iotDevice.getIotDeviceId() + " data has been" +
                            " added to the Virtual Firealarm database.");
                }
            }
        } catch (SQLException e) {
            String msg = "Error occurred while adding the Virtual Firealarm device '" +
                    iotDevice.getIotDeviceId() + "' to the Virtual Firealarm db.";
            log.error(msg, e);
            throw new IotDeviceManagementDAOException(msg, e);
        } finally {
            IotDeviceManagementDAOUtil.cleanupResources(stmt, null);
        }
        return status;
    }

    @Override
    public boolean updateIotDevice(IotDevice iotDevice)
            throws IotDeviceManagementDAOException {
        boolean status = false;
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DroneAnalyzerDAO.getConnection();
            String updateDBQuery =
                    "UPDATE VIRTUAL_FIREALARM_DEVICE SET  DEVICE_NAME = ? WHERE VIRTUAL_FIREALARM_DEVICE_ID = ?";
            stmt = conn.prepareStatement(updateDBQuery);
            if (iotDevice.getDeviceProperties() == null) {
                iotDevice.setDeviceProperties(new HashMap<String, String>());
            }
            stmt.setString(1, iotDevice.getIotDeviceName());
            stmt.setString(2, iotDevice.getIotDeviceId());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                status = true;
                if (log.isDebugEnabled()) {
                    log.debug("Drone device " + iotDevice.getIotDeviceId() + " data has been" +
                            " modified.");
                }
            }
        } catch (SQLException e) {
            String msg = "Error occurred while modifying the Drone device '" +
                    iotDevice.getIotDeviceId() + "' data.";
            log.error(msg, e);
            throw new IotDeviceManagementDAOException(msg, e);
        } finally {
            IotDeviceManagementDAOUtil.cleanupResources(stmt, null);
        }
        return status;
    }

    @Override
    public boolean deleteIotDevice(String iotDeviceId)
            throws IotDeviceManagementDAOException {
        boolean status = false;
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DroneAnalyzerDAO.getConnection();
            System.out.println("delete device ");
           /* String deleteDBQuery =
                    "DELETE FROM VIRTUAL_FIREALARM_DEVICE WHERE VIRTUAL_FIREALARM_DEVICE_ID = ?";
            stmt = conn.prepareStatement(deleteDBQuery);
            stmt.setString(1, iotDeviceId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                status = true;
                if (log.isDebugEnabled()) {
                    log.debug("Drone device " + iotDeviceId + " data has deleted" +
                            " from the Virtual Drone database.");
                }
            }*/
        } /*catch (SQLException e) {
            String msg = "Error occurred while deleting Virtual Firealarm device " + iotDeviceId;
            log.error(msg, e);
            throw new IotDeviceManagementDAOException(msg, e);
        }*/ finally {
            IotDeviceManagementDAOUtil.cleanupResources(stmt, null);
        }
        return status;

    }

    @Override
    public List<IotDevice> getAllIotDevices()
            throws IotDeviceManagementDAOException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        IotDevice iotDevice;
        List<IotDevice> iotDevices = new ArrayList<IotDevice>();

        try {
            conn = DroneAnalyzerDAO.getConnection();
            String selectDBQuery =
                    "SELECT VIRTUAL_FIREALARM_DEVICE_ID, DEVICE_NAME " +
                            "FROM VIRTUAL_FIREALARM_DEVICE";
            stmt = conn.prepareStatement(selectDBQuery);
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                iotDevice = new IotDevice();
                iotDevice.setIotDeviceId(resultSet.getString(DroneConstants.DEVICE_PLUGIN_DEVICE_ID));
                iotDevice.setIotDeviceName(resultSet.getString(DroneConstants.DEVICE_PLUGIN_DEVICE_NAME));

                Map<String, String> propertyMap = new HashMap<String, String>();

                iotDevice.setDeviceProperties(propertyMap);
                iotDevices.add(iotDevice);
            }
            if (log.isDebugEnabled()) {
                log.debug("All Virtual Firealarm device details have fetched from Firealarm database.");
            }
            return iotDevices;
        } catch (SQLException e) {
            String msg = "Error occurred while fetching all Virtual Firealarm device data'";
            log.error(msg, e);
            throw new IotDeviceManagementDAOException(msg, e);
        } finally {
            IotDeviceManagementDAOUtil.cleanupResources(stmt, resultSet);
            DroneAnalyzerDAO.closeConnection();
        }

    }
}
