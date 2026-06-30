package com.texas.developers.ams.controller;


import com.texas.developers.ams.configuration.service.DashboardService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("dashboardDataDto", dashboardService.getDashboardData());
        return "dashboard";
    }
}
