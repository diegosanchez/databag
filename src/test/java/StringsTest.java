import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringsTest {
    @Test
    public void testSplit() {
        String[] steps = "step1.step2.step3".split("\\.");

        assertEquals("step1", steps[0]);
        assertEquals("step2", steps[1]);
        assertEquals("step3", steps[2]);
    }

    @Test
    public void testSplitSingleElement() {
        String[] steps = "step1".split("\\.");

        assertEquals("step1", steps[0]);
    }
}
