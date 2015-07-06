package spi;

import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.daisy.dotify.consumer.hyphenator.HyphenatorFactoryMaker;

import base.HyphenatorFactoryMakerTestbase;

public class HyphenatorFactoryMakerTest extends HyphenatorFactoryMakerTestbase {
	
	@Override
	public HyphenatorFactoryMakerService getHyphenatorFMS() {
		return HyphenatorFactoryMaker.newInstance();
	}

}
