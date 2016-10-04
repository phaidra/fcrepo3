
package org.fcrepo.server.security.servletfilters.mongodb;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

public class FilterMongoDBConfigBean
        implements FilterConfig {

    private ServletContext cxt;

    private String filterName;

    private String ASSOCIATED_FILTERS = null;
    private String HOST = null;
    private String PORT = null;
    private String USERNAME = null;
    private String PASSWORD = null;
    private String DATABASE = null; 
    private String COLLECTION = null;

    private final Map<String, String> params =
            new LinkedHashMap<String, String>();

    public void setFilterName(String name) {
        filterName = name;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setAssociatedFilters(String v) {
        ASSOCIATED_FILTERS = v;
    }

    public String getAssociatedFilters() {
        return ASSOCIATED_FILTERS;
    } 

    public void setHost(String v) {
        HOST = v;
    }

    public String getHost() {
        return HOST;
    }    

    public void setPort(String v) {
        PORT = v;
    }

    public String getPort() {
        return PORT;
    }

    public void setUsername(String v) {
        USERNAME = v;
    }

    public String getUsername() {
        return USERNAME;
    }

    public void setPassword(String v) {
        PASSWORD = v;
    }

    public String getPassword() {
        return PASSWORD;
    }

    public void setDatabase(String v) {
        DATABASE = v;
    }

    public String getDatabase() {
        return DATABASE;
    }

    public void setCollection(String v) {
        COLLECTION = v;
    }

    public String getCollection() {
        return COLLECTION;
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
