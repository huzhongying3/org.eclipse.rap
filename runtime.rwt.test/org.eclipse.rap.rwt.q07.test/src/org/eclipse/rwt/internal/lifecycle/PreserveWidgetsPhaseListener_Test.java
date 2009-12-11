/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;

import javax.servlet.ServletContextEvent;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.*;
import org.eclipse.rwt.internal.browser.Ie6;
import org.eclipse.rwt.internal.browser.Ie6up;
import org.eclipse.rwt.internal.engine.RWTServletContextListener;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class PreserveWidgetsPhaseListener_Test extends TestCase {

  public static final class TestEntryPointWithShell implements IEntryPoint {
    public int createUI() {
      Display display = new Display();
      new Shell( display, SWT.NONE );
      int count = 0;
      while( count < 1 ) {
        if( !display.readAndDispatch() ) {
          display.sleep();
        }
        count++;
      }
      return 0;
    }
  }
  
  protected void setUp() throws Exception {
    System.setProperty( IInitialization.PARAM_LIFE_CYCLE,
                        RWTLifeCycle.class.getName() );
    Fixture.fakeContext();
    Fixture.fakeNewRequest();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testInitialization() throws Exception {
    // ensures that the default WidgetCopyPhaseListener is registered
    // and executes at the designated phases
    Fixture.fakeBrowser( new Ie6up( true, true ) );
    Fixture.fakeResponseWriter();
    Fixture.registerAdapterFactories();
    Fixture.createContextWithoutResourceManager();
    RWTServletContextListener listener = new RWTServletContextListener();
    TestServletContext servletContext = new TestServletContext();
    listener.contextInitialized( new ServletContextEvent( servletContext ) );
    Fixture.deregisterResourceManager();
    Fixture.registerResourceManager();
    ThemeManager.getInstance().initialize();
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    final Text text = new Text( shell, SWT.NONE );
    text.setText( "hello" );
    Fixture.markInitialized( display );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    final StringBuffer log = new StringBuffer();
    lifeCycle.addPhaseListener( new PhaseListener() {

      private static final long serialVersionUID = 1L;

      public void beforePhase( final PhaseEvent event ) {
        if( PhaseId.PROCESS_ACTION.equals( event.getPhaseId() ) ) {
          IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
          if( "hello".equals( adapter.getPreserved( Props.TEXT ) ) ) {
            log.append( "copy created" );
          }
        }
      }

      public void afterPhase( final PhaseEvent event ) {
      }

      public PhaseId getPhaseId() {
        return PhaseId.ANY;
      }
    } );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( "copy created", log.toString() );
    // clean up
    Fixture.removeContext();
  }

  public void testExecutionOrder() {
    // Build test environment:
    final StringBuffer log = new StringBuffer();
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6up( true, true ) );
    PhaseListenerRegistry.add( new PreserveWidgetsPhaseListener() );
    AdapterFactory lifeCycleAdapterFactory = new AdapterFactory() {

      private AdapterFactory factory = new LifeCycleAdapterFactory();

      public Object getAdapter( final Object adaptable, final Class adapter ) {
        Object result = null;
        if( adaptable instanceof Display && adapter == ILifeCycleAdapter.class )
        {
          result = new IDisplayLifeCycleAdapter() {

            public void preserveValues( final Display display ) {
              log.append( display.getClass().getName() );
            }

            public void processAction( Device display ) {
            }

            public void readData( Display display ) {
            }

            public void render( Display display ) throws IOException {
            }
          };
        } else {
          result = new AbstractWidgetLCA() {

            public void preserveValues( final Widget widget ) {
              log.append( widget.getClass().getName() );
            }

            public void readData( final Widget widget ) {
            }

            public void renderInitialization( final Widget widget )
              throws IOException
            {
            }

            public void renderChanges( final Widget widget ) throws IOException
            {
            }

            public void renderDispose( final Widget widget ) throws IOException
            {
            }
          };
        }
        return result;
      }

      public Class[] getAdapterList() {
        return factory.getAdapterList();
      }
    };
    AdapterManager manager = AdapterManagerImpl.getInstance();
    manager.registerAdapters( lifeCycleAdapterFactory, Display.class );
    manager.registerAdapters( lifeCycleAdapterFactory, Widget.class );
    Fixture.registerResourceManager();
    // Create test widget hierarchy
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    new Text( shell, SWT.NONE );
    // Execute life cycle
    Fixture.markInitialized( display );
    Fixture.executeLifeCycleFromServerThread( );
    String expected = Display.class.getName()
                    + Shell.class.getName()
                    + Text.class.getName();
    assertEquals( expected, log.toString() );
  }

  public void testStartup() throws Exception {
    // Simulate startup with no startup entry point set
    // First request: (renders html skeletion that contains 'application')
    Fixture.createContextWithoutResourceManager();
    Fixture.fakeBrowser( new Ie6( true, true ) );
    Fixture.fakeResponseWriter();
    Fixture.registerResourceManager();
    ThemeManager.getInstance().initialize();
    Fixture.registerAdapterFactories();
    EntryPointManager.register( EntryPointManager.DEFAULT,
                                TestEntryPointWithShell.class );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
    lifeCycle.execute();
    // Second request: first 'real' one that writes JavaScript to create display
    Fixture.fakeResponseWriter();
    fakeUIRootRequestParam( RWTLifeCycle.getSessionDisplay() );
    lifeCycle.execute();
    assertTrue( Fixture.getAllMarkup().indexOf( "setSpace" ) != -1 );
  }
  
  public void testClearPreservedWithDisposedDisplay() {
    Fixture.fakePhase( PhaseId.RENDER );
    Display display = new Display();
    display.dispose();
    try {
      PreserveWidgetsPhaseListener.clearPreserved( display );
    } catch( Exception e ) {
      fail( "Preserve-phase-listener must succeed even with disposed display" );
    }
  }

  private static void fakeUIRootRequestParam( final Display display ) {
    Object adapter = display.getAdapter( IWidgetAdapter.class );
    IWidgetAdapter displayAdapter = ( IWidgetAdapter )adapter;
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayAdapter.getId() );
  }
}
