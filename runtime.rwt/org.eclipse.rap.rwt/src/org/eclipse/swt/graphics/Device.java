/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import org.eclipse.rwt.internal.theme.QxFont;
import org.eclipse.rwt.internal.theme.ThemeUtil;
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
    int pixel = 0x02000000;
    switch( id ) {
      case SWT.COLOR_WHITE:
        pixel = 0x02FFFFFF;
      break;
      case SWT.COLOR_BLACK:
        pixel = 0x02000000;
      break;
      case SWT.COLOR_RED:
        pixel = 0x020000FF;
      break;
      case SWT.COLOR_DARK_RED:
        pixel = 0x02000080;
      break;
      case SWT.COLOR_GREEN:
        pixel = 0x0200FF00;
      break;
      case SWT.COLOR_DARK_GREEN:
        pixel = 0x02008000;
      break;
      case SWT.COLOR_YELLOW:
        pixel = 0x0200FFFF;
      break;
      case SWT.COLOR_DARK_YELLOW:
        pixel = 0x02008080;
      break;
      case SWT.COLOR_BLUE:
        pixel = 0x02FF0000;
      break;
      case SWT.COLOR_DARK_BLUE:
        pixel = 0x02800000;
      break;
      case SWT.COLOR_MAGENTA:
        pixel = 0x02FF00FF;
      break;
      case SWT.COLOR_DARK_MAGENTA:
        pixel = 0x02800080;
      break;
      case SWT.COLOR_CYAN:
        pixel = 0x02FFFF00;
      break;
      case SWT.COLOR_DARK_CYAN:
        pixel = 0x02808000;
      break;
      case SWT.COLOR_GRAY:
        pixel = 0x02C0C0C0;
      break;
      case SWT.COLOR_DARK_GRAY:
        pixel = 0x02808080;
      break;
    }
    return ResourceFactory.getColor( pixel );
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
    QxFont font = ThemeUtil.getTheme().getFont( "widget.font", null );
    return QxFont.createFont( font );
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
