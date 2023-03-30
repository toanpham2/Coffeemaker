package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.User;

/**
 * UserRepository is used to provide CRUD operations for the User model. Spring
 * will generate appropriate code with JPA.
 *
 * @author stnguye3
 * @author tgpham
 * @author bcchavez
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * finds the user by username
     *
     * @param username
     *            the name of the user to find
     * @return the user object
     */
    User findUser ( String username );

}
