/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.lifecycle;

import java.io.*;
import java.security.Principal;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.eclipse.swt.internal.lifecycle.UICallBackServiceHandler;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;

import com.w4t.engine.service.ContextProvider;
import com.w4t.engine.service.ServiceContext;

/**
 * A utility class that provides some static helper methods to perform
 * commonly needed tasks with respect to backround thread management.
 */
public class UICallBackUtil {
  
  private static final class DummyResponse implements HttpServletResponse {

    public void addCookie( Cookie cookie ) {
    }

    public void addDateHeader( String name, long date ) {
    }

    public void addHeader( String name, String value ) {
    }

    public void addIntHeader( String name, int value ) {
    }

    public boolean containsHeader( String name ) {
      return false;
    }

    public String encodeRedirectURL( String url ) {
      return null;
    }

    public String encodeRedirectUrl( String url ) {
      return null;
    }

    public String encodeURL( String url ) {
      return null;
    }

    public String encodeUrl( String url ) {
      return null;
    }

    public void sendError( int sc ) throws IOException {
    }

    public void sendError( int sc, String msg ) throws IOException {
    }

    public void sendRedirect( String location ) throws IOException {
    }

    public void setDateHeader( String name, long date ) {
    }

    public void setHeader( String name, String value ) {
    }

    public void setIntHeader( String name, int value ) {
    }

    public void setStatus( int sc ) {
    }

    public void setStatus( int sc, String sm ) {
    }

    public void flushBuffer() throws IOException {
    }

    public int getBufferSize() {
      return 0;
    }

    public String getCharacterEncoding() {
      return null;
    }

    public String getContentType() {
      return null;
    }

    public Locale getLocale() {
      return null;
    }

    public ServletOutputStream getOutputStream() throws IOException {
      return null;
    }

    public PrintWriter getWriter() throws IOException {
      return null;
    }

    public boolean isCommitted() {
      return false;
    }

    public void reset() {
    }

    public void resetBuffer() {
    }

    public void setBufferSize( int size ) {
    }

    public void setCharacterEncoding( String charset ) {
    }

    public void setContentLength( int len ) {
    }

    public void setContentType( String type ) {
    }

    public void setLocale( Locale loc ) {
    }
  }

  private static final class DummyRequest implements HttpServletRequest {

    private final HttpSession session;

    private DummyRequest( HttpSession session ) {
      this.session = session;
    }

    public String getAuthType() {
      return null;
    }

    public String getContextPath() {
      return null;
    }

    public Cookie[] getCookies() {
      return null;
    }

    public long getDateHeader( String name ) {
      return 0;
    }

    public String getHeader( String name ) {
      return null;
    }

    public Enumeration getHeaderNames() {
      return null;
    }

    public Enumeration getHeaders( String name ) {
      return null;
    }

    public int getIntHeader( String name ) {
      return 0;
    }

    public String getMethod() {
      return null;
    }

    public String getPathInfo() {
      return null;
    }

    public String getPathTranslated() {
      return null;
    }

    public String getQueryString() {
      return null;
    }

    public String getRemoteUser() {
      return null;
    }

    public String getRequestURI() {
      return null;
    }

    public StringBuffer getRequestURL() {
      return null;
    }

    public String getRequestedSessionId() {
      return null;
    }

    public String getServletPath() {
      return null;
    }

    public HttpSession getSession() {
      return session;
    }

    public HttpSession getSession( boolean create ) {
      return session;
    }

    public Principal getUserPrincipal() {
      return null;
    }

    public boolean isRequestedSessionIdFromCookie() {
      return false;
    }

    public boolean isRequestedSessionIdFromURL() {
      return false;
    }

    public boolean isRequestedSessionIdFromUrl() {
      return false;
    }

    public boolean isRequestedSessionIdValid() {
      return false;
    }

    public boolean isUserInRole( String role ) {
      return false;
    }

    public Object getAttribute( String name ) {
      return null;
    }

    public Enumeration getAttributeNames() {
      return null;
    }

    public String getCharacterEncoding() {
      return null;
    }

    public int getContentLength() {
      return 0;
    }

    public String getContentType() {
      return null;
    }

    public ServletInputStream getInputStream() throws IOException {
      return null;
    }

    public String getLocalAddr() {
      return null;
    }

    public String getLocalName() {
      return null;
    }

    public int getLocalPort() {
      return 0;
    }

    public Locale getLocale() {
      return null;
    }

    public Enumeration getLocales() {
      return null;
    }

    public String getParameter( String name ) {
      return null;
    }

    public Map getParameterMap() {
      return null;
    }

    public Enumeration getParameterNames() {
      return null;
    }

    public String[] getParameterValues( String name ) {
      return null;
    }

    public String getProtocol() {
      return null;
    }

    public BufferedReader getReader() throws IOException {
      return null;
    }

    public String getRealPath( String path ) {
      return null;
    }

    public String getRemoteAddr() {
      return null;
    }

    public String getRemoteHost() {
      return null;
    }

    public int getRemotePort() {
      return 0;
    }

    public RequestDispatcher getRequestDispatcher( String path ) {
      return null;
    }

    public String getScheme() {
      return null;
    }

    public String getServerName() {
      return null;
    }

    public int getServerPort() {
      return 0;
    }

    public boolean isSecure() {
      return false;
    }

    public void removeAttribute( String name ) {
    }

    public void setAttribute( String name, Object o ) {
    }

    public void setCharacterEncoding( String env ) throws UnsupportedEncodingException {
    }
  }
  
  /**
   * Sometimes a backround thread needs to access values that are stored
   * in the session object that started the thread. In particular these
   * values may be stored in session singletons. Accessing these singletons
   * directly from the background thread would fail. This method fakes the
   * missing request context and allows the runnable code to access those 
   * singletons.
   * 
   * @param display The display that is bound to the session that contains the
   *                data to which the current thread should get access.
   * @param runnable The runnable that contains the critical code that 
   *                 needs to have access to a request context.
   *        
   * @see <code>SessionSingletonBase</code>
   * @see <code>ContextProvider</code>
   */
  public static void runNonUIThreadWithFakeContext( final Display display,
                                                    final Runnable runnable )
  {
    boolean useFakeContext = !ContextProvider.hasContext();
    if( useFakeContext ) {
      IDisplayAdapter adapter = getAdapter( display );
      DummyRequest request = new DummyRequest( adapter.getSession() );
      DummyResponse response = new DummyResponse();
      ServiceContext context = new ServiceContext( request, response );
      ContextProvider.setContext( context );
    }
    try {
      runnable.run();
    } finally {
      if( useFakeContext ) {
        ContextProvider.disposeContext();
      }
    }
  }

  private static IDisplayAdapter getAdapter( final Display display ) {
    return ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
  }
  
  /**
   * To allow automatically UI-updates by server side background threads
   * activate the UICallBack mechanism. Call this method before the start of
   * a thread and {@link deactivateUICallBack} at the end. Each activation
   * needs a session unique identifier as a kind of reference pointer to be able
   * to decide when all background threads are finished.
   * 
   * <p>Note: this method can only be called in the UI-Thread of a RWT 
   *          application.</p>
   * 
   * @param id A session unique identifier to trace the activation and
   *           deactivation.
   *           
   * @see <code>Display#syncExcec</code>
   * @see <code>Display#asyncExcec</code>
   * @see <code>Display#getThread</code>
   * @see <code>Display#wake</code>
   */
  public static void activateUICallBack( final String id ) {
    UICallBackServiceHandler.activateUICallBacksFor( id );
  }
  
  /**
   * To allow automatically UI-updates by server side background threads
   * activate the UICallBack mechanism. Call {@link deactivateUICallBack} method
   * before the start of a thread and deactivateUICallBack at the end. Each 
   * activation needs a session unique identifier as a kind of reference pointer
   * to be able to decide when all background threads are finished.
   * 
   * <p>Note: this method can only be called in the UI-Thread of a RWT 
   *          application.</p>
   *          
   * @param id A session unique identifier to trace the activation and
   *           deactivation.
   *           
   * @see <code>Display#syncExcec</code>
   * @see <code>Display#asyncExcec</code>
   * @see <code>Display#getThread</code>
   * @see <code>Display#wake</code>
   */
  public static void deactivateUICallBack( final String id ) {
    UICallBackServiceHandler.deactivateUICallBacksFor( id );
  }
  
  private UICallBackUtil() {
    // prevent instance creation
  }  
}
