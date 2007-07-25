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

package org.eclipse.swt.internal.widgets.controlkit;

import java.io.IOException;
import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.internal.theme.ThemeManager;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;
import com.w4t.Fixture;
import com.w4t.util.browser.Mozilla1_7up;


public class ControlLCA_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUpWithoutResourceManager();
    RWTFixture.fakeUIThread();
    ThemeManager.getInstance().initialize();
  }

  protected void tearDown() throws Exception {
// TODO [rst] Keeping the ThemeManager initialized speeds up TestSuite
//    ThemeManager.getInstance().deregisterAll();
    RWTFixture.removeUIThread();
    RWTFixture.tearDown();
  }

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    ControlListener controlListener = new ControlListener() {
      public void controlMoved( final ControlEvent event ) {
      }
      public void controlResized( final ControlEvent event ) {
      }
    };
    shell.addControlListener( controlListener );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( shell );
    assertEquals( adapter.getPreserved( Props.BOUNDS ), shell.getBounds() );
    Boolean listeners;
    listeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, listeners );
  }

  public void testWriteVisibility() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    shell.open();
    AbstractWidgetLCA lca = WidgetUtil.getLCA( button );

    // Initial JavaScript code must not contain setVisibility()
    Fixture.fakeResponseWriter();
    lca.renderInitialization( button );
    lca.renderChanges( button );
    assertTrue( Fixture.getAllMarkup().indexOf( "setVisibility" ) == -1 );

    // Unchanged visible attribute must not be rendered
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( display );
    RWTFixture.markInitialized( button );
    RWTFixture.preserveWidgets();
    lca.renderInitialization( button );
    lca.renderChanges( button );
    assertTrue( Fixture.getAllMarkup().indexOf( "setVisibility" ) == -1 );

    // Changed visible attribute must not be rendered
    Fixture.fakeResponseWriter();
    RWTFixture.preserveWidgets();
    button.setVisible( false );
    lca.renderInitialization( button );
    lca.renderChanges( button );
    assertTrue( Fixture.getAllMarkup().indexOf( "setVisibility" ) != -1 );
  }

  public void testWriteBounds() throws IOException {
    Fixture.fakeBrowser( new Mozilla1_7up( true, true ) );
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Control control = new Button( shell, SWT.PUSH );
    Composite parent = control.getParent();

    // call writeBounds once to elimniate the uninteresting JavaScript prolog
    Fixture.fakeResponseWriter();
    WidgetLCAUtil.writeBounds( control, parent, control.getBounds(), false );

    // Test without clip
    Fixture.fakeResponseWriter();
    control.setBounds( 1, 2, 100, 200 );
    WidgetLCAUtil.writeBounds( control, parent, control.getBounds(), false );
    // TODO [fappel]: check whether minWidth and minHeight is still needed -
    //                causes problems on FF with caching
    // String expected
    //   = "w.setSpace( 1, 100, 2, 200 );"
    //   + "w.setMinWidth( 0 );w.setMinHeight( 0 );";
    String expected = "w.setSpace( 1, 100, 2, 200 );";
    assertEquals( expected, Fixture.getAllMarkup() );

    // Test with clip
    Fixture.fakeResponseWriter();
    control.setBounds( 1, 2, 100, 200 );
    WidgetLCAUtil.writeBounds( control, parent, control.getBounds(), true );
    // TODO [fappel]: check whether minWidth and minHeight is still needed -
    //                causes problems on FF with caching
    //expected
    //  =   "w.setSpace( 1, 100, 2, 200 );"
    //    + "w.setMinWidth( 0 );w.setMinHeight( 0 );"
    //    + "w.setClipHeight( 200 );w.setClipWidth( 100 );";
    expected
      =   "w.setSpace( 1, 100, 2, 200 );"
        + "w.setClipWidth( 100 );w.setClipHeight( 200 );";
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
}
