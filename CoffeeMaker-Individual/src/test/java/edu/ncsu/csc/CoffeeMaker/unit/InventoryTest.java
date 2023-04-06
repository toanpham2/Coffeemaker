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

/**
 * Tests the Inventory and its functionalities
 *
 * @author tgpham
 * @author jncoppet
 * @author mpwarren
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class InventoryTest {

    /** inventory service used for testing */
    @Autowired
    private InventoryService inventoryService;

    /**
     * Sets up the inventory service before testing
     */
    @BeforeEach
    public void setup () {
        final Inventory ivt = inventoryService.getInventory();
        final Ingredient coffee = new Ingredient( "Coffee", 500 );
        final Ingredient milk = new Ingredient( "Milk", 500 );
        final Ingredient sugar = new Ingredient( "Sugar", 500 );
        final Ingredient chocolate = new Ingredient( "Chocolate", 500 );
        ivt.addIngredients( coffee );
        ivt.addIngredients( milk );
        ivt.addIngredients( sugar );
        ivt.addIngredients( chocolate );
        inventoryService.save( ivt );
    }

    /**
     * Test using ingredients from the inventory
     */
    @Test
    @Transactional
    public void testConsumeInventory () {
        final Inventory i = inventoryService.getInventory();

        final Recipe recipe = new Recipe();
        recipe.setName( "Mocha" );
        recipe.setPrice( 5 );
        final Ingredient coffee2 = new Ingredient( "Coffee", 1 );
        final Ingredient milk2 = new Ingredient( "Milk", 20 );
        final Ingredient sugar2 = new Ingredient( "Sugar", 5 );
        final Ingredient chocolate2 = new Ingredient( "Chocolate", 10 );
        recipe.addIngredient( coffee2 );
        recipe.addIngredient( milk2 );
        recipe.addIngredient( sugar2 );
        recipe.addIngredient( chocolate2 );
        i.useIngredients( recipe );

        /*
         * Make sure that all of the inventory fields are now properly updated
         */

        Assertions.assertEquals( 490, (int) i.getOneIngredient( "Chocolate" ).getAmount() );
        Assertions.assertEquals( 480, (int) i.getOneIngredient( "Milk" ).getAmount() );
        Assertions.assertEquals( 495, (int) i.getOneIngredient( "Sugar" ).getAmount() );
        Assertions.assertEquals( 499, (int) i.getOneIngredient( "Coffee" ).getAmount() );
    }

    /**
     * Test updating the inventory
     */
    @Test
    @Transactional
    public void testUpdateInventory1 () {
        Inventory ivt = inventoryService.getInventory();

        final Ingredient coffee1 = new Ingredient( "Coffee", 5 );
        final Ingredient milk1 = new Ingredient( "Milk", 3 );
        final Ingredient sugar1 = new Ingredient( "Sugar", 7 );
        final Ingredient chocolate1 = new Ingredient( "Chocolate", 2 );
        ivt.updateIngredients( coffee1 );
        ivt.updateIngredients( milk1 );
        ivt.updateIngredients( sugar1 );
        ivt.updateIngredients( chocolate1 );

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

    /**
     * Test adding inventory with invalid coffee amount
     */
    @Test
    @Transactional
    public void testAddInventory2 () {
        final Inventory ivt = inventoryService.getInventory();

        try {
            // ivt.addIngredients( -5, 3, 7, 2 );
            final Ingredient coffee1 = new Ingredient( "Coffee", -5 );
            final Ingredient milk1 = new Ingredient( "Milk", 3 );
            final Ingredient sugar1 = new Ingredient( "Sugar", 7 );
            final Ingredient chocolate1 = new Ingredient( "Chocolate", 2 );
            ivt.addIngredients( coffee1 );
            ivt.addIngredients( milk1 );
            ivt.addIngredients( sugar1 );
            ivt.addIngredients( chocolate1 );
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

    /**
     * Test adding inventory with invalid milk amount
     */
    @Test
    @Transactional
    public void testAddInventory3 () {
        final Inventory ivt = inventoryService.getInventory();

        try {
            // ivt.addIngredients( 5, -3, 7, 2 );
            final Ingredient coffee1 = new Ingredient( "Coffee", 5 );
            final Ingredient milk1 = new Ingredient( "Milk", -3 );
            final Ingredient sugar1 = new Ingredient( "Sugar", 7 );
            final Ingredient chocolate1 = new Ingredient( "Chocolate", 2 );
            ivt.addIngredients( coffee1 );
            ivt.addIngredients( milk1 );
            ivt.addIngredients( sugar1 );
            ivt.addIngredients( chocolate1 );
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

    /**
     * Test updating inventory with invalid sugar amount
     */
    @Test
    @Transactional
    public void testAddInventory4 () {
        final Inventory ivt = inventoryService.getInventory();

        try {
            final Ingredient coffee1 = new Ingredient( "Coffee", 5 );
            final Ingredient milk1 = new Ingredient( "Milk", 3 );
            final Ingredient sugar1 = new Ingredient( "Sugar", -7 );
            final Ingredient chocolate1 = new Ingredient( "Chocolate", 2 );
            ivt.addIngredients( coffee1 );
            ivt.addIngredients( milk1 );
            ivt.addIngredients( sugar1 );
            ivt.addIngredients( chocolate1 );
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

    /**
     * Test adding inventory with invalid milk amount
     */
    @Test
    @Transactional
    public void testAddInventory5 () {
        final Inventory ivt = inventoryService.getInventory();

        try {
            final Ingredient coffee1 = new Ingredient( "Coffee", 5 );
            final Ingredient milk1 = new Ingredient( "Milk", -3 );
            final Ingredient sugar1 = new Ingredient( "Sugar", 7 );
            final Ingredient chocolate1 = new Ingredient( "Chocolate", -2 );
            ivt.addIngredients( coffee1 );
            ivt.addIngredients( milk1 );
            ivt.addIngredients( sugar1 );
            ivt.addIngredients( chocolate1 );
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

    /**
     * Test setting id for inventory
     */
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

    /**
     * Test checking the ingredients for sugar
     */
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

    /**
     * Test toString method in inventory
     */
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

    /**
     * Test using ingredients when there is not enough inventory
     */
    @Test
    @Transactional
    public void testNotEnoughIngredents () {
        final Inventory i = inventoryService.getInventory();

        final Recipe recipe = new Recipe();
        final Ingredient coffee = new Ingredient( "Coffee", 501 );
        final Ingredient milk = new Ingredient( "Milk", 1 );
        final Ingredient sugar = new Ingredient( "Sugar", 1 );
        final Ingredient chocolate = new Ingredient( "Chocolate", 1 );
        recipe.addIngredient( coffee );
        recipe.addIngredient( milk );
        recipe.addIngredient( sugar );
        recipe.addIngredient( chocolate );
        recipe.setPrice( 12 );

        Assertions.assertFalse( i.useIngredients( recipe ) );

    }

}
