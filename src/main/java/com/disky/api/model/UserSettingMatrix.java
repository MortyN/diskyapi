package com.disky.api.model;

import lombok.Data;

@Data
public class UserSettingMatrix {
    private UserSetting userSetting;
    private User user;
    private String settingValue;
    private Boolean active;

    public UserSettingMatrix(UserSetting userSetting, User user, String settingValue, Boolean active) {
        this.userSetting = userSetting;
        this.user = user;
        this.settingValue = settingValue;
        this.active = active;
    }

    public static String getColumns(){
        return "user_settings_matrix.SETTING_ID, user_settings_matrix.SETTING_VALUE, user_settings_matrix.ACTIVE FROM user_settings_matrix ";
    }
}
