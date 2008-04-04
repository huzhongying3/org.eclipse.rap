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
package org.eclipse.swt.events;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class MouseEvent_Test extends TestCase {

  private static final String MOUSE_DOWN = "mouseDown|";
  private static final String MOUSE_UP = "mouseUp|";
  private static final String MOUSE_DOUBLE_CLICK = "mouseDoubleClick|";

  private String log;

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    log = "";
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testAddRemoveListener() {
    MouseListener listener = new MouseListener() {
      public void mouseDoubleClick( MouseEvent e ) {
        log += MOUSE_DOUBLE_CLICK;
      }
      public void mouseDown( MouseEvent e ) {
        log += MOUSE_DOWN;
      }
      public void mouseUp( MouseEvent e ) {
        log += MOUSE_UP;
      }
    };
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    MouseEvent.addListener( shell, listener );

    MouseEvent event;
    event = new MouseEvent( shell, MouseEvent.MOUSE_DOWN );
    event.processEvent();
    event = new MouseEvent( shell, MouseEvent.MOUSE_UP );
    event.processEvent();
    event = new MouseEvent( shell, MouseEvent.MOUSE_DOUBLE_CLICK );
    event.processEvent();
    assertEquals( MOUSE_DOWN + MOUSE_UP + MOUSE_DOUBLE_CLICK, log );

    log = "";
    MouseEvent.removeListener( shell, listener );
    event = new MouseEvent( shell, MouseEvent.MOUSE_DOWN );
    event.processEvent();
    assertEquals( "", log );
  }

  public void testTypedMouseEventOrder() {
    final java.util.List events = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setLocation( 100, 100 );
    shell.open();
    shell.addMouseListener( new MouseListener() {
      public void mouseDoubleClick( final MouseEvent event ) {
        events.add( event );
      }
      public void mouseDown( final MouseEvent event ) {
        events.add( event );
      }
      public void mouseUp( final MouseEvent event ) {
        events.add( event );
      }
    } );
    String displayId = DisplayUtil.getId( display );
    String shellId = WidgetUtil.getId( shell );
    int shellX = shell.getLocation().x;
    int shellY = shell.getLocation().y;
    // Simulate request that sends a mouseDown + mouseUp sequence
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    fakeMouseDownRequest( shellId, shellX + 2, shellY + 2 );
    fakeMouseUpRequest( shellId, shellX + 2, shellY + 2 );
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( 2, events.size() );
    MouseEvent mouseEvent = ( ( MouseEvent )events.get( 0 ) );
    assertEquals( MouseEvent.MOUSE_DOWN, mouseEvent.getID() );
    assertSame( shell, mouseEvent.widget );
    assertEquals( 1, mouseEvent.button );
    assertEquals( 2, mouseEvent.x );
    assertEquals( 2, mouseEvent.y );
    mouseEvent = ( ( MouseEvent )events.get( 1 ) );
    assertEquals( MouseEvent.MOUSE_UP, mouseEvent.getID() );
    assertSame( shell, mouseEvent.widget );
    assertEquals( 1, mouseEvent.button );
    assertEquals( 2, mouseEvent.x );
    assertEquals( 2, mouseEvent.y );
    // Simulate request that sends a mouseDown + mouseUp + dblClick sequence
    events.clear();
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    fakeMouseDownRequest( shellId, shellX + 2, shellY + 2 );
    fakeMouseUpRequest( shellId, shellX + 2, shellY + 2 );
    fakeMouseDoubleClickRequest( shellId, shellX + 2, shellY + 2 );
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( 3, events.size() );
    mouseEvent = ( ( MouseEvent )events.get( 0 ) );
    assertEquals( MouseEvent.MOUSE_DOWN, mouseEvent.getID() );
    assertSame( shell, mouseEvent.widget );
    assertEquals( 1, mouseEvent.button );
    assertEquals( 2, mouseEvent.x );
    assertEquals( 2, mouseEvent.y );
    mouseEvent = ( ( MouseEvent )events.get( 1 ) );
    assertEquals( MouseEvent.MOUSE_DOUBLE_CLICK, mouseEvent.getID() );
    assertSame( shell, mouseEvent.widget );
    assertEquals( 1, mouseEvent.button );
    assertEquals( 2, mouseEvent.x );
    assertEquals( 2, mouseEvent.y );
    mouseEvent = ( ( MouseEvent )events.get( 2 ) );
    assertEquals( MouseEvent.MOUSE_UP, mouseEvent.getID() );
    assertSame( shell, mouseEvent.widget );
    assertEquals( 1, mouseEvent.button );
    assertEquals( 2, mouseEvent.x );
    assertEquals( 2, mouseEvent.y );
  }

  public void testUntypedMouseEventOrder() {
    final java.util.List events = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setLocation( 100, 100 );
    shell.open();
    shell.addListener( SWT.MouseDown, new Listener() {
      public void handleEvent( final Event event ) {
        events.add( event );
      }
    } );
    shell.addListener( SWT.MouseUp, new Listener() {
      public void handleEvent( final Event event ) {
        events.add( event );
      }
    } );
    shell.addListener( SWT.MouseDoubleClick, new Listener() {
      public void handleEvent( final Event event ) {
        events.add( event );
      }
    } );
    String displayId = DisplayUtil.getId( display );
    String shellId = WidgetUtil.getId( shell );
    int shellX = shell.getLocation().x;
    int shellY = shell.getLocation().y;
    // Simulate request that sends a mouseDown + mouseUp sequence
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    fakeMouseDownRequest( shellId, shellX + 2, shellY + 2 );
    fakeMouseUpRequest( shellId, shellX + 2, shellY + 2 );
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( 2, events.size() );
    Event mouseEvent = ( ( Event )events.get( 0 ) );
    assertEquals( SWT.MouseDown, mouseEvent.type );
    assertSame( shell, mouseEvent.widget );
    assertEquals( 1, mouseEvent.button );
    assertEquals( 2, mouseEvent.x );
    assertEquals( 2, mouseEvent.y );
    mouseEvent = ( ( Event )events.get( 1 ) );
    assertEquals( SWT.MouseUp, mouseEvent.type );
    assertSame( shell, mouseEvent.widget );
    assertEquals( 1, mouseEvent.button );
    assertEquals( 2, mouseEvent.x );
    assertEquals( 2, mouseEvent.y );
    // Simulate request that sends a mouseDown + mouseUp + dblClick sequence
    events.clear();
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    fakeMouseDownRequest( shellId, shellX + 2, shellY + 2 );
    fakeMouseUpRequest( shellId, shellX + 2, shellY + 2 );
    fakeMouseDoubleClickRequest( shellId, shellX + 2, shellY + 2 );
    RWTFixture.executeLifeCycleFromServerThread();
    assertEquals( 3, events.size() );
    mouseEvent = ( ( Event )events.get( 0 ) );
    assertEquals( SWT.MouseDown, mouseEvent.type );
    assertSame( shell, mouseEvent.widget );
    assertEquals( 1, mouseEvent.button );
    assertEquals( 2, mouseEvent.x );
    assertEquals( 2, mouseEvent.y );
    mouseEvent = ( ( Event )events.get( 1 ) );
    assertEquals( SWT.MouseDoubleClick, mouseEvent.type );
    assertSame( shell, mouseEvent.widget );
    assertEquals( 1, mouseEvent.button );
    assertEquals( 2, mouseEvent.x );
    assertEquals( 2, mouseEvent.y );
    mouseEvent = ( ( Event )events.get( 2 ) );
    assertEquals( SWT.MouseUp, mouseEvent.type );
    assertSame( shell, mouseEvent.widget );
    assertEquals( 1, mouseEvent.button );
    assertEquals( 2, mouseEvent.x );
    assertEquals( 2, mouseEvent.y );
  }

  private static void fakeMouseDoubleClickRequest( final String shellId,
                                                   final int x,
                                                   final int y )
  {
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOUBLE_CLICK, shellId );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOUBLE_CLICK_BUTTON, "1" );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOUBLE_CLICK_X,
                              String.valueOf( x ) );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOUBLE_CLICK_Y,
                              String.valueOf( y ) );
  }

  private static void fakeMouseUpRequest( final String shellId, 
                                          final int x, 
                                          final int y ) {
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_UP, shellId );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_UP_BUTTON, "1" );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_UP_X, String.valueOf( x ) );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_UP_Y, String.valueOf( y ) );
  }

  private static void fakeMouseDownRequest( final String shellId, 
                                            final int x, 
                                            final int y ) {
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOWN, shellId );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOWN_BUTTON, "1" );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOWN_X, String.valueOf( x ) );
    Fixture.fakeRequestParam( JSConst.EVENT_MOUSE_DOWN_Y, String.valueOf( y ) );
  }
}
