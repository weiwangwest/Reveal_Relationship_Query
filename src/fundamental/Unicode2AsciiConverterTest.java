package fundamental;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class Unicode2AsciiConverterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testConverter() {
        String[] strings = {
                "This is a small string",
                "foobar",
                "the end",
                "not-a-g00d-Exampl333",
                "Smaz is a simple compression library",
                "1000 numbers 2000 will 10 20 30 compress very little",
                "and now a few italian sentences:",
                "Nel mezzo del cammin di nostra vita, mi ritrovai in una selva oscura",
                "Mi illumino di immenso",
                "try it against urls",
                "http://google.com",
                "http://programming.reddit.com",
                "http://github.com/antirez/smaz/tree/master"
        };
        for (String inStr: strings){
        	assertEquals(Unicode2AsciiConverter.convert(inStr), inStr);
        }
    	assertThat(Unicode2AsciiConverter.convert("äöüßÄÖÜẞ"), equalTo("äöüßÄÖÜẞ"));
    	assertThat(Unicode2AsciiConverter.convert("äöüßÄÖÜẞ"), not(equalTo("äöüßÄÖÜẞ")));
    	//assertThat(new Mapper().getValue("äöüßÄÖÜẞ"), equalTo(0));
    	assertThat(Unicode2AsciiConverter.convert("ФфДдЯяЗзЙйЛл"), equalTo("ФфДдЯяЗзЙйЛл"));
    	assertThat(Unicode2AsciiConverter.convert("ФфДдЯяЗзЙйЛл"), not(equalTo("ФфДдЯяЗзЙйЛл")));
    	//assertThat(new Mapper().getValue("ФфДдЯяЗзЙйЛл"), equalTo(0));
	}
	@Test
	//@Test(expected=Exception.class)
	public void testException1() {
    	assertNotNull(new DBMapper("vertex").getValue("äöüßÄÖÜẞ"));
	}
	@Test
	//@Test(expected=Exception.class)
	public void testException2() {
    	assertNotNull(new DBMapper("vertex").getValue("ФфДдЯяЗзЙйЛл"));
	}
}
