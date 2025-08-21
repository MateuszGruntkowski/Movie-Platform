package com.mgrunt.movies.domain.dtos;

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
    private Set<String> moviesToWatchIds;
    private Set<String> moviesWatchedIds;
    private List<UUID> reviewIds;

}
