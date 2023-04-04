package edu.ncsu.csc.CoffeeMaker.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * API controller for users
 *
 * @author stnguye3
 * @author tgpham
 * @author bcchavez
 *
 */
@RestController
public class APIUserController extends APIController {

    /** the service for users */
    @Autowired
    private UserService service;

    /**
     * gets a user from the database
     *
     * @param username
     *            the username of the user to get
     * @return response entity
     */
    @SuppressWarnings ( { "rawtypes", "unchecked" } )
    @GetMapping ( BASE_PATH + "/users/{username}" )
    public ResponseEntity login ( @PathVariable final String username ) {

        final User user = service.findByUsername( username );

        if ( null == user ) { // user not found
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }

        else {
            return new ResponseEntity( user, HttpStatus.OK );
        }

    }

    /**
     * POST for a new user (create a new user)
     *
     * @param user
     *            the user to add
     * @return Response Entity
     */
    @SuppressWarnings ( { "rawtypes", "unchecked" } )
    @PostMapping ( BASE_PATH + "/users" )
    public ResponseEntity register ( @RequestBody final User user ) {
        // if there is already a user with the same username
        if ( null != service.findByUsername( user.getUsername() ) ) {
            return new ResponseEntity(
                    errorResponse( "User with the username " + user.getUsername() + " already exists" ),
                    HttpStatus.CONFLICT );
        }

        // no existing account with username yet, so add new user
        service.save( user );
        return new ResponseEntity(
                successResponse( "User account with username: " + user.getUsername() + " successfully created" ),
                HttpStatus.OK );
    }

}
