package it.uniroma3.triples;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.uniroma3.bean.WikiLanguage;
import it.uniroma3.configuration.Configuration;
import it.uniroma3.configuration.Lector;
import it.uniroma3.util.reader.TSVReader;
/**
 * 
 * @author matteo
 *
 */
public class PlaceholderFilter {

    private static Set<String> nationalities;
    
    private static Set<String> badPhrases = new HashSet<String>(Arrays.asList(new String[]{
	    "and", ", and", ",and" , "' and '", "' and", ", and a", ", and in", ", and then", 
	    ", and other", "and the", ",and the", ", and the", "n and", "s and", "'s and", "s, and"}));

    public PlaceholderFilter(){
	nationalities = TSVReader.getLines(Configuration.getNationalitiesList());
    }

    /**
     * 
     * @param phrase
     * @return
     */
    private String replaceNationalities(String phrase){
	for (String nat : nationalities){
	    nat = nat.replaceAll("_", " ");
	    Pattern NAT = Pattern.compile("\\b"+nat+"\\b", Pattern.CASE_INSENSITIVE);
	    phrase = NAT.matcher(phrase).replaceAll("#NAT#");
	}
	return phrase;
    }

    /**
     * 
     * @param phrase
     * @return
     */
    private String replacePosition(String phrase){
	Pattern POS1 = Pattern.compile("\\b("
		+ "south(ern)?(-)?west(ern)?|"
		+ "south(ern)?(-)?east(ern)?|"
		+ "north(ern)?(-)?east(ern)?|"
		+ "north(ern)?(-)?west(ern)?|"
		+ "south(ern)?(-)?central|"
		+ "north(ern)?(-)?central|"
		+ "west(ern)?(-)?central|"
		+ "south(ern)?(-)?central|"
		+ "central(-)?north(ern)?|"
		+ "central(-)?south(ern)?|"
		+ "central(-)?east(ern)?|"
		+ "central(-)?west(ern)?"
		+ ")\\b", Pattern.CASE_INSENSITIVE);
	Pattern POS2 = Pattern.compile("\\b("
		+ "northern|"
		+ "southern|"
		+ "western|"
		+ "eastern"
		+ ")\\b", Pattern.CASE_INSENSITIVE);
	Pattern POS3 = Pattern.compile("\\b("
		+ "north|"
		+ "south|"
		+ "west|"
		+ "east)\\b", Pattern.CASE_INSENSITIVE);
	phrase = POS1.matcher(phrase).replaceAll("#POS#");
	phrase = POS2.matcher(phrase).replaceAll("#POS#");
	phrase = POS3.matcher(phrase).replaceAll("#POS#");
	return phrase;
    }

    /**
     * \b(\d+(\s\d+)*)\s?(km|kilometer|mi|ft|yd|m)(s?)\b
     * @param phrase
     * @return
     */
    private String replaceLength(String phrase){
	Pattern LEN1 = Pattern.compile("\\d+(\\s\\d+)*\\s?(km|kilometer|mi|ft|yd|m)(s?)\\b");
	phrase = LEN1.matcher(phrase).replaceAll("#LEN#");
	return phrase;
    }

    /**
     * 
     * @param phrase
     * @return
     */
    private String replaceDate(String phrase){
	Pattern DATE3 = Pattern.compile("#YEAR#(\\s|,\\s)#DAY#");
	Pattern DATE4 = Pattern.compile("#DAY#(\\s|,\\s)#YEAR#");
	phrase = DATE3.matcher(phrase).replaceAll("#DATE#");
	phrase = DATE4.matcher(phrase).replaceAll("#DATE#");
	return phrase;
    }

    /**
     * 
     * @param phrase
     * @return
     */
    private String replaceDay(String phrase){
	Pattern DATE1 = Pattern.compile("([0-3]?[0-9]–)?[0-3]?[0-9]\\s#MONTH#");
	Pattern DATE2 = Pattern.compile("#MONTH#\\s([0-3]?[0-9]–)?[0-3]?[0-9]");
	phrase = DATE1.matcher(phrase).replaceAll("#DAY#");
	phrase = DATE2.matcher(phrase).replaceAll("#DAY#");
	return phrase;
    }

    /**
     * 
     * @param phrase
     * @return
     */
    private String replaceMonths(String phrase){
	Pattern MONTH = Pattern.compile("\\b(january|february|march|april|may|june|july|august|"
		+ "september|october|november|december)\\b", Pattern.CASE_INSENSITIVE);
	phrase = MONTH.matcher(phrase).replaceAll("#MONTH#");
	return phrase;
    }

    /**
     * 
     * @param phrase
     * @return
     */
    private String replaceYears(String phrase){
	Pattern YEAR = Pattern.compile("\\b((1|2)\\d\\d\\d)\\b");
	phrase = YEAR.matcher(phrase).replaceAll("#YEAR#");
	Pattern ERA1 = Pattern.compile("\\b#YEAR#s\\b");
	Pattern ERA2 = Pattern.compile("\\b#[0-9]0s\\b");
	phrase = ERA1.matcher(phrase).replaceAll("#ERA#");
	phrase = ERA2.matcher(phrase).replaceAll("#ERA#");
	return phrase;
    }

    /**
     * 
     * @param phrase
     * @return
     */
    private String replaceOrdinals(String phrase){
	Pattern ORD1 = Pattern.compile("\\b\\d*1st\\b");
	Pattern ORD2 = Pattern.compile("\\b\\d*2nd\\b");
	Pattern ORD3 = Pattern.compile("\\b\\d*3rd\\b");
	Pattern ORD4 = Pattern.compile("\\b(\\d)*\\dth\\b");
	Pattern ORD5 = Pattern.compile("\\b(first|second|third|fourth|fifth)\\b");
	phrase = ORD1.matcher(phrase).replaceAll("#ORD#");
	phrase = ORD2.matcher(phrase).replaceAll("#ORD#");
	phrase = ORD3.matcher(phrase).replaceAll("#ORD#");
	phrase = ORD4.matcher(phrase).replaceAll("#ORD#");
	phrase = ORD5.matcher(phrase).replaceAll("#ORD#");
	return phrase;
    }

    /**
     * Eliminate parethesis.
     * 
     * @param sentence
     * @return
     */
    public String preprocess(String sentence){
	sentence = Lector.getTextParser().removeParenthesis(sentence);
	sentence = sentence.toLowerCase();
	sentence = replaceOrdinals(replaceLength(replaceNationalities(replacePosition(replaceDate(replaceDay(replaceMonths(replaceYears(sentence))))))));
	Pattern pattern = Pattern.compile("([A-Za-z0-9,'´#\\.\\- ]+)");
	Matcher matcher = pattern.matcher(sentence);
	if(!matcher.matches())
	    sentence = "";
	else{
	    pattern = Pattern.compile("([,'´#\\.\\- ]+)");
	    matcher = pattern.matcher(sentence);
	    if(matcher.matches())
		sentence = "";
	}
	sentence = sentence.replaceAll("''", "");
	if (badPhrases.contains(sentence))
	    sentence = "";
	return sentence = sentence.trim();
    }

    
    public static void main(String[] args){
	Configuration.init(new String[0]);
	Lector.init(new WikiLanguage(Configuration.getLanguageCode(), Configuration.getLanguageProperties()));
	PlaceholderFilter p = new PlaceholderFilter();
	Set<String> s = TSVReader.getLines("/Users/matteo/Desktop/test.tsv");
	s.stream().map(t -> t.split("\t")[0]).map(t -> t + "\t" + p.preprocess(t)).forEach(System.out::println);
    }

}
