/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.displaykit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rwt.AdapterFactory;
import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.*;
import org.eclipse.rwt.internal.browser.Ie6up;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.widgets.*;


public class DisplayLCA_Test extends TestCase {

  private AdapterFactory lifeCycleAdapterFactory;
  private WidgetAdapterFactory widgetAdapterFactory;
  private final List log = new ArrayList();
  private final List renderInitLog = new ArrayList();
  private final List renderChangesLog = new ArrayList();
  private final List renderDisposeLog = new ArrayList();

  private final class DisposeTestButton extends Button {

    public DisposeTestButton( final Composite parent, final int style ) {
      super( parent, style );
    }
  }

  public void testPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display );
    new Button( shell, SWT.PUSH );
    RWTFixture.markInitialized( display );
    shell.setFocus();
    shell.open();
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    assertEquals( shell, adapter.getPreserved( DisplayLCA.PROP_FOCUS_CONTROL ) );
    Object currTheme = adapter.getPreserved( DisplayLCA.PROP_CURR_THEME );
    assertEquals( ThemeUtil.getCurrentThemeId(), currTheme );
    Object exitConf = adapter.getPreserved( DisplayLCA.PROP_EXIT_CONFIRMATION );
    assertNull( exitConf );
    display.dispose();
  }

  public void testStartup() throws IOException {
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6up( true, true ) );
    Display display = new Display();
    Object adapter = display.getAdapter( ILifeCycleAdapter.class );
    IDisplayLifeCycleAdapter lcAdapter = ( IDisplayLifeCycleAdapter )adapter;
    // first request: render html to load JavaScript "application"
    lcAdapter.render( display );
    String allMarkup = Fixture.getAllMarkup();
    assertTrue( allMarkup.indexOf( "<html" ) != -1 );
    assertTrue( allMarkup.indexOf( "<body" ) != -1 );
    String expected = "var req = org.eclipse.swt.Request.getInstance();";
    assertTrue( allMarkup.indexOf( expected ) != -1 );
    assertTrue( allMarkup.indexOf( "req.setUIRootId( \"w1\" )" ) != -1 );
  }

  public void testRenderProcessing() throws IOException {
    Fixture.fakeResponseWriter();
    // fake request param to simulate second request
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    Display display = new Display();
    Composite shell1 = new Shell( display , SWT.NONE );
    Button button1 = new Button( shell1, SWT.PUSH );
    Composite shell2 = new Shell( display , SWT.NONE );
    Button button2 = new Button( shell2, SWT.PUSH );
    Object adapter = display.getAdapter( ILifeCycleAdapter.class );
    IDisplayLifeCycleAdapter lcAdapter = ( IDisplayLifeCycleAdapter )adapter;
    lcAdapter.render( display );
    assertEquals( 4, log.size() );
    assertSame( shell1, log.get( 0 ) );
    assertSame( button1, log.get( 1 ) );
    assertSame( shell2, log.get( 2 ) );
    assertSame( button2, log.get( 3 ) );
    clearLogs();
    new Composite( shell1, SWT.NONE );
    try {
      lcAdapter.render( display );
      String msg = "IOException of the renderer adapter in case of composite"
                 + "should be rethrown.";
      fail( msg );
    } catch( final IOException ioe ) {
      // expected
    }
    assertEquals( 2, log.size() );
    assertSame( shell1, log.get( 0 ) );
    assertSame( button1, log.get( 1 ) );
  }

  public void testReadDataProcessing() {
    Fixture.fakeResponseWriter();
    // fake request param to simulate second request
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    Text text = new Text( shell, SWT.NONE );
    Object adapter = display.getAdapter( ILifeCycleAdapter.class );
    IDisplayLifeCycleAdapter lcAdapter = ( IDisplayLifeCycleAdapter )adapter;
    lcAdapter.readData( display );
    assertEquals( 3, log.size() );
    assertSame( shell, log.get( 0 ) );
    assertSame( button, log.get( 1 ) );
    assertSame( text, log.get( 2 ) );
  }

  public void testReadData() {
    Display display = new Display();
    IDisplayLifeCycleAdapter lca = DisplayUtil.getLCA( display );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".bounds.width", "30" );
    Fixture.fakeRequestParam( displayId + ".bounds.height", "70" );
    lca.readData( display );
    assertEquals( new Rectangle( 0, 0, 30, 70 ), display.getBounds() );
  }
  
  public void testRenderChangedButDisposed() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Button button = new DisposeTestButton( shell, SWT.PUSH );
    final Button button2 = new DisposeTestButton( shell, SWT.PUSH );
    final Button button3 = new DisposeTestButton( shell, SWT.CHECK );

    String displayId = DisplayUtil.getId( display );
    String buttonId = WidgetUtil.getId( button );

    // Run requests to initialize the 'system'
    RWTFixture.fakeNewRequest();
    RWTFixture.executeLifeCycleFromServerThread( );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    RWTFixture.executeLifeCycleFromServerThread( );
    
    // Run the actual test request: the button is clicked
    // It changes its text and disposes itself
    clearLogs();
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        button.setText( "should be ignored" );
        button.dispose();
      }
    } );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    RWTFixture.executeLifeCycleFromServerThread( );
    
    assertEquals( 0, renderInitLog.size() );
    assertFalse( renderChangesLog.contains( button ) );
    assertTrue( renderDisposeLog.contains( button ) );

    clearLogs();
    button2.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        button2.setText( "should be ignored" );
        button2.dispose();
      }
    } );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    RWTFixture.executeLifeCycleFromServerThread( );

    assertEquals( 0, renderInitLog.size() );
    assertFalse( renderChangesLog.contains( button2 ) );
    assertTrue( renderDisposeLog.contains( button2 ) );

    clearLogs();
    button3.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        button3.setText( "should be ignored" );
        button3.dispose();
      }
    } );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    RWTFixture.executeLifeCycleFromServerThread( );

    assertEquals( 0, renderInitLog.size() );
    assertFalse( renderChangesLog.contains( button3 ) );
    assertTrue( renderDisposeLog.contains( button3 ) );
  }

  public void testIsInitializedState() throws IOException {
    final Boolean[] compositeInitState = new Boolean[] { null };
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Composite composite = new Shell( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    WidgetAdapter controlAdapter
      = ( WidgetAdapter )WidgetUtil.getAdapter( control );
    controlAdapter.setRenderRunnable( new IRenderRunnable() {
      public void afterRender() throws IOException {
        boolean initState = WidgetUtil.getAdapter( composite ).isInitialized();
        compositeInitState[ 0 ] = Boolean.valueOf( initState );
      }
    } );

    // Ensure that the isInitialized state is to to true *right* after a widget
    // was rendered; as opposed to being set to true after the whole widget
    // tree was rendered
    Fixture.fakeResponseWriter();
    String displayId = DisplayUtil.getId( display );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    // check precondition
    assertEquals( false, WidgetUtil.getAdapter( composite ).isInitialized() );
    IDisplayLifeCycleAdapter displayLCA = DisplayUtil.getLCA( display );
    displayLCA.render( display );
    assertEquals( Boolean.TRUE, compositeInitState[ 0 ] );
  }
  
  public void testFocusControl() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Control control = new Button( shell, SWT.PUSH );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    String controlId = WidgetUtil.getId( control );

    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".focusControl", controlId );
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( control, display.getFocusControl() );

    // Request parameter focusControl with value 'null' is ignored
    Control previousFocusControl = display.getFocusControl();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".focusControl", "null" );
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( previousFocusControl, display.getFocusControl() );
  }

  public void testResizeMaximizedShells() {
    Display display = new Display();
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    displayAdapter.setBounds( new Rectangle( 0, 0, 800, 600 ) );
    Shell shell1 = new Shell( display, SWT.NONE );
    shell1.setBounds( 0, 0, 800, 600 );
    Shell shell2 = new Shell( display, SWT.NONE );
    shell2.setBounds( 0, 0, 300, 400 );
    shell2.setMaximized( true );
    // fake display resize
    IDisplayLifeCycleAdapter lca = DisplayUtil.getLCA( display );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".bounds.width", "700" );
    Fixture.fakeRequestParam( displayId + ".bounds.height", "500" );
    lca.readData( display );
    // shell1 is not resized although it has the same size as the display
    assertEquals( new Rectangle( 0, 0, 800, 600 ), shell1.getBounds() );
    // shell2 is resized because it's maximized
    assertEquals( new Rectangle( 0, 0, 700, 500 ), shell2.getBounds() );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    System.setProperty( IInitialization.PARAM_LIFE_CYCLE, 
                        RWTLifeCycle.class.getName() );
    ThemeManager.getInstance().initialize();
    AdapterManager manager = AdapterManagerImpl.getInstance();
    lifeCycleAdapterFactory = new AdapterFactory() {

      private AdapterFactory factory = new LifeCycleAdapterFactory();

      public Object getAdapter( final Object adaptable, final Class adapter ) {
        Object result = null;
        if( adaptable instanceof Display && adapter == ILifeCycleAdapter.class )
        {
          result = factory.getAdapter( adaptable, adapter );
        } else {
          result = new AbstractWidgetLCA() {

            public void preserveValues( final Widget widget ) {
            }

            public void readData( final Widget widget ) {
              log.add( widget );
              if( widget instanceof DisposeTestButton ) {
                SelectionEvent event
                  = new SelectionEvent( widget,
                                        null,
                                        SelectionEvent.WIDGET_SELECTED );
                event.processEvent();
              }
            }

            public void renderInitialization( final Widget widget )
              throws IOException
            {
              renderInitLog.add( widget );
            }

            public void renderChanges( final Widget widget ) throws IOException
            {
              if( widget.getClass().equals( Composite.class ) ) {
                throw new IOException();
              }
              log.add( widget );
              renderChangesLog.add( widget );
            }


            public void renderDispose( final Widget widget ) throws IOException
            {
              renderDisposeLog.add( widget );
            }
          };
        }
        return result;
      }

      public Class[] getAdapterList() {
        return factory.getAdapterList();
      }
    };
    manager.registerAdapters( lifeCycleAdapterFactory, Display.class );
    manager.registerAdapters( lifeCycleAdapterFactory, Widget.class );
    widgetAdapterFactory = new WidgetAdapterFactory();
    manager.registerAdapters( widgetAdapterFactory, Display.class );
    manager.registerAdapters( widgetAdapterFactory, Widget.class );
    clearLogs();
    RWTFixture.registerResourceManager();
    PhaseListenerRegistry.add( new CurrentPhase.Listener() );
    PhaseListenerRegistry.add( new PreserveWidgetsPhaseListener() );
  }

  protected void tearDown() throws Exception {
// TODO [rst] Keeping the ThemeManager initialized speeds up TestSuite
//    ThemeManager.getInstance().deregisterAll();
    AdapterManager manager = AdapterManagerImpl.getInstance();
    manager.deregisterAdapters( lifeCycleAdapterFactory, Display.class );
    manager.deregisterAdapters( lifeCycleAdapterFactory, Widget.class );
    manager.deregisterAdapters( widgetAdapterFactory, Display.class );
    manager.deregisterAdapters( widgetAdapterFactory, Widget.class );
    RWTFixture.deregisterResourceManager();
    Fixture.tearDown();
  }

  private void clearLogs() {
    log.clear();
    renderInitLog.clear();
    renderChangesLog.clear();
    renderDisposeLog.clear();
  }
}
