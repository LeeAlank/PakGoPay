package com.pakgopay.controller;

import com.pakgopay.util.TokenUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/pakGoPay/server")
public class HeartController {

    @GetMapping("/heart")
    public String heart(HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");
        String token = authorization != null && authorization.startsWith("Bearer ") ? authorization.substring(7) : null;
        System.out.println("token:"+token);
        if (token != null && TokenUtils.validateToken(token)) {
            // 有效token
            return "success";
        } else {
            return "login";
        }
    }
}
