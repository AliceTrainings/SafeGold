package com.goldloan.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FaviconController {

    @GetMapping("favicon.ico")
    public void favicon() {
        // Do nothing, just return HTTP 200
    }
}
