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

    /** users enterd code for the staff */
    private String              submittedStaffCode;

    /** code for if a user is a staff member */
    private static final String STAFF_CODE = "OnePiece";

    /** whether a user is anonymous or not */
    private boolean             isAnon;

    /** what order number the user is on - only applicable to customers */
    private int                 orderNumber;

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
     * @param submittedStaffCode
     *            code to indicate if a user is staff or not
     * @param isStaff
     *            true if user is staff, false otherwise
     */
    public User ( final String username, final String password, final String submittedStaffCode, final boolean isStaff,
            final boolean isAnon ) {
        this.username = username;
        this.password = password;
        this.isStaff = isStaff;
        this.submittedStaffCode = submittedStaffCode;
        if ( isStaff && !submittedStaffCode.equals( STAFF_CODE ) ) {
            throw new IllegalArgumentException( "Staff code is incorrect" );
        }
        this.isAnon = isAnon;
        this.orderNumber = 0;
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
     * gets the staff code
     *
     * @return the staff code
     */
    public String getStaffCode () {
        return STAFF_CODE;
    }

    /**
     * gets the submitted staff code
     *
     * @return the staff code submitted
     */
    public String getSubmittedStaffCode () {
        return submittedStaffCode;
    }

    /**
     * sets the submitted staff code
     *
     * @param submittedStaffCode
     *            the code to set
     */
    public void setSubmittedStaffCode ( final String submittedStaffCode ) {
        this.submittedStaffCode = submittedStaffCode;
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
     * Gets staff the isAnon status of user
     *
     * @return true if isAnon is true, false otherwise
     */
    public boolean getIsAnon () {
        return isAnon;
    }

    /**
     * sets the is anon of user
     *
     * @param boolean
     *            status to set
     *
     */
    public void setIsAnon ( final boolean status ) {
        this.isAnon = status;
    }

    /**
     * Gets the order number for the user
     *
     * @return order number for customer
     */
    public int getOrderNumber () {
        return orderNumber;
    }

    /**
     * Sets the order number for the user
     *
     * @param newOrderNumber
     *            order number to set to
     */
    public void setOrderNumber ( final int newOrderNumber ) {
        orderNumber = newOrderNumber;
    }

    /**
     * Gets string representation of User object
     *
     * @return string with user information
     */
    @Override
    public String toString () {
        return "User [id=" + id + ", username=" + username + ", password=" + password + ", isStaff=" + isStaff + "]";
    }

}
