package com.spring.signalMate.domain.users.model.dto;

import com.spring.signalMate.domain.users.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatchUserResponseDto {
    private UserEntity user;
}
