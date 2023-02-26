package ch.hftm.interfaces;

import java.util.List;

/**
 * Common Interface for writing data to a json file
 *
 * @param <T> the type of the data to write to the json file
 */
public interface JsonPersisting<T> {
    /**
     * @param data the data to write to the json file
     */
    void writeDataToJson(List<T> data);
}
