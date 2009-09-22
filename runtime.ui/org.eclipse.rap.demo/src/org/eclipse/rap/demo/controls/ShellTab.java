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

package org.eclipse.rap.demo.controls;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class ShellTab extends ExampleTab {

  private static final String ICON_IMAGE_PATH = "resources/newfile_wiz.gif";

  private final java.util.List shells;
  private int shellCounter;
  private final ShellAdapter confirmCloseListener;
  private Image shellImage;
  private Button createInvisibleButton;
  private Button createAsDialogButton;
  private Button createWithMenuButton;
  private Button showClientAreaButton;
  private Button confirmCloseButton;
//  private Button customBgColorButton;

  private int alpha = 255;

  public ShellTab( final CTabFolder topFolder ) {
    super( topFolder, "Shell" );
    shells = new ArrayList();
    confirmCloseListener = new ShellAdapter() {
      public void shellClosed( final ShellEvent event ) {
        Shell shell = ( Shell )event.widget;
        String msg = "Close " + shell.getText() + "?";
        boolean canClose = MessageDialog.openConfirm( shell, "Confirm", msg );
        event.doit = canClose;
      }
    };
    setDefaultStyle( SWT.SHELL_TRIM );
  }

  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "SHELL_TRIM", SWT.SHELL_TRIM, true );
    createStyleButton( "DIALOG_TRIM", SWT.DIALOG_TRIM );
    createStyleButton( "APPLICATION_MODAL", SWT.APPLICATION_MODAL );
    createStyleButton( "TITLE", SWT.TITLE );
    createStyleButton( "MIN", SWT.MIN );
    createStyleButton( "MAX", SWT.MAX );
    createStyleButton( "CLOSE", SWT.CLOSE );
    createStyleButton( "RESIZE", SWT.RESIZE );
    createStyleButton( "TOOL", SWT.TOOL );
    createStyleButton( "SHEET", SWT.SHEET );
    createStyleButton( "ON_TOP", SWT.ON_TOP );
    createInvisibleButton = createPropertyButton( "Create invisible" );
    createAsDialogButton = createPropertyButton( "Create as dialog" );
    createWithMenuButton = createPropertyButton( "Add menu" );
    showClientAreaButton = createPropertyButton( "Show client area" );
    confirmCloseButton = createPropertyButton( "Confirm Close" );
//    customBgColorButton = createPropertyButton( "Custom background" );
    createAlphaControls( parent );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new RowLayout( SWT.VERTICAL ) );
    if( shellImage == null ) {
      ClassLoader classLoader = getClass().getClassLoader();
      shellImage = Graphics.getImage( ICON_IMAGE_PATH, classLoader );
    }
    Button openShellButton = new Button( parent, SWT.PUSH );
    openShellButton.setText( "Open Shell" );
    openShellButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        createShell();
      }} );

    Button bringFirstToTopButton = new Button( parent, SWT.PUSH );
    bringFirstToTopButton.setText( "Bring first Shell to top" );
    bringFirstToTopButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        Shell[] shells = getShells();
        if( shells.length > 0 ) {
          shells[ 0 ].open();
        }
      }
    } );

    Button showAllButton = new Button( parent, SWT.PUSH );
    showAllButton.setText( "Show All Shells" );
    showAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        setShellsVisible( true );
      }
    } );

    Button hideAllButton = new Button( parent, SWT.PUSH );
    hideAllButton.setText( "Hide All Shells" );
    hideAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        setShellsVisible( false );
      }
    } );

    Button MaximizeAllButton = new Button( parent, SWT.PUSH );
    MaximizeAllButton.setText( "Maximize All Shells" );
    MaximizeAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        setShellsMaximized( true );
      }
    } );

    Button minimizeAllButton = new Button( parent, SWT.PUSH );
    minimizeAllButton.setText( "Minimize All Shells" );
    minimizeAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        setShellsMinimized( true );
      }
    } );

    Button restoreAllButton = new Button( parent, SWT.PUSH );
    restoreAllButton.setText( "Restore All Shells" );
    restoreAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        setShellsMinimized( false );
        setShellsMaximized( false );
      }
    } );

    Button enableAllButton = new Button( parent, SWT.PUSH );
    enableAllButton.setText( "Enable All Shells" );
    enableAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        setShellsEnabled( true );
      }
    } );

    Button disableAllButton = new Button( parent, SWT.PUSH );
    disableAllButton.setText( "Disable All Shells" );
    disableAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        setShellsEnabled( false );
      }
    } );

    Button closeAllButton = new Button( parent, SWT.PUSH );
    closeAllButton.setText( "Close All Shells" );
    closeAllButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        closeShells();
      }} );
  }

  private void createAlphaControls( final Composite parent ) {
    Composite comp = new Composite( parent, SWT.NONE );
    comp.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( comp, SWT.NONE );
    label.setText( "Alpha" );
    final Text text = new Text( comp, SWT.BORDER );
    text.setText( "255" );
    Button button = new Button( comp, SWT.PUSH );
    button.setText( "set" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        try {
          alpha = Integer.parseInt( text.getText() );
        } catch( NumberFormatException e1 ) {
          // ignore
        }
      }
    } );
  }

  private void createShell() {
    Shell shell;
    if( createAsDialogButton.getSelection() ) {
      shell = new Shell( getShell(), getStyle() );
    } else {
      shell = new Shell( getShell().getDisplay(), getStyle() );
    }
//    if( customBgColorButton.getSelection() ) {
//      shell.setBackground( BG_COLOR_BROWN );
//    }
    shell.setLocation( getNextShellLocation() );
    if( true ) {
      createShellContents1( shell );
    } else {
      createShellContents2( shell );
    }
    shellCounter++;
    shell.setText( "Test Shell " + shellCounter );
    shell.setAlpha( alpha );
    shell.setImage( shellImage );
    if( confirmCloseButton.getSelection() ) {
      shell.addShellListener( confirmCloseListener );
    }
    if( !createInvisibleButton.getSelection() ) {
      shell.open();
    }
    shells.add( shell );
  }

  /*
   * Creates a shell with a size of 300 x 200 px and displays the bounds of its
   * client area.
   */
  private void createShellContents1( final Shell shell ) {
    shell.setSize( 300, 200 );
    if( createWithMenuButton.getSelection() ) {
      createMenuBar( shell );
    }
    final Composite comp1 = new Composite( shell, SWT.NONE );
    final Composite comp2 = new Composite( shell, SWT.NONE );
    comp2.moveAbove( comp1 );
    if( showClientAreaButton.getSelection() ) {
      comp1.setBackground( Graphics.getColor( 200, 0, 0 ) );
      comp2.setBackground( Graphics.getColor( 200, 200, 200 ) );
    }
    Rectangle ca = shell.getClientArea();
    comp1.setBounds( ca.x, ca.y, ca.width, ca.height );
    comp2.setBounds( ca.x + 1, ca.y + 1, ca.width - 2, ca.height - 2 );
    Button closeButton = new Button( shell, SWT.PUSH );
    closeButton.setText( "Close This Shell" );
    closeButton.pack();
    closeButton.moveAbove( comp2 );
    int centerX = ( ca.width - ca.x ) / 2;
    closeButton.setLocation( centerX - closeButton.getSize().x / 2, ca.height - 45 );
    closeButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        shell.close();
      }
    } );
    shell.addControlListener( new ControlAdapter() {
      public void controlResized( final ControlEvent event ) {
        Rectangle ca = shell.getClientArea();
        comp1.setBounds( ca.x, ca.y, ca.width, ca.height );
        comp2.setBounds( ca.x + 1, ca.y + 1, ca.width - 2, ca.height - 2 );
      }
    } );
  }

  /*
   * Alternative implementation:
   * Creates a shell with that contains only a button with a predefined size of
   * 140 x 40 px. Can be used to test the Shell.computeTrim mehtod.
   */
  private void createShellContents2( final Shell shell ) {
    if( createWithMenuButton.getSelection() ) {
      createMenuBar( shell );
    }
    RowLayout layout = new RowLayout();
    layout.marginLeft = 0;
    layout.marginTop = 0;
    layout.marginRight = 0;
    layout.marginBottom = 0;
    shell.setLayout( layout );
    Button closeButton = new Button( shell, SWT.PUSH );
    closeButton.setText( "Close This Window" );
    closeButton.setBackground( Graphics.getColor( 25, 55, 55 ) );
    closeButton.setLayoutData( new RowData( 140, 40 ) );
    closeButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        shell.close();
      }
    } );
  }

  private void createMenuBar( final Shell shell ) {
    // menu bar
    Menu menuBar = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menuBar );
    MenuItem fileItem = new MenuItem( menuBar, SWT.CASCADE );
    fileItem.setText( "File" );
    MenuItem editItem = new MenuItem( menuBar, SWT.CASCADE );
    editItem.setText( "Edit" );
    MenuItem searchItem = new MenuItem( menuBar, SWT.CASCADE );
    searchItem.setText( "Search" );
    MenuItem disabledItem = new MenuItem( menuBar, SWT.CASCADE );
    disabledItem.setText( "Disabled" );
    disabledItem.setEnabled( false );
    new MenuItem( menuBar, SWT.CASCADE ).setText( "Item 6" );
    new MenuItem( menuBar, SWT.CASCADE ).setText( "Item 7" );
    new MenuItem( menuBar, SWT.CASCADE ).setText( "Item 8" );
    new MenuItem( menuBar, SWT.CASCADE ).setText( "Item 9" );
    // file menu
    Menu fileMenu = new Menu( shell, SWT.DROP_DOWN );
    fileItem.setMenu( fileMenu );
    MenuItem newItem = new MenuItem( fileMenu, SWT.PUSH );
    newItem.setText( "New" );
    newItem.setImage( Graphics.getImage( "resources/newfile_wiz.gif" ) );
    new MenuItem( fileMenu, SWT.PUSH ).setText( "Open" );
    new MenuItem( fileMenu, SWT.PUSH ).setText( "Close" );
    // edit menu
    Menu editMenu = new Menu( shell, SWT.DROP_DOWN );
    editItem.setMenu( editMenu );
    MenuItem item;
    new MenuItem( editMenu, SWT.PUSH ).setText( "Copy" );
    new MenuItem( editMenu, SWT.PUSH ).setText( "Paste" );
    new MenuItem( editMenu, SWT.SEPARATOR );
    // cascade menu
    item = new MenuItem( editMenu, SWT.CASCADE );
    item.setText( "Insert" );
    Menu cascadeMenu = new Menu( shell, SWT.DROP_DOWN );
    item.setMenu( cascadeMenu );
    new MenuItem( cascadeMenu, SWT.PUSH ).setText( "Date" );
    new MenuItem( cascadeMenu, SWT.PUSH ).setText( "Line Break" );
    // search
    Menu searchMenu = new Menu( shell, SWT.DROP_DOWN );
    searchItem.setMenu( searchMenu );
    new MenuItem( searchMenu, SWT.PUSH ).setText( "Enabled" );
    item = new MenuItem( searchMenu, SWT.PUSH );
    item.setText( "Disabled" );
    item.setEnabled( false );
    new MenuItem( searchMenu, SWT.PUSH ).setText( "Push" );
    new MenuItem( searchMenu, SWT.SEPARATOR );
    item = new MenuItem( searchMenu, SWT.CHECK );
    item.setText( "Check" );
    item = new MenuItem( searchMenu, SWT.RADIO );
    item.setText( "Radio 1" );
    item = new MenuItem( searchMenu, SWT.RADIO );
    item.setText( "Radio 2" );
    item = new MenuItem( searchMenu, SWT.RADIO );
    item.setText( "Radio 3" );
    item.setEnabled( false );
    // disabled
    Menu disabledMenu = new Menu( shell, SWT.DROP_DOWN );
    disabledMenu.setEnabled( false );
    disabledItem.setMenu( disabledMenu );
    new MenuItem( disabledMenu, SWT.PUSH ).setText( "Import" );
    new MenuItem( disabledMenu, SWT.PUSH ).setText( "Export" );
  }

  private Point getNextShellLocation() {
    Point result = getShell().getLocation();
    int count = getShells().length % 12;
    result.x += 50 + count * 10;
    result.y += 50 + count * 10;
    return result ;
  }

  private void closeShells() {
    Shell[] shells2 = getShells();
    for( int i = 0; i < shells2.length; i++ ) {
      shells2[ i ].removeShellListener( confirmCloseListener );
      shells2[ i ].close();
      shells2[ i ].dispose();
    }
  }

  private void setShellsVisible( final boolean visible ) {
    Shell[] shells = getShells();
    for( int i = 0; i < shells.length; i++ ) {
      shells[ i ].setVisible( visible );
    }
  }

  private void setShellsEnabled( final boolean enabled ) {
    Shell[] shells = getShells();
    for( int i = 0; i < shells.length; i++ ) {
      shells[ i ].setEnabled( enabled );
    }
  }

  private void setShellsMinimized( final boolean minimized ) {
    Shell[] shells = getShells();
    for( int i = 0; i < shells.length; i++ ) {
      shells[ i ].setMinimized( minimized );
    }
  }

  private void setShellsMaximized( final boolean maximized ) {
    Shell[] shells = getShells();
    for( int i = 0; i < shells.length; i++ ) {
      shells[ i ].setMaximized( maximized );
    }
  }

  private Shell[] getShells() {
    // remove eventually disposed of shells (may happen when shells without
    // ShellListeners are created and close by user interaction)
    Shell[] result;
    Iterator iter = shells.iterator();
    while( iter.hasNext() ) {
      Shell shell = ( Shell )iter.next();
      if( shell.isDisposed() ) {
        iter.remove();
      }
    }
    result = new Shell[ shells.size() ];
    shells.toArray( result );
    return result;
  }
}
