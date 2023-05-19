// $Id$

package org.fcrepo.server.security.servletfilters.db;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.sql.*;
import javax.sql.*;
import java.util.Vector;
import javax.servlet.FilterConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fcrepo.server.security.servletfilters.BaseCaching;
import org.fcrepo.server.security.servletfilters.CacheElement;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.naming.Context;
import javax.naming.InitialContext;

public class FilterDB extends BaseCaching {
	protected static Logger log = LoggerFactory.getLogger(FilterDB.class);

	private String RESOURCE = null;
	private String QUERY = null;
	private String[] ATTRIBUTES = null;
	private String[] BINDPARAMS = null;
	private Boolean isUnivie = false;
	private Map ORGPARENTS = new LinkedHashMap<String, String>();

	@Override
	public void init(FilterConfig config) {
		log.info("initializing start..");

		FilterDBConfigBean filterConfig = (FilterDBConfigBean)config;	

		String assFilters = filterConfig.getAssociatedFilters();
		String[] temp = assFilters.split(",");
        FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES = new Vector<String>(temp.length);
        for (String element : temp) {
        	log.debug("FilterDB:   ASSOCIATED FILTER: " + element);
					if (element.equals("DBFilterAffiliationUnivie")) {
						isUnivie = true;
					}
            FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES.add(element);
        }

		RESOURCE = filterConfig.getResource();
		QUERY = filterConfig.getQuery();
		String attributesStr = filterConfig.getAttributes();
		if (attributesStr.indexOf(",") < 0) {
			if ("".equals(attributesStr)) {
				ATTRIBUTES = null;
			} else {
				ATTRIBUTES = new String[1];					
				ATTRIBUTES[0] = attributesStr;
			}
		} else {
			ATTRIBUTES = attributesStr.split(",");  							
		}
		String bindparamsStr = filterConfig.getBindparams();
		if (bindparamsStr.indexOf(",") < 0) {
			if ("".equals(bindparamsStr)) {
				BINDPARAMS = null;
			} else {
				BINDPARAMS = new String[1];					
				BINDPARAMS[0] = bindparamsStr;
			}
		} else {
			BINDPARAMS = bindparamsStr.split(",");  							
		}
		
			
		log.info("FilterDB: initialized.");
		log.debug("  RESOURCE: " + RESOURCE);
		log.debug("  QUERY: " + QUERY);
		for(int i=0,j=1;i<BINDPARAMS.length;i++){
			log.debug("  BINDPARAM: " + BINDPARAMS[i]);
		}
		for(int i=0,j=1;i<ATTRIBUTES.length;i++){
			log.debug("  ATTRIBUTE: " + ATTRIBUTES[i]);
		}
		log.debug("is univie: " + isUnivie);

		if (isUnivie) {
			log.info("FilterDB: reading org json...");
			JSONParser parser = new JSONParser();
			try {     
					JSONArray a = (JSONArray) parser.parse(new FileReader("/usr/local/phaidra/phaidra-api/lib/phaidra_directory/Phaidra/Directory/univie.json"));

					for (Object o : a)
					{
							JSONObject u = (JSONObject) o;
							log.info("reading parent org mapping: " + u.get("oracle_id").toString() + " <- " + u.get("parent_oracle_id").toString());
							String id = u.get("oracle_id").toString();
							String parent = u.get("parent_oracle_id").toString();

							ORGPARENTS.put(id, parent);
					}
			} catch (FileNotFoundException e) {
					e.printStackTrace();
			} catch (IOException e) {
					e.printStackTrace();
			} catch (ParseException e) {
					e.printStackTrace();
			}
	}

		super.init(filterConfig);

		inited = false;
		if (! initErrors) {

		}
		else {
			log.error("FilterDB not initialized; see previous error");
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

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet res = null;
		try
		{
			// get DB connection from pool
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup(RESOURCE);
			conn = ds.getConnection();
			
			// Prepare statement
			pstmt = conn.prepareStatement(QUERY);
			String userId=cacheElement.getUserid();
			for(int i=0,j=1;i<BINDPARAMS.length;i++)
			{
				if(BINDPARAMS[i].compareTo("username")==0)
				{
					pstmt.setString(j, userId);
					j++;
				}
			}
			
			// Execute statement
			res = pstmt.executeQuery();
			
			// fetch results
			while(res.next())
			{
				for(int i=0; i < ATTRIBUTES.length; i++)
				{
					String key = ATTRIBUTES[i];
					log.debug("FilterDB: inspecting key: " + key);
					String value = res.getString(i+1);
					log.debug("FilterDB: value: " + value);
					if (isUnivie) {
						if (value != null) {
							if (key.equals("fakcode") && value.equals("A0")) {
								log.debug("FilterDB: reading inum");
								if(map.containsKey("inum"))
								{
									log.debug("FilterDB: map contains inum");
									Set inums = (Set)map.get("inum");
									log.debug("FilterDB: inums: " + inums.toString());
									String inum = (String) inums.toArray()[0];
									log.debug("FilterDB: inum: " + inum.toString());

									String parent = (String) ORGPARENTS.get(inum);
									log.debug("FilterDB: rewriting |" + value + "| to |" + parent + "|");
									value = parent;
								}
							}
						}
					}

					Set values;
					if(map.containsKey(key))
					{
						values = (Set)map.get(key);
					}
					else
					{
						values = new HashSet();
						map.put(key, values);
					}
					
					if(!values.contains(value))
					{
						values.add(value);
						log.debug("FilterDB: added value |" + value + "| for key |" + key + "|");
					}
				}
			}

			cacheElement.populate(authenticated, null, map, null);
		}
		catch(Exception e)
		{
			log.error("FilterDB: error while querying database: " + e.toString());
			e.printStackTrace();
		}
		
		try
		{
			if(res!=null)
				res.close();
			if(pstmt!=null)
				pstmt.close();
			if(conn!=null)
				conn.close();
		}
		catch(Exception e)
		{
			// don't care
		}
	}
}
