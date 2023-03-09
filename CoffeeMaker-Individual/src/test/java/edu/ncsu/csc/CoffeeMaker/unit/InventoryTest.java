package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class InventoryTest {

    @Autowired
    private InventoryService inventoryService;

    @BeforeEach
    public void setup () {
        final Inventory ivt = inventoryService.getInventory();
        final Ingredient Coffee = new Ingredient( "Coffee", 500 );
        final Ingredient Milk = new Ingredient( "Milk", 500 );
        final Ingredient Sugar = new Ingredient( "Sugar", 500 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 500 );
        ivt.addIngredients( Coffee );
        ivt.addIngredients( Milk );
        ivt.addIngredients( Sugar );
        ivt.addIngredients( Chocolate );
        inventoryService.save( ivt );
    }

    @Test
    @Transactional
    public void testConsumeInventory () {
        final Inventory i = inventoryService.getInventory();

        final Recipe recipe = new Recipe();
        recipe.setName( "Mocha" );
        recipe.setPrice( 5 );
        final Ingredient Coffee2 = new Ingredient( "Coffee", 1 );
        final Ingredient Milk2 = new Ingredient( "Milk", 20 );
        final Ingredient Sugar2 = new Ingredient( "Sugar", 5 );
        final Ingredient Chocolate2 = new Ingredient( "Chocolate", 10 );
        recipe.addIngredient( Coffee2 );
        recipe.addIngredient( Milk2 );
        recipe.addIngredient( Sugar2 );
        recipe.addIngredient( Chocolate2 );
        i.useIngredients( recipe );

        /*
         * Make sure that all of the inventory fields are now properly updated
         */

        Assertions.assertEquals( 490, (int) i.getOneIngredient( "Chocolate" ).getAmount() );
        Assertions.assertEquals( 480, (int) i.getOneIngredient( "Milk" ).getAmount() );
        Assertions.assertEquals( 495, (int) i.getOneIngredient( "Sugar" ).getAmount() );
        Assertions.assertEquals( 499, (int) i.getOneIngredient( "Coffee" ).getAmount() );
    }

    @Test
    @Transactional
    public void testUpdateInventory1 () {
        Inventory ivt = inventoryService.getInventory();

        final Ingredient Coffee1 = new Ingredient( "Coffee", 5 );
        final Ingredient Milk1 = new Ingredient( "Milk", 3 );
        final Ingredient Sugar1 = new Ingredient( "Sugar", 7 );
        final Ingredient Chocolate1 = new Ingredient( "Chocolate", 2 );
        ivt.updateIngredients( Coffee1 );
        ivt.updateIngredients( Milk1 );
        ivt.updateIngredients( Sugar1 );
        ivt.updateIngredients( Chocolate1 );

        /* Save and retrieve again to update with DB */
        inventoryService.save( ivt );

        ivt = inventoryService.getInventory();

        Assertions.assertEquals( 502, (int) ivt.getOneIngredient( "Chocolate" ).getAmount(),
                "Adding to the inventory should result in correctly-updated values for coffee" );
        Assertions.assertEquals( 503, (int) ivt.getOneIngredient( "Milk" ).getAmount(),
                "Adding to the inventory should result in correctly-updated values for milk" );
        Assertions.assertEquals( 507, (int) ivt.getOneIngredient( "Sugar" ).getAmount(),
                "Adding to the inventory should result in correctly-updated values sugar" );
        Assertions.assertEquals( 505, (int) ivt.getOneIngredient( "Coffee" ).getAmount(),
                "Adding to the inventory should result in correctly-updated values chocolate" );

    }

    @Test
    @Transactional
    public void testAddInventory2 () {
        final Inventory ivt = inventoryService.getInventory();

        try {
            // ivt.addIngredients( -5, 3, 7, 2 );
            final Ingredient Coffee1 = new Ingredient( "Coffee", -5 );
            final Ingredient Milk1 = new Ingredient( "Milk", 3 );
            final Ingredient Sugar1 = new Ingredient( "Sugar", 7 );
            final Ingredient Chocolate1 = new Ingredient( "Chocolate", 2 );
            ivt.addIngredients( Coffee1 );
            ivt.addIngredients( Milk1 );
            ivt.addIngredients( Sugar1 );
            ivt.addIngredients( Chocolate1 );
        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Coffee" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- coffee" );
            Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Milk" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- milk" );
            Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Sugar" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- sugar" );
            Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Chocolate" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- chocolate" );
        }
    }

    @Test
    @Transactional
    public void testAddInventory3 () {
        final Inventory ivt = inventoryService.getInventory();

        try {
            // ivt.addIngredients( 5, -3, 7, 2 );
            final Ingredient Coffee1 = new Ingredient( "Coffee", 5 );
            final Ingredient Milk1 = new Ingredient( "Milk", -3 );
            final Ingredient Sugar1 = new Ingredient( "Sugar", 7 );
            final Ingredient Chocolate1 = new Ingredient( "Chocolate", 2 );
            ivt.addIngredients( Coffee1 );
            ivt.addIngredients( Milk1 );
            ivt.addIngredients( Sugar1 );
            ivt.addIngredients( Chocolate1 );
        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Coffee" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- coffee" );
            Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Milk" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- milk" );
            Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Sugar" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- sugar" );
            Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Chocolate" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- chocolate" );

        }

    }

    @Test
    @Transactional
    public void testAddInventory4 () {
        final Inventory ivt = inventoryService.getInventory();

        try {
            final Ingredient Coffee1 = new Ingredient( "Coffee", 5 );
            final Ingredient Milk1 = new Ingredient( "Milk", 3 );
            final Ingredient Sugar1 = new Ingredient( "Sugar", -7 );
            final Ingredient Chocolate1 = new Ingredient( "Chocolate", 2 );
            ivt.addIngredients( Coffee1 );
            ivt.addIngredients( Milk1 );
            ivt.addIngredients( Sugar1 );
            ivt.addIngredients( Chocolate1 );
        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Coffee" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- coffee" );
            Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Milk" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- milk" );
            Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Sugar" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- sugar" );
            Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Chocolate" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- chocolate" );

        }

    }

    @Test
    @Transactional
    public void testAddInventory5 () {
        final Inventory ivt = inventoryService.getInventory();

        try {
            final Ingredient Coffee1 = new Ingredient( "Coffee", 5 );
            final Ingredient Milk1 = new Ingredient( "Milk", -3 );
            final Ingredient Sugar1 = new Ingredient( "Sugar", 7 );
            final Ingredient Chocolate1 = new Ingredient( "Chocolate", -2 );
            ivt.addIngredients( Coffee1 );
            ivt.addIngredients( Milk1 );
            ivt.addIngredients( Sugar1 );
            ivt.addIngredients( Chocolate1 );
        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Coffee" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- coffee" );
            Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Milk" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- milk" );
            Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Sugar" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- sugar" );
            Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Chocolate" ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- chocolate" );

        }

    }

    @Test
    @Transactional
    public void testSetID () {
        final Inventory ivt = inventoryService.getInventory();
        Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Chocolate" ).getAmount() );
        Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Milk" ).getAmount() );
        Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Sugar" ).getAmount() );
        Assertions.assertEquals( 500, (int) ivt.getOneIngredient( "Coffee" ).getAmount() );

        ivt.setId( (long) 9 );
        assertEquals( 9, ivt.getId() );
    }

    @Test
    @Transactional
    public void testCheckIngredients () {

        final Inventory ivt = inventoryService.getInventory();
        // final Ingredient Sugar = new Ingredient( "Sugar", 500 );

        Assertions.assertEquals( 500, ivt.checkIngredient( "500" ), "Checking a valid value for sugar" );

        Assertions.assertThrows( IllegalArgumentException.class, () -> {
            ivt.checkIngredient( "-5" );
        }, "Checking a valid value for sugar" );
        Assertions.assertThrows( IllegalArgumentException.class, () -> {
            ivt.checkIngredient( "a" );
        }, "Checking a valid value for sugar" );

    }

    @Test
    @Transactional
    public void testToString () {
        final Inventory i = inventoryService.getInventory();

        Assertions.assertEquals(
                "Ingredient [id=" + "" + i.getOneIngredient( "Coffee" ).getId() + ", amount=500, type=Coffee]"
                        + "Ingredient [id=" + "" + i.getOneIngredient( "Milk" ).getId() + ""
                        + ", amount=500, type=Milk]" + "Ingredient [id=" + "" + i.getOneIngredient( "Sugar" ).getId()
                        + "" + ", amount=500, type=Sugar]Ingredient [id=" + ""
                        + i.getOneIngredient( "Chocolate" ).getId() + "" + ", amount=500, type=Chocolate]",
                i.toString() );
    }

    @Test
    @Transactional
    public void testNotEnoughIngredents () {
        final Inventory i = inventoryService.getInventory();

        final Recipe recipe = new Recipe();
        final Ingredient Coffee = new Ingredient( "Coffee", 501 );
        final Ingredient Milk = new Ingredient( "Milk", 1 );
        final Ingredient Sugar = new Ingredient( "Sugar", 1 );
        final Ingredient Chocolate = new Ingredient( "Chocolate", 1 );
        recipe.addIngredient( Coffee );
        recipe.addIngredient( Milk );
        recipe.addIngredient( Sugar );
        recipe.addIngredient( Chocolate );
        recipe.setPrice( 12 );

        Assertions.assertFalse( i.useIngredients( recipe ) );

    }

}
