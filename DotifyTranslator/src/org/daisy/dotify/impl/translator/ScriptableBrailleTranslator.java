package org.daisy.dotify.impl.translator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.translator.BrailleTranslator;
import org.daisy.dotify.translator.BrailleTranslatorResult;
import org.daisy.dotify.translator.attributes.TextAttribute;

public class ScriptableBrailleTranslator implements BrailleTranslator {
	private final Invocable inv;
	private final JavascriptBrailleTranslator inner;

	public ScriptableBrailleTranslator(String path)throws IllegalArgumentException, ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        
        // evaluate script

		InputStream is = null;
		try {
			is = StreamFetcher.getInputStream(path);
			engine.eval(new InputStreamReader(is, "UTF-8"));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("File does not exist: " + path, e);
		} catch (UnsupportedEncodingException e) {
			// should not happen
			throw new RuntimeException("Failed to load UTF-8");
		} finally {        			
			if (is!=null) {
				try { is.close(); } catch (IOException e) { }
			}
		}
        
        // get script object on which we want to implement the interface with
        Object obj = engine.get("translator");

        inv = (Invocable) engine;

        // get Runnable interface object from engine. This interface methods
        // are implemented by script methods of object 'obj'
        inner = inv.getInterface(obj, JavascriptBrailleTranslator.class);
	}

	@Override
	public BrailleTranslatorResult translate(String text, FilterLocale locale)
			throws UnsupportedLocaleException {
        // get script object on which we want to implement the interface with
        Object obj = inner.translate(text, locale);
        // get Runnable interface object from engine. This interface methods
        // are implemented by script methods of object 'obj'
        return inv.getInterface(obj, BrailleTranslatorResult.class);	
	}

	@Override
	public BrailleTranslatorResult translate(String text) {
        // get script object on which we want to implement the interface with
        Object obj = inner.translate(text);
        // get Runnable interface object from engine. This interface methods
        // are implemented by script methods of object 'obj'
        return inv.getInterface(obj, BrailleTranslatorResult.class);	
    }

	@Override
	public void setHyphenating(boolean value) {
		inner.setHyphenating(value);
	}

	@Override
	public boolean isHyphenating() {
		return inner.isHyphenating();
	}

	@Override
	public BrailleTranslatorResult translate(String text, FilterLocale locale,
			TextAttribute attributes) throws UnsupportedLocaleException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BrailleTranslatorResult translate(String text,
			TextAttribute attributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTranslatorMode() {
		// TODO Auto-generated method stub
		return null;
	}

}
