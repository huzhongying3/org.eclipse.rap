/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.widgets;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.ITextAdapter;


public class Text_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testInitialValues() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Text text = new Text( shell, SWT.NONE );
    assertEquals( "", text.getText() );
    assertEquals( Text.LIMIT, text.getTextLimit() );
    assertEquals( 0, text.getSelectionCount() );
    assertEquals( new Point( 0, 0 ), text.getSelection() );
  }

  public void testTextLimit() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Text text = new Text( shell, SWT.NONE );
    text.setTextLimit( -1 );
    assertEquals( -1, text.getTextLimit() );
    text.setTextLimit( -20 );
    assertEquals( -20, text.getTextLimit() );
    text.setTextLimit( -12345 );
    assertEquals( -12345, text.getTextLimit() );
    text.setTextLimit( 20 );
    assertEquals( 20, text.getTextLimit() );
    try {
      text.setTextLimit( 0 );
      fail( "Must not allow to set textLimit to zero" );
    } catch( IllegalArgumentException e ) {
      // as expected
    }
  }

  public void testSelection() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Text text = new Text( shell, SWT.NONE );

    // test select all
    text.setText( "abc" );
    text.selectAll();
    assertEquals( new Point( 0, 3 ), text.getSelection() );
    assertEquals( "abc", text.getSelectionText() );

    // test clearSelection
    text.setText( "abc" );
    text.clearSelection();
    assertEquals( new Point( 0, 0 ), text.getSelection() );
    assertEquals( "", text.getSelectionText() );

    // test setSelection
    text.setText( "abc" );
    text.setSelection( 1 );
    assertEquals( new Point( 1, 1 ), text.getSelection() );
    assertEquals( 0, text.getSelectionCount() );
    assertEquals( "", text.getSelectionText() );
    text.setSelection( 1000 );
    assertEquals( new Point( 3, 3 ), text.getSelection() );
    assertEquals( 0, text.getSelectionCount() );
    assertEquals( "", text.getSelectionText() );
    Point saveSelection = text.getSelection();
    text.setSelection( -1 );
    assertEquals( saveSelection, text.getSelection() );
    assertEquals( 0, text.getSelectionCount() );
    assertEquals( "", text.getSelectionText() );

    // test selection when changing text
    text.setText( "abcefg" );
    text.setSelection( 1, 2 );
    text.setText( "gfecba" );
    assertEquals( new Point( 0, 0 ), text.getSelection() );
    // ... even setting the same text again will clear the selection
    text.setText( "abcefg" );
    text.setSelection( 1, 2 );
    text.setText( text.getText() );
    assertEquals( new Point( 0, 0 ), text.getSelection() );
  }

  public void testModifyEvent() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display );
    final Text text = new Text( shell, SWT.NONE );
    text.addModifyListener( new ModifyListener() {
      public void modifyText( final ModifyEvent event ) {
        log.append( "modifyEvent|" );
        assertSame( text, event.getSource() );
      }
    } );
    // Changing the text fires a modifyEvent
    text.setText( "abc" );
    assertEquals( "modifyEvent|", log.toString() );
    // Setting the same value also fires a modifyEvent
    log.setLength( 0 );
    text.setText( "abc" );
    assertEquals( "modifyEvent|", log.toString() );
  }

  public void testVerifyEvent() {
    VerifyListener verifyListener;
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    final Text text = new Text( shell, SWT.NONE );
    text.addModifyListener( new ModifyListener() {
      public void modifyText( final ModifyEvent event ) {
        log.add( event );
      }
    } );
    text.addVerifyListener( new VerifyListener() {
      public void verifyText( final VerifyEvent event ) {
        log.add( event );
      }
    } );

    // VerifyEvent is also sent when setting text to the already set value
    log.clear();
    text.setText( "" );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    assertEquals( ModifyEvent.class, log.get( 1 ).getClass() );

    // Test verifyListener that prevents (doit=false) change
    text.setText( "" );
    log.clear();
    verifyListener = new VerifyListener() {
      public void verifyText( final VerifyEvent event ) {
        event.doit = false;
      }
    };
    text.addVerifyListener( verifyListener );
    text.setText( "other" );
    assertEquals( 1, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    assertEquals( "", text.getText() );
    text.removeVerifyListener( verifyListener );

    // Test verifyListener that manipulates text
    text.setText( "" );
    log.clear();
    verifyListener = new VerifyListener() {
      public void verifyText( final VerifyEvent event ) {
        event.text = "manipulated";
      }
    };
    text.addVerifyListener( verifyListener );
    text.setText( "other" );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    assertEquals( ModifyEvent.class, log.get( 1 ).getClass() );
    assertEquals( "manipulated", text.getText() );
    text.removeVerifyListener( verifyListener );

    // Ensure that:
    // VerifyEvent#start denotes start of modified text range
    // VerifyEvent#end denotes end of modified text range
    // VerifyEvent#text denotes text to replace the range (start, end) with
    ITextAdapter adapter = ( ITextAdapter )text.getAdapter( ITextAdapter.class );
    String oldText = "old text";
    text.setText( oldText );
    log.clear();
    String newText = "old changed text";
    adapter.setText( newText, new Point( 0, 0 ) );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    VerifyEvent verifyEvent = ( VerifyEvent )log.get( 0 );
    assertEquals( 4, verifyEvent.start );
    assertEquals( 4, verifyEvent.end );
    assertEquals( "changed ", verifyEvent.text );
    assertEquals( ModifyEvent.class, log.get( 1 ).getClass() );

    text.setText( oldText );
    log.clear();
    newText = "old t1234t";
    adapter.setText( newText, new Point( 0, 0 ) );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    verifyEvent = ( VerifyEvent )log.get( 0 );
    assertEquals( 5, verifyEvent.start );
    assertEquals( 7, verifyEvent.end );
    assertEquals( "1234", verifyEvent.text );

    text.setText( oldText );
    log.clear();
    newText = "old";
    adapter.setText( newText, new Point( 0, 0 ) );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    verifyEvent = ( VerifyEvent )log.get( 0 );
    assertEquals( 3, verifyEvent.start );
    assertEquals( 8, verifyEvent.end );
    assertEquals( "", verifyEvent.text );

    text.setText( oldText );
    log.clear();
    newText = "";
    adapter.setText( newText, new Point( 0, 0 ) );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    verifyEvent = ( VerifyEvent )log.get( 0 );
    assertEquals( 0, verifyEvent.start );
    assertEquals( 8, verifyEvent.end );
    assertEquals( "", verifyEvent.text );

    text.setText( oldText );
    log.clear();
    newText = "new text";
    text.setText( newText );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    verifyEvent = ( VerifyEvent )log.get( 0 );
    assertEquals( 0, verifyEvent.start );
    assertEquals( oldText.length(), verifyEvent.end );
    assertEquals( newText, verifyEvent.text );

    // Ensure that VerifyEvent gets fired when setEditable was set to false
    text.setText( "" );
    text.setEditable( false );
    log.clear();
    text.setText( "whatever" );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    assertEquals( ModifyEvent.class, log.get( 1 ).getClass() );
    text.setEditable( true );
  }

  // TODO [bm] extend testcase with newline chars and getLineCount
  public void testInsert() {
    Display display = new Display();
    Shell shell = new Shell( display );

    // Test insert on multi-line Text
    Text text = new Text( shell, SWT.MULTI );
    text.setBounds( 0, 0, 500, 500 );
    // Ensure initial state
    assertEquals( "", text.getText() );
    // Test with allowed arguments
    text.insert( "" );
    assertEquals( "", text.getText() );
    text.insert( "fred" );
    assertEquals( "fred", text.getText() );
    text.setSelection( 2 );
    text.insert( "helmut" );
    assertEquals( "frhelmuted", text.getText() );
    // Test with illegal argument
    try {
      text.setText( "oldText" );
      text.insert( null );
      fail( "No exception thrown on string == null" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "oldText", text.getText() );
    }

    // Test insert on single-line Text
    text = new Text( shell, SWT.SINGLE );
    assertEquals( "", text.getText() );
    text.insert( "" );
    assertEquals( "", text.getText() );
    text.insert( "fred" );
    assertEquals( "fred", text.getText() );
    text.setSelection( 2 );
    text.insert( "helmut" );
    assertEquals( "frhelmuted", text.getText() );
    // Test with illegal arguments
    text = new Text( shell, SWT.SINGLE );
    try {
      text.setText( "oldText" );
      text.insert( null );
      fail( "No exception thrown on string == null" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "oldText", text.getText() );
    }
	}

  // TODO [bm] extend testcase with newline chars for SWT.MULTI
  public void testAppend() {
		Display display = new Display();
		Shell shell = new Shell(display);
		Text text = new Text(shell, SWT.SINGLE);

		try {
			text.append(null);
			fail("No exception thrown for string == null");
		} catch (IllegalArgumentException e) {
		}

		text = new Text(shell, SWT.SINGLE);

		try {
			text.append(null);
			fail("No exception thrown on string == null");
		} catch (IllegalArgumentException e) {
		}

		// tests a SINGLE line text editor
		text = new Text(shell, SWT.SINGLE);

		text.setText("01");
		text.append("23");
		assertEquals("0123", text.getText());
		text.append("45");
		assertEquals("012345", text.getText());
		text.setSelection(0);
		text.append("67");
		assertEquals("01234567", text.getText());

	}

  public void testInsertWithModifyListener() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final java.util.List log = new ArrayList();
    Display display = new Display();
    Shell shell = new Shell( display );
    Text text = new Text( shell, SWT.SINGLE );
    text.setBounds( 0, 0, 100, 20 );
    text.addModifyListener( new ModifyListener() {
      public void modifyText( final ModifyEvent event ) {
        log.add( event );
      }
    } );

    // Test that event is fired when correctly using insert
    log.clear();
    text.insert( "abc" );
    assertEquals( 1, log.size() );

    // Test that event is *not* fired when passing illegal argument to insert
    log.clear();
    text = new Text( shell, SWT.SINGLE );
    try {
      text.insert( null );
      fail( "No exception thrown on string == null" );
    } catch( IllegalArgumentException e ) {
    }
    assertEquals( 0, log.size() );
  }
}
