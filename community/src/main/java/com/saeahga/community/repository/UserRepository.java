package com.saeahga.community.repository;

import com.saeahga.community.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, String> { // JpaRepository<엔티티, 키 값>

}
