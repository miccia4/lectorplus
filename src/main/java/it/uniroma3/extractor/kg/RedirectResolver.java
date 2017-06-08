package it.uniroma3.extractor.kg;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import it.uniroma3.extractor.bean.Configuration;
import it.uniroma3.extractor.bean.Lector;
import it.uniroma3.extractor.bean.WikiLanguage;
import it.uniroma3.extractor.util.KeyValueIndex;

public class RedirectResolver {

    private KeyValueIndex indexRedirect;

    /**
     * 
     */
    public RedirectResolver(){
	
	if (!new File(Configuration.getRedirectIndex()).exists()){
	    System.out.print("Creating REDIRECT resolver ...");
	    long start_time = System.currentTimeMillis();
	    this.indexRedirect = new KeyValueIndex(Configuration.getRedirectFile(), Configuration.getRedirectIndex());
	    long end_time = System.currentTimeMillis();
	    System.out.println(" done in " + TimeUnit.MILLISECONDS.toSeconds(end_time - start_time)  + " sec.");
	}
	else // we already have the index
	    this.indexRedirect = new KeyValueIndex(Configuration.getRedirectIndex());

    }

    /**
     * 
     * @param possibleRedirect
     * @return
     */
    public String resolveRedirect(String possibleRedirect){
	possibleRedirect = StringUtils.capitalize(possibleRedirect);
	String targetPage = possibleRedirect;
	Optional<String> target = indexRedirect.retrieveKeys(possibleRedirect).stream().findFirst();
	if (target.isPresent())
	    targetPage = target.get();
	return targetPage;
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args){
	Configuration.init(args);
	Lector.init(new WikiLanguage(Configuration.getLanguageCode(), Configuration.getLanguageProperties()), 
		new HashSet<String>(Arrays.asList(new String[]{"FE"})));
	
	RedirectResolver t = new RedirectResolver();

	String entity = "Matteo_Carcassi";

	System.out.println("\nRedirect of: ");
	System.out.println(t.resolveRedirect(entity));

    }
}
