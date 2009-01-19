/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import org.eclipse.rwt.internal.theme.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.graphics.ResourceFactory;

/**
 * This class is the abstract superclass of all device objects,
 * such as Display.
 *
 * <p>This class is <em>not</em> intended to be directly used by clients.</p>
 */
public abstract class Device {

  /**
   * Returns the matching standard color for the given
   * constant, which should be one of the color constants
   * specified in class <code>SWT</code>. Any value other
   * than one of the SWT color constants which is passed
   * in will result in the color black. This color should
   * not be free'd because it was allocated by the system,
   * not the application.
   *
   * @param id the color constant
   * @return the matching color
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see SWT
   */
  public Color getSystemColor( final int id ) {
    checkDevice();
    Color result;
    switch( id ) {
      case SWT.COLOR_WHITE:
        result = ResourceFactory.getColor( 255, 255, 255 );
      break;
      case SWT.COLOR_BLACK:
        result = ResourceFactory.getColor( 0, 0, 0 );
      break;
      case SWT.COLOR_RED:
        result = ResourceFactory.getColor( 255, 0, 0 );
      break;
      case SWT.COLOR_DARK_RED:
        result = ResourceFactory.getColor( 128, 0, 0 );
      break;
      case SWT.COLOR_GREEN:
        result = ResourceFactory.getColor( 0, 255, 0 );
      break;
      case SWT.COLOR_DARK_GREEN:
        result = ResourceFactory.getColor( 0, 128, 0 );
      break;
      case SWT.COLOR_YELLOW:
        result = ResourceFactory.getColor( 255, 255, 0 );
      break;
      case SWT.COLOR_DARK_YELLOW:
        result = ResourceFactory.getColor( 128, 128, 0 );
      break;
      case SWT.COLOR_BLUE:
        result = ResourceFactory.getColor( 0, 0, 255 );
      break;
      case SWT.COLOR_DARK_BLUE:
        result = ResourceFactory.getColor( 0, 0, 128 );
      break;
      case SWT.COLOR_MAGENTA:
        result = ResourceFactory.getColor( 255, 0, 255 );
      break;
      case SWT.COLOR_DARK_MAGENTA:
        result = ResourceFactory.getColor( 128, 0, 128 );
      break;
      case SWT.COLOR_CYAN:
        result = ResourceFactory.getColor( 0, 255, 255 );
      break;
      case SWT.COLOR_DARK_CYAN:
        result = ResourceFactory.getColor( 0, 128, 128 );
      break;
      case SWT.COLOR_GRAY:
        result = ResourceFactory.getColor( 192, 192, 192 );
      break;
      case SWT.COLOR_DARK_GRAY:
        result = ResourceFactory.getColor( 128, 128, 128 );
      break;
      default:
        result = ResourceFactory.getColor( 0, 0, 0 );
      break;
    }
    return result;
  }

  /**
   * Returns a reasonable font for applications to use.
   * On some platforms, this will match the "default font"
   * or "system font" if such can be found.  This font
   * should not be free'd because it was allocated by the
   * system, not the application.
   * <p>
   * Typically, applications which want the default look
   * should simply not set the font on the widgets they
   * create. Widgets are always created with the correct
   * default font for the class of user-interface component
   * they represent.
   * </p>
   *
   * @return a font
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public Font getSystemFont() {
    checkDevice();
    QxType font = ThemeUtil.getCssValue( "*", "font", SimpleSelector.DEFAULT );
    return QxFont.createFont( ( QxFont )font );
  }

//  /**
//   * Throws an <code>SWTException</code> if the receiver can not
//   * be accessed by the caller. This may include both checks on
//   * the state of the receiver and more generally on the entire
//   * execution context. This method <em>should</em> be called by
//   * device implementors to enforce the standard SWT invariants.
//   * <p>
//   * Currently, it is an error to invoke any method (other than
//   * <code>isDisposed()</code> and <code>dispose()</code>) on a
//   * device that has had its <code>dispose()</code> method called.
//   * </p><p>
//   * In future releases of SWT, there may be more or fewer error
//   * checks and exceptions may be thrown for different reasons.
//   * <p>
//   *
//   * @exception SWTException <ul>
//   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
//   * </ul>
//   */
  protected void checkDevice() {
    // TODO [rh] implementation missing
  }

}
