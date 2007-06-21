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

package org.eclipse.swt.events;

import java.io.IOException;

import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;

import com.w4t.Fixture;
import com.w4t.engine.lifecycle.PhaseId;
import com.w4t.engine.requests.RequestParams;


public class UntypedEvents_Test extends TestCase {
  
  private static final String WIDGET_SELECTED = "widgetSelected";
  private static final String WIDGET_DEFAULT_SELECTED = "widgetSelected";
  private String log = "";
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testUntypedEventInvocation() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    final Widget widget = new Button( shell, SWT.PUSH );
    
    Listener listener = new Listener() {
      public void handleEvent( final Event event ) {
        assertSame( widget, event.widget );
        assertNull( event.item );
        assertEquals( 10, event.x );
        assertEquals( 20, event.y );
        assertEquals( 30, event.width );
        assertEquals( 40, event.height );
        assertEquals( true, event.doit );
        log += WIDGET_SELECTED;
      }
    };
    widget.addListener( SWT.Selection, listener );
    SelectionEvent event = new SelectionEvent( widget,
                                               null,
                                               SelectionEvent.WIDGET_SELECTED,
                                               new Rectangle( 10, 20, 30, 40 ),
                                               null,
                                               true,
                                               SWT.NONE );
    event.processEvent();
    assertEquals( WIDGET_SELECTED, log );
    widget.removeListener( SWT.Selection, listener );
    
    log = "";
    listener = new Listener() {
      public void handleEvent( final Event event ) {
        assertSame( widget, event.widget );
        assertNull( event.item );
        assertEquals( 10, event.x );
        assertEquals( 20, event.y );
        assertEquals( 30, event.width );
        assertEquals( 40, event.height );
        assertEquals( true, event.doit );
        log += WIDGET_SELECTED;
      }
    };
    widget.addListener( SWT.DefaultSelection, listener );
    event = new SelectionEvent( widget,
                                null,
                                SelectionEvent.WIDGET_DEFAULT_SELECTED,
                                new Rectangle( 10, 20, 30, 40 ),
                                null,
                                true,
                                SWT.NONE );
    event.processEvent();
    assertEquals( WIDGET_DEFAULT_SELECTED, log );
    widget.removeListener( SWT.DefaultSelection, listener );
  }
  
  public void testFilter() throws IOException {
    final boolean[] executed = new boolean[ 1 ];
    Display display = new Display();
    display.addFilter( SWT.Selection, new Listener() {
      public void handleEvent( final Event event ) {
        event.type = SWT.None;
        executed[ 0 ] = true;
      }
    } );
    Shell shell = new Shell( display, SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        throw new RuntimeException( "This should never be called." );
      }
    } );
    String displayId = DisplayUtil.getId( display );
    String buttonId = WidgetUtil.getId( button );
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_ACTIVATED, buttonId );
    
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    lifeCycle.execute();
    assertTrue( executed[ 0 ] );
  }

}
