/*
 * 
 */
package io.github.s0cks.rapidjson.reflect;

import io.github.s0cks.rapidjson.JsonException;
import io.github.s0cks.rapidjson.SerializedName;
import io.github.s0cks.rapidjson.RapidJson;
import io.github.s0cks.rapidjson.Value;
import io.github.s0cks.rapidjson.Values;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating Instance objects.
 */
public final class InstanceFactory{
    
    /** The adapters. */
    private final Map<Type, TypeAdapter> adapters;
    
    /** The factories. */
    private final List<TypeAdapterFactory> factories;
    
    /** The json. */
    private final RapidJson json;

    /**
     * Instantiates a new instance factory.
     *
     * @param json the json
     * @param adapters the adapters
     * @param factories the factories
     */
    public InstanceFactory(RapidJson json, Map<Type, TypeAdapter> adapters, List<TypeAdapterFactory> factories){
        this.json = json;
        this.adapters = adapters;
        this.adapters.put(Types.TYPE_BOOLEAN, TypeAdapters.BOOLEAN_ADAPTER);
        this.adapters.put(Types.TYPE_BYTE, TypeAdapters.BYTE_ADAPTER);
        this.adapters.put(Types.TYPE_DOUBLE, TypeAdapters.DOUBLE_ADAPTER);
        this.adapters.put(Types.TYPE_FLOAT, TypeAdapters.FLOAT_ADAPTER);
        this.adapters.put(Types.TYPE_INTEGER, TypeAdapters.INTEGER_ADAPTER);
        this.adapters.put(Types.TYPE_LONG, TypeAdapters.LONG_ADAPTER);
        this.adapters.put(Types.TYPE_SHORT, TypeAdapters.SHORT_ADAPTER);
        this.adapters.put(Types.TYPE_STRING, TypeAdapters.STRING_ADAPTER);
        this.factories = factories;
        this.factories.add(TypeAdapterFactories.COLLECTION_FACTORY);
        this.factories.add(TypeAdapterFactories.ENUM_FACTORY);
    }

    /**
     * Gets the adapter.
     *
     * @param t the t
     * @return the adapter
     */
    public TypeAdapter<?> getAdapter(Type t){
        return this.getAdapter(new TypeToken(t));
    }

    /**
     * Emit.
     *
     * @param <T> the generic type
     * @param t the t
     * @param root the root
     * @throws IllegalAccessException the illegal access exception
     * @throws JsonException the json exception
     */
    @SuppressWarnings("unchecked")
    public <T> void emit(T t, Value root)
    throws IllegalAccessException, JsonException {
        for(Field field : t.getClass().getDeclaredFields()){
            if(!field.isAccessible()){
                field.setAccessible(true);
            }

            TypeToken token = TypeToken.of(field);
            Type type = Types.unbox(token.type);
            if(Types.array(type)){
                type = Types.unbox(((GenericArrayType) type).getGenericComponentType());
                Object array = field.get(t);
                Value[] values = new Value[Array.getLength(array)];
                for(int i = 0; i < values.length; i++){
                    values[i] = (isGeneric(type) ? Values.of(Array.get(array, i)) : (this.adapters.get(type) != null ? this.adapters.get(type).serialize(Array.get(array, i)) : Values.NullValue.NULL));
                }
                root.setValue(this.getFieldName(field), new Values.ArrayValue(values));
            } else{
                this.emitField(t, field, type, root);
            }
        }
    }

    /**
     * Gets the field name.
     *
     * @param f the f
     * @return the field name
     */
    private String getFieldName(Field f){
        return f.isAnnotationPresent(SerializedName.class) ? f.getAnnotation(SerializedName.class).value() : f.getName();
    }

    /**
     * Emit field.
     *
     * @param <T> the generic type
     * @param instance the instance
     * @param f the f
     * @param type the type
     * @param root the root
     * @throws IllegalAccessException the illegal access exception
     * @throws JsonException the json exception
     */
    @SuppressWarnings("unchecked")
    private <T> void emitField(T instance, Field f, Type type, Value root)
    throws IllegalAccessException, JsonException {
        if(isGeneric(type)){
            root.setValue(this.getFieldName(f), Values.of(instance, f));
        } else if(this.adapters.containsKey(type)){
            root.setValue(this.getFieldName(f), this.adapters.get(type).serialize(f.get(instance)));
        } else{
            throw new JsonException("No type adapter for type: " + type);
        }
    }

    /**
     * Gets the adapter.
     *
     * @param <T> the generic type
     * @param token the token
     * @return the adapter
     */
    @SuppressWarnings("unchecked")
    private <T> TypeAdapter<T> getAdapter(TypeToken<T> token){
        if(this.adapters.containsKey(token.rawType)){
            return this.adapters.get(token.rawType);
        }

        for(TypeAdapterFactory factory : this.factories){
            if(factory.can(token)){
                return factory.create(this, token);
            }
        }

        return null;
    }

    /**
     * Creates the.
     *
     * @param <T> the generic type
     * @param t the t
     * @param value the value
     * @return the t
     * @throws IllegalAccessException the illegal access exception
     * @throws JsonException the json exception
     * @throws NoSuchFieldException the no such field exception
     */
    @SuppressWarnings("unchecked")
    public <T> T create(TypeToken<T> t, Value value)
    throws IllegalAccessException, JsonException, NoSuchFieldException{
        Type type = Types.unbox(t.type);
        TypeAdapter<T> adapter = this.getAdapter(t);
        if(adapter != null){
            return adapter.deserialize((Class<T>) t.rawType, value);
        }

        if(Types.array(t.type) || Types.array(t.rawType)){
            Value[] values = value.asArray();
            Object array = Array.newInstance(Types.getRawType(((GenericArrayType) t.type).getGenericComponentType()), values.length);
            for(int i = 0; i < values.length; i++){
                Array.set(array, i, this.create(Types.getRawType(((GenericArrayType) t.type).getGenericComponentType()), values[i]));
            }
            return (T) array;
        }

        ObjectConstructor<T> tObjectConstructor = ObjectConstructorFactory.get(t);
        T instance = tObjectConstructor.construct();
        for(Field field : t.rawType.getDeclaredFields()){
            if(!Modifier.isStatic(field.getModifiers())){
                if(!field.isAccessible()) {
                    field.setAccessible(true);
                }

                TypeToken token = TypeToken.of(field);
                Type ftype = Types.unbox(token.type);
                int mods = field.getModifiers();

                if(Modifier.isFinal(mods)){
                    Field modField = t.rawType.getClass().getDeclaredField("modifiers");
                    modField.setAccessible(true);
                    modField.setInt(field, mods & ~Modifier.FINAL);
                    if(isGeneric(type)){
                        set(instance, ftype, field, value.getValue(getFieldName(field)));
                    } else{
                        field.set(instance, this.create(Types.getRawType(ftype), value.getValue(getFieldName(field))));
                    }
                    modField.set(field, mods);
                } else{
                    if(isGeneric(type)){
                        set(instance, ftype, field, value.getValue(getFieldName(field)));
                    } else{
                        field.set(instance, this.create(Types.getRawType(ftype), value.getValue(getFieldName(field))));
                    }
                }
            }
        }

        return instance;
    }

    /**
     * Creates the.
     *
     * @param <T> the generic type
     * @param tClass the t class
     * @param value the value
     * @return the t
     * @throws IllegalAccessException the illegal access exception
     * @throws NoSuchFieldException the no such field exception
     */
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> tClass, Value value)
    throws IllegalAccessException, NoSuchFieldException {
        TypeToken t = TypeToken.of(tClass);
        Type type = Types.unbox(t.type);
        TypeAdapter<T> adapter = this.getAdapter(t);
        if(adapter != null){
            return adapter.deserialize((Class<T>) t.rawType, value);
        }

        if(Types.array(t.type) || Types.array(t.rawType)){
            Value[] values = value.asArray();
            Object array = Array.newInstance(Types.getRawType(((GenericArrayType) t.type).getGenericComponentType()), values.length);
            for(int i = 0; i < values.length; i++){
                Array.set(array, i, this.create(Types.getRawType(((GenericArrayType) t.type).getGenericComponentType()), values[i]));
            }
            return (T) array;
        }

        ObjectConstructor<T> tObjectConstructor = ObjectConstructorFactory.get(TypeToken.of(tClass));
        T instance = tObjectConstructor.construct();
        for(Field field : tClass.getDeclaredFields()){
            if(!Modifier.isStatic(field.getModifiers())){
                if(!field.isAccessible()) {
                    field.setAccessible(true);
                }

                TypeToken token = TypeToken.of(field);
                Type ftype = Types.unbox(token.type);
                int mods = field.getModifiers();

                if(Modifier.isFinal(mods)){
                    Field modField = field.getClass().getDeclaredField("modifiers");
                    modField.setAccessible(true);
                    modField.setInt(field, mods & ~Modifier.FINAL);
                    if(isGeneric(type)){
                        set(instance, ftype, field, value.getValue(getFieldName(field)));
                    } else{
                        field.set(instance, this.create(Types.getRawType(ftype), value.getValue(getFieldName(field))));
                    }
                    modField.set(field, mods);
                } else{
                    if(isGeneric(type)){
                        set(instance, ftype, field, value.getValue(getFieldName(field)));
                    } else{
                        field.set(instance, this.create(Types.getRawType(ftype), value.getValue(getFieldName(field))));
                    }
                }
            }
        }

        return instance;
    }

    /**
     * Sets the.
     *
     * @param <T> the generic type
     * @param instance the instance
     * @param raw the raw
     * @param f the f
     * @param value the value
     * @throws IllegalAccessException the illegal access exception
     */
    private static <T> void set(T instance, Type raw, Field f, Value value)
    throws IllegalAccessException {
        if(raw.equals(Types.TYPE_BOOLEAN)){
            f.setBoolean(instance, value.asBoolean());
        } else if(raw.equals(Types.TYPE_BYTE)){
            f.setByte(instance, value.asByte());
        } else if(raw.equals(Types.TYPE_DOUBLE)){
            f.setDouble(instance, value.asDouble());
        } else if(raw.equals(Types.TYPE_FLOAT)){
            f.setFloat(instance, value.asFloat());
        } else if(raw.equals(Types.TYPE_INTEGER)){
            f.setInt(instance, value.asInt());
        } else if(raw.equals(Types.TYPE_LONG)){
            f.setLong(instance, value.asLong());
        } else if(raw.equals(Types.TYPE_SHORT)){
            f.setShort(instance, value.asShort());
        } else if(raw.equals(Types.TYPE_STRING)){
            f.set(instance, value.asString());
        } else{
            throw new RuntimeException("Invalid primitive type: " + raw);
        }
    }

    /**
     * Checks if is generic.
     *
     * @param raw the raw
     * @return true, if is generic
     */
    private static boolean isGeneric(Type raw){
        return raw.equals(Types.TYPE_BOOLEAN)
            || raw.equals(Types.TYPE_BYTE)
            || raw.equals(Types.TYPE_DOUBLE)
            || raw.equals(Types.TYPE_FLOAT)
            || raw.equals(Types.TYPE_INTEGER)
            || raw.equals(Types.TYPE_LONG)
            || raw.equals(Types.TYPE_SHORT)
            || raw.equals(Types.TYPE_STRING);
    }
}