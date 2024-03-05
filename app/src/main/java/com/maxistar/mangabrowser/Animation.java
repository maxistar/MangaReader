/*
 */
package com.maxistar.mangabrowser;

/**
 * @author Jason Polites
 *
 */
public interface Animation {

    /**
     * Transforms the view.
     * @return true if this animation should remain active.  False otherwise.
     */
    public boolean update();

}
