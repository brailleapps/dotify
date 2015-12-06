package spi;

import org.daisy.dotify.api.translator.BrailleFilterFactoryMakerService;
import org.daisy.dotify.consumer.translator.BrailleFilterFactoryMaker;

import base.BrailleFilterFactoryMakerTestbase;

public class BrailleFilterFactoryMakerTest extends BrailleFilterFactoryMakerTestbase {
	
	@Override
	public BrailleFilterFactoryMakerService getBrailleFilterFMS() {
		return BrailleFilterFactoryMaker.newInstance();
	}
}