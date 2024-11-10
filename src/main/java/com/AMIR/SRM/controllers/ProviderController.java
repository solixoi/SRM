package com.AMIR.SRM.controllers;

import com.AMIR.SRM.domain.Order;
import com.AMIR.SRM.domain.PastOrder;
import com.AMIR.SRM.domain.Response;
import com.AMIR.SRM.domain.User;
import com.AMIR.SRM.repositories.OrderRepo;
import com.AMIR.SRM.repositories.PastOrderRepo;
import com.AMIR.SRM.repositories.ResponseRepo;
import com.AMIR.SRM.repositories.UserRepo;
import com.AMIR.SRM.service.OrderService;
import com.AMIR.SRM.service.PastOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Date;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("srm/supplier/")
@PreAuthorize("hasAuthority('SUPPLIER')")
public class ProviderController {
    @Autowired
    private PastOrderService pastOrderService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private PastOrderRepo pastOrderRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ResponseRepo responseRepo;

    @GetMapping("current_orders")
    public String current_orders(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Текущие заказы");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());

        List<Order> order = orderService.getAllOrdersWithProviderName();

        User user = userRepo.findByUsername(authentication.getName());
        List<Response> responses = responseRepo.findBySupplier(user);

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

        for (Order orders: order) {
            boolean hasRespond = false;
            for (Response response : responses) {
                if (response.getOrder().getId() == orders.getId()) {
                    hasRespond = true;
                    break;
                }
            }
            orders.setRespond(hasRespond);
        }
        model.addAttribute("order", order);
        return "SRM/supplier/current_orders";
    }

    @GetMapping("current_orders/respond/{order}")
    public String respond_order(@PathVariable Order order, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByUsername(authentication.getName());
        Response response = new Response(user, order);
        responseRepo.save(response);
        order.setRespond(true);
        return "redirect:/srm/supplier/current_orders";
    }

    @GetMapping("current_orders/cancel/{order}")
    public String cancelling_respond(@PathVariable Order order, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByUsername(authentication.getName());
        Response response = responseRepo.findByOrderAndSupplier(order, user);
        responseRepo.deleteById(response.getId());
        order.setRespond(false);
        return "redirect:/srm/supplier/current_orders";
    }

    @GetMapping("completed_orders")
    public String completed_orders(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Завершенные заказы");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());

        User user = userRepo.findByUsername(authentication.getName());
        List<PastOrder> pastOrder = pastOrderService.getAllPastOrdersWithProviderNameByProviderId((int)user.getId());

        model.addAttribute("pastOrder", pastOrder);
        return "SRM/supplier/past_orders";
    }

    @GetMapping("completed_orders/analytics")
    public String analyticOrders(Model model) {
        DecimalFormat df = new DecimalFormat("#,###.00");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Аналитика по завершенным заказам");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());
        User user = userRepo.findByUsername(authentication.getName());
        List<PastOrder> pastOrder = pastOrderService.getAllPastOrdersWithProviderNameByProviderId((int)user.getId());
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

        if (percentOfCanceled != 0) {
            model.addAttribute("sum", df.format(sum));
            model.addAttribute("count", count);
            model.addAttribute("avgPrice", avgPrice);
            model.addAttribute("percentOfCanceled", percentOfCanceled);
        }

        return "SRM/orders/past_orders/analytics";
    }
}
