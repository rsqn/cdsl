package tech.rsqn.cdsl.model;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

@Test
public class CdslAuditEventTest {

    @Test
    public void shouldCreateInstanceWithDefaultConstructor() {
        CdslAuditEvent event = new CdslAuditEvent();
        Assert.assertNotNull(event);
        Assert.assertNotNull(event.getTs());
        Assert.assertNull(event.getSource());
        Assert.assertNull(event.getCategory());
        Assert.assertNull(event.getAttr());
        Assert.assertNull(event.getFrom());
        Assert.assertNull(event.getTo());
    }

    @Test
    public void shouldSetAndGetValues() {
        CdslAuditEvent event = new CdslAuditEvent();
        Date testDate = new Date();
        event.setTs(testDate);
        event.setSource("testSource");
        event.setCategory("testCategory");
        event.setAttr("testAttr");
        event.setFrom("testFrom");
        event.setTo("testTo");

        Assert.assertEquals(event.getTs(), testDate);
        Assert.assertEquals(event.getSource(), "testSource");
        Assert.assertEquals(event.getCategory(), "testCategory");
        Assert.assertEquals(event.getAttr(), "testAttr");
        Assert.assertEquals(event.getFrom(), "testFrom");
        Assert.assertEquals(event.getTo(), "testTo");
    }

    @Test
    public void shouldUseWithMethod() {
        CdslAuditEvent event = new CdslAuditEvent().with("testSource", "testCategory");
        Assert.assertNotNull(event);
        Assert.assertNotNull(event.getTs());
        Assert.assertEquals(event.getSource(), "testSource");
        Assert.assertEquals(event.getCategory(), "testCategory");
        Assert.assertNull(event.getAttr());
        Assert.assertNull(event.getFrom());
        Assert.assertNull(event.getTo());
    }

    @Test
    public void shouldHaveProperToString() {
        CdslAuditEvent event = new CdslAuditEvent().with("testSource", "testCategory");
        String toString = event.toString();
        Assert.assertNotNull(toString);
        Assert.assertTrue(toString.contains("testSource"));
        Assert.assertTrue(toString.contains("testCategory"));
    }
}