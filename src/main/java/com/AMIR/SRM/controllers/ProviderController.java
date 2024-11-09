package com.AMIR.SRM.controllers;

import com.AMIR.SRM.domain.Order;
import com.AMIR.SRM.domain.PastOrder;
import com.AMIR.SRM.repositories.OrderRepo;
import com.AMIR.SRM.repositories.PastOrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Date;
import java.util.List;

@Controller
@RequestMapping("srm/supplier")
@PreAuthorize("hasAuthority('SUPPLIER')")
public class ProviderController {
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private PastOrderRepo pastOrderRepo;

    @GetMapping("current_orders")
    public String current_orders(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Текущие заказы");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());

        List<Order> order = orderRepo.findAll();

        Date currentDate = new Date(System.currentTimeMillis() - 86400000);
        for (int i = 0; i < order.size(); i++)
        {
            if (order.get(i).getExpected_date().before(currentDate) && (order.get(i).getProvider() == null))
            {
                PastOrder pastOrder = new PastOrder(order.get(i), "canceled");
                pastOrderRepo.save(pastOrder);
                orderRepo.delete(order.get(i));
            }
        }
        model.addAttribute("order", order);
        return "SRM/supplier/current_orders";
    }
}
