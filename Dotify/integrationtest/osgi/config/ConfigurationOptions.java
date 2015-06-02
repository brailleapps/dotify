package osgi.config;

import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import org.ops4j.pax.exam.Option;

public abstract class ConfigurationOptions {
	
	public static Option felixDS() {
		return mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.scr").version("1.6.2");
	}

	static Option dotifyApi() {
		return mavenBundle().groupId("org.daisy.dotify").artifactId("dotify.api").version("1.0.0");
	}
	
	static Option dotifyCommon() {
		return mavenBundle().groupId("org.daisy.dotify").artifactId("dotify.common").version("1.0.0");
	}
	
	public static Option dotifyText() {
		return composite(
				dotifyApi(),
				mavenBundle().groupId("org.daisy.dotify").artifactId("dotify.text.impl").version("1.0.0")
				);
	}
	
	static Option texhyphj() {
		return mavenBundle().groupId("com.googlecode.texhyphj").artifactId("texhyphj").version("1.2");
	}
	
	public static Option dotifyHyphenator() {
		return composite(
				dotifyApi(),
				dotifyCommon(),
				texhyphj(),
				mavenBundle().groupId("org.daisy.dotify").artifactId("dotify.hyphenator.impl").version("1.0.0")
				);
	}
	
	public static Option dotifyTranslator() {
		return composite(
				dotifyHyphenator(),
				mavenBundle().groupId("org.daisy.dotify").artifactId("dotify.translator.impl").version("1.0.0")
				);
	}

	static Option sonatypeStaging(String group, String artifact, String version) {
		String path = group.replaceAll("\\.", "/");
		return bundle("https://oss.sonatype.org/content/groups/staging/"+path+
				"/"+artifact+"/"+version+"/"+artifact+"-"+version+".jar");
	}
}
