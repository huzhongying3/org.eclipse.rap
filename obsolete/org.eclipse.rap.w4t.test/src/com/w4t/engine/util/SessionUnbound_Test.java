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
package com.w4t.engine.util;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.eclipse.rwt.internal.browser.Default;
import org.eclipse.rwt.internal.browser.Ie6;
import org.eclipse.rwt.internal.lifecycle.LifeCycle;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.rwt.lifecycle.*;

import com.w4t.*;
import com.w4t.W4TFixture.TestResponse;
import com.w4t.engine.W4TModel;
import com.w4t.engine.W4TModelUtil;

/**
 * <p>Test case to ensure that the HttpSessionBindingListener used in W4T
 * are 'appServer-friendly'; that is don't use getId on invalidated sessions
 * </p> 
 */
public class SessionUnbound_Test extends TestCase {

  private static final String ALL_PHASES 
    = "beforePREPARE_UI_ROOT|afterPREPARE_UI_ROOT|" 
    + "beforeREAD_DATA|afterREAD_DATA|" 
    + "beforePROCESS_ACTION|afterPROCESS_ACTION|" 
    + "beforeRENDER|afterRENDER|";

  private static final class PhaseLogger implements PhaseListener {
    private static final long serialVersionUID = 1L;
    String log = "";
    public void beforePhase( final PhaseEvent event ) {
      log += "before" + event.getPhaseId() + "|";
    }
    public void afterPhase( final PhaseEvent event ) {
      log += "after" + event.getPhaseId() + "|";      
    }
    public PhaseId getPhaseId() {
      return PhaseId.ANY;
    }
  }
  
  private static final class InvalidatePhaseListener implements PhaseListener {
    private static final long serialVersionUID = 1L;
    public void afterPhase( final PhaseEvent event ) {
    }
    public void beforePhase( final PhaseEvent event ) {
      W4TContext.invalidate();
    }
    public PhaseId getPhaseId() {
      return PhaseId.PROCESS_ACTION;
    }
  }

  protected void setUp() throws Exception {
    W4TFixture.setUp();
    W4TFixture.createContext( false );
  }
  
  protected void tearDown() throws Exception {
    W4TFixture.tearDown();
    W4TFixture.removeContext();
  }
  
  // Tomcat does not allow to query the session id (HttpSession#getId()) after 
  // it was invalidated. Results in IllegalStateException
  // This test only ensures that the TestSession simulates this behaviour.
  public void testTomcatSessionInvalidate() {
    W4TFixture.TestSession session = new W4TFixture.TestSession();
    session.invalidate();
    try {
      session.getId();
      fail( "Test-Session must simulate app-server behaviour" );
    } catch( IllegalStateException e ) {
      // expected
    }
  }
  
  public void testW4TModelList() {
    W4TModel[] models = W4TModelList.getInstance().getList();
    assertEquals( "initial condition invalid", 0, models.length );
    W4TFixture.TestSession session = new W4TFixture.TestSession();
    W4TModelList.getInstance().add( session, W4TModel.getInstance() );
    session.invalidate();
    models = W4TModelList.getInstance().getList();
    assertEquals( 0, models.length );
  }
  
  public void testInvalidateAndIsInvalidated() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    assertEquals( "initially, isInvalidated must return false", 
                  false, 
                  stateInfo.isInvalidated() );
    W4TContext.invalidate();
    assertEquals( true, stateInfo.isInvalidated() );
  }
  
  public void testW4TContextInvalidate_Noscript() throws Exception {
    // prepare
    W4TFixture.fakeResponseWriter();
    W4TModelUtil.initModel();
    WebForm form = W4TFixture.getEmptyWebFormInstance();
    W4TFixture.fakeEngineForRequestLifeCycle( form );
    W4TFixture.fakeBrowser( new Default( false ) );
    PhaseLogger phaseLogger = new PhaseLogger();
    // invalidate session during lifecycle execution
    LifeCycle lifeCycle = ( LifeCycle )W4TContext.getLifeCycle();
    lifeCycle.addPhaseListener( new InvalidatePhaseListener() );
    lifeCycle.addPhaseListener( phaseLogger );
    lifeCycle.execute();
    String markup = W4TFixture.getAllMarkup();
    assertEquals( ALL_PHASES, phaseLogger.log );
    HttpServletResponse response = ContextProvider.getResponse();
    W4TFixture.TestResponse testResponse = ( TestResponse )response;
    assertEquals( HTML.CONTENT_TEXT_HTML_UTF_8, testResponse.getContentType() );
    assertTrue( "The expected page (W4Toolit Exit Page) was not rendered",
                markup.indexOf( "W4Toolkit Exit Page" ) != -1 );
    assertEquals( ExitForm.class, FormManager.getActive().getClass() );
    assertEquals( true, ContextProvider.getStateInfo().isInvalidated() );
  }
  
  public void testW4TContextInvalidate_Script() throws Exception {
    // prepare
    W4TFixture.fakeResponseWriter();
    W4TModelUtil.initModel();
    WebForm form = W4TFixture.getEmptyWebFormInstance();
    W4TFixture.fakeEngineForRequestLifeCycle( form );
    W4TFixture.fakeBrowser( new Default( true ) );
    PhaseLogger phaseLogger = new PhaseLogger();
    // invalidate session during lifecycle execution
    LifeCycle lifeCycle = ( LifeCycle )W4TContext.getLifeCycle();
    lifeCycle.addPhaseListener( new InvalidatePhaseListener() );
    lifeCycle.addPhaseListener( phaseLogger );
    lifeCycle.execute();
    String markup = W4TFixture.getAllMarkup();
    assertEquals( ALL_PHASES, phaseLogger.log );
    HttpServletResponse response = ContextProvider.getResponse();
    W4TFixture.TestResponse testResponse = ( TestResponse )response;
    assertEquals( HTML.CONTENT_TEXT_HTML_UTF_8, testResponse.getContentType() );
    assertTrue( markup.indexOf( "W4Toolkit Exit Page" ) != -1 );
    assertEquals( ExitForm.class, FormManager.getActive().getClass() );
    assertEquals( true, ContextProvider.getStateInfo().isInvalidated() );
  }
  
  public void testW4TContextInvalidate_Ajax() throws Exception {
    // prepare
    W4TModelUtil.initModel();
    W4TFixture.fakeResponseWriter();
    WebForm form = W4TFixture.getEmptyWebFormInstance();
    W4TFixture.fakeEngineForRequestLifeCycle( form );
    W4TFixture.fakeBrowser( new Ie6( true, true ) );
    PhaseLogger phaseLogger = new PhaseLogger();
    // invalidate session during lifecycle execution
    LifeCycle lifeCycle = ( LifeCycle )W4TContext.getLifeCycle();
    lifeCycle.addPhaseListener( new InvalidatePhaseListener() );
    lifeCycle.addPhaseListener( phaseLogger );
    lifeCycle.execute();
    assertEquals( ALL_PHASES, phaseLogger.log );
    String markup = W4TFixture.getAllMarkup();
    String expected 
      = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" 
      + "<ajax-response>" 
      + "<script type=\"text/javascript\">" 
      + "window.open( 'resources/html/exit.html', 'w1', '' );" 
      + "</script>" 
      + "</ajax-response>";
    assertEquals( expected, markup );
    HttpServletResponse response = ContextProvider.getResponse();
    W4TFixture.TestResponse testResponse = ( TestResponse )response;
    assertEquals( HTML.CONTENT_TEXT_XML, testResponse.getContentType()  );
    assertEquals( ExitForm.class, FormManager.getActive().getClass() );
    assertEquals( true, ContextProvider.getStateInfo().isInvalidated() );
  }
}
