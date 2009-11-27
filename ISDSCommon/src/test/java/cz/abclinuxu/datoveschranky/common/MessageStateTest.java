package cz.abclinuxu.datoveschranky.common;

import cz.abclinuxu.datoveschranky.common.entities.MessageState;
import java.util.EnumSet;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 *
 * @author xrosecky
 */
public class MessageStateTest extends TestCase {
    
    public MessageStateTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void test() {
        int value1 = MessageState.toInt(EnumSet.of(MessageState.READ));
        Assert.assertEquals(128, value1);
        int value4 = MessageState.toInt(EnumSet.of(MessageState.DELIVERED_BY_FICTION,
                MessageState.DELIVERED_BY_LOGIN));
        Assert.assertEquals(96, value4);
    }

}
