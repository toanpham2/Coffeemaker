package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.CoffeeOrder;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * Testing Order object
 *
 * @author tgpham
 *
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class CoffeeOrderTest {

    /** user service for testing */
    @Autowired
    private OrderService  service;

    /** user service for testing */
    @Autowired
    private UserService   userService;

    /** user service for testing */
    @Autowired
    private RecipeService recipeService;

    /**
     * Sets up service by clearing any existing users
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();
        userService.deleteAll();
        recipeService.deleteAll();
    }

    /**
     * Test creating a customer account
     */
    @Test
    @Transactional
    public void testCoffeeOrderValid () {
        assertEquals( 0, service.findAll().size(), "There should be no coffeeOrder in the CoffeeMaker" );

        final User user1 = new User( "username123", "pass123", "n/a", false, false );

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        final Ingredient coffee = new Ingredient( "Coffee", 1 );
        final Ingredient milk = new Ingredient( "Milk", 0 );
        final Ingredient sugar = new Ingredient( "Sugar", 0 );
        final Ingredient chocolate = new Ingredient( "Chocolate", 0 );
        r1.addIngredient( coffee );
        r1.addIngredient( milk );
        r1.addIngredient( sugar );
        r1.addIngredient( chocolate );
        r1.setPrice( 1 );

        final CoffeeOrder order1 = new CoffeeOrder( r1, user1.getUsername() );

        userService.save( user1 );
        recipeService.save( r1 );
        service.save( order1 );
        assertEquals( 1, service.count() );
        assertFalse( order1.getisFulfilled() );
        assertFalse( order1.getisPickedUp() );
        assertEquals( "username123", order1.getName() );
        assertEquals( r1, order1.getRecipe() );
    }

}
