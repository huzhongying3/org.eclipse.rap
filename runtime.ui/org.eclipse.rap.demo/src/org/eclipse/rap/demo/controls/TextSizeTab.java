/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Color;
import org.eclipse.rap.rwt.widgets.*;

public class TextSizeTab extends ExampleTab {

  private static String[] testStrings;
  
  private boolean propFixedSize;
  
  private int nextIndex = 0;
  
  private String labelText = "";

  public TextSizeTab( final TabFolder parent ) {
    super( parent, "TextSize" );
    testStrings = createTestStrings();
    switchText();
  }

  protected void createStyleControls( ) {
    createStyleButton( "WRAP" );
    createFontChooser();
    final Button fixedSizeButton = createPropertyButton( "Fixed Size" );
    fixedSizeButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        propFixedSize = fixedSizeButton.getSelection();
        createNew();
      }
    } );
    Button nextButton = createPropertyButton( "Next Text", RWT.PUSH );
    nextButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        switchText();
        createNew();
      }
    } );
  }

  protected void createExampleControls( final Composite parent ) {
    int style = getStyle();
    
    Label label1 = new Label( parent, style );
    label1.setBackground( Color.getColor( 0xcc, 0xb7, 0x91 ) );
    label1.setText( labelText );
    label1.setLocation( 10, 10 );
    if( propFixedSize ) {
      label1.setSize( label1.computeSize( 200, RWT.DEFAULT ) );
    } else {
      label1.pack();
    }
    
    registerControl( label1 );
  }
  
  private void switchText() {
    labelText = testStrings[ nextIndex ];
    nextIndex = (nextIndex + 1) % testStrings.length;
  }
  
  private static String[] createTestStrings() {
    String[] result = new String[] {
      "Lorem ipsum",
      "Lorem ipsum dolor sit amet",
      "Lorem ipsum dolor sit amet, consectetur adipisici "
      + "elit, sed do eiusmod tempor incididunt ut labore et "
      + "dolore magna aliqua.",
      
      "Lorem ipsum dolor sit amet, consectetur adipisici "
      + "elit, sed do eiusmod tempor incididunt ut labore et "
      + "dolore magna aliqua.\n"
      + "Ut enim ad minim veniam, quis nostrud exercitation "
      + "ullamco laboris nisi ut aliquip ex ea commodo "
      + "consequat.\n",
      
      "Lorem ipsum dolor sit amet, consectetur adipisici "
      + "elit, sed do eiusmod tempor incididunt ut labore et "
      + "dolore magna aliqua. "
      + "Ut enim ad minim veniam, quis nostrud exercitation "
      + "ullamco laboris nisi ut aliquip ex ea commodo "
      + "consequat. ",
      
      ""
    };
    return result;
  }
}
