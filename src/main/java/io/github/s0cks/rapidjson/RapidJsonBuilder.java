/*
 * 
 */
package io.github.s0cks.rapidjson;

import io.github.s0cks.rapidjson.reflect.TypeAdapter;
import io.github.s0cks.rapidjson.reflect.TypeAdapterFactory;
import io.github.s0cks.rapidjson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class RapidJsonBuilder . TODO
 */
public final class RapidJsonBuilder{
    
    /** The {@link HashMap} of the {@link Types} and {@link TypeAdapters}. */
    protected final Map<Type, TypeAdapter> adapters = new HashMap<>();
    
    /** The {@link LinkedList} of {@link TypeAdapterFactory}. */
    protected final List<TypeAdapterFactory> factories = new LinkedList<>();

    /**
     * Registers the {@link TypeAdapter}.
     *
     * @param t the Type to register 
     * @param tad the TypeAdapter tad
     * @return the RapidJsonBuilder
     */
    public RapidJsonBuilder registerTypeAdapter(Type t, TypeAdapter tad){
        this.adapters.put(t, tad);
        return this;
    }

    /**
     * Registers the {@link TypeAdapter}. TODO
     *
     * @param c the generic Class c
     * @param tad the TypeAdapter tad
     * @return the RapidJsonBuilder
     */
    public RapidJsonBuilder registerTypeAdpater(Class<?> c, TypeAdapter tad){
        this.adapters.put(TypeToken.of(c).type, tad);
        return this;
    }

    /**
     * Registers the {@link TypeAdapterFactory} with the LinkedList of factories.
     *
     * @param fact the {@code TypeAdapterFactory} that
     * @return the RapidJsonBuilder
     */
    public RapidJsonBuilder registerTypeAdapterFactory(TypeAdapterFactory fact){
        this.factories.add(fact);
        return this;
    }

    /**
     * Builds the {@link RapidJson} to convert to/from JSON and the equivalent Java Class.
     *
     * @return the RapidJson
     */
    public RapidJson build(){
        return new RapidJson(this);
    }
}