package org.daisy.dotify.obfl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.daisy.dotify.api.text.Integer2Text;
import org.daisy.dotify.api.text.Integer2TextConfigurationException;
import org.daisy.dotify.api.text.IntegerOutOfRange;
import org.daisy.dotify.consumer.text.Integer2TextFactoryMaker;

/**
 * <p>
 * Expression is a small expressions language interpreter. The language uses
 * prefix notation with arguments separated by whitespace. The entire expression
 * must be surrounded with parentheses.
 * </p>
 * <p>
 * The following operators are defined: +, -, *, /, %, =, &lt;, &lt;=, >, >=,
 * &amp;, |
 * </p>
 * <p>
 * All operators require at least two arguments. E.g. (+ 5 7 9) evaluates to 21.
 * </p>
 * <p>
 * Special keywords:
 * </p>
 * <ul>
 * <li>if: (if (boolean_expression) value_when_true value_when_false)</li>
 * <li>now: (now date_format) where date_format is as defined by
 * {@link SimpleDateFormat}</li>
 * <li>round: (round value)</li>
 * <li>set: (set key value) where key is the key that will be replaced by value
 * in any subsequent expressions (within the same evaluation).</li>
 * <li>int2text: (int2text number language-code) where number is an integer
 * number to be converted into text using the language specified by
 * language-code.</li>
 * <li>concat: (concat ...) all arguments are concatenated to a single string</li>
 * </ul>
 * <p>
 * Quotes must surround arguments containing whitespace.
 * </p>
 * 
 * @author Joel Håkansson
 */
public class Expression {
	private HashMap<String, Object> vars;
	private final static Integer2TextFactoryMaker integer2textFactoryMaker = Integer2TextFactoryMaker.newInstance();
	
	/**
	 * Evaluate is the method to use when evaluating an expression.
	 * @param expr the expression to evaluate
	 * @return returns the evaluation result
	 */
	public Object evaluate(String expr) {
		// init
		vars = new HashMap<String, Object>();
		// return value
		String[] exprs = getArgs(expr);
		for (int i=0; i<exprs.length-1; i++) {
			doEvaluate(exprs[i]);
		}
		return doEvaluate(exprs[exprs.length-1]);
	}
	
	/**
	 * Evaluates this expression by first replacing any occurrences of the supplied variable
	 * names (prefixed by $) with the corresponding values in the map. The variable names must only
	 * contain word characters.
	 * 
	 * @param expr
	 * @param variables
	 * @return returns the evaluation result
	 */
	public Object evaluate(String expr, Map<String, String> variables) {
		if (variables==null) {
			return evaluate(expr);
		}
		for (String varName : variables.keySet()) {
			expr = expr.replaceAll("\\$"+varName+"(?=\\W)", variables.get(varName));
		}
		return evaluate(expr);
	}
	
	public Object evaluate(String expr, String ... vars) {
		for (String var : vars) {
			String[] v = var.split("=", 2);
			expr = expr.replaceAll("\\$"+v[0]+"(?=\\W)", v[1]);
		}
		return evaluate(expr);
	}
	
	private Object doEval1(String expr) {
		if (expr.startsWith("\"") && expr.endsWith("\"")) {
			return expr.substring(1, expr.length()-1);
		}
		if (vars.containsKey(expr)) {
			return vars.get(expr);
		}
		try {
			return toNumber(expr);
		} catch (NumberFormatException e) {
			return expr;
		}
	}
	
	private Object doEval2(String[] args1) {
		String operator = args1[0].trim();
		Object[] args = new Object[args1.length-1];
		for (int i=0; i<args.length; i++) {
			args[i] = doEvaluate(args1[i+1]);
		}
		//System.arraycopy(args1, 1, args, 0, args1.length-1);
		if ("+".equals(operator)) {
			return add(args);
		} else if ("-".equals(operator)) {
			return subtract(args);
		} else if ("*".equals(operator)) {
			return multiply(args);
		} else if ("/".equals(operator)) {
			return divide(args);
		} else if ("%".equals(operator)) {
			return modulo(args);
		} else if ("=".equals(operator)) {
			return equals(args);
		} else if ("<".equals(operator)) {
			return smallerThan(args);
		}  else if ("<=".equals(operator)) {
			return smallerThanOrEqualTo(args);
		} else if (">".equals(operator)) {
			return greaterThan(args);
		} else if (">=".equals(operator)) {
			return greaterThanOrEqualTo(args);
		} else if ("&".equals(operator)) {
			return and(args);
		} else if ("|".equals(operator)) {
			return or(args);
		} else if ("if".equals(operator)) {
			return ifOp(args);
		} else if ("now".equals(operator)) {
			return now(args);
		} else if ("round".equals(operator)) {
			return round(args);
		} else if ("set".equals(operator)) {
			return set(args);
		} else if ("int2text".equals(operator)) {
			return int2text(args);
		} else if ("concat".equals(operator)) {
			return concat(args);
		}
		else {
			throw new IllegalArgumentException("Unknown operator: '" + operator + "'");
		}
	}

	private Object doEvaluate(String expr) {
		
		expr = expr.trim();
		expr = expr.replaceAll("\\s+", " ");
		int leftPar = expr.indexOf('(');
		int rightPar = expr.lastIndexOf(')');
		if (leftPar==-1 && rightPar==-1) {
			return doEval1(expr);
		} else if (leftPar>-1 && rightPar>-1) {
			return doEval2( getArgs(expr.substring(leftPar+1, rightPar)));
		} else {
			throw new IllegalArgumentException("Unmatched parenthesis");
		}
	}

	private static double toNumber(Object input) {
		return Double.parseDouble(input.toString());
	}
	
	private static double add(Object[] input) {
		double ret = toNumber(input[0]);
		for (int i=1; i<input.length; i++) { ret += toNumber(input[i]); }
		return ret;
	}
	
	private static double subtract(Object[] input) {
		double ret = toNumber(input[0]);
		for (int i=1; i<input.length; i++) { ret -= toNumber(input[i]); }
		return ret;
	}
	
	private static double multiply(Object[] input) {
		double ret = toNumber(input[0]);
		for (int i=1; i<input.length; i++) { ret *= toNumber(input[i]); }
		return ret;
	}
	
	private static double divide(Object[] input) {
		double ret = toNumber(input[0]);
		for (int i=1; i<input.length; i++) { ret /= toNumber(input[i]); }
		return ret;
	}
	
	private static double modulo(Object[] input) {
		double ret = toNumber(input[0]);
		for (int i=1; i<input.length; i++) { ret %= toNumber(input[i]); }
		return ret;
	}
	
	private static boolean equals(Object[] input) {
		try {
			for (int i=1; i<input.length; i++) { 
				if (((Double)(input[i-1])).doubleValue()!=((Double)(input[i])).doubleValue()) {
					return false;
				}
			}
			return true;
		} catch (ClassCastException e) {
			for (int i=1; i<input.length; i++) { 
				if (!(input[i-1]).equals((input[i]))) {
					return false;
				}
			}
			return true;
		}
	}
	
	private static boolean smallerThan(Object[] input) {
		for (int i=1; i<input.length; i++) { 
			if (!(toNumber(input[i-1])<toNumber((input[i])))) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean smallerThanOrEqualTo(Object[] input) {
		for (int i=1; i<input.length; i++) { 
			if (!(toNumber(input[i-1])<=toNumber((input[i])))) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean greaterThan(Object[] input) {
		for (int i=1; i<input.length; i++) { 
			if (!(toNumber(input[i-1])>toNumber((input[i])))) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean greaterThanOrEqualTo(Object[] input) {
		for (int i=1; i<input.length; i++) { 
			if (!(toNumber(input[i-1])>=toNumber((input[i])))) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean and(Object[] input) {
		for (int i=1; i<input.length; i++) { 
			if (!((Boolean)(input[i-1]) & (Boolean)((input[i])))) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean or(Object[] input) {
		for (int i=0; i<input.length; i++) { 
			if ((Boolean)(input[i])) {
				return true;
			}
		}
		return false;
	}
	
	private static Object ifOp(Object[] input) {
		if (input.length!=3) {
			throw new IllegalArgumentException("Wrong number of arguments: (if arg1 arg2 arg3)");
		}
		if ((Boolean)(input[0])) {
			return (input[1]);
		} else {
			return (input[2]);
		}
	}
	
	private static String now(Object[] input) {
		if (input.length>1) {
			throw new IllegalArgumentException("Wrong number of arguments: (now format)");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(input[0].toString());
		return sdf.format(new Date());
	}
	
	private static int round(Object[] input) {
		if (input.length>1) {
			throw new IllegalArgumentException("Wrong number of arguments: (round value)");
		}
		return (int)Math.round(toNumber(input[0]));
	}
	
	private Object set(Object[] input) {
		if (input.length>2) {
			throw new IllegalArgumentException("Wrong number of arguments: (set key value)");
		}
		vars.put("$"+input[0].toString(), input[1]);
		return input[1];
	}

	private static String int2text(Object[] input) {
		if (input.length > 2) {
			throw new IllegalArgumentException("Wrong number of arguments: (int2text integer language-code)");
		}
		Integer2Text t;
		try {
			t = integer2textFactoryMaker.newInteger2Text(input[1].toString());
		} catch (Integer2TextConfigurationException e) {
			throw new IllegalArgumentException("Unsupported locale: " + input[1], e);
		}
		try {
			if (input[0] instanceof Integer) {
				return t.intToText((Integer) input[0]);
			} else {
				double d = toNumber(input[0]);
				if (Math.round(d) == d) {
					return t.intToText((int) Math.round(d));
				} else {
					throw new IllegalArgumentException("First argument must be an integer: " + input[0]);
				}
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("First argument must be an integer: " + input[0], e);
		} catch (IntegerOutOfRange e) {
			throw new IllegalArgumentException("Integer out of range: " + input[0], e);
		}
	}

	private Object concat(Object[] input) {
		StringBuilder sb = new StringBuilder();
		for (Object o : input) {
			sb.append(o);
		}
		return sb.toString();
	}

	private static String[] getArgs(String expr) {
		expr = expr.trim();
		ArrayList<String> ret = new ArrayList<String>();
		int ci = 0;
		int level = 0;
		boolean str = false;
		for (int i=0; i<expr.length(); i++) {
			if (expr.charAt(i)=='(') {
				if (str) {
					throw new IllegalArgumentException("Unmatched quote");
				}
				level++;
			} else if (expr.charAt(i)==')') {
				if (str) {
					throw new IllegalArgumentException("Unmatched quote");
				}
				level--;
			} else if (expr.charAt(i)=='"') {
				str = !str;
			}
			else if (expr.charAt(i)==' ' && level==0 && !str) {
				ret.add(expr.substring(ci, i));
				ci=i+1;
			}
		}
		ret.add(expr.substring(ci, expr.length()));
		String[] r = new String[ret.size()];
		/*
		for (int i=0; i<ret.size(); i++) {
			String arg = ret.get(i);
			if (arg.startsWith("\"") && arg.endsWith("\"")) {
				arg = arg.substring(1, arg.length()-1);
				ret.set(i, arg);
			}
		}*/
		return ret.toArray(r);
	}

}