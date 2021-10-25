package com.disky.api.filter;

import com.disky.api.model.User;
import com.disky.api.model.UserSetting;
import lombok.Data;

import java.util.List;
@Data
public class UserSettingMatrixFilter {
    private User user;
    private Long setting_Id;
    private String settingDescription;
    private String setting_Name;
    private boolean isActive = true;
}
