package com.xing.study.demo.controller;

import com.xing.study.demo.service.TestService;
import com.xing.study.spring.framework.annotation.*;
import com.xing.study.spring.framework.web.servlet.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author qzexing
 * @version 1.0
 * @date 2022/3/23 16:49
 */
@Lazy
@Controller("myTestController")
@RequestMapping("/web")
public class TestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Lazy
    @Autowired
    private TestService testService;

    @RequestMapping("/detail")
    public ModelAndView detail(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam("name") String name) {
        String result = testService.detail(name);
        return out(response, result);
    }

    @RequestMapping("/add")
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam("name") String name, @RequestParam("addr") String addr) {
        String result = null;
        try {
            result = testService.add(name, addr);
            return out(response, result);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Map<String, Object> model = new HashMap<>();
            model.put("detail", e.getCause().getMessage());
            model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", ""));
            return new ModelAndView("500", model);
        }
    }

    @RequestMapping("/delete")
    public ModelAndView delete(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam("id") Integer id) {
        String result = testService.delete(id);
        return out(response, result);
    }

    @RequestMapping("/edit")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam("id") Integer id,
                             @RequestParam("name") String name) {
        String result = testService.edit(id, name);
        return out(response, result);
    }


    private ModelAndView out(HttpServletResponse resp, String str) {
        Map<String, Object> map = new HashMap<>();
        map.put("user", "xingle");
        map.put("data", "hello world for spring");
        map.put("token", UUID.randomUUID().toString().replaceAll("-", ""));
        return new ModelAndView("first", map);
       /* try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;*/
    }


}
