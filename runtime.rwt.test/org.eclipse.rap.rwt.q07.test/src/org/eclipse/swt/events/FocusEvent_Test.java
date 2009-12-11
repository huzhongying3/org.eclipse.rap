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

package org.eclipse.swt.events;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class FocusEvent_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testFocusLost() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display );
    final Control unfocusControl = new Button( shell, SWT.PUSH );
    shell.open();
    unfocusControl.setFocus();
    unfocusControl.addFocusListener( new FocusAdapter() {
      public void focusLost( final FocusEvent event ) {
        log.append( "focusLost" );
        assertSame( unfocusControl, event.getSource() );
      }
      public void focusGained( final FocusEvent e ) {
        fail( "Unexpected event: focusGained" );
      }
    } );
    Control focusControl = new Button( shell, SWT.PUSH );
    String displayId = DisplayUtil.getId( display );
    String focusControlId = WidgetUtil.getId( focusControl );

    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".focusControl", focusControlId );
    Fixture.fakeRequestParam( "org.eclipse.swt.events.focusLost", 
                              focusControlId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( "focusLost", log.toString() );
  }
  
  public void testFocusGained() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.open();
    final Control control = new Button( shell, SWT.PUSH );
    control.addFocusListener( new FocusAdapter() {
      public void focusLost( final FocusEvent e ) {
        fail( "Unexpected event: focusLost" );
      }
      public void focusGained( final FocusEvent event ) {
        log.append( "focusGained" );
        assertSame( control, event.getSource() );
      }
    } );
    String displayId = DisplayUtil.getId( display );
    String controlId = WidgetUtil.getId( control );
    
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".focusControl", controlId );
    Fixture.fakeRequestParam( "org.eclipse.swt.events.focusGained", 
                              controlId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( "focusGained", log.toString() );
  }
}
