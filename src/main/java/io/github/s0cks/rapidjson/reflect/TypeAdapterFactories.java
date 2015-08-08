/*
 * 
 */
package io.github.s0cks.rapidjson.reflect;

import io.github.s0cks.rapidjson.Value;
import io.github.s0cks.rapidjson.Values;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class TypeAdapterFactories.
 */
final class TypeAdapterFactories{
    
    /**
     * The Class EnumTypeAdapter.
     *
     * @param <T> the generic type
     */
    private static final class EnumTypeAdapter<T extends Enum<T>>
    implements TypeAdapter<T>{
        
        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.reflect.TypeAdapter#deserialize(java.lang.Class, io.github.s0cks.rapidjson.Value)
         */
        @Override
        public T deserialize(Class<T> tClass, Value v) {
            return T.valueOf(tClass, v.asString());
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.reflect.TypeAdapter#serialize(java.lang.Object)
         */
        @Override
        public Value serialize(T value) {
            return new Values.StringValue(value.name());
        }
    }

    /** The Constant ENUM_FACTORY. */
    public static final TypeAdapterFactory ENUM_FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> boolean can(TypeToken<T> token) {
            return Enum.class.isAssignableFrom(token.rawType) || token.rawType != Enum.class;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(InstanceFactory factory, TypeToken<T> token) {
            Class<? super T> rawType = token.rawType;
            if(!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class){
                return null;
            }
            return (TypeAdapter<T>) new EnumTypeAdapter();
        }
    };

    /** The Constant COLLECTION_FACTORY. */
    public static final TypeAdapterFactory COLLECTION_FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> boolean can(TypeToken<T> token) {
            return List.class.isAssignableFrom(token.rawType);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(InstanceFactory factory, TypeToken<T> token) {
            Class<? super T> rawType = token.rawType;
            if(!Collection.class.isAssignableFrom(rawType) || rawType == Collection.class){
                return null;
            }
            Type elemType = Types.getCollectionElementType(token.type, rawType);
            TypeAdapter<?> adapter = factory.getAdapter(elemType);
            ObjectConstructor<T> constructor = ObjectConstructorFactory.get(token);
            return (TypeAdapter<T>) new ListTypeAdapter<>(elemType, adapter, constructor, factory);
        }
    };

    /**
     * The Class ListTypeAdapter.
     *
     * @param <T> the generic type
     */
    private static final class ListTypeAdapter<T>
    implements TypeAdapter<Collection<T>>{
        
        /** The elem type. */
        private final Type elemType;
        
        /** The adapter. */
        private final TypeAdapter adapter;
        
        /** The constructor. */
        private final ObjectConstructor<?> constructor;
        
        /** The factory. */
        private final InstanceFactory factory;

        /**
         * Instantiates a new list type adapter.
         *
         * @param elemType the elem type
         * @param adapter the adapter
         * @param constructor the constructor
         * @param factory the factory
         */
        private ListTypeAdapter(Type elemType, TypeAdapter adapter, ObjectConstructor<?> constructor, InstanceFactory factory){
            this.elemType = elemType;
            this.adapter = adapter;
            this.constructor = constructor;
            this.factory = factory;
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.reflect.TypeAdapter#deserialize(java.lang.Class, io.github.s0cks.rapidjson.Value)
         */
        @Override
        @SuppressWarnings("unchecked")
        public Collection<T> deserialize(Class<Collection<T>> listClass, Value v) {
            Collection<T> tList = (Collection<T>) this.constructor.construct();

            for(Value value : v.asArray()){
                try{
                    if(this.adapter != null){
                        tList.add((T) this.adapter.deserialize((Class) this.elemType, value));
                    } else{
                        tList.add((T) this.factory.create((Class) this.elemType, value));
                    }
                } catch(Exception e){
                    throw new RuntimeException(e);
                }
            }

            return tList;
        }

        /* (non-Javadoc)
         * @see io.github.s0cks.rapidjson.reflect.TypeAdapter#serialize(java.lang.Object)
         */
        @Override
        public Value serialize(Collection<T> value) {
            return null;
        }
    }
}