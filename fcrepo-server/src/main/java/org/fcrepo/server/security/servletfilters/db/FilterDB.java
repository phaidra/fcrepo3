// $Id$

package org.fcrepo.server.security.servletfilters.db;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Hashtable;
import java.sql.*;
import javax.sql.*;
import java.util.Vector;
import javax.servlet.FilterConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fcrepo.server.security.servletfilters.BaseCaching;
import org.fcrepo.server.security.servletfilters.CacheElement;

import javax.naming.Context;
import javax.naming.InitialContext;

public class FilterDB extends BaseCaching {
	protected static Logger log = LoggerFactory.getLogger(FilterDB.class);

	private String RESOURCE = null;
	private String QUERY = null;
	private String[] ATTRIBUTES = null;
	private String[] BINDPARAMS = null;

	@Override
	public void init(FilterConfig config) {
		log.info("initializing start..");

		FilterDBConfigBean filterConfig = (FilterDBConfigBean)config;	

		String assFilters = filterConfig.getAssociatedFilters();
		String[] temp = assFilters.split(",");
        FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES = new Vector<String>(temp.length);
        for (String element : temp) {
        	log.debug("FilterDB:   ASSOCIATED FILTER: " + element);
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
					String value = res.getString(i+1);
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
