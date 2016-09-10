package hu.poketerkep.client.support;


import org.junit.Assert;
import org.junit.Test;

public class RiversTest {

    @Test
    public void test() throws Exception {
        Rivers rivers = new Rivers(1, 2, 5);

        Assert.assertFalse(rivers.increase()); //1
        Assert.assertFalse(rivers.increase()); //2
        Assert.assertFalse(rivers.increase()); //3
        Assert.assertFalse(rivers.increase()); //4
        Assert.assertTrue(rivers.increase()); //5

        rivers.decrease(); //3

        Assert.assertFalse(rivers.increase()); //4
        Assert.assertTrue(rivers.increase()); //5

        rivers.reset();

        Assert.assertFalse(rivers.increase()); //1
    }
}