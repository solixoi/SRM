package com.AMIR.SRM.repositories;

import com.AMIR.SRM.domain.Order;
import com.AMIR.SRM.domain.Response;
import com.AMIR.SRM.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponseRepo  extends JpaRepository<Response, Long> {
    List<Response> findByOrder(Order order);
    List<Response> findBySupplier(User user);
    Response findByOrderAndSupplier(Order order, User user);
}
