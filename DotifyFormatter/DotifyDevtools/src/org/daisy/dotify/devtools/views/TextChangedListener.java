package org.daisy.dotify.devtools.views;

import java.util.EventListener;

public interface TextChangedListener extends EventListener {

	public void textChanged(TextChangedEvent event);
}
