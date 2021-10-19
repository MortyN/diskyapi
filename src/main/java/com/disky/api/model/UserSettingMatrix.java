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
}
