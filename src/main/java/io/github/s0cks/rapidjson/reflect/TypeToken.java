/*
 * 
 */
package io.github.s0cks.rapidjson.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

// TODO: Auto-generated Javadoc
/**
 * The Class TypeToken.
 *
 * @param <T> the generic type
 */
public class TypeToken<T>{
    
    /** The raw type. */
    public final Class<? super T> rawType;
    
    /** The type. */
    public final Type type;

    /** The hash code. */
    private final int hashCode;

    /**
     * Of.
     *
     * @param f the f
     * @return the type token
     */
    public static TypeToken of(Field f){
        return new TypeToken(f.getType());
    }

    /**
     * Of.
     *
     * @param tClass the t class
     * @return the type token
     */
    public static TypeToken of(Class<?> tClass){
        return new TypeToken(tClass);
    }

    /**
     * Instantiates a new type token.
     */
    @SuppressWarnings("unchecked")
    protected TypeToken(){
        this.type = getSuperclassTypeParameter(this.getClass());
        this.rawType = (Class<? super T>) Types.getRawType(this.type);
        this.hashCode = type.hashCode();
    }


    /**
     * Instantiates a new type token.
     *
     * @param t the t
     */
    @SuppressWarnings("unchecked")
    protected TypeToken(Type t){
        this.type = Types.canonicalize(t);
        this.rawType = (Class<? super T>) Types.getRawType(this.type);
        this.hashCode = this.type.hashCode();
    }

    /**
     * Gets the superclass type parameter.
     *
     * @param subclass the subclass
     * @return the superclass type parameter
     */
    private static Type getSuperclassTypeParameter(Class<?> subclass){
        Type superClass = subclass.getGenericSuperclass();
        if(superClass instanceof Class){
            throw new RuntimeException("Missing type parameter");
        }
        ParameterizedType pt = (ParameterizedType) superClass;
        return Types.canonicalize(pt.getActualTypeArguments()[0]);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode(){
        return this.hashCode;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(Object o){
        return o instanceof TypeToken<?>
            && Types.equals(this.type, ((TypeToken<?>) o).type);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString(){
        return Types.typeToString(this.type);
    }
}