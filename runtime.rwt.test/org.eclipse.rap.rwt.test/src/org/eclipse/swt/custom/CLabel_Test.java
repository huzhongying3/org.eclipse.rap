/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.custom;

import junit.framework.TestCase;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class CLabel_Test extends TestCase {

  public void testSetBackgroundColor() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.SHELL_TRIM );
    CLabel label = new CLabel( shell, SWT.RIGHT );
    Color red = display.getSystemColor( SWT.COLOR_RED );
    label.setBackground( red );
    assertEquals( label.getBackground(), red );
  }

  public void testSetToolTipText() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.SHELL_TRIM );
    CLabel label = new CLabel( shell, SWT.RIGHT );
    label.setToolTipText( "foo" );
    assertEquals( label.getToolTipText(), "foo" );
  }

  public void testSetAlignment() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.SHELL_TRIM );
    CLabel label = new CLabel( shell, SWT.LEFT );
    assertEquals( label.getAlignment(), SWT.LEFT );
    label.setAlignment( SWT.RIGHT );
    assertEquals( label.getAlignment(), SWT.RIGHT );
  }

  public void testSetImage() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.SHELL_TRIM );
    CLabel label = new CLabel( shell, SWT.RIGHT );
    assertEquals( label.getImage(), null );
    label.setImage( Graphics.getImage( RWTFixture.IMAGE1,
                                        getClass().getClassLoader() ) );
    assertEquals( label.getImage(),
                  Graphics.getImage( RWTFixture.IMAGE1,
                                      getClass().getClassLoader() ) );
  }

  public void testSetText() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.SHELL_TRIM );
    CLabel label = new CLabel( shell, SWT.RIGHT );
    assertEquals( null, label.getText() );
    label.setText( "bar" );
    assertEquals( label.getText(), "bar" );
  }

  public void testComputeSize() throws Exception {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display, SWT.SHELL_TRIM );
    CLabel label = new CLabel( shell, SWT.RIGHT );
    Point expected = new Point( 6, 17 );
    assertEquals( expected, label.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    label.setText( "bar" );
    expected = new Point( 22, 20 );
    assertEquals( expected, label.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    label.setImage( Graphics.getImage( RWTFixture.IMAGE_100x50 ) );
    expected = new Point( 127, 56 );
    assertEquals( expected, label.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
