
package org.fcrepo.server.security.servletfilters.restservice;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

public class FilterRestServiceConfigBean
        implements FilterConfig {

    private ServletContext cxt;

    private String filterName;

    private String ASSOCIATED_FILTERS = null;
    private String URL = null;

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

    public void setUrl(String v) {
        URL = v;
    }

    public String getUrl() {
        return URL;
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
