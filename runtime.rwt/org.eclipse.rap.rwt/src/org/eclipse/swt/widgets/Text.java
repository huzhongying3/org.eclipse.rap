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

package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.FontSizeCalculator;

/**
 * Instances of this class are selectable user interface
 * objects that allow the user to enter and modify text.
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>CENTER, LEFT, MULTI, PASSWORD, SINGLE, RIGHT, READ_ONLY, WRAP</dd>
 * <dt><b>Events:</b></dt>
 * <dd>DefaultSelection, Modify, Verify</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles MULTI and SINGLE may be specified. 
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * <p>Due to limitations of the JavaScript library, the current WRAP behavior 
 * of a MULI line text is always as if WRAP was set.</p> 
 */
public class Text extends Scrollable {

  public static final int MAX_TEXT_LIMIT = -1;
  
  private String text = "";
  private int textLimit = MAX_TEXT_LIMIT;
  private final Point selection = new Point( 0, 0 );

  /**
   * Constructs a new instance of this class given its parent
   * and a style value describing its behavior and appearance.
   * <p>
   * The style value is either one of the style constants defined in
   * class <code>SWT</code> which is applicable to instances of this
   * class, or must be built by <em>bitwise OR</em>'ing together 
   * (that is, using the <code>int</code> "|" operator) two or more
   * of those <code>SWT</code> style constants. The class description
   * lists the style constants that are applicable to the class.
   * Style bits are also inherited from superclasses.
   * </p>
   *
   * @param parent a composite control which will be the parent of the new instance (cannot be null)
   * @param style the style of control to construct
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @see SWT#SINGLE
   * @see SWT#MULTI
   * @see SWT#READ_ONLY
   * @see SWT#WRAP
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public Text( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  /**
   * Sets the contents of the receiver to the given string. If the receiver has style
   * SINGLE and the argument contains multiple lines of text, the result of this
   * operation is undefined and may vary from platform to platform.
   *
   * @param string the new text
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setText( final String text ) {
    checkWidget();
    if( text == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    this.text = text;
    selection.x = 0;
    selection.y = 0;
    ModifyEvent modifyEvent = new ModifyEvent( this );
    modifyEvent.processEvent();
  }

  /**
   * Returns the widget text.
   * <p>
   * The text for a text widget is the characters in the widget, or
   * an empty string if this has never been set.
   * </p>
   *
   * @return the widget text
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public String getText() {
    checkWidget();
    return text;
  }

  /**
   * Returns the line delimiter.
   *
   * @return a string that is the line delimiter
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see #DELIMITER
   */
  public String getLineDelimiter() {
    checkWidget();
    return "\n";
  }
  
  //////////////////////////
  // Imput length constraint
  
  /**
   * Sets the maximum number of characters that the receiver
   * is capable of holding to be the argument.
   * <p>
   * Instead of trying to set the text limit to zero, consider
   * creating a read-only text widget.
   * </p><p>
   * To reset this value to the default, use <code>setTextLimit(Text.LIMIT)</code>.
   * Specifying a limit value larger than <code>Text.LIMIT</code> sets the
   * receiver's limit to <code>Text.LIMIT</code>.
   * </p>
   *
   * @param limit new text limit
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_CANNOT_BE_ZERO - if the limit is zero</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see #LIMIT
   */
  public void setTextLimit( final int textLimit ) {
    checkWidget();
    if( textLimit == 0 ) {
      error( SWT.ERROR_CANNOT_BE_ZERO );
    }
    // Note that we mimic here the behavior of SWT Text with style MULTI on 
    // Windows. In SWT, other operating systems and/or style flags behave 
    // different.  
    this.textLimit = textLimit;
  }

  /**
   * Returns the maximum number of characters that the receiver is capable of holding. 
   * <p>
   * If this has not been changed by <code>setTextLimit()</code>,
   * it will be the constant <code>Text.LIMIT</code>.
   * </p>
   * 
   * @return the text limit
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see #LIMIT
   */
  public int getTextLimit() {
    checkWidget ();
    return textLimit;
  }
  
  ///////////////////////////////////////////
  // Selection start, count and selected text
  
  /**
   * Sets the selection.
   * <p>
   * Indexing is zero based.  The range of
   * a selection is from 0..N where N is
   * the number of characters in the widget.
   * </p><p>
   * Text selections are specified in terms of
   * caret positions.  In a text widget that
   * contains N characters, there are N+1 caret
   * positions, ranging from 0..N.  This differs
   * from other functions that address character
   * position such as getText () that use the
   * regular array indexing rules.
   * </p>
   *
   * @param start new caret position
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setSelection( final int start ) {
    checkWidget();
    setSelection( start, start );
 }

  /**
   * Sets the selection to the range specified
   * by the given start and end indices.
   * <p>
   * Indexing is zero based.  The range of
   * a selection is from 0..N where N is
   * the number of characters in the widget.
   * </p><p>
   * Text selections are specified in terms of
   * caret positions.  In a text widget that
   * contains N characters, there are N+1 caret
   * positions, ranging from 0..N.  This differs
   * from other functions that address character
   * position such as getText () that use the
   * usual array indexing rules.
   * </p>
   *
   * @param start the start of the range
   * @param end the end of the range
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setSelection( final int start, final int end ) {
    checkWidget();
    if( start >= 0 && end >= 0 && start <= end ) {
      int validatedStart = Math.min( start, text.length() );
      int validatedEnd = Math.min( end, text.length() );
      selection.x = validatedStart;
      selection.y = validatedEnd;
    }
  }

  /**
   * Sets the selection to the range specified
   * by the given point, where the x coordinate
   * represents the start index and the y coordinate
   * represents the end index.
   * <p>
   * Indexing is zero based.  The range of
   * a selection is from 0..N where N is
   * the number of characters in the widget.
   * </p><p>
   * Text selections are specified in terms of
   * caret positions.  In a text widget that
   * contains N characters, there are N+1 caret
   * positions, ranging from 0..N.  This differs
   * from other functions that address character
   * position such as getText () that use the
   * usual array indexing rules.
   * </p>
   *
   * @param selection the point
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setSelection( final Point selection ) {
    checkWidget();
    if( selection == null ) {
      error( SWT.ERROR_NULL_ARGUMENT );
    }
    setSelection( selection.x, selection.y );
  }

  /**
   * Returns a <code>Point</code> whose x coordinate is the
   * character position representing the start of the selected
   * text, and whose y coordinate is the character position
   * representing the end of the selection. An "empty" selection
   * is indicated by the x and y coordinates having the same value.
   * <p>
   * Indexing is zero based.  The range of a selection is from
   * 0..N where N is the number of characters in the widget.
   * </p>
   *
   * @return a point representing the selection start and end
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Point getSelection() {
    checkWidget();
    return new Point( selection.x, selection.y );
  }

  /**
   * Returns the number of selected characters.
   *
   * @return the number of selected characters.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getSelectionCount() {
    checkWidget();
    return selection.y - selection.x;
  }

  /**
   * Gets the selected text, or an empty string if there is no current selection.
   *
   * @return the selected text
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public String getSelectionText() {
    checkWidget();
    return text.substring( selection.x, selection.y );
  }
  
  /**
   * Clears the selection.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void clearSelection() {
    checkWidget();
    selection.x = 0;
    selection.y = 0;
  }
  
  /**
   * Selects all the text in the receiver.
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void selectAll() {
    checkWidget();
    selection.x = 0;
    selection.y = text.length();
  }
  
  ///////////
  // Editable

  /**
   * Sets the editable state.
   *
   * @param editable the new editable state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setEditable( final boolean editable ) {
    checkWidget();
    style &= ~SWT.READ_ONLY;
    if( !editable ) {
      style |= SWT.READ_ONLY;
    }
  }

  /**
   * Returns the editable state.
   *
   * @return whether or not the receiver is editable
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public boolean getEditable() {
    checkWidget();
    return ( style & SWT.READ_ONLY ) == 0;
  }
  
  /**
   * Inserts a string.
   * <p>
   * The old selection is replaced with the new text.
   * </p>
   *
   * @param string the string
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is <code>null</code></li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void insert( final String string ) {
    checkWidget();
    if( string == null ) {
      error( SWT.ERROR_INVALID_ARGUMENT );
    }
    String oldText = getText();
    Point sel = getSelection();
    String replace = oldText.substring( 0, sel.x );
    replace += string;
    replace += oldText.substring( sel.y );
    setText( replace );
  }

  ////////////////////
  // Widget dimensions
  
  public Point computeSize( final int wHint, 
                            final int hHint, 
                            final boolean changed ) 
  {
    checkWidget();
    int height = 0, width = 0;
    if( wHint == SWT.DEFAULT || hHint == SWT.DEFAULT ) {
      boolean wrap = ( style & ( SWT.MULTI | SWT.WRAP ) ) != 0 ;
      int wrapWidth = 0;
      if( wrap && wHint != SWT.DEFAULT ) {
        wrapWidth = wHint;
      }
      Point extent
        = FontSizeCalculator.textExtent( getFont(), text, wrapWidth );
      if( extent.x != 0 ) {
        width = extent.x + 10;
      }
      if( extent.y != 0 ) {
        height = extent.y + 4;
      }
    }
    if( width == 0 ) {
      width = DEFAULT_WIDTH;
    }
    if( height == 0 ) {
      height = DEFAULT_HEIGHT;
    }
    if( wHint != SWT.DEFAULT ) {
      width = wHint;
    }
    if( hHint != SWT.DEFAULT ) {
      height = hHint;
    }
    Rectangle trim = computeTrim( 0, 0, width, height );
    return new Point( trim.width, trim.height );
  }
  
  public Rectangle computeTrim( final int x,
                                final int y,
                                final int width,
                                final int height )
  {
    Rectangle result = super.computeTrim( x, y, width, height );
    if( ( style & SWT.H_SCROLL ) != 0 ) {
      result.width++;
    }
    int border = getBorderWidth();
    result.x -= border;
    result.y -= border;
    result.width += 2 * border;
    result.height += 2 * border;
    return result;
  }

  ///////////////////////////////////////
  // Listener registration/deregistration
  
  /**
   * Adds the listener to the collection of listeners who will
   * be notified when the control is selected, by sending
   * it one of the messages defined in the <code>SelectionListener</code>
   * interface.
   * <p>
   * <code>widgetSelected</code> is not called for texts.
   * <code>widgetDefaultSelected</code> is typically called when ENTER is pressed in a single-line text.
   * </p>
   *
   * @param listener the listener which should be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see SelectionListener
   * @see #removeSelectionListener
   * @see SelectionEvent
   */
  public void addSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.addListener( this, listener );
  }
  
  /**
   * Removes the listener from the collection of listeners who will
   * be notified when the control is selected.
   *
   * @param listener the listener which should no longer be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see SelectionListener
   * @see #addSelectionListener
   */
  public void removeSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.removeListener( this, listener );
  }
  
  /**
   * Adds the listener to the collection of listeners who will
   * be notified when the receiver's text is modified, by sending
   * it one of the messages defined in the <code>ModifyListener</code>
   * interface.
   *
   * @param listener the listener which should be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see ModifyListener
   * @see #removeModifyListener
   */
  public void addModifyListener( final ModifyListener listener ) {
    checkWidget();
    ModifyEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will
   * be notified when the receiver's text is modified.
   *
   * @param listener the listener which should no longer be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see ModifyListener
   * @see #addModifyListener
   */
  public void removeModifyListener( final ModifyListener listener ) {
    checkWidget();
    ModifyEvent.removeListener( this, listener );
  }
  
  boolean isTabGroup() {
    return true;
  }
  
  ///////////////////////////////////////
  // Helping method to adjust style flags 
  
  private static int checkStyle( final int style ) {
    int result = style;
    if( ( result & SWT.SINGLE ) != 0 && ( result & SWT.MULTI ) != 0 ) {
      result &= ~SWT.MULTI;
    }
    result = checkBits( result, SWT.LEFT, SWT.CENTER, SWT.RIGHT, 0, 0, 0 );
    if( ( result & SWT.SINGLE ) != 0 ) {
      result &= ~( SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP );
    }
    if( ( result & SWT.WRAP ) != 0 ) {
      result |= SWT.MULTI;
      result &= ~SWT.H_SCROLL;
    }
    if( ( result & SWT.MULTI ) != 0 ) {
      result &= ~SWT.PASSWORD;
    }
    if( ( result & ( SWT.SINGLE | SWT.MULTI ) ) != 0 ) {
      return result;
    }
    if( ( style & ( SWT.H_SCROLL | SWT.V_SCROLL ) ) != 0 ) {
      return result | SWT.MULTI;
    }
    return result | SWT.SINGLE;
  }
}
