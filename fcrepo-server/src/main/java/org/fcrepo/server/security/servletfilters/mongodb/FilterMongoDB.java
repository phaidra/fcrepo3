// $Id$
package org.fcrepo.server.security.servletfilters.mongodb;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Hashtable;
import java.util.Arrays;
import java.util.Vector;
import javax.servlet.FilterConfig;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fcrepo.server.security.servletfilters.BaseCaching;
import org.fcrepo.server.security.servletfilters.CacheElement;

import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.MongoClientURI;
//import com.mongodb.MongoCredential;

public class FilterMongoDB extends BaseCaching {
	protected static Logger log = LoggerFactory.getLogger(FilterMongoDB.class);
	
    private String HOST = null;
    private String PORT = null;
    private String USERNAME = null;
    private String PASSWORD = null;
    private String DATABASE = null; 
		private String AUTHDATABASE = null; 
    private String COLLECTION = null;
	private MongoClient CLIENT = null;

	@Override
	public void init(FilterConfig config) {
		log.info("initializing start..");

		FilterMongoDBConfigBean filterConfig = (FilterMongoDBConfigBean)config;	

		String assFilters = filterConfig.getAssociatedFilters();
		String[] temp = assFilters.split(",");
        FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES = new Vector<String>(temp.length);
        for (String element : temp) {
        	log.debug("FilterMongoDB:   ASSOCIATED FILTER: " + element);
            FILTERS_CONTRIBUTING_AUTHENTICATED_ATTRIBUTES.add(element);
        }

		HOST = filterConfig.getHost();
		PORT = filterConfig.getPort();
		USERNAME = filterConfig.getUsername();
		PASSWORD = filterConfig.getPassword();
		DATABASE = filterConfig.getDatabase();
		AUTHDATABASE = filterConfig.getAuthdatabase();
		COLLECTION = filterConfig.getCollection();
						
		log.info("FilterMongoDB:   HOST: " + HOST);
		log.info("FilterMongoDB:   PORT: " + PORT);
		log.info("FilterMongoDB:   USERNAME: " + USERNAME);
		log.info("FilterMongoDB:   PASSWORD: see config");
		log.info("FilterMongoDB:   DATABASE: " + DATABASE);
		log.info("FilterMongoDB:   AUTHDATABASE: " + AUTHDATABASE);
		log.info("FilterMongoDB:   COLLECTION: " + COLLECTION);

		// this handles pooling
		CLIENT = new MongoClient(new MongoClientURI("mongodb://"+USERNAME+":"+PASSWORD+"@"+HOST+":"+PORT+"/?authSource="+AUTHDATABASE));
		
		super.init(filterConfig);

		inited = false;
		if (! initErrors) {

		}
		else {
			log.error("FilterMongoDB not initialized; see previous error");
		}
		
		inited = true;		
	}

	public void destroy() {
		CLIENT.close();
		super.destroy();
	}

	protected void initThisSubclass(String key, String value) {

	}

	// implemented from BaseCaching
	public void populateCacheElement(CacheElement cacheElement, String password) { 
		
		String userId = cacheElement.getUserid();
		
		HashSet groups = new HashSet();
		Map map = new Hashtable();
		map.put("gruppe", groups);

		cacheElement.populate(null, null, map, null);

		log.debug("searching "+"mongodb://"+USERNAME+":<see config>@"+HOST+":"+PORT+"/?authSource="+AUTHDATABASE+" for groups of user " + userId);

		try
		{			
			// get DB connection from pool			
			DB db = CLIENT.getDB(DATABASE);
			DBCollection coll = db.getCollection(COLLECTION);
			
			// Prepare statement			
			BasicDBObject query = new BasicDBObject("members", userId);
			
			// Execute statement
			DBCursor cursor = coll.find(query);			
			try {
			  while(cursor.hasNext()) {
			  	DBObject o = cursor.next();
			  	// log.info("Found "+o.toString());
			  	String groupid = (String)o.get("groupid");
			  	if(!groups.contains(groupid))
				{
					groups.add(groupid);
					log.info("group " + groupid + " added for user " + userId);
				}
			  }
			  cacheElement.populate(null, null, map, null);
			} catch(Exception e) {			
				log.error("error while querying database: " + e.toString());
				e.printStackTrace();			
			} finally {
			  cursor.close();
			}
			
		}
		catch(Exception e)
		{
			log.error("error while querying database: " + e.toString());
			e.printStackTrace();
		}
			
	}	
	
}
