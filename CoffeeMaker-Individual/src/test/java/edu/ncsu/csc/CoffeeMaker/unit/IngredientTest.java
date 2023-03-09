package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

public class IngredientTest {

    @Autowired
    private IngredientService service;

    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    @Test
    @Transactional
    public void testGetAmount () {
        final Ingredient ingredient1 = new Ingredient( "Coffee", 10 );
        assertEquals( 10, ingredient1.getAmount() );

        ingredient1.setAmount( 0 );
        assertEquals( 0, ingredient1.getAmount() );
    }

    @Test
    @Transactional
    public void testGetType () {
        final Ingredient ingredient1 = new Ingredient( "Coffee", 10 );
        assertEquals( "Coffee", ingredient1.getType() );

        ingredient1.setType( "Chocolate" );
        assertEquals( "Chocolate", ingredient1.getType() );
    }

    @Test
    @Transactional
    public void testToString () {
        final Ingredient ingredient1 = new Ingredient( "Coffee", 10 );
        assertEquals( "Coffee", ingredient1.getType() );
        assertEquals( 10, ingredient1.getAmount() );

        assertEquals( "Ingredient [id=null, amount=10, type=Coffee]", ingredient1.toString() );
    }

}
