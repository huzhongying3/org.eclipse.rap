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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ContainmentTab extends ExampleTab {

  private Composite comp2;
  private Composite comp3;
  private Composite comp1;

  public ContainmentTab( final CTabFolder topFolder ) {
    super( topFolder, "Containment" );
  }

  protected void createStyleControls( final Composite parent ) {
    Button visibleButton = createVisibilityButton();
    visibleButton.setText( "Blue Visible" );
    visibleButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        changeVisible();
      }
    } );
    Button enabledButton = createEnablementButton();
    enabledButton.setText( "Blue Enabled" );
    enabledButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        changeEnabled();
      }
    } );
    createFgColorButton();
    createFontChooser();
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    int style = getStyle();
    FillLayout layout = new FillLayout();
    layout.marginWidth = 20;
    layout.marginHeight = 20;
    comp1 = new Composite( parent, style );
    comp1.setBackground( BG_COLOR_GREEN );
    comp1.setLayout( layout );
    comp2 = new Composite( comp1, style );
    comp2.setBackground( BG_COLOR_BLUE );
    comp2.setLayout( layout );
    comp3 = new Composite( comp2, style );
    comp3.setBackground( BG_COLOR_BROWN );
    comp3.setLayout( layout );
    Button button = new Button( comp3, SWT.PUSH );
    button.setText( "Button" );
    registerControl( comp2 );
  }

  private void changeVisible() {
    System.out.println();
    System.out.println( "Comp1" );
    System.out.println( "  getParent: " + comp1.getParent() );
    System.out.println( "  isVisible: " + comp1.isVisible() );
    System.out.println( "  getVisible: " + comp1.getVisible() );
    System.out.println( "Comp2" );
    System.out.println( "  getParent: " + comp2.getParent() );
    System.out.println( "  isVisible: " + comp2.isVisible() );
    System.out.println( "  getVisible: " + comp2.getVisible() );
    System.out.println( "Comp3" );
    System.out.println( "  getParent: " + comp3.getParent() );
    System.out.println( "  isVisible: " + comp3.isVisible() );
    System.out.println( "  getVisible: " + comp3.getVisible() );
  }

  private void changeEnabled() {
    System.out.println();
    System.out.println( "Comp1" );
    System.out.println( "  getParent: " + comp1.getParent() );
    System.out.println( "  isEnabled: " + comp1.isEnabled() );
    System.out.println( "  getEnabled: " + comp1.getEnabled() );
    System.out.println( "Comp2" );
    System.out.println( "  getParent: " + comp2.getParent() );
    System.out.println( "  isEnabled: " + comp2.isEnabled() );
    System.out.println( "  getEnabled: " + comp2.getEnabled() );
    System.out.println( "Comp3" );
    System.out.println( "  getParent: " + comp3.getParent() );
    System.out.println( "  isEnabled: " + comp3.isEnabled() );
    System.out.println( "  getEnabled: " + comp3.getEnabled() );
  }

}
