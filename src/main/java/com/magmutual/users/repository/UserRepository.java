package com.magmutual.users.repository;


import com.magmutual.users.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.sql.Date;

public interface UserRepository extends JpaRepository<Users, String>, PagingAndSortingRepository<Users, String> {

    Page<Users> findByDateCreatedBetween(Date startDate, Date endDate, Pageable pageable);

    Page<Users> findByProfession(String profession, Pageable pageable);

    Page<Users> findByDateCreatedBetweenAndProfession(Date startDate, Date endDate, String profession, Pageable pageable);
}
