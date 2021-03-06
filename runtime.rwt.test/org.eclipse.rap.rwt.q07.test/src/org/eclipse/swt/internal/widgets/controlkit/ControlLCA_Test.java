/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.controlkit;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;


public class ControlLCA_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUpWithoutResourceManager();
    ThemeManager.getInstance().initialize();
  }

  protected void tearDown() throws Exception {
// TODO [rst] Keeping the ThemeManager initialized speeds up TestSuite
//    ThemeManager.getInstance().deregisterAll();
    Fixture.tearDown();
  }

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Control control = new Button( shell, SWT.PUSH );
    Boolean hasListeners;
    Fixture.markInitialized( display );
    //bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    control.setBounds( rectangle );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    //z-index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    //visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    control.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    //enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    control.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ));
    Fixture.clearPreserved();
    control.setEnabled( true );
    //control_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    control.addControlListener( new ControlAdapter() { } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    hasListeners = ( Boolean ) adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    control.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    control.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    control.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    //tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    //tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( null, control.getToolTipText() );
    Fixture.clearPreserved();
    control.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( "some text", control.getToolTipText() );
    Fixture.clearPreserved();
    //activate_listeners   Focus_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    control.addFocusListener( new FocusAdapter() { } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    hasListeners = ( Boolean ) adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    ActivateEvent.addListener( control, new ActivateAdapter() { } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    hasListeners = ( Boolean ) adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    display.dispose();
  }

  public void testWriteVisibility() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    button.setSize( 10, 10 );
    shell.open();
    AbstractWidgetLCA lca = WidgetUtil.getLCA( button );

    // Initial JavaScript code must not contain setVisibility()
    Fixture.fakeResponseWriter();
    lca.renderInitialization( button );
    lca.renderChanges( button );
    assertTrue( Fixture.getAllMarkup().indexOf( "setVisibility" ) == -1 );

    // Unchanged visible attribute must not be rendered
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );
    Fixture.preserveWidgets();
    lca.renderInitialization( button );
    lca.renderChanges( button );
    assertTrue( Fixture.getAllMarkup().indexOf( "setVisibility" ) == -1 );

    // Changed visible attribute must not be rendered
    Fixture.fakeResponseWriter();
    Fixture.preserveWidgets();
    button.setVisible( false );
    lca.renderInitialization( button );
    lca.renderChanges( button );
    assertTrue( Fixture.getAllMarkup().indexOf( "setVisibility" ) != -1 );
  }

  public void testWriteBounds() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Control control = new Button( shell, SWT.PUSH );
    Composite parent = control.getParent();

    // call writeBounds once to elimniate the uninteresting JavaScript prolog
    Fixture.fakeResponseWriter();
    WidgetLCAUtil.writeBounds( control, parent, control.getBounds() );

    // Test without clip
    Fixture.fakeResponseWriter();
    control.setBounds( 1, 2, 100, 200 );
    WidgetLCAUtil.writeBounds( control, parent, control.getBounds() );
    String expected = "w.setSpace( 1, 100, 2, 200 );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testWriteFocusListener() throws IOException {
    FocusAdapter focusListener = new FocusAdapter() {
    };
    Display display = new Display();
    Shell shell = new Shell( display );
    Label label = new Label( shell, SWT.NONE );
    label.addFocusListener( focusListener );
    Button button = new Button( shell, SWT.PUSH );
    button.addFocusListener( focusListener );
    // Test that JavaScript focus listeners are rendered for a focusable control
    // (e.g. button)
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeChanges( button ); // calls writeFocusListener
    String focusGained = "org.eclipse.swt.EventUtil.focusGained";
    String focusLost = "org.eclipse.swt.EventUtil.focusLost";
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( focusGained ) != -1 );
    assertTrue( markup.indexOf( focusLost ) != -1 );

    // Test that for a non-focusable control (e.g. label), no focus-listener
    // JavaScript code is emitted
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeChanges( label );
    markup = Fixture.getAllMarkup();
    assertEquals( -1, markup.indexOf( focusGained ) );
    assertEquals( -1, markup.indexOf( focusLost ) );
  }

  public void testMenuDetectListener() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display );
    Label label = new Label( shell, SWT.NONE );
    final java.util.List log = new ArrayList();
    label.addMenuDetectListener( new MenuDetectListener() {
      public void menuDetected( final MenuDetectEvent event ) {
        log.add( event );
      }
    });
    String displayId = DisplayUtil.getId( display );
    String labelId = WidgetUtil.getId( label );
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_MENU_DETECT, labelId );
    Fixture.fakeRequestParam( JSConst.EVENT_MENU_DETECT_X, "10" );
    Fixture.fakeRequestParam( JSConst.EVENT_MENU_DETECT_Y, "30" );
    Fixture.executeLifeCycleFromServerThread();
    MenuDetectEvent event = ( MenuDetectEvent )log.get( 0 );
    assertSame( label, event.widget );
    assertEquals( 10, event.x );
    assertEquals( 30, event.y );
  }

  public void testRedrawAndDispose() {
    final StringBuffer log = new StringBuffer();
    // Set up test scenario
    Display display = new Display();
    Shell shell = new Shell( display );
    Control control = new Composite( shell, SWT.NONE ) {
      public Object getAdapter( final Class adapter ) {
        Object result;
        if( adapter == ILifeCycleAdapter.class ) {
          result = new AbstractWidgetLCA() {
            public void preserveValues( final Widget widget ) {
            }
            public void renderChanges( final Widget widget )
              throws IOException
            {
            }
            public void renderDispose( final Widget widget )
              throws IOException
            {
              log.append( "renderDispose" );
            }
            public void renderInitialization( final Widget widget )
              throws IOException
            {
            }
            public void readData( final Widget widget ) {
            }
            public void doRedrawFake( final Control control ) {
              log.append( "FAILED: doRedrawFake was called" );
            }
          };
        } else {
          result = super.getAdapter( adapter );
        }
        return result;
      }
    };
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( control );
    // redraw & dispose: must revoke redraw
    control.redraw();
    control.dispose();
    // run life cycle that (in this case) won't call doRedrawFake
    Fixture.fakeResponseWriter();
    String displayId = DisplayUtil.getId( display );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( "renderDispose", log.toString() );
  }
}
