/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.displaykit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;


/*
 * Put in separate class becase this test does not share the same setUp/tearDown 
 * as the tests in DisplayLCA_Test. 
 */
public class DisplayLCAFocus_Test extends TestCase {

  public void testUnchangedFocus() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    shell.setSize( 400, 400 );
    Button button1 = new Button( shell, SWT.PUSH );
    shell.setLayout( new FillLayout() );
    shell.layout();
    shell.open();
    
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
    String displayId = DisplayUtil.getId( display );
    String button1Id = WidgetUtil.getId( button1 );

    // Simulate initial request that constructs UI
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.executeLifeCycleFromServerThread();
    
    // Simulate request that is sent when button was pressed
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".focusControl", button1Id );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, button1Id );
    Fixture.executeLifeCycleFromServerThread();
    
    assertEquals( -1, Fixture.getAllMarkup().indexOf( "focus" ) );
  }
  
  /* Test case for https://bugs.eclipse.org/bugs/show_bug.cgi?id=196911 */
  public void testSetFocusToClientSideFocusedControl() {
    final Shell[] childShell = { null };
    Display display = new Display();
    final Shell shell = new Shell( display, SWT.NONE );
    shell.setSize( 400, 400 );
    final Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        childShell[ 0 ] = new Shell( shell, SWT.NONE );
        childShell[ 0 ].setBounds( 0, 0, 100, 100 );
        childShell[ 0 ].open();
        button.setFocus();
      }
    } );
    shell.setLayout( new FillLayout() );
    shell.layout();
    shell.open();
    
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
    String displayId = DisplayUtil.getId( display );
    String buttonId = WidgetUtil.getId( button );

    // Simulate initial request that constructs UI
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.executeLifeCycleFromServerThread( );
    
    // Simulate request that is sent when button was pressed
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".focusControl", buttonId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.executeLifeCycleFromServerThread( );
    
    // ensure that widgetSelected was called
    assertNotNull( childShell[ 0 ] );
    String markup = Fixture.getAllMarkup();
    String expected 
      = "org.eclipse.swt.WidgetManager.getInstance().focus( \"" 
      + buttonId
      + "\" );";
    assertTrue( markup.indexOf( expected ) != -1 );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
