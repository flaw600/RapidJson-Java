/*
 * 
 */
package io.github.s0cks.rapidjson.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating ObjectConstructor objects.
 */
final class ObjectConstructorFactory{
    
    /**
     * Gets the.
     *
     * @param <T> the generic type
     * @param token the token
     * @return the object constructor
     */
    public static <T> ObjectConstructor<T> get(TypeToken<T> token){
        Type type = token.type;
        Class<? super T> rawType = token.rawType;

        ObjectConstructor<T> constructor = newDefaultConstructor(rawType);
        if(constructor != null){
            return constructor;
        }

        constructor = newUnsafeConstructor(type, rawType);
        return constructor;
    }

    /**
     * New default constructor.
     *
     * @param <T> the generic type
     * @param rawType the raw type
     * @return the object constructor
     */
    public static <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> rawType){
        try{
            final Constructor<? super T> c = rawType.getDeclaredConstructor();
            if(!c.isAccessible()){
                c.setAccessible(true);
            }
            return new ObjectConstructor<T>() {
                @Override
                public T construct() {
                    try{
                        return (T) c.newInstance();
                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        } catch(NoSuchMethodException e){
            return null;
        }
    }

    /**
     * New unsafe constructor.
     *
     * @param <T> the generic type
     * @param type the type
     * @param rawType the raw type
     * @return the object constructor
     */
    public static <T> ObjectConstructor<T> newUnsafeConstructor(final Type type, final Class<? super T> rawType){
        return new ObjectConstructor<T>() {
            @Override
            public T construct() {
                try{
                    return (T) UnsafeAllocator.instance().newInstance(rawType);
                } catch(Exception e){
                    e.printStackTrace(System.err);
                    return null;
                }
            }
        };
    }
}