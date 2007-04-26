/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

public class CBannerTab extends ExampleTab {

  public CBannerTab( final TabFolder parent ) {
    super( parent, "CBanner" );
  }

  protected void createStyleControls() {
    createStyleButton( "BORDER" );
  }

  protected void createExampleControls( final Composite top ) {
    top.setLayout( new RowLayout(SWT.VERTICAL) );
    int style = getStyle();
    
    CBanner banner = new CBanner( top, style );
//    banner.setBackground( Color.getColor( 240, 250, 190 ) );
    
    Label rightLabel = new Label( banner, SWT.NONE );
    rightLabel.setText( "Right" );
//    rightLabel.setBackground( Color.getColor( 250, 250, 250 ) );
    banner.setRight( rightLabel );
    
    Label leftLabel = new Label( banner, SWT.NONE );
    leftLabel.setText( "Left" );
//    leftLabel.setBackground( Color.getColor( 250, 250, 250 ) );
    banner.setLeft( leftLabel );
    
//    Label bottomLabel = new Label( banner, SWT.NONE );
//    bottomLabel.setText( "Bottom" );
//    bottomLabel.setBackground( Color.getColor( 250, 250, 250 ) );
//    banner.setBottom( bottomLabel );
//    
//    Label contentLabel = new Label( banner, SWT.BORDER );
//    contentLabel.setToolTipText( "Content" );
  }
}
