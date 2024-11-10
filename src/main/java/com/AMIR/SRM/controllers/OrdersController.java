package com.AMIR.SRM.controllers;

import com.AMIR.SRM.domain.Order;
import com.AMIR.SRM.domain.PastOrder;
import com.AMIR.SRM.domain.Response;
import com.AMIR.SRM.repositories.OrderRepo;
import com.AMIR.SRM.repositories.PastOrderRepo;
import com.AMIR.SRM.repositories.ResponseRepo;
import com.AMIR.SRM.service.OrderService;
import com.AMIR.SRM.service.PastOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.text.DecimalFormat;
import java.util.*;

@Controller
@RequestMapping("srm/orders/")
public class OrdersController {
    @Autowired
    private ResponseRepo responseRepo;
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private PastOrderRepo pastOrderRepo;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PastOrderService pastOrderService;

    @GetMapping("new_order")
    public String new_order(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Создание заказа");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());
        return "SRM/orders/curr_orders/new_order";
    }

    @DateTimeFormat(pattern = "dd-mm-yyyy")
    @PostMapping("new_order")
    public String add(
            @RequestParam String product_name,
            @RequestParam String description,
            @RequestParam double max_price,
            @RequestParam int count,
            @RequestParam Date expected_date,
            Map<String, Object> model) {

        model.put("message", "Заказ успешно создан");
        Order order = new Order(product_name, description, max_price, count, expected_date);
        orderRepo.save(order);

        return "redirect:/srm/orders/new_order?created";
    }

    @GetMapping("current_orders")
    public String current_orders(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Текущие заказы");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());

        List<Order> order = orderService.getAllOrdersWithProviderNameByAuthor(authentication.getName());

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
        return "SRM/orders/curr_orders/current_orders";
    }


    @GetMapping("current_orders/{order}")
    public String orderProvider(@PathVariable Order order, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<Response> responses = responseRepo.findByOrder(order);
        model.addAttribute("title", "Выбор поставщика");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());
        model.addAttribute("responses", responses);
        model.addAttribute("currentOrder", order);

        return "SRM/orders/curr_orders/provider";
    }

    @PostMapping("/current_orders")
    public String providerSave(
            @RequestParam("provider_name") Long providerId,
            @RequestParam("responseOrderId") Long responseOrderId
    ) {
        Optional<Order> orderOptional = orderRepo.findById(responseOrderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setProvider(providerId.intValue());
            orderRepo.save(order);
        } else {
            System.out.println("Заказ с ID " + responseOrderId + " не найден.");
        }
        return "redirect:/srm/orders/current_orders";
    }

    @GetMapping("future_orders/cancelling/{order}")
    public String cancel_future_order(@PathVariable Order order, Model model) {
        PastOrder pastOrder = new PastOrder(order, "canceled");
        pastOrderRepo.save(pastOrder);
        orderRepo.delete(order);
        return "redirect:/srm/orders/future_orders";
    }
    @GetMapping("current_orders/cancelling/{order}")
    public String cancel_order(@PathVariable Order order, Model model) {
        PastOrder pastOrder = new PastOrder(order, "canceled");
        pastOrderRepo.save(pastOrder);
        orderRepo.delete(order);
        return "redirect:/srm/orders/current_orders";
    }

    @GetMapping("wait_approved/cancelling/{order}")
    public String wait_approved(@PathVariable Order order, Model model) {
        PastOrder pastOrder = new PastOrder(order, "canceled");
        pastOrderRepo.save(pastOrder);
        orderRepo.delete(order);
        return "redirect:/srm/orders/wait_approved";
    }

    @GetMapping("current_orders/finishing/{order}")
    public String finish_order(@PathVariable Order order, Model model) {
        PastOrder pastOrder = new PastOrder(order, "completed");
        pastOrderRepo.save(pastOrder);
        orderRepo.delete(order);
        return "redirect:/srm/orders/current_orders";
    }

    @PreAuthorize("hasAnyAuthority('DIRECTOR', 'ADMIN')")
    @GetMapping("future_orders")
    public String future_orders(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Согласовать");
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
        return "SRM/orders/curr_orders/future_orders";
    }

    @PreAuthorize("hasAnyAuthority('USER')")
    @GetMapping("wait_approved")
    public String wait_approved(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Ожидание соглосования");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());

        List<Order> order = orderService.getAllOrdersWithProviderNameByAuthor(authentication.getName());
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
        return "SRM/orders/curr_orders/wait_approved";
    }

    @GetMapping("future_orders/{order}")
    public String orderEditForm(@PathVariable Order order, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Согласование заказа");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());
        model.addAttribute("order", order);

        return "SRM/orders/curr_orders/approve_order";
    }

    @PostMapping("/future_orders")
    public String orderSave(
            @RequestParam String product_name,
            @RequestParam String description,
            @RequestParam int count,
            @RequestParam double max_price,
            @RequestParam Date expected_date,
            @RequestParam("orderId") Order order
    ) {
        order.setProduct_name(product_name);
        order.setDescription(description);
        order.setCount(count);
        order.setMax_price(max_price);
        order.setExpected_date(expected_date);
        order.setIs_approved(true);

        orderRepo.save(order);
        return "redirect:/srm/orders/future_orders";
    }

    @GetMapping("completed_orders")
    public String completed_orders(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Завершенные заказы");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());

        List<PastOrder> pastOrder = pastOrderService.getAllPastOrdersWithProviderNameByAuthor(authentication.getName());

        model.addAttribute("pastOrder", pastOrder);
        return "SRM/orders/past_orders/completed_orders";
    }

    @GetMapping("completed_orders/repeating/{pastOrder}")
    public String orderRepeatForm(@PathVariable PastOrder pastOrder, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Повторение заказа");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());
        model.addAttribute("order", pastOrder);

        Order order = new Order(pastOrder);
        orderRepo.save(order);

        return "redirect:/srm/orders/completed_orders";
    }

    @GetMapping("canceled_orders/repeating/{pastOrder}")
    public String orderCancelRepeatForm(@PathVariable PastOrder pastOrder, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Повторение заказа");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());
        model.addAttribute("order", pastOrder);

        Order order = new Order(pastOrder);
        orderRepo.save(order);

        return "redirect:/srm/orders/canceled_orders";
    }

    @GetMapping("canceled_orders")
    public String canceled_orders(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Отмененные заказы");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());

        List<PastOrder> pastOrder = pastOrderService.getAllPastOrdersWithProviderNameByAuthor(authentication.getName());

        model.addAttribute("pastOrder", pastOrder);
        return "SRM/orders/past_orders/canceled_orders";
    }

    @GetMapping("delete_order/{pastOrder}")
    public String deleteNew(@PathVariable PastOrder pastOrder, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Новость");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());

        String status = pastOrder.getStatus();

        pastOrderRepo.delete(pastOrder);
        if (Objects.equals(status, "canceled"))
            return "redirect:/srm/orders/canceled_orders";
        else
            return "redirect:/srm/orders/completed_orders";
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

        if (percentOfCanceled != 0) {
            model.addAttribute("sum", df.format(sum));
            model.addAttribute("count", count);
            model.addAttribute("avgPrice", avgPrice);
            model.addAttribute("percentOfCanceled", percentOfCanceled);
        }

        return "SRM/orders/past_orders/analytics";
    }
}
