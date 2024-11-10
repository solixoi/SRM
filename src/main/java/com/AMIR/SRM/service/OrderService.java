package com.AMIR.SRM.service;

import com.AMIR.SRM.domain.Order;
import com.AMIR.SRM.repositories.OrderRepo;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepo orderRepository;

    @Autowired
    private ProviderService providerService;

    public Order getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            loadProviderName(order);
        }
        return order;
    }

    private void loadProviderName(Order order) {
        if (order.getProvider() != null) {
            String providerName = providerService.fetchProviderNameByProviderId(Long.valueOf(order.getProvider()));
            order.setProviderName(providerName);
        }
    }

    @Transactional
    public List<Order> getAllOrdersWithProviderName() {
        List<Order> orders = orderRepository.findAll();

        for (Order order : orders) {
            if (order.getProvider() != null) {
                loadProviderName(order);
            }
        }

        return orders;
    }

    @Transactional
    public List<Order> getAllOrdersWithProviderNameByAuthor(String author){
        List<Order> orders = orderRepository.findByAuthor(author);
        for(Order order : orders){
            if(order.getProvider() != null){
                loadProviderName(order);
            } else{
                order.setProviderName("Не назначен");
            }
        }
        return orders;
    }
}
