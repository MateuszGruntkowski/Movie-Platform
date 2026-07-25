package com.mgrunt.movies.constants;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AvatarConstants {
    public static final int AVATAR_COUNT = 16;

    public static final Set<String> ALLOWED_AVATARS = IntStream.rangeClosed(1, AVATAR_COUNT)
            .mapToObj(i -> "/avatars/avatar" + i + ".png")
            .collect(Collectors.toUnmodifiableSet());

    private AvatarConstants() {
    }
}
