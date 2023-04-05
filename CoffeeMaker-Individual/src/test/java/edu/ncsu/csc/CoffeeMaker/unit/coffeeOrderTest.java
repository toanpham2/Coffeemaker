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
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
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
public class coffeeOrderTest {

    /** user service for testing */
    @Autowired
    private OrderService      service;

    @Autowired
    private IngredientService Ingservice;

    /** user service for testing */
    @Autowired
    private UserService       UserService;

    /** user service for testing */
    @Autowired
    private RecipeService     recipeService;

    /**
     * Sets up service by clearing any existing users
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();
        UserService.deleteAll();
        recipeService.deleteAll();
    }

    /**
     * Test creating a customer account
     */
    @Test
    @Transactional
    public void testCoffeeOrderValid () {
        assertEquals( 0, service.findAll().size(), "There should be no coffeeOrder in the CoffeeMaker" );

        final User user1 = new User( "username123", "pass123", "n/a", false );

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        final Ingredient Coffee = new Ingredient( "Coffee", 1 );
        final Ingredient Milk = new Ingredient( "Milk", 0 );
        final Ingredient Sugar = new Ingredient( "Sugar", 0 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 0 );
        r1.addIngredient( Coffee );
        r1.addIngredient( Milk );
        r1.addIngredient( Sugar );
        r1.addIngredient( Chocolate );
        r1.setPrice( 1 );

        final CoffeeOrder order1 = new CoffeeOrder( r1, user1 );

        UserService.save( user1 );
        recipeService.save( r1 );
        service.save( order1 );
        assertEquals( 1, service.count() );
        assertFalse( order1.getFulfilled() );
        assertFalse( order1.getPickedUp() );
        assertEquals( "username123", order1.getName() );
        assertEquals( r1, order1.getRecipe() );
    }

}
