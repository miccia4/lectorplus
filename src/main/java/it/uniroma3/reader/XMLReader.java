package it.uniroma3.reader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.bzip2.CBZip2InputStream;
/**
 * 
 * @author matteo
 *
 */
public class XMLReader {
    private BufferedReader br;
    private int chunk;

    /**
     * Creates an input stream for reading Wikipedia articles from a bz2-compressed dump file.
     * @param file
     */
    public XMLReader(String file, int chunk) {
	FileInputStream fis;
	this.chunk = chunk;
	
	try {
	    fis = new FileInputStream(file);
	    byte[] ignoreBytes = new byte[2];
	    fis.read(ignoreBytes); // "B", "Z" bytes from commandline tools
	    br = new BufferedReader(new InputStreamReader(new CBZip2InputStream(fis), "UTF8"));
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Returns the next article in the dump.
     * @return
     */
    public List<String> nextArticles(){
	List<String> s = new ArrayList<String>(chunk);
	StringBuffer sb;
	
	while(chunk > 0){
	    sb = new StringBuffer();
	    String line;
	    try {
		// read untill the next article
		while ((line = br.readLine()) != null) {
		    if (line.endsWith("<page>"))
			break;
		}
		// no articles found in the dump
		if (line == null) {
		    br.close();
		    return null;
		}else{ // extract an article
		    sb.append(line + "\n");
		    while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
			if (line.endsWith("</page>"))
			    break;
		    }
		}

	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    
	    s.add(sb.toString());
	    chunk-=1;
	}

	return s;
    }
}
