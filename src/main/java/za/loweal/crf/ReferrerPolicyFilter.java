package za.loweal.crf;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReferrerPolicyFilter implements Filter 
{
    private static final Logger logger = LoggerFactory.getLogger(ReferrerPolicyFilter.class);
    public static final String REFERRER_HEADER = "Referrer-Policy";
    public static final String REFERRER_DEFAULT = "'no-referrer-when-downgrade'";

    private String referrer;
    

    public void init(FilterConfig filterConfig) throws ServletException {
        referrer = getParameterValue(filterConfig, REFERRER_HEADER, REFERRER_DEFAULT);
	}

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
        
                logger.debug("Adding Header {} = {}", REFERRER_HEADER, referrer);
                httpResponse.addHeader(REFERRER_HEADER, referrer);
        
                chain.doFilter(request, response);
    }

    private String getParameterValue(FilterConfig filterConfig, String paramName, String defaultValue) {
        String value = filterConfig.getInitParameter(paramName);
        if (StringUtils.isBlank(value)) {
            value = defaultValue;
        }
        return value;
    }

	public void destroy() {
		//not required
	}
}
