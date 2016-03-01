package org.daisy.dotify.obfl;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.Transformer;

final class RendererInfo {
	private final NamespaceContext namespaceContext;
	private final Transformer processor;
	private final String qualifier;
	private final String cost;
	RendererInfo(Transformer p, NamespaceContext nc, String qualifier, String cost) {
		this.processor = p;
		this.namespaceContext = nc;
		this.qualifier = qualifier;
		this.cost = cost;
	}

	Transformer getProcessor() {
		return processor;
	}

	String getQualifier() {
		return qualifier;
	}

	String getCost() {
		return cost;
	}

	NamespaceContext getNamespaceContext() {
		return namespaceContext;
	}
	
	
}
