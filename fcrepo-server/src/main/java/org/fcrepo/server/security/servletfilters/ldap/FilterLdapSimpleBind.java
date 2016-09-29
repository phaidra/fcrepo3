// $Id: FilterLdapSimpleBind.java 51 2009-03-31 13:38:21Z tw $

package org.fcrepo.server.security.servletfilters.ldap; // changed to org.fcrepo
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import javax.servlet.FilterConfig;
import org.fcrepo.server.security.servletfilters.ldap.FilterLdapConfigBean;

/*
previous 3.3.x version changed by Hugh Barnard October 2014
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// changed path of these two, added org, Hugh Barnard October 2014
import org.fcrepo.server.security.servletfilters.BaseCaching;
import org.fcrepo.server.security.servletfilters.CacheElement;


import javax.naming.Context;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.DirContext;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.servlet.ServletException;

/** 
 *  @author Thomas Wana (thomas.wana@univie.ac.at)
 */
public class FilterLdapSimpleBind extends BaseCaching {
	protected static Logger log = LoggerFactory.getLogger(FilterLdapSimpleBind.class);

	public static final String CONTEXT_VERSION_KEY = "java.naming.ldap.version";   

/*

in security.xml
  <bean id="LdapFilterForAttributes" class="org.fcrepo.server.security.servletfilters.ldap.FilterLdapSimpleBind"
    lazy-init="true" init-method="init">
    <property name="config">
      <bean class="org.fcrepo.server.security.servletfilters.ldap.FilterLdapConfigBean">
        <property name="filterName" value="LdapFilterForAttributes"/>
        <property name="version" value="3"/>          
        <property name="url" value="ldaps://ds.univie.ac.at:636/"/>
        <property name="securityPrincipal" value="cn=phaidra,ou=accounts,dc=univie,dc=ac,dc=at"/>
        <property name="securityCredentials" value="blablabla"/>
        <property name="userSearchBase" value="dc=univie,dc=ac,dc=at"/>
        <property name="userSearchFilter" value="(&amp;(uid={0})(objectClass=univiePerson))"/>
        <property name="userIdAttribute" value="uid"/>
        <property name="userAttributes" value="cn,givenName,mail"/>
        <property name="groupSearchBase" value="ou=zid,ou=groups,dc=univie,dc=ac,dc=at"/>
        <property name="groupSearchFilter" value="(member=uid={0},ou=pers,dc=univie,dc=ac,dc=at)"/>
        <property name="groupAttribute" value="cn"/>
        <property name="groupAttributeAlias" value="groups"/>        
      </bean>
    </property>
  </bean>

previously in web.xml:
 <init-param>
    <param-name>version</param-name>
    <param-value>3</param-value>
 </init-param>
 <init-param>
    <param-name>url</param-name>
    <param-value>ldaps://das.univie.ac.at:636/</param-value>
 </init-param>
 <init-param>
    <param-name>security-principal</param-name>
    <param-value>cn=cms,ou=accounts,dc=univie,dc=ac,dc=at</param-value>
 </init-param>
 <init-param>
    <param-name>security-credentials</param-name>
    <param-value>234abcdef12311</param-value>
 </init-param>
 <init-param>
    <param-name>user-search-base</param-name>
    <param-value>dc=univie,dc=ac,dc=at</param-value>
 </init-param>
 <init-param>
    <param-name>user-search-filter</param-name>
    <param-value>(&amp;(uid={0})(objectClass=univiePerson))</param-value>
 </init-param>
 <init-param>
    <param-name>user-id-attribute</param-name>
    <param-value>uid</param-value>
 </init-param>
 <init-param>
    <param-name>user-attributes</param-name>
    <param-value>cn,givenName,mail</param-value>
 </init-param>
 <init-param>
    <param-name>group-search-base</param-name>
    <param-value>ou=groups,dc=univie,dc=ac,dc=at</param-value>
 </init-param>
 <init-param>
    <param-name>group-search-filter</param-name>
    <param-value>(member=uid={0},ou=pers,dc=univie,dc=ac,dc=at)</param-value>
 </init-param>
 <init-param>
    <param-name>group-attribute</param-name>
    <param-value>cn</param-value>
 </init-param>
 <init-param>
    <param-name>group-attribute-alias</param-name>
    <param-value>groups</param-value>
 </init-param>   
*/

	public static final String VERSION_KEY = "version";       
	public static final String URL_KEY = "url";   
	public static final String SECURITY_PRINCIPAL_KEY = "security-principal";
	public static final String SECURITY_CREDENTIALS_KEY = "security-credentials";
	public static final String USER_SEARCH_BASE_KEY = "user-search-base";   
	public static final String USER_SEARCH_FILTER_KEY = "user-search-filter";
	public static final String USER_ID_ATTRIBUTE_KEY = "user-id-attribute";   
	public static final String USER_ATTRIBUTES_KEY = "user-attributes";
	public static final String GROUP_SEARCH_BASE_KEY = "group-search-base";
	public static final String GROUP_SEARCH_FILTER_KEY = "group-search-filter";
	public static final String GROUP_ATTRIBUTE_KEY = "group-attribute";
	public static final String GROUP_ATTRIBUTE_ALIAS_KEY = "group-attribute-alias";
	public static final String MEMBER_OF_GROUP_DN_KEY = "member-of-group-dn";
	public static final String MEMBER_OF_GROUP_ATTRIBUTE_KEY = "member-of-group-attribute";
	public static final String KEYSTORE_KEY = "keystore";
	public static final String KEYSTORE_PASSWORD_KEY = "keystore-password";
	public static final String TRUSTSTORE_KEY = "truststore";
	public static final String TRUSTSTORE_PASSWORD_KEY = "truststore-password";

	private String VERSION = "2";
	private String URL = null;
	private String SECURITY_PRINCIPAL = null;
	private String SECURITY_CREDENTIALS = null;
	private List<String> USER_SEARCH_BASE = new ArrayList<String>();
	private String USER_SEARCH_FILTER = null;
	private String USER_ID_ATTRIBUTE = null;
	private String[] USER_ATTRIBUTES = null;
	private String GROUP_SEARCH_BASE = null;
	private String GROUP_SEARCH_FILTER = null;
	private String GROUP_ATTRIBUTE = null;
	private String GROUP_ATTRIBUTE_ALIAS = null;
	private String MEMBER_OF_GROUP_DN = null;
	private String MEMBER_OF_GROUP_ATTRIBUTE = null;
	private String KEYSTORE = null;
	private String KEYSTORE_PASSWORD = null;
	private String TRUSTSTORE = null;
	private String TRUSTSTORE_PASSWORD = null;

	private Hashtable envSimpleBind = null;

	@Override
	public void init(FilterConfig config) {
		log.info("initializing start..blili");
		FilterLdapConfigBean filterConfig = (FilterLdapConfigBean)config;
		log.info(filterConfig.toString());

		VERSION = filterConfig.getVersion();
		URL = filterConfig.getUrl();		
		SECURITY_PRINCIPAL = filterConfig.getSecurityPrincipal();
		SECURITY_CREDENTIALS = filterConfig.getSecurityCredentials();

		String value = filterConfig.getUserSearchBase();
		String[] user_search_base_array = value.split(";");  	
		for(int i=0; i < user_search_base_array.length; i++)
		{
			String sb = user_search_base_array[i];
			USER_SEARCH_BASE.add(sb);
		}	
		
		USER_SEARCH_FILTER = filterConfig.getUserSearchFilter();
		USER_ID_ATTRIBUTE = filterConfig.getUserIdAttribute();

		value = filterConfig.getUserAttributes();
		if (value.indexOf(",") < 0) {
			if ("".equals(value)) {
				USER_ATTRIBUTES = null;
			} else {
				USER_ATTRIBUTES = new String[1];
				USER_ATTRIBUTES[0] = value;
			}
		} else {
			USER_ATTRIBUTES = value.split(",");  							
		}			
		
		GROUP_SEARCH_BASE = filterConfig.getGroupSearchBase();		
		GROUP_SEARCH_FILTER = filterConfig.getGroupSearchFilter();		
		GROUP_ATTRIBUTE = filterConfig.getGroupAttribute();		
		GROUP_ATTRIBUTE_ALIAS = filterConfig.getGroupAttributeAlias();

		log.info("initialized.");
		log.debug("  VERSION: " + VERSION);
		log.debug("  URL: " + URL);
		log.debug("  SECURITY_PRINCIPAL: " + SECURITY_PRINCIPAL);
		log.debug("  SECURITY_CREDENTIALS: " + SECURITY_CREDENTIALS);
		for(String s : USER_SEARCH_BASE) {
		    log.info("  USER_SEARCH_BASE: " + s);
		}	
		log.debug("  USER_SEARCH_FILTER: " + USER_SEARCH_FILTER);
		log.debug("  USER_ID_ATTRIBUTE: " + USER_ID_ATTRIBUTE);
		log.debug("  GROUP_SEARCH_BASE: " + GROUP_SEARCH_BASE);
		log.debug("  GROUP_SEARCH_FILTER: " + GROUP_SEARCH_FILTER);
		log.debug("  GROUP_ATTRIBUTE: " + GROUP_ATTRIBUTE);
		log.debug("  GROUP_ATTRIBUTE_ALIAS: " + GROUP_ATTRIBUTE_ALIAS);

		super.init(filterConfig);

		inited = false;
		if (! initErrors) {

		}
		else {
			log.error("FilterLdapSimpleBind not initialized; see previous error");
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
		DirContext ctx = null;
		String dn = null;

		/*
		log.info("using:");
		log.debug("  VERSION: " + VERSION);
		log.debug("  URL: " + URL);
		log.debug("  SECURITY_PRINCIPAL: " + SECURITY_PRINCIPAL);
		log.debug("  SECURITY_CREDENTIALS: " + SECURITY_CREDENTIALS);
		for(String s : USER_SEARCH_BASE) {
		    log.info("  USER_SEARCH_BASE: " + s);
		}	
		log.debug("  USER_SEARCH_FILTER: " + USER_SEARCH_FILTER);
		log.debug("  USER_ID_ATTRIBUTE: " + USER_ID_ATTRIBUTE);
		log.debug("  GROUP_SEARCH_BASE: " + GROUP_SEARCH_BASE);
		log.debug("  GROUP_SEARCH_FILTER: " + GROUP_SEARCH_FILTER);
		log.debug("  GROUP_ATTRIBUTE: " + GROUP_ATTRIBUTE);
		log.debug("  GROUP_ATTRIBUTE_ALIAS: " + GROUP_ATTRIBUTE_ALIAS);
		*/

		try
		{
			if(TRUSTSTORE!=null && !TRUSTSTORE.equals(""))
				System.setProperty("javax.net.ssl.trustStore", TRUSTSTORE);
			if(TRUSTSTORE_PASSWORD!=null && !TRUSTSTORE_PASSWORD.equals(""))
				System.setProperty("javax.net.ssl.trustStorePassword", TRUSTSTORE_PASSWORD);
			if(KEYSTORE!=null && !KEYSTORE.equals(""))
				System.setProperty("javax.net.ssl.keyStore", KEYSTORE);
			if(KEYSTORE_PASSWORD!=null && !KEYSTORE_PASSWORD.equals(""))
				System.setProperty("javax.net.ssl.keyStorePassword", KEYSTORE_PASSWORD);

			// Connect to LDAP as simple bind user
			if(!SECURITY_PRINCIPAL.equals(""))
			{
				try
				{
					
					ctx = connectLdap("");
				}
				catch(Exception e)
				{
					log.error("connectLdap failed: "+e.toString());
					throw e;
				}

				// Get the user's dn
				try
				{					
					dn = getUserDN(ctx, cacheElement.getUserid(), map);
				}
				catch(Exception e)
				{
					log.error("getUserDN failed: "+e.toString());
					throw e;
				}
			}
			else
			{
				// skip simple bind authentication, log in 
				// directly as the user
				dn = USER_ID_ATTRIBUTE + "=" + cacheElement.getUserid() + "," + USER_SEARCH_BASE;
				log.debug("user's dn: "+dn);
			}

			if(AUTHENTICATE)
			{
				// Log in as that user
				try
				{
					authenticated = connectLdapAsUser(cacheElement.getUserid(), dn, password);
				}
				catch(Exception e)
				{
					log.error("connectLdapAsUser failed: "+e.toString());
					throw e;
				}
			}

			// reopen context, so we can switch the search base
			if(!SECURITY_PRINCIPAL.equals(""))
			{
				try
				{
					ctx.close();
					ctx = connectLdap(GROUP_SEARCH_BASE);
				}
				catch(Exception e)
				{
					log.error("re-connectLdap failed: "+e.toString());
					throw e;
				}
			}

			// get group memberships
			try
			{
				//getGroupMemberships(ctx, cacheElement.getUserid(), map);
			}
			catch(Exception e)
			{
				log.error("getGroupDetails failed: "+e.toString());
				throw e;
			}
			log.debug("authenticated = "+authenticated+", map = "+map);
			cacheElement.populate(authenticated, null, map, null);
		}
		catch(Exception e)
		{
			// fall through
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);

			log.error("caught exception: "+e.toString() + " " + sw.toString());

		}
		finally
		{
			if(ctx != null)
			{
				try
				{
					ctx.close();
				}
				catch(Exception e)
				{
					// don't care
					log.error("exception while closing ctx: "+e.toString());
				}
				ctx = null;
			}

			log.debug("finally: authenticated = "+authenticated+", map = "+map);
		}
	}

	// Connect to ldap as simple bind user
	private DirContext connectLdap(String searchbase) throws NamingException
	{
		// create a new LDAP environment for simple binding
		envSimpleBind = new Hashtable(11);
		envSimpleBind.put("java.naming.ldap.version", VERSION);
		envSimpleBind.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		envSimpleBind.put(Context.PROVIDER_URL, URL + searchbase);
		envSimpleBind.put(Context.SECURITY_AUTHENTICATION, "simple");
		envSimpleBind.put(Context.SECURITY_PRINCIPAL, SECURITY_PRINCIPAL);
		envSimpleBind.put(Context.SECURITY_CREDENTIALS, SECURITY_CREDENTIALS);
		log.debug("simple bind environment:");
		log.debug("   PROVIDER_URL: " + envSimpleBind.get(Context.PROVIDER_URL));
		log.debug("   SECURITY_PRINCIPAL: " + envSimpleBind.get(Context.SECURITY_PRINCIPAL));
		log.debug("   SECURITY_CREDENTIALS: " + envSimpleBind.get(Context.SECURITY_CREDENTIALS));

		// log in as simple bind user
		DirContext ctx = null;
		try
		{
			ctx = new InitialDirContext(envSimpleBind);
		}
		catch(NamingException e)
		{
			log.error("error creating LDAP connection: "+e.toString());
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			log.error("trace: "+sw.toString());

			throw e;
		}
		log.debug("log in as simple bind user successful");
		return ctx;
	}

	private String getUserDN(DirContext ctx, String username, Map map) throws Exception, NamingException, PatternSyntaxException, NullPointerException
	{
		// look up the requested object's DN
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration results = null;
		try
		{
			log.debug("USER SEARCH BASE list: "+USER_SEARCH_BASE.toString());
			for(String sb : USER_SEARCH_BASE) {
				String filter = getFilter(USER_SEARCH_FILTER, username);
				log.info("searching for user |" + username + "| in search base (" + sb + ") with filter: " + filter);
				results = ctx.search(sb, filter, controls);
				if(results.hasMore()){
					log.info("user |" + username + "| found in search base " + sb);
					break;
				}
			}
			
		}
		catch(NamingException e)
		{
			log.error("error looking up the user's dn: "+e.toString());
			throw e;
		}
		catch(PatternSyntaxException e)
		{
			log.error("syntax error in filter: "+e.toString());
			throw e;
		}
		catch(NullPointerException e)
		{
			log.error("null filter: "+e.toString());
			throw e;
		}
		if(!results.hasMore())
		{
			throw new Exception("user |"+username+"| not found in directory");
		}
		SearchResult searchResult = (SearchResult)results.next();
		String dn = searchResult.getNameInNamespace();
		log.debug("got users's dn: " + dn);

		// fill in the requested attributes
		Attributes attrs = searchResult.getAttributes();
		for(int i=0; i < USER_ATTRIBUTES.length; i++)
		{
			String key = USER_ATTRIBUTES[i];
			log.debug("looking for attribute |" + key + "|");
			Attribute attr = attrs.get(key);
			if(attr != null)
			{
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
				for(int j=0; j < attr.size(); j++)
				{
					Object o = attr.get(j);
					values.add(o);
					log.debug("added value |" + o.toString() + "| for key |" + key + "|, class: " + o.getClass().getName());
				}
			}
			else
			{
				log.debug("attribute |" + key + "| not found in directory, continuing");
			}
		}

		return dn;
	}	

	private Boolean userBelongsToGroup (DirContext ctx, String username )  throws NamingException
	{
		// look up the requested object's DN
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration results = null;
		try
		{
			String filter = "(" + MEMBER_OF_GROUP_ATTRIBUTE + "=" + username + ")"; 
			log.debug("searching for group |" + MEMBER_OF_GROUP_DN + "| with filter: " + filter); 
			results = ctx.search("", filter, controls); 
		} 
		catch(NamingException e)
		{
			log.error("error looking up the user's dn: "+e.toString());
			return false;
		}
		catch(PatternSyntaxException e)
		{
			log.error("syntax error in filter: "+e.toString());
			return false;
		}
		catch(NullPointerException e)
		{
			log.error("null filter: "+e.toString());
			return false;
		}
		catch(Exception e)
		{
			log.error("exception: "+e.toString());
			return false;
		}
		boolean found = false;
		while(!found && results.hasMore())
		{
			SearchResult searchResult = (SearchResult)results.next();
			String dn = searchResult.getNameInNamespace();
			if(dn.equals(MEMBER_OF_GROUP_DN))
			{
				// user belongs to group MEMBER_OF_GROUP_DN!
				log.debug("user " + username + " belongs to group " + MEMBER_OF_GROUP_DN + " - OK!");
				found = true;
			}
		}
		if(found==false)
		{
			log.debug("user " + username + " doesn't belong to group " + MEMBER_OF_GROUP_DN);
		}
		return found;
	}

	private Boolean connectLdapAsUser(String username, String dn, String password) throws NamingException
	{
		Boolean rval = null;
		Hashtable env = new Hashtable(11);
		env.put("java.naming.ldap.version", VERSION);
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, URL);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, dn);
		env.put(Context.SECURITY_CREDENTIALS, password);
		try
		{
			DirContext ctx = new InitialDirContext(env);
			if(MEMBER_OF_GROUP_DN!=null && !MEMBER_OF_GROUP_DN.equals(""))
			{
				// User needs to be a member of the group MEMBER_OF_GROUP_DN
				rval = userBelongsToGroup(ctx, username);
			}
			else
			{
				rval = Boolean.TRUE;
			}
			if(rval==Boolean.TRUE)
			{
				log.info("successful login for user "+dn);
			}
			else
			{
				log.info("login failed for user |"+dn+"|");
			}
			ctx.close();
		}
		catch(NamingException e)
		{
			// login failed = not authenticated
			rval = Boolean.FALSE;
			log.info("login failed for user |"+dn+"|, error was: "+e.toString());
		}

		return rval;
	}

	private String getFilter(String filter, String username) throws PatternSyntaxException, NullPointerException
	{
		String rval = filter.replaceAll("\\{0\\}", username);
		// TODO: security: strip username from invalid characters?
		return rval;
	}

	private void getGroupMemberships(DirContext ctx, String username, Map map) throws NamingException, PatternSyntaxException, NullPointerException
	{
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration results = null;

		try
		{
			String filter = getFilter(GROUP_SEARCH_FILTER, username);
			log.debug("searching for group memberships with filter: " + filter);
			results = ctx.search("", filter, controls);
		}
		catch(NamingException e)
		{
			log.error("error looking up the user's dn: "+e.toString());
			throw e;
		}
		catch(PatternSyntaxException e)
		{
			log.error("syntax error in filter: "+e.toString());
			throw e;
		}
		catch(NullPointerException e)
		{
			log.error("null filter: "+e.toString());
			throw e;
		}
		while(results.hasMore())
		{
			SearchResult searchResult = (SearchResult)results.next();
			String dn = searchResult.getNameInNamespace();
			log.info("got group membership for user "+username+" in group: " + dn);

			Attributes attrs = searchResult.getAttributes();
			Attribute attr = attrs.get(GROUP_ATTRIBUTE);
			if(attr == null)
			{
				log.error("configured group attribute " + GROUP_ATTRIBUTE + " not found in group " + dn);
			}
			else
			{
				Set values;
				if(map.containsKey(GROUP_ATTRIBUTE_ALIAS))
				{
					values = (Set)map.get(GROUP_ATTRIBUTE_ALIAS);
				}
				else
				{
					values = new HashSet();
					map.put(GROUP_ATTRIBUTE_ALIAS, values);
				}
				Object o = attr.get(0);
				values.add(o);
				log.debug("added value |" + o.toString() + "| for key |" + GROUP_ATTRIBUTE_ALIAS + "|, class: " + o.getClass().getName());
			}
		}
	}
}
