package io.github.s0cks.rapidjson;

import io.github.s0cks.rapidjson.io.JsonParser;
import io.github.s0cks.rapidjson.reflect.InstanceFactory;
import io.github.s0cks.rapidjson.reflect.TypeAdapter;
import io.github.s0cks.rapidjson.reflect.TypeAdapterFactory;
import io.github.s0cks.rapidjson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;

public final class RapidJson{
    private final InstanceFactory instanceFactory;

    public RapidJson(){
        this.instanceFactory = new InstanceFactory(this, new HashMap<Type, TypeAdapter>(), new LinkedList<TypeAdapterFactory>());
    }

    protected RapidJson(RapidJsonBuilder builder){
        this.instanceFactory = new InstanceFactory(this, builder.adapters, builder.factories);
    }

    public <T> T fromJson(String json, Class<T> tClass)
    throws JsonException{
        try {
            return this.instanceFactory.create(tClass, new JsonParser(json).parse());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T fromJson(String json, TypeToken<T> t)
    throws JsonException{
        try{
            return this.instanceFactory.create(t, new JsonParser(json).parse());
        } catch(IllegalAccessException | NoSuchFieldException e){
            throw new RuntimeException(e);
        }
    }

    public <T> T fromJson(InputStream in, TypeToken<T> t)
    throws JsonException{
        return fromJson(IO.consume(in), t);
    }

    public <T> T fromJson(InputStream in, Class<T> tClass)
    throws JsonException{
        return fromJson(IO.consume(in), tClass);
    }

    public String toJson(Object obj)
    throws JsonException{
        Value v = new Values.ObjectValue();
        try {
            this.instanceFactory.emit(obj, v);
        } catch (IllegalAccessException e) {
            throw new JsonException(e.getMessage());
        }
        return v.toString();
    }
}