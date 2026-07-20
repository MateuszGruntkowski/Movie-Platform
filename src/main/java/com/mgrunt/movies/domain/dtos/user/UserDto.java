package com.mgrunt.movies.domain.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String username;
    private String avatarPath;
    private Set<Long> moviesToWatchIds;
    private Set<Long> moviesWatchedIds;
}
