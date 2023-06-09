package com.nutri.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nutri.backend.model.User;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface
UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByName(String name);
    Optional<User> findByEmail(String email);
    List<User> findByUserType(String userType);
    boolean existsByEmail(String foo);
    @Query(value = "SELECT COUNT(*) FROM user_table WHERE ENTRY_DATE= :date",nativeQuery = true)
    int findByEntryDate(int date);
    @Query(value = "SELECT COUNT(*) FROM user_table WHERE USER_TYPE= :typeOfUser",nativeQuery = true)
    int findAllByuser(String typeOfUser);

    @Query(value = "SELECT COUNT(*) FROM user_table WHERE USER_TYPE= :typeOfUser AND ENTRY_DATE= :date",nativeQuery = true)
    int findAllByUserMonth(String typeOfUser, int date);
    Page<User> findByUserType(Pageable page,String userType);

    @Query(value = "SELECT * FROM user_table WHERE DIET_ID= :dietId",nativeQuery = true)
    List<User> findByDiet(int dietId);
}
