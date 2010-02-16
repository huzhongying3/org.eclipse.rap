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
package org.eclipse.swt.internal.widgets.spinnerkit;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.Spinner;

public final class SpinnerThemeAdapter extends ControlThemeAdapter {

  public Rectangle getFieldPadding( final Spinner spinner ) {
    return getCssBoxDimensions( "Spinner-Field", "padding", spinner );
  }

  public int getButtonWidth( final Spinner spinner ) {
    int upButtonWidth = getCssDimension( "Spinner-UpButton",
                                         "width",
                                         spinner );
    int downButtonWidth = getCssDimension( "Spinner-DownButton",
                                           "width",
                                           spinner );
    return Math.max( upButtonWidth, downButtonWidth );
  }
}
