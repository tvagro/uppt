package tv.puppetmaster.data.i;

import java.io.Serializable;
import java.util.Iterator;

public interface Puppet extends Serializable {

    /*
     * The name of this puppet, usually used to display the title of the content
     */
    String getName();

    /*
     * The broader category that describes the content represented by this puppet
     */
    String getCategory();

    /*
     * A brief description of the content
     */
    String getShortDescription();

    /*
     * Url for a thumbnail preferably a 300px^2 .png
     */
    String getImageUrl();

    /*
     * Url for a large image to display as a background, preferably in a compressed format
     */
    String getBackgroundImageUrl();

    /*
     * If the content is available in the given region
     * @param region Usually a 2 character lower-case country code (ISO 3166 alpha-2)
     * @return If the content represented by this puppet is available in the given region
     */
    boolean isAvailable(String region);

    /*
     * @return A list of regions best supporting access to the content represented by this puppet
     * or null if all regions are supported
     */
    String[] preferredRegions();

    /*
     * @return An integer between 0-9 representing the immigration stricture of the primary
     * preferred region:
     * 0 == free immigration
     * 9 == full separation
     */
    int immigrationStricture();

    /*
     * The parent of this puppet or null if it is a top-level item
     */
    ParentPuppet getParent();

    /*
     * Media related to this puppet
     */
    PuppetIterator getRelated();

    /*
     * Whatever describes this puppet best, usually an internal String representation in the form
     * of: top-level < first descendant < ... < current item
     */
    String toString();

    abstract class PuppetIterator implements Iterator<Puppet>, Serializable {
        public abstract void add(Puppet puppet);
    }

}