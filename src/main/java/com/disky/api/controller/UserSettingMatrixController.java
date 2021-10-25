package com.disky.api.controller;

import com.disky.api.Exceptions.GetUserException;
import com.disky.api.Exceptions.UserSettingsMatrixException;
import com.disky.api.filter.UserSettingMatrixFilter;
import com.disky.api.model.User;
import com.disky.api.model.UserSetting;
import com.disky.api.model.UserSettingMatrix;
import com.disky.api.util.DatabaseConnection;
import com.disky.api.util.Parse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UserSettingMatrixController {

    //TODO: Delete every row with a specific USER_ID. ONLY to be used when deleting a user.
    public static void deleteSettings(UserSettingMatrix userSettingMatrix) throws UserSettingsMatrixException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            int psId = 1;
            Logger log = Logger.getLogger(String.valueOf(UserSettingMatrixController.class));

            String sql = "DELETE FROM user_settings_matrix WHERE USER_ID = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(psId++, userSettingMatrix.getUser().getUserId());

            log.info("Rows affected: " + stmt.executeUpdate());

        } catch (SQLException e) {
            throw new UserSettingsMatrixException(e.getMessage());
        }
    }

    public static void deactivateSetting(UserSettingMatrix userSettingMatrix) throws UserSettingsMatrixException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            int psId = 1;
            Logger log = Logger.getLogger(String.valueOf(UserSettingMatrixController.class));

            String sql = "Update user_settings_matrix SET ACTIVE = ? WHERE SETTING_ID = ? AND USER_ID = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBoolean(psId++, userSettingMatrix.getActive());
            stmt.setLong(psId++, userSettingMatrix.getUserSetting().getSettingId());
            stmt.setLong(psId++, userSettingMatrix.getUser().getUserId());

            log.info("Rows affected " + stmt.executeUpdate());

        } catch (SQLException e) {
            throw new UserSettingsMatrixException(e.getMessage());
        }
    }

    public void save(UserSettingMatrix userSettingMatrix) throws UserSettingsMatrixException {
        Logger log = Logger.getLogger(String.valueOf(UserSettingsMatrixException.class));
        Connection conn = DatabaseConnection.getConnection();

        try {
            int psId = 1;


            //TODO: SETTING ID might not be necessary
            //TODO: However, this method is probably going to be removed eventually. Probably better to have a trigger in the DB.
            String sql = "INSERT INTO user_settings_matrix (SETTING_ID = ?, USER_ID = ?, SETTING_VALUE = ?, ACTIVE = ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(psId++, userSettingMatrix.getUserSetting().getSettingId());
            stmt.setLong(psId++, userSettingMatrix.getUser().getUserId());
            stmt.setBoolean(psId++, userSettingMatrix.getActive());

        } catch (SQLException e) {
            throw new UserSettingsMatrixException(e.getMessage());
        }
    }

    public static void update(UserSettingMatrix userSettingMatrix) throws UserSettingsMatrixException {
        Logger log = Logger.getLogger(String.valueOf(UserSettingsMatrixException.class));
        Connection conn = DatabaseConnection.getConnection();

        try {
            int psId = 1;

            String sql = "Update user_settings_matrix SET SETTING_VALUE= ? WHERE SETTING_ID = ? AND USER_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(psId++, userSettingMatrix.getSettingValue());
            stmt.setLong(psId++, userSettingMatrix.getUserSetting().getSettingId());
            stmt.setLong(psId++, userSettingMatrix.getUser().getUserId());

            log.info("Rows affected: " + stmt.executeUpdate());
        } catch (SQLException e) {
            throw new UserSettingsMatrixException(e.getMessage());
        }
    }

    public static List<UserSettingMatrix> get(UserSettingMatrixFilter filter) throws UserSettingsMatrixException {
        Logger log = Logger.getLogger(String.valueOf(UserSettingMatrixController.class));
        List<UserSettingMatrix> userSettingMatrixResult = new ArrayList<>();

        Connection conn = DatabaseConnection.getConnection();

        try {
            UserSetting userSetting = new UserSetting(filter.getSetting_Id(), filter.getSetting_Name(), filter.getSettingDescription());
            String where = "where 1=1 ";
            String innerJoin = " INNER JOIN user_settings USING(SETTING_ID)";
            if (filter.getUser().getUserId() != null) {
                where += " user_settings_matrix.USER_ID = ?";
            }
            if (filter.getSetting_Name() != null) {
                where += " AND user_settings.SETTING_NAME IN (" + filter.getSetting_Name() + ")";
            }
            if (filter.isActive()) {
                where += " AND user_settings_matrix = ?";
            }

            String sql = "SELECT " + UserSettingMatrix.getColumns() + userSetting.getColumns() + innerJoin + where;
            PreparedStatement stmt = conn.prepareStatement(sql);
            int psId = 1;

            stmt.setLong(psId++, filter.getUser().getUserId());
            stmt.setString(psId++, filter.getSetting_Name());
            stmt.setBoolean(psId++, filter.isActive());
            ResultSet res = stmt.executeQuery();

            while (res.next()) {
                UserSetting newUserSetting = new UserSetting(
                        res.getLong("user_settings.SETTING_ID"),
                        res.getString("user_setting.SETTING_NAME"),
                        res.getString("user_setting.SETTING_DESCRIPTION")
                );
                //TODO: Figure out how to create user without requesting the db.
                UserSettingMatrix userSettingMatrix = new UserSettingMatrix(
                        newUserSetting,
                        UserController.getOne(new User(
                                res.getLong("user_settings_matrix.USER_ID"))),
                        res.getString("usersettingmatrix.SETTING_VALUE"),
                        res.getBoolean("usersettingmatrix.ACTIVE")
                );
                userSettingMatrixResult.add(userSettingMatrix);
            }
            log.info("Successfully retrieved " + userSettingMatrixResult.size() + " settings.");
            return userSettingMatrixResult;

        } catch (SQLException | GetUserException e) {
            throw new UserSettingsMatrixException(e.getMessage());
        }
    }
}
