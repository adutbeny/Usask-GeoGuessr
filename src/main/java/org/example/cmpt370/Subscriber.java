package org.example.cmpt370;

/* Property of swagtown
 * CMPT370
 */

public interface Subscriber {
    /** Interface for any class that will need to update
     * when the model changes. For now this will only be the
     * View, but leaves room for expansions/adjustments later on
     */

    void modelUpdated();
}
