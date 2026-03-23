package com.example.demo.controller.web;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthWebController {
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String redirect,
                        @RequestParam(required = false) String error, Model model) {
        model.addAttribute("redirect", redirect);
        model.addAttribute("pageTitle", "Đăng nhập");
        if (error != null) model.addAttribute("errorMsg", "Email hoặc mật khẩu không đúng");
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("pageTitle", "Đăng ký tài khoản");
        return "auth/register";
    }
}
