package com.AMIR.SRM.controllers;

import com.AMIR.SRM.domain.*;
import com.AMIR.SRM.repositories.PastOrderRepo;
import com.AMIR.SRM.repositories.UserRepo;
import com.AMIR.SRM.service.OrderService;
import com.AMIR.SRM.service.PastOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.AMIR.SRM.repositories.OrderRepo;

import java.sql.Date;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("srm/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private PastOrderService pastOrderService;
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private PastOrderRepo pastOrderRepo;
    @Autowired
    private UserRepo userRepo;
    @GetMapping("")
    public String admin(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        model.addAttribute("title", "Админ-панель");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());
        model.addAttribute("users", userRepo.findAll());
        return "SRM/admin/admin";
    }

    @GetMapping("banned")
    public String banned(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        model.addAttribute("title", "Админ-панель");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());
        model.addAttribute("users", userRepo.findAll());
        return "SRM/admin/banned";
    }

    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Изменение пользователя");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", Role.values());
        model.addAttribute("role", authentication.getAuthorities().toString());
        model.addAttribute("user", user);
        return "SRM/admin/userEdit";
    }

    @PostMapping()
    public String userSave(
            @RequestParam String role,
            @RequestParam("userId") User user
    ){
        user.getRoles().clear();
        user.getRoles().add(Role.valueOf(role));

        userRepo.save(user);
        return "redirect:/srm/admin/";
    }

    @GetMapping("ban/{user}")
    public String banUser(@PathVariable User user, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Забан");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", Role.values());
        model.addAttribute("role", authentication.getAuthorities().toString());
        model.addAttribute("user", user);

        user.setActive(false);
        userRepo.save(user);
        return "redirect:/srm/admin/";
    }

    @GetMapping("unban/{user}")
    public String unbanUser(@PathVariable User user, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "разбан");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", Role.values());
        model.addAttribute("role", authentication.getAuthorities().toString());
        model.addAttribute("user", user);
        user.setActive(true);
        userRepo.save(user);
        return "redirect:/srm/admin/";
    }



    @GetMapping("current_orders")
    public String current_orders(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Текущие заказы");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());

        List<Order> order = orderService.getAllOrdersWithProviderName();

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
        return "SRM/admin/all_curr_order_stats";
    }
    @GetMapping("current_orders/cancelling/{order}")
    public String cancel_order(@PathVariable Order order, Model model) {
        PastOrder pastOrder = new PastOrder(order, "canceled");
        pastOrderRepo.save(pastOrder);
        orderRepo.delete(order);
        return "redirect:/srm/admin/current_orders";
    }

    @GetMapping("completed_orders")
    public String completed_orders(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Завершенные заказы");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());

        List<PastOrder> pastOrder = pastOrderService.getAllPastOrdersWithProviderName();

        model.addAttribute("pastOrder", pastOrder);
        return "SRM/admin/all_past_order_stats";
    }

    @GetMapping("completed_orders/analytics")
    public String analyticOrders(Model model) {
        DecimalFormat df = new DecimalFormat("#,###.00");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Аналитика по завершенным заказам");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());

        List<PastOrder> pastOrder = pastOrderService.getAllPastOrdersWithProviderNameByAuthor(authentication.getName());
        int countOfPastOrders = pastOrder.size();
        for (int i = 0; i < pastOrder.size(); i++) {
            if (Objects.equals(pastOrder.get(i).getStatus(), "canceled")) {
                pastOrder.remove(i);
                i--;
            }
        }

        int count = 0;
        float sum = 0;

        PastOrder pastOrderI;
        for (int i = 0; i < pastOrder.size(); i++) {
            pastOrderI = pastOrder.get(i);
            count += pastOrderI.getCount();
            sum += pastOrderI.getCount() * pastOrderI.getMax_price();
        }

        float avgPrice = (float) (Math.ceil(sum * 100 / count) / 100);

        float percentOfCanceled;
        try {
            percentOfCanceled = (float) Math.ceil((countOfPastOrders - pastOrder.size()) * 10000 / countOfPastOrders) / 100;
        } catch (ArithmeticException e) {
            percentOfCanceled = 0;
        }

        model.addAttribute("sum", df.format(sum));
        model.addAttribute("count", count);
        model.addAttribute("avgPrice", avgPrice);
        model.addAttribute("percentOfCanceled", percentOfCanceled);

        return "SRM/orders/past_orders/analytics";
    }

    @GetMapping("canceled_orders")
    public String canceled_orders(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Отмененные заказы");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());

        List<PastOrder> pastOrder = pastOrderService.getAllPastOrdersWithProviderName();

        model.addAttribute("pastOrder", pastOrder);
        return "SRM/admin/all_canceled_order_stats";
    }

    @GetMapping("youtube")
    public String youtube(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());
        return "SRM/admin/youtube";
    }
}
