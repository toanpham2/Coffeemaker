package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Inventory for the coffee maker. Inventory is tied to the database using
 * Hibernate libraries. See InventoryRepository and InventoryService for the
 * other two pieces used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
public class Inventory extends DomainObject {

    /** id for inventory entry */
    @Id
    @GeneratedValue
    private Long     id;

    /** List of ingredients */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    List<Ingredient> ingredientsInv;

    /**
     * Empty constructor for Hibernate
     */
    public Inventory () {
        // Intentionally empty so that Hibernate can instantiate
        // Inventory object.
    }

    /**
     * Returns the ID of the entry in the DB
     *
     * @return long
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Inventory (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Add the number of each ingredients units in the inventory to the current
     * amount of chocolate units.
     *
     * @param ingredient
     *            the ingredient to check amount amount of each ingredients
     * @return checked amount of each ingredients
     * @throws IllegalArgumentException
     *             if the parameter isn't a positive integer
     */
    public Integer checkIngredient ( final String ingredient ) throws IllegalArgumentException {
        Integer amtIngredient = 0;
        try {
            amtIngredient = Integer.parseInt( ingredient );
        }
        catch ( final NumberFormatException e ) {
            throw new IllegalArgumentException( "Units of ingredients must be a positive integer" );
        }
        if ( amtIngredient < 0 ) {
            throw new IllegalArgumentException( "Units of ingrdients must be a positive integer" );
        }

        return amtIngredient;
    }

    /**
     * Returns true if there are enough ingredients to make the beverage.
     *
     * @param r
     *            recipe to check if there are enough ingredients
     * @return true if enough ingredients to make the beverage
     */
    public boolean enoughIngredients ( final Recipe r ) {
        if ( ingredientsInv == null ) {
            return false;
        }
        boolean isEnough = true;
        for ( int i = 0; i < ingredientsInv.size(); i++ ) {
            for ( int j = 0; j < r.ingredients.size(); j++ ) {
                if ( ( ingredientsInv.get( i ).getType().equals( r.ingredients.get( j ).getType() ) )
                        && ( ingredientsInv.get( i ).getAmount() < r.ingredients.get( j ).getAmount() ) ) {

                    isEnough = false;

                }
            }
        }
        return isEnough;
    }

    /**
     * Removes the ingredients used to make the specified recipe. Assumes that
     * the user has checked that there are enough ingredients to make
     *
     * @param r
     *            recipe to make
     * @return true if recipe is made.
     */
    public boolean useIngredients ( final Recipe r ) {
        if ( ingredientsInv == null ) {
            return false;
        }
        if ( enoughIngredients( r ) ) {
            for ( int i = 0; i < ingredientsInv.size(); i++ ) {
                for ( int j = 0; j < r.ingredients.size(); j++ ) {
                    if ( ingredientsInv.get( i ).getType().equals( r.ingredients.get( j ).getType() ) ) {
                        ingredientsInv.get( i )
                                .setAmount( ingredientsInv.get( i ).getAmount() - r.ingredients.get( j ).getAmount() );
                    }
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Adds ingredients to the inventory changed addIngredients to update
     * ingredient to update the amount of certain ingredients
     *
     * @param ingredient
     *            amt of ingredient
     * @return true if successful, false if not
     */
    public boolean updateIngredients ( final Ingredient ingredient ) {

        for ( int i = 0; i < ingredientsInv.size(); i++ ) {
            if ( ingredient.getType().equals( ingredientsInv.get( i ).getType() ) ) {
                final int newAmount = ingredientsInv.get( i ).getAmount() + ingredient.getAmount();
                if ( newAmount >= 0 ) {
                    ingredientsInv.get( i ).setAmount( newAmount );
                }
                else {
                    throw new IllegalArgumentException( "New total cannot be negative" );
                }
            }
        }
        return true;
    }

    /**
     * Adds new ingredients to the inventory
     *
     * @param ingredient
     *            amt of ingredient
     * @return true if successful, false if not
     */
    public boolean addIngredients ( final Ingredient ingredient ) {
        if ( ingredient.getAmount() < 0 ) {
            throw new IllegalArgumentException( "Amount cannot be negative" );
        }
        if ( ingredientsInv == null ) {
            ingredientsInv = new ArrayList<Ingredient>();
        }
        for ( int i = 0; i < ingredientsInv.size(); i++ ) {
            if ( ingredient.getType().equals( ingredientsInv.get( i ).getType() ) ) {
                throw new IllegalArgumentException( "Ingredient already exist" );
            }
        }
        ingredientsInv.add( ingredient );
        return true;
    }

    /**
     * gets the list of ingredients
     *
     * @return the list of ingredients in the inventory
     */
    public List<Ingredient> getIngredients () {
        return ingredientsInv;
    }

    /**
     * a * Returns a string describing the current contents of the inventory.
     *
     * @return String
     */
    @Override
    public String toString () {
        final StringBuffer buf = new StringBuffer();
        for ( int i = 0; i < ingredientsInv.size(); i++ ) {
            buf.append( ingredientsInv.get( i ) );
        }
        return buf.toString();
    }

    /**
     * gets one ingredient
     *
     * @param type
     *            the name of the ingredient to get
     * @return the ingredient object to get
     */
    public Ingredient getOneIngredient ( final String type ) {
        for ( int i = 0; i < ingredientsInv.size(); i++ ) {
            if ( ingredientsInv.get( i ).getType().equals( type ) ) {
                return ingredientsInv.get( i );
            }
        }
        return null;
    }
}
