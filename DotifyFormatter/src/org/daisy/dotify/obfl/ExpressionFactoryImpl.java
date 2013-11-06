package org.daisy.dotify.obfl;

import org.daisy.dotify.api.obfl.Expression;
import org.daisy.dotify.api.obfl.ExpressionFactory;
import org.daisy.dotify.api.text.Integer2TextFactoryMakerService;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component
public class ExpressionFactoryImpl implements ExpressionFactory {
	private Integer2TextFactoryMakerService itf;

	public Expression newExpression() {
		return new ExpressionImpl(itf);
	}

	@Reference
	public void setInteger2TextFactory(Integer2TextFactoryMakerService itf) {
		this.itf = itf;
	}

	public void unsetInteger2TextFactory(Integer2TextFactoryMakerService itf) {
		this.itf = null;
	}

}
