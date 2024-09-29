package com.my.memo.domain.user;

public interface UserRepository {

    Long save(User user);

    boolean existsByEmail(String email);


}
