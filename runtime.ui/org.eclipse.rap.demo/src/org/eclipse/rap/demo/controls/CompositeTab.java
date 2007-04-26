/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

public class CompositeTab extends ExampleTab {

  public CompositeTab( final TabFolder parent ) {
    super( parent, "Composite" );
  }

  protected void createStyleControls() {
    createStyleButton( "BORDER" );
    createVisibilityButton();
    createBgColorButton();
  }

  protected void createExampleControls( final Composite top ) {
    top.setLayout( new FillLayout() );
    int style = getStyle();
    final Composite comp = new Composite( top, style );
    registerControl( comp );
  }
}
