package com.disky.api.controller;

import com.disky.api.Exceptions.UserSettingException;
import com.disky.api.filter.UserSettingFilter;
import com.disky.api.model.UserSetting;
import com.disky.api.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UserSettingController {

    //TODO: DELTE - Done, but the query could be improved. INNER join didn't work.


    public static void delete(UserSetting userSetting) throws UserSettingException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            Logger log = Logger.getLogger(String.valueOf(UserSettingController.class));

            String sql = "DELETE FROM user_settings_matrix WHERE user_settings_matrix.SETTING_ID = ?; DELETE FROM user_settings WHERE SETTING_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setLong(1, userSetting.getSettingId());
            stmt.setLong(2, userSetting.getSettingId());

            log.info("Deleted number of columns: " + stmt.executeUpdate());
        } catch (SQLException e) {
            throw new UserSettingException("Unable to delete the setting.");
        }
    }

    public static void save(UserSetting userSetting) throws UserSettingException {
        Logger log = Logger.getLogger(String.valueOf(UserSettingController.class));
        Connection conn = DatabaseConnection.getConnection();

        try {
            int psId = 1;
            if (userSetting.getSettingId() != 0L) {
                update(userSetting);
                return;
            }

            String sql = "INSERT INTO user_settings (SETTING_NAME, SETTING_DESCRIPTION) VALUES (?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(psId++, userSetting.getSettingName());
            stmt.setString(psId++, userSetting.getSettingDescription());

            log.info("Rows affected: " + stmt.executeUpdate());
        } catch (SQLException e) {
            throw new UserSettingException(e.getMessage());
        }
    }

    private static void update(UserSetting userSetting) throws UserSettingException {
        Logger log = Logger.getLogger(String.valueOf(UserSetting.class));
        Connection conn = DatabaseConnection.getConnection();

        try {
            int psId = 1;

            String sql = "UPDATE user_settings SET SETTING_NAME = ?, SETTING_DESCRIPTION = ? WHERE SETTING_ID = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(psId++, userSetting.getSettingName());
            stmt.setString(psId++, userSetting.getSettingDescription());
            stmt.setLong(psId++, userSetting.getSettingId());
        } catch (SQLException e) {
            throw new UserSettingException(e.getMessage());
        }
    }

    public static List<UserSetting> get(UserSettingFilter filter) throws UserSettingException {
        Logger log = Logger.getLogger(String.valueOf(UserSettingController.class));
        List<UserSetting> userSettingResults = new ArrayList<>();

        Connection conn = DatabaseConnection.getConnection();

        try {
            UserSetting userSetting = new UserSetting(filter.getSettingId(), filter.getSettingName(), filter.getSettingDescription());
            String where = "where";
            if (filter.getSettingId() != null) {
                where += " user_settings.SETTING_ID = ?";
            }
            if (filter.getSettingName() != null) {
                where += "AND user_settings.SETTING_NAME = ?";
            }
            if (filter.getSettingDescription() != null) {
                where += " AND user_settings.SETTING_DESCRIPTION = ?";
            }

            String sql = "SELECT " + where;
            PreparedStatement stmt = conn.prepareStatement(sql);
            int psId = 1;

            stmt.setLong(psId ++, filter.getSettingId());
            stmt.setString(psId++, filter.getSettingName());
            stmt.setString(psId++, filter.getSettingDescription());
            ResultSet res = stmt.executeQuery();

            while (res.next()){
                UserSetting newUserSetting  = new UserSetting(
                        res.getLong("user_settings.SETTING_ID"),
                        res.getString("user_settings.SETTING_NAME"),
                        res.getString("user_settings.SETTING_DESCRIPTION"));
                userSettingResults.add(newUserSetting);
            }
            log.info("Successfully retrieved: " + userSettingResults.size() + " settings");
            return userSettingResults;
        }catch (SQLException e){
            throw new UserSettingException(e.getMessage());
        }

    }

}
