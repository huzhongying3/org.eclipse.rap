/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Point;

/**
 * Instances of the receiver represent is an unselectable user interface object
 * that is used to display progress, typically in the form of a bar.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SMOOTH, HORIZONTAL, VERTICAL, INDETERMINATE</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles HORIZONTAL and VERTICAL may be specified.
 * </p>
 * <p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em> within the
 * SWT implementation.
 * </p>
 */
public class ProgressBar extends Control {

  // TODO [fappel]: base for progressbar size calculation, should this be
  //                themable?
  private static final int SIZE_BASE = 16;
  private int minimum;
  private int selection;
  private int maximum = 100;

  /**
   * Constructs a new instance of this class given its parent and a style value
   * describing its behavior and appearance.
   * <p>
   * The style value is either one of the style constants defined in class
   * <code>SWT</code> which is applicable to instances of this class, or must
   * be built by <em>bitwise OR</em>'ing together (that is, using the
   * <code>int</code> "|" operator) two or more of those <code>SWT</code>
   * style constants. The class description lists the style constants that are
   * applicable to the class. Style bits are also inherited from superclasses.
   * </p>
   * 
   * @param parent a composite control which will be the parent of the new
   *            instance (cannot be null)
   * @param style the style of control to construct
   * @exception IllegalArgumentException
   *                <ul>
   *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *                </ul>
   * @exception SWTException
   *                <ul>
   *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *                thread that created the parent</li>
   *                <li>ERROR_INVALID_SUBCLASS - if this class is not an
   *                allowed subclass</li>
   *                </ul>
   * @see SWT#SMOOTH
   * @see SWT#HORIZONTAL
   * @see SWT#VERTICAL
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public ProgressBar( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  static int checkStyle( final int style ) {
    int currStyle = style | SWT.NO_FOCUS;
    return checkBits( currStyle, SWT.HORIZONTAL, SWT.VERTICAL, 0, 0, 0, 0 );
  }

  public Point computeSize( final int wHint,
                            final int hHint,
                            final boolean changed )
  {
    checkWidget();
    int border = getBorderWidth();
    int width = border * 2, height = border * 2;
    if( ( style & SWT.HORIZONTAL ) != 0 ) {
      width += SIZE_BASE * 10;
      height += SIZE_BASE;
    } else {
      width += SIZE_BASE;
      height += SIZE_BASE * 10;
    }
     if( wHint != SWT.DEFAULT ) {
       width = wHint + ( border * 2 );
     }
     if( hHint != SWT.DEFAULT ) {
       height = hHint + ( border * 2 );
     }
    return new Point( width, height );
  }

  /**
   * Returns the maximum value which the receiver will allow.
   * 
   * @return the maximum
   * @exception SWTException
   *                <ul>
   *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
   *                disposed</li>
   *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *                thread that created the receiver</li>
   *                </ul>
   */
  public int getMaximum() {
    checkWidget();
    return maximum;
  }

  /**
   * Returns the minimum value which the receiver will allow.
   * 
   * @return the minimum
   * @exception SWTException
   *                <ul>
   *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
   *                disposed</li>
   *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *                thread that created the receiver</li>
   *                </ul>
   */
  public int getMinimum() {
    checkWidget();
    return minimum;
  }

  /**
   * Returns the single 'selection' that is the receiver's position.
   * 
   * @return the selection
   * @exception SWTException
   *                <ul>
   *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
   *                disposed</li>
   *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *                thread that created the receiver</li>
   *                </ul>
   */
  public int getSelection() {
    checkWidget();
    return selection;
  }

  /**
   * Sets the maximum value that the receiver will allow. This new value will be
   * ignored if it is not greater than the receiver's current minimum value. If
   * the new maximum is applied then the receiver's selection value will be
   * adjusted if necessary to fall within its new range.
   * 
   * @param value the new maximum, which must be greater than the current
   *            minimum
   * @exception SWTException
   *                <ul>
   *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
   *                disposed</li>
   *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *                thread that created the receiver</li>
   *                </ul>
   */
  public void setMaximum( final int value ) {
    checkWidget();
    if( value > getMinimum() ) {
      this.maximum = value;
      if( selection > maximum ) {
        selection = maximum;
      }
    }
  }

  /**
   * Sets the minimum value that the receiver will allow. This new value will be
   * ignored if it is negative or is not less than the receiver's current
   * maximum value. If the new minimum is applied then the receiver's selection
   * value will be adjusted if necessary to fall within its new range.
   * 
   * @param value the new minimum, which must be nonnegative and less than the
   *            current maximum
   * @exception SWTException
   *                <ul>
   *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
   *                disposed</li>
   *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *                thread that created the receiver</li>
   *                </ul>
   */
  public void setMinimum( final int value ) {
    checkWidget();
    if( value > 0 && value < getMaximum() ) {
      this.minimum = value;
      if( minimum > selection ) {
        selection = minimum;
      }
    }
  }

  /**
   * Sets the single 'selection' that is the receiver's position to the argument
   * which must be greater than or equal to zero.
   * 
   * @param value the new selection (must be zero or greater)
   * @exception SWTException
   *                <ul>
   *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
   *                disposed</li>
   *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *                thread that created the receiver</li>
   *                </ul>
   */
  public void setSelection( final int value ) {
    checkWidget();
    if( value < minimum ) {
      selection = minimum;
    } else if( value > maximum ) {
      selection = maximum;
    } else {
      this.selection = value;
    }
  }
}
