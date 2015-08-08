/*
 * 
 */
package io.github.s0cks.rapidjson.reflect;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;

// TODO: Auto-generated Javadoc
/**
 * The Class Types.
 */
public final class Types{
    
    /** The Constant EMPTY_TYPE_ARRAY. */
    private static final Type[] EMPTY_TYPE_ARRAY = new Type[0];

    /** The Constant TYPE_STRING. */
    public static final Type TYPE_STRING = new TypeToken<String>(){}.rawType;
    
    /** The Constant TYPE_BYTE. */
    public static final Type TYPE_BYTE = new TypeToken<Byte>(){}.rawType;
    
    /** The Constant TYPE_SHORT. */
    public static final Type TYPE_SHORT = new TypeToken<Short>(){}.rawType;
    
    /** The Constant TYPE_INTEGER. */
    public static final Type TYPE_INTEGER = new TypeToken<Integer>(){}.rawType;
    
    /** The Constant TYPE_LONG. */
    public static final Type TYPE_LONG = new TypeToken<Long>(){}.rawType;
    
    /** The Constant TYPE_FLOAT. */
    public static final Type TYPE_FLOAT = new TypeToken<Float>(){}.rawType;
    
    /** The Constant TYPE_DOUBLE. */
    public static final Type TYPE_DOUBLE = new TypeToken<Double>(){}.rawType;
    
    /** The Constant TYPE_BOOLEAN. */
    public static final Type TYPE_BOOLEAN = new TypeToken<Boolean>(){}.rawType;

    /**
     * Unbox.
     *
     * @param t the t
     * @return the type
     */
    public static Type unbox(Type t){
        if(t.equals(boolean.class)){
            return TYPE_BOOLEAN;
        } else if(t.equals(short.class)){
            return TYPE_SHORT;
        } else if(t.equals(byte.class)){
            return TYPE_BYTE;
        } else if(t.equals(int.class)){
            return TYPE_INTEGER;
        } else if(t.equals(float.class)){
            return TYPE_FLOAT;
        } else if(t.equals(double.class)){
            return TYPE_DOUBLE;
        } else{
            return t;
        }
    }

    /**
     * Gets the generic supertype.
     *
     * @param ctx the ctx
     * @param rawType the raw type
     * @param toResolve the to resolve
     * @return the generic supertype
     */
    private static Type getGenericSupertype(Type ctx, Class<?> rawType, Class<?> toResolve){
        if(toResolve == rawType){
            return ctx;
        }

        if(toResolve.isInterface()){
            Class<?>[] interfaces = rawType.getInterfaces();
            for(int i = 0; i < interfaces.length; i++){
                if(interfaces[i] == toResolve){
                    return rawType.getGenericInterfaces()[i];
                } else if(toResolve.isAssignableFrom(interfaces[i])){
                    return getGenericSupertype(rawType.getGenericInterfaces()[i], interfaces[i], toResolve);
                }
            }
        }

        if(!rawType.isInterface()){
            while(rawType != Object.class){
                Class<?> rawSuperType = rawType.getSuperclass();
                if(rawSuperType == toResolve){
                    return rawType.getGenericSuperclass();
                } else if(toResolve.isAssignableFrom(rawSuperType)){
                    return getGenericSupertype(rawType.getGenericSuperclass(), rawSuperType, toResolve);
                }
                rawType = rawSuperType;
            }
        }

        return toResolve;
    }

    /**
     * Array of.
     *
     * @param compType the comp type
     * @return the generic array type
     */
    public static GenericArrayType arrayOf(Type compType){
        return new GenericArrayTypeImpl(compType);
    }

    /**
     * Subtype of.
     *
     * @param bound the bound
     * @return the wildcard type
     */
    public static WildcardType subtypeOf(Type bound){
        return new WildcardTypeImpl(new Type[]{ bound }, EMPTY_TYPE_ARRAY);
    }

    /**
     * Supertype of.
     *
     * @param bound the bound
     * @return the wildcard type
     */
    public static WildcardType supertypeOf(Type bound){
        return new WildcardTypeImpl(new Type[]{ Object.class }, new Type[]{ bound });
    }

    /**
     * New parameterized type with owner.
     *
     * @param owner the owner
     * @param raw the raw
     * @param args the args
     * @return the parameterized type
     */
    public static ParameterizedType newParameterizedTypeWithOwner(Type owner, Type raw, Type... args){
        return new ParameterizedTypeImpl(owner, raw, args);
    }

    /**
     * Resolve.
     *
     * @param ctx the ctx
     * @param ctxRawType the ctx raw type
     * @param toResolve the to resolve
     * @return the type
     */
    public static Type resolve(Type ctx, Class<?> ctxRawType, Type toResolve){
        while(true){
            if(toResolve instanceof TypeVariable){
                TypeVariable<?> tv = (TypeVariable<?>) toResolve;
                toResolve = resolveTypeVariable(ctx, ctxRawType, tv);
                if(toResolve == tv){
                    return toResolve;
                }
            } else if(toResolve instanceof Class && ((Class<?>) toResolve).isArray()){
                Class<?> original = (Class<?>) toResolve;
                Type compType = original.getComponentType();
                Type newCompType = resolve(ctx, ctxRawType, compType);
                return compType == newCompType ? original : arrayOf(newCompType);
            } else if(toResolve instanceof GenericArrayType){
                GenericArrayType original = (GenericArrayType) toResolve;
                Type compType = original.getGenericComponentType();
                Type newCompType = resolve(ctx, ctxRawType, compType);
                return compType == newCompType ? original : arrayOf(newCompType);
            } else if(toResolve instanceof ParameterizedType){
                ParameterizedType original = (ParameterizedType) toResolve;
                Type owner = original.getOwnerType();
                Type newOwner = resolve(ctx, ctxRawType, owner);
                boolean changed = newOwner != owner;

                Type[] args = original.getActualTypeArguments();
                for(int i = 0; i < args.length; i++){
                    Type resolvedType = resolve(ctx, ctxRawType, args[i]);
                    if(resolvedType != args[i]){
                        if(!changed){
                            args = args.clone();
                            changed = true;
                        }
                        args[i] = resolvedType;
                    }
                }

                return changed ? newParameterizedTypeWithOwner(newOwner, original.getRawType(), args) : original;
            } else if(toResolve instanceof WildcardType){
                WildcardType original = (WildcardType) toResolve;
                Type[] originalLower = original.getLowerBounds();
                Type[] originalUpper = original.getUpperBounds();

                if(originalLower.length == 1){
                    Type lower = resolve(ctx, ctxRawType, originalLower[0]);
                    if(lower != originalLower[0]){
                        return supertypeOf(lower);
                    }
                } else if(originalUpper.length == 1){
                    Type upper = resolve(ctx, ctxRawType, originalUpper[0]);
                    if(upper != originalUpper[0]){
                        return subtypeOf(upper);
                    }
                }

                return original;
            } else{
                return toResolve;
            }
        }
    }

    /**
     * Resolve type variable.
     *
     * @param ctx the ctx
     * @param ctxRawType the ctx raw type
     * @param tv the tv
     * @return the type
     */
    private static Type resolveTypeVariable(Type ctx, Class<?> ctxRawType, TypeVariable tv){
        Class<?> dec = declaringClassOf(tv);

        if(dec == null){
            return tv;
        }

        Type decBy = getGenericSupertype(ctx, ctxRawType, dec);
        if(decBy instanceof ParameterizedType){
            int index = indexOf(dec.getTypeParameters(), tv);
            return ((ParameterizedType) decBy).getActualTypeArguments()[index];
        }

        return tv;
    }

    /**
     * Index of.
     *
     * @param array the array
     * @param toFind the to find
     * @return the int
     */
    private static int indexOf(Object[] array, Object toFind){
        for(int i = 0; i < array.length; i++){
            if(toFind.equals(array[i])){
                return i;
            }
        }

        throw new NoSuchElementException();
    }

    /**
     * Declaring class of.
     *
     * @param typeVariable the type variable
     * @return the class
     */
    private static Class<?> declaringClassOf(TypeVariable<?> typeVariable){
        GenericDeclaration genDec = typeVariable.getGenericDeclaration();
        return genDec instanceof Class ? (Class<?>) genDec : null;
    }

    /**
     * Gets the super type.
     *
     * @param ctx the ctx
     * @param ctxRawType the ctx raw type
     * @param superType the super type
     * @return the super type
     */
    private static Type getSuperType(Type ctx, Class<?> ctxRawType, Class<?> superType){
        if(!superType.isAssignableFrom(ctxRawType)){
            throw new IllegalArgumentException("super type not assignable from ctxRawType");
        }
        return resolve(ctx, ctxRawType, getGenericSupertype(ctx, ctxRawType, superType));
    }

    /**
     * Gets the collection element type.
     *
     * @param ctx the ctx
     * @param ctxRawType the ctx raw type
     * @return the collection element type
     */
    public static Type getCollectionElementType(Type ctx, Class<?> ctxRawType){
        Type collType = getSuperType(ctx, ctxRawType, Collection.class);
        if(collType instanceof WildcardType){
            collType = ((WildcardType) collType).getUpperBounds()[0];
        }

        if(collType instanceof ParameterizedType){
            return ((ParameterizedType) collType).getActualTypeArguments()[0];
        }

        return Object.class;
    }

    /**
     * Array.
     *
     * @param t the t
     * @return true, if successful
     */
    public static boolean array(Type t){
        return t instanceof GenericArrayType
            || (t instanceof Class && ((Class<?>) t).isArray());
    }

    /**
     * Canonicalize.
     *
     * @param t the t
     * @return the type
     */
    public static Type canonicalize(Type t){
        if(t instanceof Class){
            Class<?> c = (Class<?>) t;
            return c.isArray() ? new GenericArrayTypeImpl(canonicalize(c.getComponentType())) : c;
        } else if(t instanceof ParameterizedType){
            ParameterizedType pt = (ParameterizedType) t;
            return new ParameterizedTypeImpl(pt.getOwnerType(), pt.getRawType(), pt.getActualTypeArguments());
        } else if(t instanceof GenericArrayType){
            return new GenericArrayTypeImpl(((GenericArrayType) t).getGenericComponentType());
        } else if(t instanceof WildcardType){
            WildcardType wt = (WildcardType) t;
            return new WildcardTypeImpl(wt.getUpperBounds(), wt.getLowerBounds());
        } else{
            return t;
        }
    }

    /**
     * Gets the raw type.
     *
     * @param t the t
     * @return the raw type
     */
    public static Class<?> getRawType(Type t){
        if(t instanceof Class<?>){
            return (Class<?>) t;
        } else if(t instanceof ParameterizedType){
            ParameterizedType pt = (ParameterizedType) t;
            Type rawType = pt.getRawType();
            if(!(rawType instanceof Class<?>)){
                throw new IllegalArgumentException("rawType not instanceof Class");
            }

            return (Class<?>) rawType;
        } else if(t instanceof GenericArrayType){
            Type compType = ((GenericArrayType) t).getGenericComponentType();
            return Array.newInstance(getRawType(compType), 0).getClass();
        } else if(t instanceof TypeVariable){
            return Object.class;
        } else if(t instanceof WildcardType){
            return getRawType(((WildcardType) t).getUpperBounds()[0]);
        } else{
            String className = t == null ? "null" : t.getClass().getName();
            throw new IllegalArgumentException("type <" + t + "> is of type <" + className + ">");
        }
    }

    /**
     * Equal.
     *
     * @param a the a
     * @param b the b
     * @return true, if successful
     */
    private static boolean equal(Object a, Object b){
        return a == b || (a != null && a.equals(b));
    }

    /**
     * Equals.
     *
     * @param a the a
     * @param b the b
     * @return true, if successful
     */
    public static boolean equals(Type a, Type b){
        if(a == b){
            return true;
        } else if(a instanceof Class){
            return a.equals(b);
        } else if(a instanceof ParameterizedType){
            if(!(b instanceof ParameterizedType)){
                return false;
            }

            ParameterizedType pa = (ParameterizedType) a;
            ParameterizedType pb = (ParameterizedType) b;
            return equal(pa.getOwnerType(), pb.getOwnerType())
                && pa.getRawType().equals(pb.getRawType())
                && Arrays.equals(pa.getActualTypeArguments(), pb.getActualTypeArguments());
        } else if(a instanceof GenericArrayType){
            if(!(b instanceof GenericArrayType)){
                return false;
            }

            GenericArrayType ga = (GenericArrayType) a;
            GenericArrayType gb = (GenericArrayType) b;
            return equals(ga.getGenericComponentType(), gb.getGenericComponentType());
        } else if(a instanceof TypeVariable){
            if(!(b instanceof TypeVariable)){
                return false;
            }

            TypeVariable<?> va = (TypeVariable<?>) a;
            TypeVariable<?> vb = (TypeVariable<?>) b;
            return va.getGenericDeclaration() == vb.getGenericDeclaration()
                && va.getName().equals(vb.getName());
        } else{
            return false;
        }
    }

    /**
     * Type to string.
     *
     * @param t the t
     * @return the string
     */
    public static String typeToString(Type t){
        return t instanceof Class<?> ? ((Class<?>) t).getName() : t.toString();
    }

    /**
     * The Class GenericArrayTypeImpl.
     */
    private static final class GenericArrayTypeImpl
    implements GenericArrayType,
               Serializable{
        
        /** The comp type. */
        private final Type compType;

        /**
         * Instantiates a new generic array type impl.
         *
         * @param compType the comp type
         */
        private GenericArrayTypeImpl(Type compType){
            this.compType = compType;
        }

        /* (non-Javadoc)
         * @see java.lang.reflect.GenericArrayType#getGenericComponentType()
         */
        @Override
        public Type getGenericComponentType() {
            return this.compType;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object o){
            return o instanceof GenericArrayType && Types.equals(this, (GenericArrayType) o);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode(){
            return this.compType.hashCode();
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString(){
            return Types.typeToString(this.compType) + "[]";
        }
    }

    /**
     * The Class WildcardTypeImpl.
     */
    private static final class WildcardTypeImpl
    implements WildcardType,
               Serializable{
        
        /** The lower. */
        private final Type upper, lower;

        /**
         * Instantiates a new wildcard type impl.
         *
         * @param upper the upper
         * @param lower the lower
         */
        private WildcardTypeImpl(Type[] upper, Type[] lower) {
            if(lower.length == 1){
                if(!(upper[0] == Object.class)
                || lower[0] == null){
                    throw new IllegalStateException("");
                }

                this.lower = canonicalize(lower[0]);
                this.upper = Object.class;
            } else{
                if(upper[0] == null){
                    throw new IllegalStateException("");
                }
                this.lower = null;
                this.upper = canonicalize(upper[0]);
            }
        }

        /* (non-Javadoc)
         * @see java.lang.reflect.WildcardType#getUpperBounds()
         */
        @Override
        public Type[] getUpperBounds() {
            return new Type[]{ this.upper };
        }

        /* (non-Javadoc)
         * @see java.lang.reflect.WildcardType#getLowerBounds()
         */
        @Override
        public Type[] getLowerBounds() {
            return new Type[]{ this.lower };
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj){
            return obj instanceof WildcardType
                && Types.equals(this, (WildcardType) obj);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode(){
            return (this.lower != null ? 31 + this.lower.hashCode() : 1)
                ^ (31 + this.upper.hashCode());
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString(){
            if(lower != null){
                return "? super " + typeToString(this.lower);
            } else if(upper == Object.class){
                return "?";
            } else{
                return "? extends " + typeToString(this.upper);
            }
        }
    }

    /**
     * The Class ParameterizedTypeImpl.
     */
    private static final class ParameterizedTypeImpl
    implements ParameterizedType,
               Serializable{
        
        /** The owner. */
        private final Type owner;
        
        /** The raw. */
        private final Type raw;
        
        /** The args. */
        private final Type[] args;

        /**
         * Instantiates a new parameterized type impl.
         *
         * @param owner the owner
         * @param raw the raw
         * @param args the args
         */
        private ParameterizedTypeImpl(Type owner, Type raw, Type... args) {
            if(raw instanceof Class){
                Class<?> c = (Class<?>) raw;
                boolean staticOrTopLevel = Modifier.isStatic(c.getModifiers())
                        || c.getEnclosingClass() == null;
                if(!(owner == null || !staticOrTopLevel)){
                    throw new IllegalStateException("staticOrTopLevel?=" + staticOrTopLevel + "; owner=" + (owner == null));
                }
            }

            this.owner = owner == null ? null : canonicalize(owner);
            this.raw = canonicalize(raw);
            this.args = new Type[args.length];
            for(int i = 0; i < args.length; i++){
                if(args[i] == null){
                    throw new IllegalStateException("");
                }

                this.args[i] = canonicalize(args[i]);
            }
        }

        /* (non-Javadoc)
         * @see java.lang.reflect.ParameterizedType#getActualTypeArguments()
         */
        @Override
        public Type[] getActualTypeArguments(){
            return this.args.clone();
        }

        /* (non-Javadoc)
         * @see java.lang.reflect.ParameterizedType#getRawType()
         */
        @Override
        public Type getRawType(){
            return this.raw;
        }

        /* (non-Javadoc)
         * @see java.lang.reflect.ParameterizedType#getOwnerType()
         */
        @Override
        public Type getOwnerType(){
            return this.owner;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object o){
            return o instanceof ParameterizedType
                && Types.equals(this, (ParameterizedType) o);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode(){
            return Arrays.hashCode(this.args)
                ^ this.raw.hashCode()
                ^ (this.owner == null ? 0 : this.owner.hashCode());
        }
    }
}