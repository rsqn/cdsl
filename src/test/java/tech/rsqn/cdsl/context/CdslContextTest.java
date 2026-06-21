package tech.rsqn.cdsl.context;

import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;

public class CdslContextTest {

    @Test
    public void shouldPutAndFetchTransientVariables() {
        CdslContext context = new CdslContext();

        // 1. Test String value
        context.putTransient("stringKey", "stringValue");
        Assert.assertEquals(context.fetchTransient("stringKey"), "stringValue");

        // 2. Test Map Object (representing a complex object)
        Map<String, Object> myMap = new HashMap<>();
        myMap.put("foo", "bar");
        myMap.put("num", 42);
        context.putTransient("objectKey", myMap);

        Map<String, Object> retrievedMap = context.fetchTransient("objectKey");
        Assert.assertNotNull(retrievedMap);
        Assert.assertEquals(retrievedMap.get("foo"), "bar");
        Assert.assertEquals(retrievedMap.get("num"), 42);

        // 3. Test Integer Object
        context.putTransient("intKey", Integer.valueOf(100));
        Assert.assertEquals(context.fetchTransient("intKey"), Integer.valueOf(100));

        // 4. Test null value
        context.putTransient("nullKey", null);
        Assert.assertNull(context.fetchTransient("nullKey"));
    }
}
