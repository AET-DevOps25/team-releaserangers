package devops25.releaserangers.coursemgmt_service.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatchUtilsTest {
    static class TestObj {
        String a;
        String b;
        Integer c;
        // getters and setters
        public String getA() { return a; }
        public void setA(String a) { this.a = a; }
        public String getB() { return b; }
        public void setB(String b) { this.b = b; }
        public Integer getC() { return c; }
        public void setC(Integer c) { this.c = c; }
    }

    @Test
    void applyPatch_ShouldCopyNonNullFields() throws IllegalAccessException {
        TestObj source = new TestObj();
        source.setA("foo");
        source.setB(null);
        source.setC(42);
        TestObj target = new TestObj();
        target.setA(null);
        target.setB("bar");
        target.setC(1);
        PatchUtils.applyPatch(source, target);
        assertEquals("foo", target.getA());
        assertEquals("bar", target.getB());
        assertEquals(42, target.getC());
    }
}

