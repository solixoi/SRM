package com.AMIR.SRM.repositories;

import com.AMIR.SRM.domain.PastOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PastOrderRepo extends JpaRepository<PastOrder, Long> {
    List<PastOrder> findByAuthor(String author);
    PastOrder findById(long id);
    List<PastOrder> findByProvider(Integer id);
}
