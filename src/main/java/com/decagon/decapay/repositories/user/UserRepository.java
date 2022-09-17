package com.decagon.decapay.repositories.user;

import com.decagon.decapay.dto.UserResponseDto;
import com.decagon.decapay.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findUserByEmail(String email);

    @Query("select new com.decagon.decapay.dto.UserResponseDto(u.firstName, u.lastName, u.email, u.phoneNumber) " +
            "from User u " +
            "where u.id =?1 " )
    UserResponseDto findUserById(Long Id);
}
