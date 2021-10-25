package com.disky.api.filter;
import lombok.Data;

@Data
public class UserSettingFilter {
    private Long settingId;
    private String settingName;
    private String settingDescription;
}
