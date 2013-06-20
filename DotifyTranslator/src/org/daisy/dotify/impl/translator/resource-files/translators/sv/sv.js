var translator = new Translator();

/*
String.prototype.handleCaps = function() {
	return this.replace(/a/g, 'b');
}*/
String.prototype.toBraille = function() {
	table = {
			'1':'\u2801',
			'2':'\u2803',
			'3':'\u2809',
			'4':'\u2819',
			'5':'\u2811',
			'6':'\u280B',
			'7':'\u281B',
			'8':'\u2813',
			'9':'\u280A',
			'0':'\u281A',
			'a':'\u2801',
			'à':'\u2837',
			'á':'\u2808\u2801',
			'â':'\u2808\u2801',
			'ã':'\u2808\u2801',
			'ā':'\u2808\u2801',
			'ă':'\u2808\u2801',
			'ą':'\u2808\u2801',
			'b':'\u2803',
			'c':'\u2809',
			'ç':'\u2808\u2809',
			'č':'\u2808\u2809',
			'\u0107':'\u2808\u2809',
			'd':'\u2819',
			'đ':'\u2808\u2819',
			'ð':'\u2808\u2819',
			'e':'\u2811',
			'è':'\u282E',
			'é':'\u283F',
			'ê':'\u2808\u2811',
			'ë':'\u2808\u2811',
			'ē':'\u2808\u2811',
			'ę':'\u2808\u2811',
			'ȩ':'\u2808\u2811',
			'ě':'\u2808\u2811',
			'ĕ':'\u2808\u2811',
			'ė':'\u2808\u2811',
			'f':'\u280B',
			'g':'\u281B',
			'ğ':'\u2808\u281B',
			'h':'\u2813',
			'i':'\u280A',
			'í':'\u2808\u280A',
			'ì':'\u2808\u280A',
			'î':'\u2808\u280A',
			'ï':'\u2808\u280A',
			'ı':'\u2808\u280A',
			'ī':'\u2808\u280A',
			'j':'\u281A',
			'k':'\u2805',
			'l':'\u2807',
			'ł':'\u2808\u2807',
			'm':'\u280D',
			'n':'\u281D',
			'ñ':'\u2808\u281D',
			'ń':'\u2808\u281D',
			'o':'\u2815',
			'ó':'\u2808\u2815',
			'ò':'\u2808\u2815',
			'ô':'\u2808\u2815',
			'õ':'\u2808\u2815',
			'ō':'\u2808\u2815',
			'ø':'\u2808\u2815',
			'ő':'\u2808\u2815',
			'p':'\u280F',
			'q':'\u281F',
			'r':'\u2817',
			'ŕ':'\u2808\u2817',
			'ř':'\u2808\u2817',
			's':'\u280E',
			'š':'\u2808\u280E',
			'ś':'\u2808\u280E',
			'ş':'\u2808\u280E',
			'ß':'\u2808\u280E',
			't':'\u281E',
			'ţ':'\u2808\u281E',
			'þ':'\u2808\u281E',
			'u':'\u2825',
			'ü':'\u2833',
			'ù':'\u2808\u2825',
			'ú':'\u2808\u2825',
			'û':'\u2808\u2825',
			'ū':'\u2808\u2825',
			'v':'\u2827',
			'w':'\u283A',
			'x':'\u282D',
			'y':'\u283D',
			'ý':'\u2808\u283D',
			'ÿ':'\u2808\u283D',
			'z':'\u2835',
			'ż':'\u2808\u2835',
			'ž':'\u2808\u2835',
			'ź':'\u2808\u2835',
			'å':'\u2821',
			'ä':'\u281C',
			'æ':'\u2808\u281C',
			'ö':'\u282A',
			'œ':'\u2808\u282A',
			',':'\u2802',
			'.':'\u2804',
			';':'\u2806',
			':':'\u2812',
			'?':'\u2822',
			'¿':'\u2822',
			'!':'\u2816',
			'¡':'\u2816',
			'(':'\u2826',
			')':'\u2834',
			'*':'\u2814',
			'"':'\u2830',
			'«':'\u2830',
			'»':'\u2830',
			'\u201c':'\u2830',
			'\u201d':'\u2830',
			'\u201e':'\u2830',
			"'":'\u2810',
			'\u2018':'\u2810',
			'\u2019':'\u2810',
			'\u201a':'\u2810',
			'\u2039':'\u2810',
			'\u203a':'\u2810',
			'/':'\u280C',
			'÷':'\u280C',
			'\u2215':'\u280C',
			'\u2010':'\u2824',
			'\u2212':'\u2824',
			'\u2013':'\u2824\u2824',
			'\u2014':'\u2824\u2824',
			'\u2015':'\u2824\u2824',
			'\u222A':'\u283b\u2825',
			'\u22C3':'\u283b\u2825',
			'+':'\u2832',
			'§':'\u282C',
			'|':'\u2838',
			'=':'\u2836',
			'•':'\u283F',
			'\u2219':'\u283b\u2804',
			'[':'\u2837',
			']':'\u283E',
			'{':'\u2820\u2837',
			'}':'\u2820\u283E',
			'〈':'\u2820\u2826',
			'〉':'\u2820\u2834',
			'¢':'\u2818\u2809',
			'£':'\u2818\u2807',
			'$':'\u2818\u280E',
			'€':'\u2818\u2811',
			'¥':'\u2818\u283D',
			'°':'\u283b\u281b',
			'&':'\u282f',
			'¸':'\u2818\u2802',
			'¨':'\u2818\u2806',
			'~':'\u2818\u2812',
			'`':'\u2818\u2822',
			'´':'\u2818\u2814',
			'\\':'\u2818\u280C',
			'_':'\u2818\u2824',
			'\u02C6':'\u2818\u2816',
			'^':'\u2818\u2816',
			'†':'\u2818\u2832',
			'ˇ':'\u2818\u2836',
			'#':'\u2818\u283C',
			'@':'\u2818\u2837',
			'\u2016':'\u2838\u2838',
			'\u2045':'\u2830\u2826',
			'\u2046':'\u2830\u2834',
			'%':'\u2839',
			'‰':'\u2839\u2839',
			'±':'\u2832\u2824',
			'<':'\u283c\u282a',
			'>':'\u283c\u2815',
			'…':'\u2804\u2804\u2804',
			'©':'\u2826\u2809\u2834',
			'™':'\u2826\u281E\u280D\u2834',
			'®':'\u2826\u2817\u2834',
			'½':'\u283c\u2801\u280C\u283c\u2803',
			'×':'\u283b\u282d',
			'¹':'\u282c\u283c\u2801',
			'²':'\u282c\u283c\u2803',
			'³':'\u282c\u283c\u2809',
			'\u2022':'\u283f',
			'º':'\u282c\u2815',
			'→':' \u2812\u2815 ',
			'↓':' \u2823\u2815 '
	}
	var ret = "";
	for (i=0;i<this.length;i++) {
		if (table[this.charAt(i)]) {
			ret += table[this.charAt(i)];
		} else {
			ret += this.charAt(i);
		}
	}
	return ret;
}

function Translator() {
	this.hyphenating = false;
}

Translator.prototype.translate = function(text, locale) { 
	return new TranslatorResult(text, locale);
}

Translator.prototype.translate = function(text) {
	return new TranslatorResult(text, null);
}

Translator.prototype.setHyphenating = function(value) { 
	this.hyphenating = value;
}

Translator.prototype.isHyphenating = function() { 
	return this.hyphenating;
}

function TranslatorResult(text, locale) {
	this.text = text;
	this.locale = locale;
}

/**
 * Gets the translated string preceding the row break, including a translated 
 * hyphen at the end, if needed. The length of the translated text must not exceed the
 * specified <tt>limit</tt>. If <tt>force</tt> is not used, the result could be empty and no 
 * characters removed from the buffer. A caller would typically set <tt>force</tt> to 
 * true when <tt>limit</tt> is equal to the maximum number of available characters 
 * on a row.
 * 
 * @param limit specifies the maximum number of characters allowed in the result
 * @param force specifies if the translator should force a break at the limit
 * 				 if no natural break point is found 
 * @return returns the translated string
 */
TranslatorResult.prototype.nextTranslatedRow = function(limit, force) {
	return this.text;
}

/**
 * Gets the translated remainder, in other words the characters not
 * yet extracted with <tt>nextTranslatedRow</tt>.
 * @return returns the translated remainder
 */
TranslatorResult.prototype.getTranslatedRemainder = function() {
	return this.text.
	// Remove zero width space
	replace(/\u200B/g, '').
	// One or more digit followed by zero or more digits, commas or periods
	replace(/([\d]+[\d,\.]*)/g, '\u283c$1').
	// Insert a "reset character" between a digit and lower case a-j
	replace(/([\d])([a-j])/g, '$1\u2831$2')
	// Handle caps
	//.handleCaps()
	// Change case to lower case
	.toLowerCase()
	// Text to braille, Pas 1
	
	// Text to braille, Pas 2
	.toBraille()
	// Remove redundant whitespace
	.replace(/(\s+)/g, ' ');
}

/**
 * Returns the number of characters remaining in the result. This
 * number equals the number of characters in <tt>getTranslatedRemainder</tt>.
 * @return returns the number of characters remaining
 */
TranslatorResult.prototype.countRemaining = function() {
	return this.getTranslatedRemainder().length;
}

/**
 * Returns true if there are characters remaining in the result, in other
 * words of there are characters not yet extracted with <tt>nextTranslatedRow</tt>.
 * @return returns true if there are characters remaining, false otherwise
 */
TranslatorResult.prototype.hasNext = function() {
	return this.countRemaining()>0;
}
