package not_handled_warning;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioWarnings;

public class NotHandledWarningTest
{
    @BeforeClass
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioWarnings();
    }

    @Test
    public void notHandledWhite()
    {
        final String warning = ":15:1: Enumeration value 'WHITE' is not handled in choice 'EnumParamChoice'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void notHandledRed()
    {
        final String warning = ":15:1: Enumeration value 'RED' is not handled in choice 'EnumParamChoice'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void checkNumberOfWarnings()
    {
        final int expectedNumberOfWarnings = 2;
        assertEquals(expectedNumberOfWarnings, zserioWarnings.getCount());
    }

    private static ZserioWarnings zserioWarnings;
}
