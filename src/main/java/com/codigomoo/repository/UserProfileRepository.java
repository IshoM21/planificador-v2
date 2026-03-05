package com.codigomoo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codigomoo.model.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long>{

}
