package spi;

import org.daisy.dotify.api.translator.BrailleTranslatorFactoryMakerService;
import org.daisy.dotify.consumer.translator.BrailleTranslatorFactoryMaker;

import base.BrailleTranslatorFactoryMakerTestbase;

public class BrailleTranslatorFactoryMakerTest extends BrailleTranslatorFactoryMakerTestbase {
	
	@Override
	public BrailleTranslatorFactoryMakerService getBrailleTranslatorFMS() {
		return BrailleTranslatorFactoryMaker.newInstance();
	}
}