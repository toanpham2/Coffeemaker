package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Recipe for the coffee maker. Recipe is tied to the database using Hibernate
 * libraries. See RecipeRepository and RecipeService for the other two pieces
 * used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
public class Recipe extends DomainObject {

    /** Recipe id */
    @Id
    @GeneratedValue
    private Long     id;

    /** Recipe name */
    private String   name;

    /** Recipe price */
    @Min ( 0 )
    private Integer  price;

    /** List of ingredients */
    @JsonFormat ( with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY )
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    List<Ingredient> ingredients;

    /**
     * Creates a default recipe for the coffee maker.
     */
    public Recipe () {
        this.name = "";
        ingredients = new ArrayList<Ingredient>();
    }

    // /**
    // * Check if all ingredient fields in the recipe are 0
    // *
    // * @return true if all ingredient fields are 0, otherwise return false
    // */
    // public boolean checkRecipe () {
    // return coffee == 0 && milk == 0 && sugar == 0 && chocolate == 0;
    // }

    /**
     * Get the ID of the Recipe
     *
     * @return the ID
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Recipe (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns name of the recipe.
     *
     * @return Returns the name.
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the recipe name.
     *
     * @param name
     *            The name to set.
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Returns the price of the recipe.
     *
     * @return Returns the price.
     */
    public Integer getPrice () {
        return price;
    }

    /**
     * Sets the recipe price.
     *
     * @param price
     *            The price to set.
     */
    public void setPrice ( final Integer price ) {
        this.price = price;
    }

    /**
     * adds an ingredient to the recipe
     *
     * @param ingredient
     *            the ingredient to add
     */
    public void addIngredient ( final Ingredient ingredient ) {
        for ( int i = 0; i < ingredients.size(); i++ ) {
            if ( ingredient.getType().equals( ingredients.get( i ).getType() ) ) {
                throw new IllegalArgumentException( "Ingredient of this type already exist" );
            }
        }

        ingredients.add( ingredient );
    }

    /**
     * Returns the name of the recipe.
     *
     * @return String
     */
    @Override
    public String toString () {
        String returnString = "";
        for ( final Ingredient ingredient : ingredients ) {
            returnString += ingredient.toString() + " ";
        }
        return returnString;
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        Integer result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        return result;
    }

    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Recipe other = (Recipe) obj;
        if ( name == null ) {
            if ( other.name != null ) {
                return false;
            }
        }
        else if ( !name.equals( other.name ) ) {
            return false;
        }
        return true;
    }

    /**
     * gets one ingredient in the ingredient list
     *
     * @param type
     *            the name of the ingredient to get
     * @return the ingredient object
     */
    public Ingredient getOneIngredient ( final String type ) {
        for ( int i = 0; i < ingredients.size(); i++ ) {
            if ( ingredients.get( i ).getType().equals( type ) ) {
                return ingredients.get( i );
            }
        }
        return null;
    }

    /**
     * gets all the ingredients
     *
     * @return a list of the ingredients
     */
    public List<Ingredient> getAllIngredients () {
        return this.ingredients;
    }

    /**
     * removes an ingredient from the recipe
     *
     * @param name
     *            the name of the ingredient to remove
     * @return true if removed, false if not found
     */
    public boolean removeIngredient ( final String name ) {
        for ( int i = 0; i < ingredients.size(); i++ ) {
            if ( ingredients.get( i ).getType().equals( name ) ) {
                ingredients.remove( i );
                return true;
            }
        }
        return false;
    }

    /**
     * updates the amount for a ingredient in the recipe
     *
     * @param ingredient
     *            the ingredient to update
     */
    public void updateIngredient ( final Ingredient ingredient ) {
        for ( int i = 0; i < ingredients.size(); i++ ) {
            if ( ingredients.get( i ).getType().equals( ingredient.getType() ) ) {
                ingredients.get( i ).setAmount( ingredient.getAmount() );
            }
        }
    }

    /**
     * sets a new list of ingredients into the recipe
     *
     * @param ingredients
     *            the list of ingredients to set
     */
    public void setIngredients ( final List<Ingredient> ingredients ) {
        this.ingredients = ingredients;
    }

}
