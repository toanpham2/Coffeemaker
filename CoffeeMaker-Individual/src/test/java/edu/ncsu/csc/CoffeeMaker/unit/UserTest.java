package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * Testing User object
 *
 * @author stnguye3
 * @author bcchavez
 *
 *
 */

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class UserTest {

    /** user service for testing */
    @Autowired
    private UserService service;

    /**
     * Sets up service by clearing any existing users
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    /**
     * Test creating a customer account
     */
    @Test
    @Transactional
    public void testCustomerValid () {
        assertEquals( 0, service.findAll().size(), "There should be no Users in the CoffeeMaker" );

        final User user1 = new User( "username123", "pass123", "n/a" );

        assertFalse( user1.getIsStaff() );
        assertEquals( "username123", user1.getUsername() );

        service.save( user1 );
        assertEquals( 1, service.count() );

    }

    /**
     * Test creating a staff account
     */
    @Test
    @Transactional
    public void testStaffValid () {
        final User user2 = new User( "staffuser4", "staffpass123", "OnePiece" );

        assertTrue( user2.getIsStaff() );
        assertEquals( "staffuser4", user2.getUsername() );

        final User user1 = new User( "username123", "pass123", "n/a" );
        service.save( user1 );
        service.save( user2 );
        assertEquals( 2, service.count() );

    }

}