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

package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;

public class Scrollable_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testComputeTrim() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Composite composite = new Composite( shell, SWT.BORDER );
    assertEquals( 2, composite.getBorderWidth() );
    Rectangle trim = composite.computeTrim( 20, 30, 200, 300 );
    assertEquals( 18, trim.x );
    assertEquals( 28, trim.y );
    assertEquals( 204, trim.width );
    assertEquals( 304, trim.height );

    composite = new Composite( shell, SWT.BORDER ) {
      int getVScrollBarWidth() {
        return 20;
      }
      int getHScrollBarHeight() {
        return 20;
      }
      Rectangle getPadding() {
        return new Rectangle( 10, 10, 10, 10 );
      }
    };
    assertEquals( 2, composite.getBorderWidth() );
    trim = composite.computeTrim( 20, 30, 200, 300 );
    assertEquals( 8, trim.x );
    assertEquals( 18, trim.y );
    assertEquals( 234, trim.width );
    assertEquals( 334, trim.height );
  }

  public void testGetClientArea() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Composite composite = new Composite( shell, SWT.BORDER );
    composite.setSize( 100, 100 );
    assertEquals( 2, composite.getBorderWidth() );
    Rectangle expected = new Rectangle( 0, 0, 96, 96 );
    assertEquals( expected, composite.getClientArea() );

    composite = new Composite( shell, SWT.BORDER ) {
      int getVScrollBarWidth() {
        return 20;
      }
      int getHScrollBarHeight() {
        return 20;
      }
      Rectangle getPadding() {
        return new Rectangle( 10, 10, 10, 10 );
      }
    };
    composite.setSize( 100, 100 );
    assertEquals( 2, composite.getBorderWidth() );
    expected = new Rectangle( 10, 10, 66, 66 );
    assertEquals( expected, composite.getClientArea() );
  }
}
