package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import filters.wrapper.XSSRequestWrapper;



/**
 * 
 * @author arjunagarwal
 * @since 15 JUNE 2016
 */
public class XSSFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public XSSFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		// Deny ClickJacking Attacks from URL's outside our application domain
		HttpServletResponse res = (HttpServletResponse) response;
		//String context2=request.get
		XSSRequestWrapper reqWrapper = new XSSRequestWrapper((HttpServletRequest) request);

		if(!reqWrapper.getRequestURI().contains("error")){
			if (reqWrapper.isMatch()) {
				res.reset();
				res.addHeader("X-FRAME-OPTIONS", "SAMEORIGIN");
				res.addHeader("X-XSS-Protection", "1; mode=block");
				res.addHeader("X-Content-Type-Options", "nosniff");
				res.setStatus(HttpServletResponse.SC_FORBIDDEN);	
				res.sendError(HttpServletResponse.SC_FORBIDDEN);
				//request.getRequestDispatcher("error").forward(request, res);
				//res.sendRedirect(request.getServletContext().getContextPath() + "/jsp/exception/403.jsp");
				return;
			}
		}
		res.addHeader("X-FRAME-OPTIONS", "SAMEORIGIN");
		res.addHeader("X-XSS-Protection", "1; mode=block");
		res.addHeader("X-Content-Type-Options", "nosniff");
		chain.doFilter(request, res);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
