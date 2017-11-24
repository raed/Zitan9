package Utils;

/** Classes implementing this interface support optimized algorithms using timestamps.
 */
public interface Timestamped {
    /**
     *
     * @return the object's current timestamp
     */
    int getTimestamp();

    /** sets the object's curent timestamp.
     *
     * @param timestamp to be set.
     */
    void setTimestamp(int timestamp);
}
