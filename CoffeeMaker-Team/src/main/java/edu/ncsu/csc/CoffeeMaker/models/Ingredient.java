package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * ingredient model class
 *
 * @author section 201 group 3
 *
 */
@Entity
public class Ingredient extends DomainObject {

    /** id for the database */
    @Id
    @GeneratedValue
    public Long    id;

    /** amount of the ingredient */
    public Integer amount;

    /** name of the ingredient */
    public String  name;

    /**
     * empty constructor
     */
    public Ingredient () {

    }

    /**
     * constructor with all fields
     *
     * @param type
     *            the name of the ingredient
     * @param amount
     *            amount of ingredient
     */
    public Ingredient ( final String type, final Integer amount ) {
        super();
        if ( amount < 0 ) {
            throw new IllegalArgumentException( "negative amount of ingredient" );
        }
        this.amount = amount;
        this.name = type;
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
     * gets the amount
     *
     * @return amount
     */
    public Integer getAmount () {
        return amount;
    }

    /**
     * sets the amount for the ingredient
     *
     * @param amount
     *            amount to set
     */
    public void setAmount ( final Integer amount ) {
        this.amount = amount;
    }

    /**
     * gets the name of the ingredient
     *
     * @return name
     */
    public String getType () {
        return name;
    }

    /**
     * sets the name of the ingredient
     *
     * @param ingredient
     *            the name to set
     */
    public void setType ( final String ingredient ) {
        this.name = ingredient;
    }

    @Override
    public String toString () {
        return "Ingredient [id=" + id + ", amount=" + amount + ", type=" + name + "]";
    }

}
