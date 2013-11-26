/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.dmerkushov.tiffhelper;

/**
 *
 * @author Dmitriy Merkushov
 */
public class TiffHelperException extends Exception {
	private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>TiffHelperException</code> without detail message.
     */
    public TiffHelperException() {
    }


    /**
     * Constructs an instance of <code>TiffHelperException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TiffHelperException(String msg) {
        super(msg);
    }


    /**
     * Constructs an instance of <code>TiffHelperException</code> with the specified cause.
	 * @param cause the cause (which is saved for later retrieval by the Exception.getCause() method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public TiffHelperException(Throwable cause) {
        super(cause);
    }


    /**
     * Constructs an instance of <code>TiffHelperException</code> with the specified detail message and cause.
     * @param msg the detail message.
	 * @param cause the cause (which is saved for later retrieval by the Exception.getCause() method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public TiffHelperException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
