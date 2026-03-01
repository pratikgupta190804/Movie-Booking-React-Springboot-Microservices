package com.moviebooking.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private String id;

    @Indexed(unique = true)
    private String email;

    private String userName;
    private String name;
    @JsonIgnore
    private String password;
    private String image;

    @Builder.Default
    private boolean enabled = true;

    @CreatedDate
    private LocalDateTime createdAt ;

    @LastModifiedDate
    private LocalDateTime updatedAt ;

    @Builder.Default
    private Provider provider = Provider.LOCAL;
    private  String providerId;

    private Role role;
}
