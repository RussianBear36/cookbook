package ru.koldaev.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import ru.koldaev.entity.User;
import ru.koldaev.service.FindService;
import ru.koldaev.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

@Controller
@SessionAttributes(value = "currentUser")
public class UserController {

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

    @GetMapping("/editProfile")
    public String getEditProfilePage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        model.addAttribute("user", user);
        return "editProfile";
    }

    @PostMapping("/editProfile")
    public String editProfile
            (
                    @RequestParam String login,
                    @RequestParam String name,
                    @RequestParam String family,
                    @RequestParam String password,
                    HttpSession session,
                    HttpServletRequest request
            ) throws SQLException {
        User oldUser = (User) session.getAttribute("currentUser");
        User updatedUser = userService.updateUser(oldUser, name, family, password);
        request.getSession().setAttribute("currentUser", updatedUser);
        return "redirect:/main";
    }

}
