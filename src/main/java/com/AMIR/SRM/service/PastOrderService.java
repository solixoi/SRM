package com.AMIR.SRM.service;

import com.AMIR.SRM.domain.Order;
import com.AMIR.SRM.domain.PastOrder;
import com.AMIR.SRM.repositories.PastOrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class PastOrderService {
    @Autowired
    private PastOrderRepo pastOrderRepo;

    @Autowired
    private ProviderService providerService;

    public PastOrder getOrderById(Long id) {
        PastOrder pastOrder = pastOrderRepo.findById(id).orElse(null);
        if (pastOrder != null) {
            loadProviderName(pastOrder);
        }
        return pastOrder;
    }

    private void loadProviderName(PastOrder pastOrder) {
        if (pastOrder.getProvider() != null) {
            String providerName = providerService.fetchProviderNameByProviderId(Long.valueOf(pastOrder.getProvider()));
            pastOrder.setProviderName(providerName);
        }
    }

    @Transactional
    public List<PastOrder> getAllPastOrdersWithProviderName() {
        List<PastOrder> pastOrders = pastOrderRepo.findAll();

        for (PastOrder pastOrder : pastOrders) {
            if (pastOrder.getProvider() != null) {
                loadProviderName(pastOrder);
            }
        }

        return pastOrders;
    }

    @Transactional
    public List<PastOrder> getAllPastOrdersWithProviderNameByAuthor(String author){
        List<PastOrder> pastOrders = pastOrderRepo.findByAuthor(author);
        for(PastOrder pastOrder : pastOrders){
            if(pastOrder.getProvider() != null){
                loadProviderName(pastOrder);
            } else{
                pastOrder.setProviderName("Не назначен");
            }
        }
        return pastOrders;
    }

    @Transactional
    public List<PastOrder> getAllPastOrdersWithProviderNameByProviderId(Integer id){
        List<PastOrder> pastOrders = pastOrderRepo.findByProvider(id);
        for(PastOrder pastOrder : pastOrders){
                loadProviderName(pastOrder);
        }
        return pastOrders;
    }
}
