package com.islamsaadi.easyroomhelperdemo;

import com.islamsaadi.easyroomhelper.base.EasyDao;
import com.islamsaadi.easyroomhelper.base.EasyRepository;

public class UserRepository extends EasyRepository<User> {

    private final UserDao userDao;

    public UserRepository(UserDao userDao) {
        // Important! Pass entity class to super()
        // and if u want to use tables names as lower case, set 2nd param to true
        super(User.class, true);
        this.userDao = userDao;
    }

    @Override
    protected EasyDao<User> getDao() {
        return userDao;
    }
}