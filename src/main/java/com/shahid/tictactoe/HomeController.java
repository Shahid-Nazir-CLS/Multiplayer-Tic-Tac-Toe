package com.shahid.tictactoe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/")
public class HomeController {
    @Autowired

    @GetMapping("/")
    public String showGame() {
        return "home";
    }

    @GetMapping("/game")
    public String showGame(Model model) {
        return "index";
    }
}
