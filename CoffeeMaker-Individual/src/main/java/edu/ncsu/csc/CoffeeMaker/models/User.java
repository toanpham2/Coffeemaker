package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Represents a User object, which includes a username, password, and boolean
 * indicating whether or not a user is a staff member
 *
 * @author stnguye3
 * @author tgpham
 * @author bcchavez
 */
@Entity
public class User extends DomainObject {

    /** id for the database */
    @Id
    @GeneratedValue
    public Long                 id;

    /** username for user */
    private String              username;

    /** password for user */
    private String              password;

    /** whether a user is staff or not */
    private boolean             isStaff;

    /** code for if a user is a staff member */
    private static final String STAFF_CODE = "OnePiece";

    /**
     * empty constructor
     */
    public User () {

    }

    /**
     * Constructs new user object
     *
     * @param username
     *            username for user
     * @param password
     *            password for user
     * @param staffCode
     *            code to indicate if a user is staff or not
     */
    public User ( final String username, final String password, final String staffCode ) {
        this.username = username;
        this.password = password;
        if ( staffCode.equals( STAFF_CODE ) ) {
            isStaff = true;
        }
        else {
            isStaff = false;
        }
    }

    /**
     * Gets username
     *
     * @return username for user
     */
    public String getUsername () {
        return username;
    }

    /**
     * Gets password
     *
     * @return password for user
     */
    public String getPassword () {
        return password;
    }

    /**
     * Sets username for user
     *
     * @param username
     *            username to set
     */
    public void setUsername ( final String username ) {
        this.username = username;
    }

    /**
     * Sets password for user
     *
     * @param password
     *            password to set
     */
    public void setPassword ( final String password ) {
        this.password = password;
    }

    /**
     * Gets staff status of user
     *
     * @return true if isStaff is true, false otherwise
     */
    public boolean getIsStaff () {
        return isStaff;
    }

    /**
     * Gets id for the user
     */
    @Override
    public Serializable getId () {
        return id;
    }

    /**
     * Gets string representation of User object
     *
     * @return string with user information
     */
    @Override
    public String toString () {
        return "User [id=" + id + ", username=" + username + ", password=" + password + ", isStaff=" + isStaff + "]\n";
    }

}
