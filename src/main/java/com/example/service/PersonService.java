package com.example.service;

import com.example.entity.Person;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.PersonMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PersonService extends ServiceImpl<PersonMapper, Person> {

    @Resource
    private PersonMapper personMapper;

}
