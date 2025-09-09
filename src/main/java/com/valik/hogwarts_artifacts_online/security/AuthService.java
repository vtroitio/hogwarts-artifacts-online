package com.valik.hogwarts_artifacts_online.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.valik.hogwarts_artifacts_online.hogwartsUser.HogwartsUser;
import com.valik.hogwarts_artifacts_online.hogwartsUser.MyUserPrincipal;
import com.valik.hogwarts_artifacts_online.hogwartsUser.converter.UserToUserDtoConverter;
import com.valik.hogwarts_artifacts_online.hogwartsUser.dto.UserDto;

@Service
public class AuthService {
    
    private final JwtProvider jwtProvider;

    private final UserToUserDtoConverter userToUserDtoConverter;

    public AuthService(JwtProvider jwtProvider, UserToUserDtoConverter userToUserDtoConverter) {
        this.jwtProvider = jwtProvider;
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    public Map<String, Object> createLoginInfo(Authentication authentication) {
        // Crea la información de usuario
        MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();
        HogwartsUser hogwartsUser = principal.getHogwartsUser();
        UserDto userDto = this.userToUserDtoConverter.convert(hogwartsUser);

        // Crea un JWT
        String token = this.jwtProvider.createToken(authentication);

        Map<String, Object> loginResultMap = new HashMap<>();

        loginResultMap.put("userInfo", userDto);
        loginResultMap.put("token", token);

        return loginResultMap;
    }

}
