package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by dsanchez on 12/2/17.
 */
public class Databag {
    private Map<String, Object> keyValues;

    public Databag(Map<String,Object> keyValues) {

        this.keyValues = keyValues;
    }

    public Databag(Object[] keys, Object[] values) {
        this.keyValues = new HashMap<String, Object>();

        int index = 0;
        for( Object key : keys) {
            if ( index < values.length) {
                this.keyValues.put(key.toString(), values[index]);
            }
            ++index;
        }
    }

    public Databag() {

        this.keyValues = new HashMap<String, Object>();
    }

    @Override
    public boolean equals(Object another) {
        if ( !another.getClass().equals(this.getClass())) {
            return false;
        }

        Databag casted = (Databag)another;

        return this.keyValues.equals( casted.keyValues );
    }

    @Override
    public int hashCode() {

        return this.keyValues.hashCode();
    }

    public Object get(Object key) {
        KeyPath path = new KeyPath(key.toString(), this.keyValues);

        return path.deepestValue();
    }

    public Object getOrDefault(String path, Object defaultValue) {
        Object result = this.get(path);

        if ( result == null) {
            return defaultValue;
        }

        return result;
    }

    public <T> T getTyped(String path) {

        return (T)this.get(path);
    }

    public <T> T getTypedOrDefault(String path, T defaultValue) {
        T result = (T)this.get(path);

        if ( result == null) {
            return defaultValue;
        }

        return result;
    }

    public Databag pick(Object ... keys) {

        return this.copyKeys(keys);
    }

    private Databag copyKeys(Object ... keys) {
        Databag result = new Databag();

        for( Object k : keys) {
            result.keyValues.put(
                new KeyPath(k.toString(), this.keyValues).deepestKey(),
                this.get(k)
            );
        }

        return result;
    }

    public Databag renameKey(String name, String newName) {
        Databag result = new Databag( (Map)((HashMap)this.keyValues).clone() );

        result.putStatefull(newName, result.get(name));
        result.removeStatefull(name);

        return result;
    }

    public Databag transformValue(String path, Function transformation) {
        Databag result = this.cloneDatabag();

        result.putStatefull(path, transformation.apply(result.get(path)));

        return result;
    }

    private Databag cloneDatabag() {
        return new Databag( (Map)((HashMap)this.keyValues).clone() );
    }

    public Databag put(String keyName, Object value) {
        Databag result = this.cloneDatabag();
        return result.putStatefull(keyName, value);
    }

    public Databag remove(String keyName) {
        Databag result = this.cloneDatabag();

        return result.removeStatefull(keyName);
    }



    @Override
    public String toString() {

        return this.getClass().getName() + " - " + this.keyValues.toString();
    }

    private Databag removeStatefull(String keyName) {
        KeyPath path = new KeyPath(keyName, this.keyValues);

        path.removeKeyFrom();

        return this;
    }

    private Databag putStatefull(String keyName, Object value) {
        KeyPath path = new KeyPath(keyName, this.keyValues);

        path.asurePathFrom();
        path.alterWith(value);

        return this;
    }

    public <T> Databag doOn(String keyName, Consumer doSomething) {
        KeyPath path = new KeyPath(keyName, this.keyValues);

        List<T> listToIterate;

        try {
            listToIterate = (List<T>)path.deepestValue();

            if ( listToIterate == null) {
                listToIterate = new ArrayList<>();
            }
        } catch (ClassCastException e) {
            listToIterate = new ArrayList<>();
        }

        listToIterate.stream().forEach(doSomething);

        return this;

    }
}
