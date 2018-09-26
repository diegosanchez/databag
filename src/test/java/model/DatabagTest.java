package model;

import harnesses.ResourceLoader;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by dsanchez on 12/2/17.
 */
public class DatabagTest {

    @Test
    public void testShouldBeEqualsIfTheyHaveSameKeysAndValues() {
        Databag a = new Databag(
                new HashMap<String, Object>(){{
                    put("key1", 1);
                    put("key2", "a");
                }}
        );
        Databag b = new Databag(
                new HashMap<String, Object>(){{
                    put("key1", 1);
                    put("key2", "a");
                }}
        );

        assertEquals(a,b);
    }

    @Test
    public void testShouldBeEqualsIfTheyHaveSameKeysAndValuesEvenCreatedFromArrays() {
        Databag a = new Databag(new String[]{"key1", "key2"}, new Object[]{1, "a"});
        Databag b = new Databag(
                new HashMap<String, Object>(){{
                    put("key1", 1);
                    put("key2", "a");
                }}
        );

        assertEquals(a,b);
    }

    @Test
    public void testShouldBeCreatedEvenWithAsymmetricCollections() {
        Databag a = new Databag(new String[]{"key1"}, new Object[]{1, "a"});
        Databag b = new Databag(
                new HashMap<String, Object>(){{
                    put("key1", 1);
                }}
        );

        assertEquals(a,b);
    }

    @Test
    public void testShouldBeDifferentIfTheyHaveDifferentKeys() {
        Databag a = new Databag(new String[]{"key2"}, new Object[]{1});
        Databag b = new Databag(
                new HashMap<String, Object>(){{
                    put("key1", 1);
                    put("key2", "a");
                }}
        );

        assertFalse(a.equals(b));
    }

    @Test
    public void testShouldBeDifferentIfTheyHaveDifferentValues() {
        Databag a = new Databag(new String[]{"key1"}, new Object[]{1});
        Databag b = new Databag(
                new HashMap<String, Object>(){{
                    put("key1", 2);
                }}
        );

        assertFalse(a.equals(b));
    }

    @Test
    public void testCreatedFromListAndFromMapShouldBeEquals() {
        Databag a = new Databag(new String[]{"key1", "key2"}, new Object[]{1, "a"});
        Databag b = new Databag(new String[]{"key1", "key2"}, new Object[]{1, "a"});

        assertEquals(a,b);
    }

    @Test
    public void testNotEqual() {
        Databag a = new Databag(
                new HashMap<String, Object>(){{
                    put("key1", 1);
                    put("key2", "*");
                }}
        );
        Databag b = new Databag(
                new HashMap<String, Object>(){{
                    put("key1", 1);
                    put("key2", ".");
                }}
        );

        assertFalse(a.equals(b));
    }

    @Test
    public void testShouldRetrieveDeeplyHeldValue_1() throws IOException {
        JSONObject complexJSON = new JSONObject( ResourceLoader.getFileAsString("json_with_nested_keys.json") );

        Databag bag = new Databag(complexJSON.toMap());

        Integer actual = bag.getTyped("nested_1.nested_2.key");
        assertEquals( new Integer(1), actual);
    }

    @Test
    public void testShouldRetrieveShallowValue() throws IOException {
        JSONObject complexJSON = new JSONObject( ResourceLoader.getFileAsString("json_with_nested_keys.json") );

        Databag bag = new Databag(complexJSON.toMap());

        Map expected = new HashMap<String,Object>(){{
            put("nested_2", new HashMap<String,Object>(){{
                put("key", 1);
            }});
        }};
        assertEquals( expected, bag.get("nested_1"));
    }

    @Test
    public void testShouldRetrieveSimplestValue() throws IOException {
        JSONObject complexJSON = new JSONObject( ResourceLoader.getFileAsString("json_with_nested_keys.json") );

        Databag bag = new Databag(complexJSON.toMap());

        assertEquals( "abcd", bag.get("simplest"));
    }

    @Test
    public void testShouldRetrieveNullIfPathIsEmpty() throws IOException {
        JSONObject complexJSON = new JSONObject( ResourceLoader.getFileAsString("json_with_nested_keys.json") );

        Databag bag = new Databag(complexJSON.toMap());

        assertNull(bag.get(""));
    }

    @Test
    public void testShouldRetrieveNullIfKeyDoesntExist() throws IOException {
        JSONObject complexJSON = new JSONObject( ResourceLoader.getFileAsString("json_with_nested_keys.json") );

        Databag bag = new Databag(complexJSON.toMap());

        assertNull(bag.get("unknown"));
    }

    @Test
    public void testShouldRetrieveDefaultValueIfValueOfKeyIsNull() throws IOException {
        JSONObject complexJSON = new JSONObject( ResourceLoader.getFileAsString("json_with_nested_keys.json") );

        Databag bag = new Databag(complexJSON.toMap());

        assertEquals(9, bag.getOrDefault("unknown", 9));
    }

    @Test
    public void testShouldRetrieveNewDatabagWithPickedKeys() throws IOException {
        JSONObject complexJSON = new JSONObject( ResourceLoader.getFileAsString("pick_from.json") );

        Databag bag = new Databag(complexJSON.toMap());

        Databag expected = new Databag(new String[]{"c", "nested_key"}, new Object[]{true, 2});
        assertEquals(expected, bag.pick("c", "nested.nested.nested_key"));
    }

    @Test
    public void testShouldRetrieveTypedValue() throws IOException {
        JSONObject complexJSON = new JSONObject( ResourceLoader.getFileAsString("arrays.json") );

        Databag bag = new Databag(complexJSON.toMap());

        List<Integer> expected = Arrays.asList( new Integer[]{1,2,3} );
        assertEquals(expected, (List<Integer>)bag.getTyped("array"));
    }

    @Test
    public void testShouldRetrieveDefaultTypedValueIfNull() throws IOException {
        JSONObject complexJSON = new JSONObject( ResourceLoader.getFileAsString("arrays.json") );

        Databag bag = new Databag(complexJSON.toMap());

        List<Integer> expected = Arrays.asList( new Integer[]{1} );
        assertEquals(expected,
                (List<Integer>)bag.getTypedOrDefault("null_array", Arrays.asList( new Integer[]{1} ))
        );
    }

    @Test
    public void testShouldRenameKeyAndReturnNewInstanceWithKeyRenamed() throws IOException {
        JSONObject complexJSON = new JSONObject( ResourceLoader.getFileAsString("key_renaming.json") );

        Databag bag = new Databag(complexJSON.toMap());

        Databag expected = new Databag(new HashMap<String,Object>(){{
            put("new_d", 9.2);
            put("nested", new HashMap<String,Object>(){{
                put("nested_key", 2);
            }});
        }});

        assertEquals(expected, bag.renameKey("d", "new_d"));
    }

    @Test
    public void testShouldRenameNestedKeyAndReturnNewInstanceWithKeyRenamed() throws IOException {
        JSONObject complexJSON = new JSONObject( ResourceLoader.getFileAsString("key_renaming.json") );

        Databag bag = new Databag(complexJSON.toMap());

        Databag expected = new Databag(new HashMap<String,Object>(){{
            put("d", 9.2);
            put("nested", new HashMap<String,Object>(){{
                put("new_nested_key", 2);
            }});
        }});

        assertEquals(expected, bag.renameKey("nested.nested_key", "nested.new_nested_key"));
    }

    @Test
    public void testShouldNotRenameNestedKeyWhetherItDoesntExist() throws IOException {
        JSONObject complexJSON = new JSONObject( ResourceLoader.getFileAsString("key_renaming.json") );

        Databag bag = new Databag(complexJSON.toMap());

        Databag expected = new Databag(new HashMap<String,Object>(){{
            put("d", 9.2);
            put("nested", new HashMap<String,Object>(){{
                put("nested_key", 2);
            }});
        }});

        assertEquals(expected, bag.renameKey("d.x.a", "d.x.h"));
    }

    @Test
    public void testShouldIterateAnArrayExecutingLambdaTwice() throws IOException {
        JSONObject complexJSON = new JSONObject( ResourceLoader.getFileAsString("arrays.json") );

        Databag bag = new Databag(complexJSON.toMap());

        Consumer doSomething = Mockito.mock(Consumer.class);

        bag.doOn( "array", doSomething);

        verify(doSomething, times(3));

    }

    @Test
    public void testShouldIterateAnArrayExecutingNoLambdaBecauseValueIsNull() throws IOException {
        JSONObject complexJSON = new JSONObject( ResourceLoader.getFileAsString("arrays.json") );

        Databag bag = new Databag(complexJSON.toMap());

        Consumer doSomething = Mockito.mock(Consumer.class);

        bag.doOn( "null_array", doSomething);

        verify(doSomething, never()).accept(null);

    }

    @Test
    public void testShouldIterateAnArrayExecutingNoLambdaBecauseArrayIsEmpty() throws IOException {
        JSONObject complexJSON = new JSONObject( ResourceLoader.getFileAsString("arrays.json") );

        Databag bag = new Databag(complexJSON.toMap());

        Consumer doSomething = Mockito.mock(Consumer.class);

        bag.doOn( "empty_array", doSomething);

        verify(doSomething, never()).accept(null);

    }

    @Test
    public void testShouldTransformValueForGivenKeyFrom9ToDouble() throws IOException {
        Databag bag = new Databag(
                new JSONObject( ResourceLoader.getFileAsString("transformation.json") ).toMap()
        );

        Databag expected = new Databag(new String[]{"value0"}, new Object[]{81.0});
        Databag actual = bag.transformValue("value0", v -> Math.pow((Integer)v, 2) );

        assertEquals(expected, actual);
    }

    @Test
    public void testShouldChangeValueForKey() throws IOException {
        Databag bag = new Databag(
                new JSONObject( ResourceLoader.getFileAsString("value_alteration.json") ).toMap()
        );

        Databag expected = new Databag(new HashMap(){{
            put("value0", new HashMap(){{
                put("value1", new HashMap(){{
                    put("value", 9);
                }});
            }});
        }});
        Databag actual = bag.put("value0.value1.value", 9);

        assertEquals(expected, actual);
    }

    @Test
    public void testShouldRemoveKey() throws IOException {
        Databag bag = new Databag(
                new JSONObject( ResourceLoader.getFileAsString("key_remotion.json") ).toMap()
        );

        Databag expected = new Databag(new HashMap(){{
            put("value1", 1);
        }});

        Databag actual = bag.remove("value0");
        assertEquals(expected, actual);
    }

    @Test
    public void testShouldRemoveNestedKey() throws IOException {
        Databag bag = new Databag(
                new JSONObject( ResourceLoader.getFileAsString("key_remotion_nested.json") ).toMap()
        );

        Databag expected = new Databag(new HashMap(){{
            put("value0", 0);
            put("value1", new HashMap(){{
                put("value", "abc");
            }});
        }});

        Databag actual = bag.remove("value1.removed");
        assertEquals(expected, actual);
    }

    @Test
    public void testShouldRemoveNoKey() throws IOException {
        Databag bag = new Databag(
                new JSONObject( ResourceLoader.getFileAsString("key_remotion_nested.json") ).toMap()
        );

        Databag expected = new Databag(new HashMap(){{
            put("value0", 0);
            put("value1", new HashMap(){{
                put("value", "abc");
                put("removed", 1);
            }});
        }});

        Databag actual = bag.remove("value1.removed.x.x.x" /* invalid key */);
        assertEquals(expected, actual);
    }



}
