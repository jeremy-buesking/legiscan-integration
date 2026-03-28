package com.example.LegiTrack.model.dto.response;

import com.example.LegiTrack.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String userName;
    private String state;

    public static UserResponse from(UserEntity userEntity) {
        return new UserResponse(
            userEntity.getId(),
            userEntity.getEmail(),
            userEntity.getFirstName(),
            userEntity.getLastName(),
            userEntity.getUserName(),
            userEntity.getState()
        );
    }
}
