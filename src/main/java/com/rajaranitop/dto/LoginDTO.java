package com.rajaranitop.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDTO {

    private @NotNull String username;
    private @NotNull String password;
}
