package edu.ncsu.csc.CoffeeMaker.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.repositories.UserRepository;

/**
 * User service used to handle CRUD operations on the User model. Has
 * functionality to retrieve user by username.
 *
 * @author stnguye3
 *
 */
@Component
@Transactional
public class UserService extends Service<User, Long> {

    /**
     * RecipeRepository, to be autowired in by Spring and provide CRUD
     * operations on Recipe model.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Gets the user repository
     *
     * @return return user repository
     */
    @Override
    protected JpaRepository<User, Long> getRepository () {
        return userRepository;
    }

    /**
     * Find a user with the provided username
     *
     * @param username
     *            Name of the user to find
     * @return found user, null if none
     */
    public User findByUsername ( final String username ) {
        return userRepository.findByUsername( username );
    }

}
