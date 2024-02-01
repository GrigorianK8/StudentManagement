package com.example.studentmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.example.studentmanagement.entity.User;
import com.example.studentmanagement.entity.UserType;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByUserType(UserType userType);
    void deleteAllByUserType(UserType userType);
}
