// $Id$
package org.fcrepo.server.security.servletfilters.restservice;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Hashtable;
import java.util.Vector;
import javax.servlet.FilterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.fcrepo.server.security.servletfilters.BaseCaching;
import org.fcrepo.server.security.servletfilters.CacheElement;
import java.net.*;
import java.io.*;

public class FilterRestService extends BaseCaching {
	protected static Logger log = LoggerFactory.getLogger(FilterRestService.class);

	public static final String URL_KEY = "url";   	

	private String URL = null;
	
	@Override
	public void init(FilterConfig config) {
		log.info("initializing start..");
		
		FilterRestServiceConfigBean filterConfig = (FilterRestServiceConfigBean)config;	

		String assFilters = filterConfig.getAssociatedFilters();
		String[] temp = assFilters.split(",");
        FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES = new Vector<String>(temp.length);
        for (String element : temp) {
        	log.debug("FilterRestService:   ASSOCIATED FILTER: " + element);
            FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES.add(element);
        }

		URL = filterConfig.getUrl();
						
		log.info("FilterRestService:   URL: " + URL);

		super.init(filterConfig);

		inited = false;
		if (! initErrors) {

		}
		else {
			log.error("FilterRestService not initialized; see previous error");
		}
		
		inited = true;		
	}

	public void destroy() {
		super.destroy();
	}

	protected void initThisSubclass(String key, String value) {

	}

	// implemented from BaseCaching
	public void populateCacheElement(CacheElement cacheElement, String password) { 
		
		Boolean authenticated = null;
		Map map = new Hashtable();

		try
		{

			String userId = cacheElement.getUserid();
			
			URL url = new URL(URL + "?username=" + userId + "&password=" + password);
			URLConnection conn = url.openConnection();			

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine = in.readLine();
			in.close();
			
			log.info("rest service reply: " + inputLine);

			if (inputLine != "NOK") {
				// UID;EMAIL;NACHNAME;VORNAME;TITELPRE;TITELPOST
				String[] parts = inputLine.split(";");

				String uid = parts[0];
				String email = parts[1];
				String lastname = parts[2];
				String firstname = parts[3];

				if(uid != null && !uid.isEmpty() && email != null && !email.isEmpty()){
					authenticated = Boolean.TRUE;

					Set uid_val = new HashSet();
					uid_val.add(uid);
					map.put("uid", uid_val);

					Set email_val = new HashSet();
					email_val.add(email);
					map.put("email", email_val);

					Set lastname_val = new HashSet();
					lastname_val.add(lastname);
					map.put("lastname", lastname_val);

					Set firstname_val = new HashSet();
					firstname_val.add(firstname);
					map.put("firstname", firstname_val);

					cacheElement.populate(authenticated, null, map, null);
				}
			} else {
				log.error("error while querying rest service: inputLine: " + inputLine);
			}
			
		}
		catch(Exception e)
		{
			log.error("error while querying rest service: " + e.toString());
			e.printStackTrace();
		}

	}
}
