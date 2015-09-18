package base;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.daisy.dotify.api.translator.BrailleFilter;
import org.daisy.dotify.api.translator.BrailleFilterFactoryMakerService;
import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.api.translator.Translatable;
import org.daisy.dotify.api.translator.TranslationException;
import org.daisy.dotify.api.translator.TranslatorConfigurationException;
import org.junit.Test;

public abstract class BrailleFilterFactoryMakerTestbase {
	
	public abstract BrailleFilterFactoryMakerService getBrailleFilterFMS();

	@Test
	public void testFilterFactory() {
		//Setup
		BrailleFilterFactoryMakerService filterFactory = getBrailleFilterFMS();
		//Test
		assertNotNull("Factory exists.", filterFactory);
		assertTrue(filterFactory.listSpecifications().size()>=1);
	}
	
	@Test
	public void testSwedishUncontractedFilter_01() throws TranslatorConfigurationException, TranslationException {
		//Setup
		BrailleFilterFactoryMakerService filterFactory = getBrailleFilterFMS();
		BrailleFilter bt = filterFactory.newFilter("sv-SE", BrailleTranslatorFactory.MODE_UNCONTRACTED);
		//Test
		assertNotNull(bt);
		assertEquals("⠼⠁⠃⠉", bt.filter(Translatable.text("123").build()));
	}
	
	@Test
	public void testSwedishUncontractedFilter_02() throws TranslatorConfigurationException, TranslationException {
		//Setup
		BrailleFilterFactoryMakerService filterFactory = getBrailleFilterFMS();
		BrailleFilter bt = filterFactory.newFilter("sv-SE", BrailleTranslatorFactory.MODE_UNCONTRACTED);
		//Test
		assertNotNull(bt);
		assertEquals("⠁⠧\u00ad⠎⠞⠁⠧\u00ad⠝⠊⠝⠛⠎\u00ad⠗⠑⠛\u00ad⠇⠑⠗", bt.filter(Translatable.text("avstavningsregler").hyphenate(true).build()));
	}

}
