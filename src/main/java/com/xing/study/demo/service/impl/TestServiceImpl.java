package com.xing.study.demo.service.impl;

import com.xing.study.demo.dao.TestDao;
import com.xing.study.demo.service.TestService;
import com.xing.study.spring.framework.annotation.Autowired;
import com.xing.study.spring.framework.annotation.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/23 16:56
 */
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestDao testDao;


    @Override
    public String detail(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        return "{name:\"" + name + "\",time:\"" + time + "\"}";
    }

    @Override
    public String add(String name, String addr) {
        return "TestService add,name=" + name + ",addr=" + addr;
    }

    @Override
    public String delete(Integer id) {
        return "TestService delete,id=" + id;
    }

    @Override
    public String edit(Integer id, String name) {
        return "TestService edit,id=" + id + ",name=" + name;
    }
}
