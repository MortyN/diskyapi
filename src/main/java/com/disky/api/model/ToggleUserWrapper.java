package com.disky.api.model;

import lombok.Data;

@Data
public class ToggleUserWrapper {
    private User senderUser;
    private User recipientUser;
}
