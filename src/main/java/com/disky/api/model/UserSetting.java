package com.disky.api.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class UserSetting extends GenericModel{
    private Long settingId;
    private String settingName;
    private String settingDescription;

    public UserSetting(Long settingId) {
        this.settingId = settingId;
    }

    public UserSetting(Long settingId, String settingName, String settingDescription) {
        this.settingId = settingId;
        this.settingName = settingName;
        this.settingDescription = settingDescription;
    }

    public static String getColumns(){
        return "user_settings.SETTING_NAME, user_settings.SETTING_DESCRIPTION";
    }


    @Override
    public Long getPrimaryKey() {
        return this.settingId;
    }
}
