package indi.mat.work.project.service.sample.impl;

import indi.mat.work.project.dao.sample.UserMapper;
import indi.mat.work.project.model.sample.User;
import indi.mat.work.project.service.sample.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper mapper;


    @Override
    public List<User> get() {
       return mapper.selectList(null);
    }

    @CacheEvict(value = "user", key = "#id")
    public void delete(Integer id) {
        System.out.println("删除key为[" + id + "]的缓存");
    }

    @Cacheable(value = "user", key = "#id", sync = true)
    public User getById(Integer id) {
        System.out.println("操作数据库，进行通过ID查询，ID: " + id);
        return new User(id, "admin", "123", 18);
    }

}