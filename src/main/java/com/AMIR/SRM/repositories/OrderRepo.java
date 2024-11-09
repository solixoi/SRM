package com.AMIR.SRM.repositories;

import com.AMIR.SRM.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findByAuthor(String author);
}
