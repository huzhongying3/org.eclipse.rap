/*******************************************************************************
 * Copyright (c) 2007, 2010 Innoopract Informationssysteme GmbH.
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

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;


public class Text_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testInitialValuesForSingleText() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Text text = new Text( shell, SWT.NONE );
    assertEquals( "", text.getText() );
    assertEquals( "", text.getMessage() );
    assertEquals( Text.LIMIT, text.getTextLimit() );
    assertEquals( 0, text.getSelectionCount() );
    assertEquals( new Point( 0, 0 ), text.getSelection() );
    assertEquals( ( char )0, text.getEchoChar() );
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
    text.setText( "Sample_text" );
    text.setTextLimit( 6 );
    assertEquals( "Sample_text", text.getText() );
    text.setText( "Other_text" );
    assertEquals( "Other_", text.getText() );
  }

  public void testGetLineHeight() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Text text = new Text( shell, SWT.MULTI );
    // default theme font is 11px
    assertEquals( 13, text.getLineHeight() );
    text.setFont( Graphics.getFont( "Helvetica", 12, SWT.NORMAL ) );
    assertEquals( 14, text.getLineHeight() );
    text.setFont( null );
    assertEquals( 13, text.getLineHeight() );
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
    text.setText( "abcdefg" );
    text.setSelection( new Point( 5, 2 ) );
    assertEquals( new Point( 2, 5 ), text.getSelection() );

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
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
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
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
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
        assertEquals( '\0', event.character );
        assertEquals( 0, event.keyCode );
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

    // Ensure that VerifyEvent#start and #end denote the positions of the old
    // text and #text denotes the text to be set
    String oldText = "old";
    text.setText( oldText );
    log.clear();
    String newText = oldText + "changed";
    text.setText( newText );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    VerifyEvent verifyEvent = ( VerifyEvent )log.get( 0 );
    assertEquals( 0, verifyEvent.start );
    assertEquals( oldText.length(), verifyEvent.end );
    assertEquals( newText, verifyEvent.text );
    assertEquals( ModifyEvent.class, log.get( 1 ).getClass() );

    // Ensure that VerifyEvent gets fired when setEditable was set to false
    text.setText( "" );
    text.setEditable( false );
    log.clear();
    text.setText( "whatever" );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    assertEquals( ModifyEvent.class, log.get( 1 ).getClass() );
    text.setEditable( true );

    // Ensure that VerifyEvent#text denotes the text to be set
    // and not the cut by textLimit one
    text.setTextLimit( 5 );
    String sampleText = "sample_text";
    log.clear();
    text.setText( sampleText );
    assertEquals( 2, log.size() );
    assertEquals( VerifyEvent.class, log.get( 0 ).getClass() );
    verifyEvent = ( VerifyEvent )log.get( 0 );
    assertEquals( sampleText, verifyEvent.text );
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
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
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

  public void testComputeSize() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display );
    Text text = new Text( shell, SWT.NONE );
    Point expected = new Point( 64, 17 );
    assertEquals( expected, text.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    text.setText( "This is a long long text!" );
    expected = new Point( 138, 19 );
    assertEquals( expected, text.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    text.setMessage( "This is a message that is longer than the text!" );
    expected = new Point( 254, 19 );
    assertEquals( expected, text.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    text = new Text( shell, SWT.MULTI );
    text.setText( "This is a long long text!\nThis is the second row." );
    expected = new Point( 138, 34 );
    assertEquals( expected, text.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    text = new Text( shell, SWT.MULTI | SWT.WRAP );
    text.setText( "This is a long long text!\nThis is the second row." );
    expected = new Point( 50, 116 );
    assertEquals( expected, text.computeSize( 50, SWT.DEFAULT ) );

    text = new Text( shell, SWT.MULTI | SWT.WRAP | SWT.BORDER );
    text.setText( "This is a long long text!\nThis is the second row." );
    expected = new Point( 52, 118 );
    assertEquals( 1, text.getBorderWidth() );
    assertEquals( expected, text.computeSize( 50, SWT.DEFAULT ) );

    expected = new Point( 102, 104 );
    assertEquals( expected, text.computeSize( 100, 100 ) );
  }

  public void testComputeTrim() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display );
    Text text = new Text( shell, SWT.SINGLE );
    Rectangle expected = new Rectangle( 0, 0, 0, 0 );
    assertEquals( expected, text.computeTrim( 0, 0, 0, 0 ) );
    expected = new Rectangle( 10, 10, 100, 100 );
    assertEquals( expected, text.computeTrim( 10, 10, 100, 100 ) );

    text = new Text( shell, SWT.H_SCROLL );
    expected = new Rectangle( 0, 0, 1, 0 );
    assertEquals( expected, text.computeTrim( 0, 0, 0, 0 ) );
    expected = new Rectangle( 10, 10, 101, 100 );
    assertEquals( expected, text.computeTrim( 10, 10, 100, 100 ) );

    text = new Text( shell, SWT.BORDER );
    expected = new Rectangle( -1, -1, 3, 2 );
    assertEquals( 1, text.getBorderWidth() );
    assertEquals( expected, text.computeTrim( 0, 0, 1, 0 ) );
  }

  public void testGetCaretPosition() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Text text = new Text( shell, SWT.SINGLE );
    text.setText( "Sample text" );
    assertEquals( 0, text.getCaretPosition() );
    text.setSelection( 5 );
    assertEquals( 5, text.getCaretPosition() );
    text.setSelection( 3, 8 );
    assertEquals( 3, text.getCaretPosition() );
    text.setSelection( 8, 5 );
    assertEquals( 5, text.getCaretPosition() );
    text.setText( "New text" );
    assertEquals( 0, text.getCaretPosition() );
    text.setSelection( 3, 8 );
    text.clearSelection();
    assertEquals( new Point( 8, 8 ), text.getSelection() );
    assertEquals( 8, text.getCaretPosition() );
  }

  public void testGetText() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Text text = new Text( shell, SWT.NONE );
    text.setText( "Test Text" );
    assertEquals( "Test", text.getText( 0, 3 ) );
    assertEquals( "", text.getText( 5, 4 ) );
    assertEquals( "s", text.getText( 2, 2 ) );
    assertEquals( "Test Text", text.getText( 0, 25 ) );
    assertEquals( "Test ", text.getText( -3, 4 ) );
  }

  public void testMessage() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Text text = new Text( shell, SWT.SINGLE );
    assertEquals( "", text.getMessage() );
    text.setMessage( "New message" );
    assertEquals( "New message", text.getMessage() );
  }

  public void testStyle() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Text text = new Text( shell, SWT.SEARCH | SWT.PASSWORD );
    int style = text.getStyle();
    assertTrue( ( style & SWT.SINGLE ) != 0 );
    assertTrue( ( style & SWT.BORDER ) != 0 );
    assertTrue( ( style & SWT.PASSWORD ) == 0 );
  }
  
  public void testEchoChar() {
    Display display = new Display();
    Shell shell = new Shell( display );
    // single line text field
    Text singleText = new Text( shell, SWT.NONE );
    assertEquals( ( char )0, singleText.getEchoChar() );
    singleText.setEchoChar( '?' );
    assertEquals( '?', singleText.getEchoChar() );
    // multi line text field
    Text multiText = new Text( shell, SWT.MULTI );
    assertEquals( ( char )0, multiText.getEchoChar() );
    multiText.setEchoChar( '?' );
    assertEquals( ( char )0, multiText.getEchoChar() );
    // password text field
    Text passwordText = new Text( shell, SWT.PASSWORD );
    assertEquals( '?', passwordText.getEchoChar() );
    passwordText.setEchoChar( '*' );
    assertEquals( '*', passwordText.getEchoChar() );
  }
}
