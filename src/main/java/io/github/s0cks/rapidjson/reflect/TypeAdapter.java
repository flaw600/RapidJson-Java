/*
 * 
 */
package io.github.s0cks.rapidjson.reflect;

import io.github.s0cks.rapidjson.Value;

// TODO: Auto-generated Javadoc
/**
 * The Interface TypeAdapter.
 *
 * @param <T> the generic type
 */
public interface TypeAdapter<T>{
    
    /**
     * Deserialize.
     *
     * @param tClass the t class
     * @param v the v
     * @return the t
     */
    public T deserialize(Class<T> tClass, Value v);
    
    /**
     * Serialize.
     *
     * @param value the value
     * @return the value
     */
    public Value serialize(T value);
}