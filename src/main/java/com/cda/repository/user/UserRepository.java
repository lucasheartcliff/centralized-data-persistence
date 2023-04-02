package com.cda.repository.user;

import com.cda.model.User;
import com.cda.repository.EntityRepository;

public interface UserRepository extends EntityRepository<User, Long> {

    User findUserByEmail(String email);
}
