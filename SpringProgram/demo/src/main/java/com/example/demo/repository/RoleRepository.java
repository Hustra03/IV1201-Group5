package com.example.demo.repository;

import com.example.demo.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * The RoleRepository interface is responsible for providing CRUD operations for the Role entity.
 * Any explicitly defined functions can only be called inside a transaction, however JPARepository inherited functions can be called outside of one
 * This should however not be done, since doing so could risk a roleback being impossible
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
//Note that the above means that any explicitly defined methods for this interface requier being called inside a transaction
//Non-explicitly defined ones, aka inherited ones from JPARepository do not requier this, however note that they should still be called inside of a transaction to avoid roleback issues
public interface RoleRepository extends JpaRepository<Role, Integer>{
        /**
     * This interface function tells JPA to generate a query finding one Role by the value of the name parameter
     * @param name The name of the languge to find
     * @return The String object with the specified name
     */
    Role findByName(String name);
}
