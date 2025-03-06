package voteapp.geostorageservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import voteapp.geostorageservice.aop.Logging;

@Controller
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Logging
@Slf4j
public class DashboardViewController {

    @GetMapping("/login")
    public String showLogin() {
        return "index";
    }
}
