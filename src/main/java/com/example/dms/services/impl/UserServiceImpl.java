package com.example.dms.services.impl;

import org.springframework.stereotype.Service;

import com.example.dms.domain.User;
import com.example.dms.services.UserService;

@Service
public class UserServiceImpl extends EntityCrudServiceImpl<User> implements UserService{

}
