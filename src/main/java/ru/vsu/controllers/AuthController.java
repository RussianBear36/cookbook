package ru.vsu.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import ru.vsu.entity.User;
import ru.vsu.service.FindService;
import ru.vsu.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@Controller
@SessionAttributes(value = "currentUser")
public class AuthController {

    @GetMapping("/")
    public String enterPage() {
        return "redirect:/login";
    }

//    @GetMapping("/main")
//    public String getFirstPage(Model model)
//    {
//        return "index";
//    }

    private FindService findService;
    private UserService userService;

    @Autowired
    public void setFindService(FindService findService) {
        this.findService = findService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("/registration")
    public String getRegistryPage() {
        return "registration";
    }

    @PostMapping("/registration")
    public String createNewUser
            (
                    @RequestParam String login,
                    @RequestParam String password,
                    @RequestParam String name,
                    @RequestParam String family,
                    Model model
            ) throws SQLException {
        if (userService.userExist(login)) {
            model.addAttribute("error", "Пользователь с таким логином уже существует!");
            return "registration";
        }
        userService.createUser(name, family, login, password);
        return "login";
    }

    @PostMapping("/login")
    public String login
            (
                    @RequestParam String login,
                    @RequestParam String password,
                    Model model,
                    HttpServletRequest request
            ) throws SQLException {
        User user = userService.getUserAfterLogin(login, password);
        if (user == null) {
            model.addAttribute("error", "Ошибка авторизации! Пожалуйста, попробуйте снова!");
            return "login";
        }

        request.getSession().setAttribute("currentUser", user);

        return "redirect:/main";
    }

    @GetMapping("/logout")
    public String logout(SessionStatus status) {
        status.setComplete();
        return "login";
    }

}
