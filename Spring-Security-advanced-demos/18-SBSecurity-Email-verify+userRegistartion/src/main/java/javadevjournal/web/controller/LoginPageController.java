package javadevjournal.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javadevjournal.core.user.service.CustomerAccountService;
import javadevjournal.web.data.user.ResetPasswordData;

@Controller
@RequestMapping("/login")
public class LoginPageController {

    public static final String LAST_USERNAME_KEY = "LAST_USERNAME";

    @Resource(name = "customerAccountService")
    private CustomerAccountService customerAccountService;

    @GetMapping
    public String login(@RequestParam(value = "error",defaultValue = "false") boolean loginError,
                        @RequestParam(value = "invalid-session", defaultValue = "false") boolean invalidSession,
                        final Model model, HttpSession session){

        String userName =getUserName(session);
        if(loginError){
            if(StringUtils.isNotEmpty(userName) && customerAccountService.loginDisabled(userName)){
                model.addAttribute("accountLocked", Boolean.TRUE);
                model.addAttribute("forgotPassword", new ResetPasswordData());
                return "account/login";
            }
        }
        if(invalidSession){
            model.addAttribute("invalidSession", "You already have an active session. We do not allow multiple active sessions");
        }
        model.addAttribute( "forgotPassword", new ResetPasswordData());
        model.addAttribute("accountLocked", Boolean.FALSE);
        return "account/login";
    }


    final String getUserName(HttpSession session){
        final String username = (String) session.getAttribute(LAST_USERNAME_KEY);
        if(StringUtils.isNotEmpty(username)){
            session.removeAttribute(LAST_USERNAME_KEY); // we don't need it and removing it.
        }
        return username;
    }
}
