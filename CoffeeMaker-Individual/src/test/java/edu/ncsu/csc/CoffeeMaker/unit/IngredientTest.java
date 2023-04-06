package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

/**
 * Tests the Ingredient object
 *
 * @author tgpham
 * @author jncoppet
 * @author mpwarren
 *
 */
public class IngredientTest {

    /** ingredient service used for testing */
    @Autowired
    private IngredientService service;

    /**
     * Sets up the ingredient service for testing
     *
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    /**
     * Test getting the amount of an ingredient
     */
    @Test
    @Transactional
    public void testGetAmount () {
        final Ingredient ingredient1 = new Ingredient( "Coffee", 10 );
        assertEquals( 10, ingredient1.getAmount() );

        ingredient1.setAmount( 0 );
        assertEquals( 0, ingredient1.getAmount() );
    }

    /**
     * Test getting the type of ingredient
     */
    @Test
    @Transactional
    public void testGetType () {
        final Ingredient ingredient1 = new Ingredient( "Coffee", 10 );
        assertEquals( "Coffee", ingredient1.getType() );

        ingredient1.setType( "Chocolate" );
        assertEquals( "Chocolate", ingredient1.getType() );
    }

    /**
     * Test the toString method in Ingredient
     */
    @Test
    @Transactional
    public void testToString () {
        final Ingredient ingredient1 = new Ingredient( "Coffee", 10 );
        assertEquals( "Coffee", ingredient1.getType() );
        assertEquals( 10, ingredient1.getAmount() );

        assertEquals( "Ingredient [id=null, amount=10, type=Coffee]", ingredient1.toString() );
    }

}
