/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.AdapterFactoryRegistry;
import org.eclipse.rwt.internal.IInitialization;
import org.eclipse.rwt.internal.engine.RWTDelegate;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.service.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

/*
 * Tests in here are separated from RWTLifeCycle_Test because they need 
 * different setUp/tearDown implementations.
 */
public class RWTLifeCycle2_Test extends TestCase {

  private static final String TEST_SESSION_ATTRIBUTE = "testSessionAttr";
  private static final String EXCEPTION_MSG = "Error in readAndDispatch";

  private static String maliciousButtonId;
  private static boolean createUIEntered;
  private static boolean createUIExited;
  private static java.util.List eventLog;
  private static PhaseId currentPhase;

  private StartupPage.IStartupPageConfigurer bufferedConfigurer;
  private TestSession session;

  public static final class ExceptionInReadAndDispatchEntryPoint 
    implements IEntryPoint 
  {
    public int createUI() {
      createUIEntered = true;
      Display display = new Display();
      try {
        display.addListener( SWT.Dispose, new Listener() {
          public void handleEvent( final Event event ) {
            eventLog.add( event );
          }
        } );
        Shell shell = new Shell( display );
        shell.setLayout( new FillLayout() );
        Button maliciousButton = new Button( shell, SWT.PUSH );
        maliciousButton.addSelectionListener( new SelectionAdapter() {
          public void widgetSelected( SelectionEvent e ) {
            HttpSession httpSession = RWT.getSessionStore().getHttpSession();
            httpSession.setAttribute( TEST_SESSION_ATTRIBUTE, new Object() );
            throw new RuntimeException( EXCEPTION_MSG );
          }
        } );
        maliciousButtonId = WidgetUtil.getId( maliciousButton );
        shell.setSize( 100, 100 );
        shell.layout();
        shell.open();
        while( !shell.isDisposed() ) {
          if( !display.readAndDispatch() ) {
            display.sleep();
          }
        }
        return 0;
      } finally {
        createUIExited = true;
      }
    }
  }
  
  public static final class EventProcessingOnSessionRestartEntryPoint 
    implements IEntryPoint 
  {
    public int createUI() {
      createUIEntered = true;
      try {
        Display display = new Display();
        final Shell shell = new Shell( display );
        shell.addDisposeListener( new DisposeListener() {
          public void widgetDisposed( final DisposeEvent event ) {
            eventLog.add(  event );
          }
        } );
        ISessionStore sessionStore = RWT.getSessionStore();
        sessionStore.addSessionStoreListener( new SessionStoreListener() {
          public void beforeDestroy( final SessionStoreEvent event ) {
            shell.dispose();
          }
        } );
        shell.setSize( 100, 100 );
        shell.layout();
        shell.open();
        while( !shell.isDisposed() ) {
          if( !display.readAndDispatch() ) {
            display.sleep();
          }
        }
        return 0;
      } finally {
        createUIExited = true;
      }
    }
  }

  public static final class TestSessionInvalidateWithDisposeInFinallyEntryPoint
    implements IEntryPoint
  {
    public int createUI() {
      createUIEntered = true;
      Display display = new Display();
      try {
        Shell shell = new Shell( display );
        while( !shell.isDisposed() ) {
          if( !display.readAndDispatch() ) {
            display.sleep();
          }
        }
      } finally {
        createUIExited = true;
        currentPhase = CurrentPhase.get();
        try {
          // Access a session singleton to ensure that we have a valid context
          SessionSingletonBase.getInstance( this.getClass() );
        } catch( Throwable thr ) {
          eventLog.add( thr );
        }
        display.dispose();
      }
      return 0;
    }
  }
  
  public void testSessionRestartAfterExceptionInUIThread() throws Exception {
    TestRequest request;
    EntryPointManager.register( EntryPointManager.DEFAULT,
                                ExceptionInReadAndDispatchEntryPoint.class );
    // send initial request - response is index.html
    request = newRequest();
    request.setParameter( RequestParams.STARTUP, "default" );
    runRWTDelegate( request );
    request = newRequest();
    assertNull( session.getAttribute( TEST_SESSION_ATTRIBUTE ) );
    assertTrue( createUIEntered );
    assertFalse( createUIExited );
    assertEquals( 0, eventLog.size() );
    
    // send 'application startup' request - response is JavaScript to create
    // client-side representation of what was created in IEntryPoint#createUI
    request = newRequest();
    request.setParameter( RequestParams.UIROOT, "w1" );
    runRWTDelegate( request );
    assertNull( session.getAttribute( TEST_SESSION_ATTRIBUTE ) );
    assertTrue( createUIEntered );
    assertFalse( createUIExited );
    assertEquals( 0, eventLog.size() );
    
    // send 'malicious button click' - response is HTTP 500
    request = newRequest();
    request.setParameter( RequestParams.UIROOT, "w1" );
    request.setParameter( JSConst.EVENT_WIDGET_SELECTED, maliciousButtonId );
    try {
      runRWTDelegate( request );
      fail();
    } catch( RuntimeException e ) {
      assertEquals( EXCEPTION_MSG, e.getMessage() );
    }
    assertNotNull( session.getAttribute( TEST_SESSION_ATTRIBUTE ) );
    assertTrue( createUIEntered );
    assertTrue( createUIExited );
    assertEquals( 0, eventLog.size() );
    
    // send 'refresh' request - session is restarted, response is index.html
    request = newRequest();
    request.setParameter( RequestParams.STARTUP, "default" );
    runRWTDelegate( request );
    assertEquals( 1, eventLog.size() );
    assertTrue( eventLog.get( 0 ) instanceof Event );
  }
  
  public void testEventProcessingOnSessionRestart() throws Exception {
    TestRequest request;
    Class entryPoint = EventProcessingOnSessionRestartEntryPoint.class;
    EntryPointManager.register( EntryPointManager.DEFAULT, entryPoint );
    // send initial request - response is index.html
    request = newRequest();
    request.setParameter( RequestParams.STARTUP, "default" );
    runRWTDelegate( request );
    assertTrue( createUIEntered );
    assertFalse( createUIExited );
    // send 'application startup' request - response is JavaScript to create
    // client-side representation of what was created in IEntryPoint#createUI
    request = newRequest();
    request.setParameter( RequestParams.UIROOT, "w1" );
    runRWTDelegate( request );
    assertTrue( createUIEntered );
    assertFalse( createUIExited );
    // send 'restart' request
    request = newRequest();
    request.setParameter( RequestParams.STARTUP, "default" );
    runRWTDelegate( request );
    assertTrue( createUIExited );
    assertEquals( 1, eventLog.size() );
  }

  /*
   * Bug 225167: [Display] dispose() causes an IllegalStateException (The 
   *             context has been disposed)
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=225167
   */
  public void testSessionInvalidateWithDisposeInFinally() throws Exception {
    TestRequest request;
    Class clazz = TestSessionInvalidateWithDisposeInFinallyEntryPoint.class;
    EntryPointManager.register( EntryPointManager.DEFAULT, clazz );
    // send initial request - response is index.html
    request = newRequest();
    request.setParameter( RequestParams.STARTUP, "default" );
    runRWTDelegate( request );
    assertTrue( createUIEntered );
    // send 'application startup' request - response is JavaScript to create
    // client-side representation of what was created in IEntryPoint#createUI
    request = newRequest();
    request.setParameter( RequestParams.UIROOT, "w1" );
    runRWTDelegate( request );
    assertTrue( createUIEntered );
    assertFalse( createUIExited );
    // send 'restart' request
    request = newRequest();
    request.setParameter( RequestParams.STARTUP, "default" );
    runRWTDelegate( request );
    assertTrue( createUIExited );
    assertEquals( PhaseId.PROCESS_ACTION, currentPhase );
    assertEquals( 0, eventLog.size() );
  }
  
  private static TestResponse runRWTDelegate( final HttpServletRequest request ) 
    throws Exception 
  {
    final Exception[] exception = { null };
    final TestResponse[] response = { newResponse() };
    Runnable runnable = new Runnable() {
      public void run() {
        synchronized( this ) {
          //
        }
        try {
          RWTDelegate delegate = new RWTDelegate();
          delegate.doPost( request, response[ 0 ] );
        } catch( Exception e ) {
          exception[ 0 ] = e;
        } 
      }
    };
    Thread thread = new Thread( runnable );
    thread.setDaemon( true );
    thread.setName( "Fake Request Thread" );
    synchronized( runnable ) {
      thread.start();
    }
    thread.join();
    if( exception[ 0 ] != null ) {
      throw exception[ 0 ];
    }
    return response[ 0 ];
  }

  private static TestResponse newResponse() {
    TestResponse result = new TestResponse();
    TestServletOutputStream outputStream = new TestServletOutputStream();
    result.setOutputStream( outputStream );
    return result;
  }
  
  private TestRequest newRequest() {
    TestRequest result = new TestRequest();
    result.setSession( session );
    result.setParameter( LifeCycleServiceHandler.RWT_INITIALIZE, "true" );
    return result;
  }
  
  protected void setUp() throws Exception {
    maliciousButtonId = null;
    createUIEntered = false;
    createUIExited = false;
    bufferedConfigurer = StartupPage.configurer;
    eventLog = new ArrayList();
    StartupPage.configurer = new RWTStartupPageConfigurer();
    Fixture.clearSingletons();
    System.setProperty( IInitialization.PARAM_LIFE_CYCLE, 
                        RWTLifeCycle.class.getName() );
    ThemeManager.getInstance().initialize();
    Fixture.registerResourceManager();
    PhaseListenerRegistry.add( new PreserveWidgetsPhaseListener() );
    PhaseListenerRegistry.add( new CurrentPhase.Listener() );
    AdapterFactoryRegistry.add( LifeCycleAdapterFactory.class,
                                Widget.class );
    AdapterFactoryRegistry.add( LifeCycleAdapterFactory.class,
                                Display.class );
    session = new TestSession();
    ServletContext servletContext = session.getServletContext();
    TestServletContext servletContextImpl
      = ( TestServletContext )servletContext;
    servletContextImpl.setLogger( new TestLogger() {
      public void log( final String message, final Throwable throwable ) {
        System.err.println( message );
        if( throwable != null ) {
          throwable.printStackTrace();
        }
      }
    } );
  }

  protected void tearDown() throws Exception {
    session.invalidate();
    session = null;
    AdapterFactoryRegistry.clear();
    Fixture.deregisterResourceManager();
    ResourceFactory.clear();
    // remove all registered PhaseListener
    PhaseListenerRegistry.clear();
    // remove all registered entry points
    String[] entryPoints = EntryPointManager.getEntryPoints();
    for( int i = 0; i < entryPoints.length; i++ ) {
      EntryPointManager.deregister( entryPoints[ i ] );
    }
    LifeCycleFactory.destroy();
    Fixture.clearSingletons();
    StartupPage.configurer = bufferedConfigurer;
  }
}
