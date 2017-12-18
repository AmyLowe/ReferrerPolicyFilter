package za.loweal.crf;

import static za.loweal.crf.ReferrerPolicyFilter.REFERRER_POLICY;
import static za.loweal.crf.ReferrerPolicyFilter.REFERRER_POLICY_DEFAULT;


import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;


@RunWith(JUnit4.class)
public class ReferrerPolicyFilterTest {

    private ReferrerPolicyFilter referrerPolicyFilter;
    private ServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;


    @Before
    public void setup() throws ServletException{
        referrerPolicyFilter = new ReferrerPolicyFilter();
        response = mock(HttpServletResponse.class);
        request = mock(ServletRequest.class);
        filterChain = mock(FilterChain.class);
    }    

    @Test
    public void test_default_value() throws IOException, ServletException {
        FilterConfig filterConfig = mock(FilterConfig.class);
        referrerPolicyFilter.init(filterConfig);
        referrerPolicyFilter.doFilter(request, response, filterChain);
        verify(response).addHeader("Referrer-Policy", "'no-referrer-when-downgrade'");
    }

    @Test
    public void test_set_value() throws IOException, ServletException {
        FilterConfig filterConfig = mock(FilterConfig.class);
        when(filterConfig.getInitParameter(REFERRER_POLICY)).thenReturn("bla");
        referrerPolicyFilter.init(filterConfig);
        referrerPolicyFilter.doFilter(request, response, filterChain);
        verify(response).addHeader(REFERRER_POLICY, "bla");
       
    }



}