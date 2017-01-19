
package org.fcrepo.server.security.servletfilters.db;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

public class FilterDBConfigBean
        implements FilterConfig {

    private ServletContext cxt;

    private String filterName;

    private String ASSOCIATED_FILTERS = null;
    private String RESOURCE = null;
    private String QUERY = null;
    private String ATTRIBUTES = null;
    private String BINDPARAMS = null;

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

    public void setResource(String v) {
        RESOURCE = v;
    }

    public String getResource() {
        return RESOURCE;
    }

    public void setQuery(String v) {
        QUERY = v;
    }

    public String getQuery() {
        return QUERY;
    }

    public void setAttributes(String v) {
        ATTRIBUTES = v;
    }

    public String getAttributes() {
        return ATTRIBUTES;
    }

    public void setBindparams(String v) {
        BINDPARAMS = v;
    }

    public String getBindparams() {
        return BINDPARAMS;
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
