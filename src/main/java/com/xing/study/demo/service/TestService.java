package com.xing.study.demo.service;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/23 16:56
 */
public interface TestService {

    String detail(String name);

    String add(String name, String addr);

    String delete(Integer id);

    String edit(Integer id, String name);
}
