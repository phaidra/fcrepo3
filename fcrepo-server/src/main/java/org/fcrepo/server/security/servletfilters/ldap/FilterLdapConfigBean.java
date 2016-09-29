
package org.fcrepo.server.security.servletfilters.ldap;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

public class FilterLdapConfigBean
        implements FilterConfig {

    private ServletContext cxt;

    private String filterName;
	private String VERSION = "2";
	private String URL = null;
	private String SECURITY_PRINCIPAL = null;
	private String SECURITY_CREDENTIALS = null;
	private String USER_SEARCH_BASE = null;
	private String USER_SEARCH_FILTER = null;
	private String USER_ID_ATTRIBUTE = null;
	private String USER_ATTRIBUTES = null;
	private String GROUP_SEARCH_BASE = null;
	private String GROUP_SEARCH_FILTER = null;
	private String GROUP_ATTRIBUTE = null;
	private String GROUP_ATTRIBUTE_ALIAS = null;
/*
	private String MEMBER_OF_GROUP_DN = null;
	private String MEMBER_OF_GROUP_ATTRIBUTE = null;
	private String KEYSTORE = null;
	private String KEYSTORE_PASSWORD = null;
	private String TRUSTSTORE = null;
	private String TRUSTSTORE_PASSWORD = null;
*/
    private final Map<String, String> params =
            new LinkedHashMap<String, String>();

    public void setFilterName(String name) {
        filterName = name;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setVersion(String version) {
        VERSION = version;
    }

    public String getVersion() {
        return VERSION;
    }

    public void setUrl(String url) {
        URL = url;
    }

    public String getUrl() {
        return URL;
    }

    public void setSecurityPrincipal(String securityPrincipal) {
        SECURITY_PRINCIPAL = securityPrincipal;
    }

    public String getSecurityPrincipal() {
        return SECURITY_PRINCIPAL;
    }

    public void setSecurityCredentials(String securityCredentials) {
        SECURITY_CREDENTIALS = securityCredentials;
    }

    public String getSecurityCredentials() {
        return SECURITY_CREDENTIALS;
    }

    public void setUserSearchBase(String v) {
        USER_SEARCH_BASE = v;
    }

    public String getUserSearchBase() {
        return USER_SEARCH_BASE;
    }

    public void setUserSearchFilter(String v) {
        USER_SEARCH_FILTER = v;
    }

    public String getUserSearchFilter() {
        return USER_SEARCH_FILTER;
    }

    public void setUserIdAttribute(String v) {
        USER_ID_ATTRIBUTE = v;
    }

    public String getUserIdAttribute() {
        return USER_ID_ATTRIBUTE;
    }

    public void setUserAttributes(String v) {
        USER_ATTRIBUTES = v;
    }

    public String getUserAttributes() {
        return USER_ATTRIBUTES;
    }

    public void setGroupSearchBase(String v) {
        GROUP_SEARCH_BASE = v;
    }

    public String getGroupSearchBase() {
        return GROUP_SEARCH_BASE;
    }

    public void setGroupSearchFilter(String v) {
        GROUP_SEARCH_FILTER = v;
    }

    public String getGroupSearchFilter() {
        return GROUP_SEARCH_FILTER;
    }

    public void setGroupAttribute(String v) {
        GROUP_ATTRIBUTE = v;
    }

    public String getGroupAttribute() {
        return GROUP_ATTRIBUTE;
    }

    public void setGroupAttributeAlias(String v) {
        GROUP_ATTRIBUTE_ALIAS = v;
    }

    public String getGroupAttributeAlias() {
        return GROUP_ATTRIBUTE_ALIAS;
    }

    public void setServletContext(ServletContext sc) {
        cxt = sc;
    }

    public ServletContext getServletContext() {
        return cxt;
    }

    public void addInitParameter(String key, String value) {
        params.put(key, value);
    }

    public String getInitParameter(String name) {
        return params.get(name);
    }

    public Enumeration<String> getInitParameterNames() {
        return new Enumeration<String>() {

            Iterator<String> i = params.keySet().iterator();

            public boolean hasMoreElements() {
                return i.hasNext();
            }

            public String nextElement() {
                return i.next();
            }
        };
    }

}
