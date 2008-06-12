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

package org.eclipse.swt.graphics;

import junit.framework.TestCase;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.graphics.IColor;
import org.eclipse.swt.widgets.Display;

public class Color_Test extends TestCase {

  public void testColorFromRGB() {
    Color salmon = Graphics.getColor( new RGB( 250, 128, 114 ) );
    assertEquals( 250, salmon.getRed() );
    assertEquals( 128, salmon.getGreen() );
    assertEquals( 114, salmon.getBlue() );
  }

  public void testColorFromInt() {
    Color salmon = Graphics.getColor( 250, 128, 114 );
    assertEquals( 250, salmon.getRed() );
    assertEquals( 128, salmon.getGreen() );
    assertEquals( 114, salmon.getBlue() );
  }

  public void testColorFromConstant() {
    Device display = new Display();
    Color color = display.getSystemColor( SWT.COLOR_RED );
    assertEquals( 255, color.getRed() );
    assertEquals( 0, color.getGreen() );
    assertEquals( 0, color.getBlue() );
  }

  public void testEquality() {
    Color salmon = Graphics.getColor( 250, 128, 114 );
    Color salmon2 = Graphics.getColor( 250, 128, 114 );
    Color chocolate = Graphics.getColor( 210, 105, 30 );
    assertTrue( salmon.equals( salmon2 ) );
    assertFalse( salmon.equals( chocolate ) );
  }

  public void testIdentity() {
    Color salmon1 = Graphics.getColor( 250, 128, 114 );
    Color salmon2 = Graphics.getColor( 250, 128, 114 );
    assertTrue( salmon1 == salmon2 );
  }

  public void testColorUtil() {
    Color salmon = Graphics.getColor( 250, 128, 114 );
    assertEquals( "#fa8072", ( ( IColor )salmon ).toColorValue() );
    Color chocolate = Graphics.getColor( 210, 105, 30 );
    assertEquals( "#d2691e", ( ( IColor )chocolate ).toColorValue() );
  }

  public void testGetRGB() {
    RGB rgbSalmon = new RGB( 250, 128, 114 );
    assertEquals( rgbSalmon, Graphics.getColor( rgbSalmon ).getRGB() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
