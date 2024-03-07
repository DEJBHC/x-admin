package com.example.util;

import com.example.mapper.PersonMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Random;
@Service
public class Generate {
    @Resource
    private PersonMapper personMapper;
    public String getAccount() {
        String caseAccount = "" ;
        Random random = new Random();
        while (true) {
            for (int i = 0; i < 8; i++) {
                caseAccount = caseAccount + "" + random.nextInt(9);
            }
            int index = personMapper.isExistAccount(caseAccount);
            if(index==0){
                break;
            }
        }
        return caseAccount;
    }
}
