package com.example.mapper;

import com.example.entity.Person;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface PersonMapper extends BaseMapper<Person> {
    public int isExistAccount(String account);
}
