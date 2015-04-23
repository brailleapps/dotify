package org.daisy.dotify.impl.translator.liblouis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.daisy.dotify.common.text.StringFilter;

public class LiblouisBrailleFilter implements StringFilter {
	private final Map<Integer, Substitution> mSubstitutionTable;

	private final List<RegexReplace> replacers;

	public static class Builder {
		private final Logger logger;
		private final Map<Integer, Substitution> mSubstitutionTable;
		//Braille Indicators
		private String numsign = "";
		private String capsign = "";
		//private String begcaps = "";
		//private String endcaps = "";
		//private String letsign = "";
		//private String noletsign = "";
		//private String noletsignbefore = "";
		//private String noletsignafter = "";

		public Builder() {
			this.mSubstitutionTable = new HashMap<Integer, Substitution>();
			this.logger = Logger.getLogger(this.getClass().getCanonicalName());
		}
		
		public Builder put(Integer key, String replacement, CharClass g) {
			//System.out.println((key>=32?(char)key.intValue():"#"+key) + " -> " + replacement +  " " + g);
			Substitution s = mSubstitutionTable.get(key);
			if (s==null) {
				s = new Substitution(replacement);
				mSubstitutionTable.put(key, s);
			}
			s.addGroup(g);
			return this;
		}
		
		public Builder numsign(String value) {
			if (!numsign.equals("")) {
				logger.warning("Numsign already set: " + numsign + " -> " + value);
			}
			numsign = value;
			return this;
		}
		
		public Builder capsign(String value) {
			if (!capsign.equals("")) {
				logger.warning("Capsign already set: " + capsign + " -> " + value);
			}
			capsign = value;
			return this;
		}
		
		public LiblouisBrailleFilter build() {
			return new LiblouisBrailleFilter(this);
		}
	}

	private LiblouisBrailleFilter(Builder b) {
		this.mSubstitutionTable = new HashMap<Integer, Substitution>();
		mSubstitutionTable.putAll(b.mSubstitutionTable);
		this.replacers = new ArrayList<RegexReplace>();
		//Add more regular expressions here, based on character classes
		char uc = CharClass.UPPERCASE.token();
		replacers.add(new RegexReplace("(?<!"+uc+")("+uc+"{1})(?!"+uc+")", b.capsign+"$1"));
		char d = CharClass.DIGIT.token();
		replacers.add(new RegexReplace("(?<!"+d+")("+d+"+)(?!"+d+")", b.numsign+"$1"));
	}

	public String filter(String input) {
		//Translate characters and determine character class for each character
		int codePoint;
		StringBuilder sbr = new StringBuilder(input.length());
		StringBuilder sbc = new StringBuilder(input.length());
		
		for (int offset = 0; offset < input.length();) {
			codePoint = input.codePointAt(offset);
			Substitution rd = mSubstitutionTable.get(Integer.valueOf(codePoint));
			if (codePoint>=0x2800&&codePoint<=0x28FF) {
				sbr.appendCodePoint(codePoint);
				sbc.append(CharClass.BRAILLE);
			} else if (null != rd && rd.getReplacement().length() > 0) {
				// a replacement occurred
				sbr.append(rd.getReplacement());
				CharClass cg;
				if (rd.getGroups().size()>1) {
					//TODO: select
					cg = rd.getGroups().iterator().next();
				} else {
					cg = rd.getGroups().iterator().next();
				}
				sbc.append(cg.token());
			} else {
				// no replacement found
				sbr.appendCodePoint(codePoint);
				sbc.append(CharClass.UNDEFINED);
			}
			offset += Character.charCount(codePoint);
		}
		String contentStr = sbr.toString();
		
		//Insert markers in charClassStr
		String charClassStr = insertMarkers(sbc.toString());
		
		//Merge markers from charClassStr and translation from contentStr
		return merge(charClassStr, contentStr);
	}
	
	private String insertMarkers(String input) {
		String ret = input;
		System.out.println(ret);
		for (RegexReplace rr : replacers) {
			ret = rr.replaceAll(ret);			
		}
		System.out.println(ret);
		return ret;
	}
	
	private String merge(String charClassStr, String contentStr) {
		StringBuilder sb = new StringBuilder();
		int j = 0;
		for (int i=0; i<charClassStr.length(); i++) {
			char c = charClassStr.charAt(i);
			if (c>=0x2800&&c<=0x28FF) {
				sb.append(c);
			} else {
				sb.append(contentStr.charAt(j));
				j++;
			}
		}
		return sb.toString();
	}
	
	private static class RegexReplace {
		private final Pattern p;
		private final String replacement;

		public RegexReplace(String regex, String replacement) {
			this.p = Pattern.compile(regex);
			this.replacement = replacement;
		}

		public String replaceAll(String input) {
			return p.matcher(input).replaceAll(replacement);
		}
	}

}
