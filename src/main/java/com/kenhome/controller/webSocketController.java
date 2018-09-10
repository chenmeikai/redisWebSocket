/**
 * Copyright © 2018
 *
 * @Package: RedisController.java
 * @author: Administrator
 * @date: 2018年6月9日 下午5:26:35
 */
package com.kenhome.controller;


import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:TODO
 * @author: cmk
 * @date: 2018年6月9日 下午5:26:35
 */

@Controller
public class webSocketController {


    @GetMapping("webSocket")
    public String webSocket() {

        return "websocket/socket";

    }

    @GetMapping("demo")
    public String webSocket2() {

        return "websocket/demo";

    }


    @GetMapping("session")
    @ResponseBody
    public String session(HttpServletRequest request, String name) {

        request.getSession().setAttribute("name", name);

        return "success";

    }

    @GetMapping("sessionHtml")
    public String sessionHtml(HttpServletRequest request, String name) {

        return "websocket/session";

    }

}
