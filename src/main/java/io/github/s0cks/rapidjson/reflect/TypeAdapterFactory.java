/*
 * 
 */
package io.github.s0cks.rapidjson.reflect;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating TypeAdapter objects.
 */
public interface TypeAdapterFactory{
    
    /**
     * Can.
     *
     * @param <T> the generic type
     * @param token the token
     * @return true, if successful
     */
    public <T> boolean can(TypeToken<T> token);
    
    /**
     * Creates the.
     *
     * @param <T> the generic type
     * @param factory the factory
     * @param token the token
     * @return the type adapter
     */
    public <T> TypeAdapter<T> create(InstanceFactory factory, TypeToken<T> token);
}