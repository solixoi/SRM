package com.AMIR.SRM.repositories;

import com.AMIR.SRM.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepo extends JpaRepository<News, Long> {
}
