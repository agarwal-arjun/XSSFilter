package filters.wrapper;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author arjunagarwal
 * @since 15 JUNE 2016
 */

public class XSSRequestWrapper extends HttpServletRequestWrapper {

	private Logger logger = LoggerFactory.getLogger(XSSRequestWrapper.class);
	
	public XSSRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	private static Pattern[] patterns = new Pattern[]{
			// Script, Style fragments
	        Pattern.compile("(<style>|<script>)((.|[\\r\\n])*?)(</style>|</script>)", Pattern.CASE_INSENSITIVE),
	        // src='...', href='...', style='...'
	        Pattern.compile("(style|href|src)((\\s|[\\r\\n])*?)=(\\s|[\\r\\n])*(\\'|\\\")((.|[\\r\\n])*?)(\\'|\\\")", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	        // DOM
	        Pattern.compile("(document|attr|element|nodemap|nodelist|text|entity|namemap).(.*?)[\\s|=|)]", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	        // lonely script and style tags
	        Pattern.compile("</script>|</style>", Pattern.CASE_INSENSITIVE),
	        Pattern.compile("<script(.*?)|<style(.*?)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	        // eval(...), expression(...)
	        Pattern.compile("(eval|expression)\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	        // javascript:... , vbscript:...
	        Pattern.compile("(javascript|vbscript):", Pattern.CASE_INSENSITIVE),
	        // on...(...)=...
	        Pattern.compile("on((.|[\\r\\n])*?)=((.|[\\r\\n])*?)[\\'|\\\"]((.|[\\r\\n])*?)[\\'|\\\"]", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	        // body, img, iframe, input, link, table, td, div, a tag
	        Pattern.compile("<(meta|html|body|iframe|img|input|link|object|a|div|td|th|span)((.|[\\r\\n])*)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	        // html tags
	        Pattern.compile("<((.|[\\r\\n])*?)>((.|[\\r\\n])*?)</((.|[\\r\\n])*)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	        // CDATA
	        Pattern.compile("<!\\[CDATA((.|[\\r\\n])*?)\\]>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	        // everything between < & > if escaped from above patterns
	        Pattern.compile("<((.|[\\r\\n])*)(>|\\\")", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	        //Pattern.compile("\\[((.|[\\r\\n])*)(\\]|\\\")", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	        //Pattern.compile("<|>|\\[|\\]|&|\"|'|%|\\(|\\)|\\+", Pattern.MULTILINE | Pattern.DOTALL)
	    };

	    @Override
	    public String[] getParameterValues(String parameter) {
	    	try {
		        String[] values = super.getParameterValues(parameter);
	
		        if (values == null) {
		            return null;
		        }
	
		        int count = values.length;
		        String[] encodedValues = new String[count];
		        for (int i = 0; i < count; i++) {
		            encodedValues[i] = stripXSS(values[i]);
		        }
		        return encodedValues;
	    	} catch (Exception e) {
	    		logger.debug(this.getClass().getName(), "getParameterValues", "Entering into TxnHistory");
	    	}
	        return null;
	    }

	    @Override
	    public String getParameter(String parameter) {
	        String value = super.getParameter(parameter);
	        return stripXSS(value);
	    }

	    @Override
	    public String getHeader(String name) {
	        String value = super.getHeader(name);
	        return stripXSS(value);
	    }

	    private String stripXSS(String value) {
	        if (value != null) {
	        	
	            // Avoid null characters
	            value = value.replaceAll("", "");

	            // Remove all sections that match a pattern
	            for (Pattern scriptPattern : patterns){
	                value = scriptPattern.matcher(value).replaceAll("");
	            }
	            //value = Encode.forHtml(value);
	        }
	        return value;
	    }
	
	  public boolean isMatch() {
		  Map<String, String[]> params = super.getParameterMap();
		  for(String key: params.keySet()) {
			  for(String value: params.get(key)) {
				  if(checkPattern(value)) return true;
			  }
		  }
		  return false;
	  }

	  private boolean checkPattern(String value) {
		  if (value != null) {
	            // Avoid null characters
	            value = value.replaceAll("", "");

	            // Remove all sections that match a pattern
	            Matcher matcher = null;
	            for (Pattern scriptPattern : patterns){
	            	matcher = scriptPattern.matcher(value);
	            	if(matcher.find()) {
	            		return true;
	            	}
	            }
	        }
	        return false;
	  }

	
}
