package com.valik.hogwarts_artifacts_online.hogwartsUser.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.valik.hogwarts_artifacts_online.hogwartsUser.HogwartsUser;
import com.valik.hogwarts_artifacts_online.hogwartsUser.dto.UserDto;

@Component
public class UserDtoToUserConverter implements Converter<UserDto, HogwartsUser> {

    @Override
    public HogwartsUser convert(@NonNull UserDto source) {
        HogwartsUser hogwartsUser = new HogwartsUser();
        hogwartsUser.setUsername(source.username());
        hogwartsUser.setEnabled(source.enabled());
        hogwartsUser.setRoles(source.roles());
        return hogwartsUser;
    }

}
