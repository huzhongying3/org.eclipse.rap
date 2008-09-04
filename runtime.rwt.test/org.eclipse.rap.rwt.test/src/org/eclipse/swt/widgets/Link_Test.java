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

package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.ILinkAdapter;


public class Link_Test extends TestCase {

  public void testInitialValues() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Link link = new Link( shell, SWT.NONE );
    assertEquals( "", link.getText() );
  }

  public void testText() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Link link = new Link( shell, SWT.NONE );
    String text
      = "Visit the <A HREF=\"www.eclipse.org\">Eclipse.org</A> project and "
      + "the <a>SWT</a> homepage.";
    link.setText( text );
    assertEquals( text, link.getText() );
    try {
      link.setText( null );
      fail( "Must not allow to set null-text." );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testAdapter() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Link link = new Link( shell, SWT.NONE );
    String text
      = "Visit the <A HREF=\"www.eclipse.org\">Eclipse.org</A> project and "
      + "the <a>SWT</a> homepage.";
    link.setText( text );
    ILinkAdapter adapter = ( ILinkAdapter )link.getAdapter( ILinkAdapter.class );
    String displayText = "Visit the Eclipse.org project and the SWT homepage.";
    assertEquals( displayText, adapter.getDisplayText() );
    String[] ids = adapter.getIds();
    assertEquals( 2, ids.length );
    assertEquals( "www.eclipse.org", ids[ 0 ] );
    assertEquals( "SWT", ids[ 1 ] );
  }

  public void testComputeSize() throws Exception {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Link link = new Link( shell, SWT.NONE );
    Point expected = new Point( 0, 0 );
    assertEquals( expected, link.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    String text
      = "Visit the <A HREF=\"www.eclipse.org\">Eclipse.org</A> project and "
      + "the <a>SWT</a> homepage.";
    link.setText( text );
    expected = new Point( 269, 15 );
    assertEquals( expected, link.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    link = new Link( shell, SWT.BORDER );
    expected = new Point( 2, 2 );
    assertEquals( expected, link.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    expected = new Point( 102, 102 );
    assertEquals( expected, link.computeSize( 100, 100 ) );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
