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

package org.eclipse.swt.internal.widgets.controlkit;

import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.theme.IControlThemeAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;

public class ControlThemeAdapter implements IControlThemeAdapter {

  public int getBorderWidth( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    QxBorder result;
    if( ( control.getStyle() & SWT.BORDER ) != 0 ) {
      result = theme.getBorder( "control.BORDER.border" );
    } else {
      result = theme.getBorder( "control.border" );
    }
    return result.width;
  }

  public Color getForeground( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    QxColor color = theme.getColor( "widget.foreground" );
    return QxColor.createColor( color );
  }

  public Color getBackground( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    QxColor color = theme.getColor( "widget.background" );
    return QxColor.createColor( color );
  }

  public Font getFont( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    QxFont font = theme.getFont( "widget.font" );
    return QxFont.createFont( font );
  }
}
