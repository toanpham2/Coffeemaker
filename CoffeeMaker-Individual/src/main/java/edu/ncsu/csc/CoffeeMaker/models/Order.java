package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Order extends DomainObject {

    /** id for the database */
    @Id
    @GeneratedValue
    public Long    id;

    /** recipe used by the order */
    public Recipe  recipe;

    /** name of the order */
    public String  name;

    /** status if order is fulfilled */
    public boolean isFulfilled = false;

    /** status if order is picked up */
    public boolean isPickedUp  = false;

    /** empty constructor */
    public Order () {

    }

    public Order ( final Recipe recipe, final user user ) {
        super();
        this.recipe = recipe;
        if
        this.name = user.getName();
    }

    @Override
    public Long getId () {
        return id;
    }

    /**
     * sets the id
     *
     * @param id
     *            id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * gets the recipe
     *
     * @return recipe
     */
    public Recipe getRecipe () {
        return recipe;
    }

    /**
     * sets the recipe for the order
     *
     * @param recipe
     *            amount to set
     */
    public void setRecipe ( final Recipe recipe ) {
        this.recipe = recipe;
    }

    /**
     * gets the name
     *
     * @return name
     */
    public String getName () {
        return name;
    }

    /**
     * sets the name of the order
     *
     * @param name
     *            name to set
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * gets fulfilled status
     *
     * @return boolean of is the order fulfilled
     */
    public Boolean getFulfilled () {
        return isFulfilled;
    }

    /**
     * sets the fulfilled status of order
     *
     * @param status
     *            status to set
     */
    public void setFulfilled ( final boolean status ) {
        this.isFulfilled = status;
    }

    /**
     * gets pickedUp status
     *
     * @return boolean of is the order pickedUp
     */
    public Boolean getPickedUp () {
        return isPickedUp;
    }

    /**
     * sets the pickedUp status of order
     *
     * @param status
     *            status to set
     */
    public void setPickedUp ( final boolean status ) {
        this.isPickedUp = status;
    }

}
