/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.widgets;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.widgets.IShellAdapter;
import org.eclipse.rap.rwt.widgets.MenuHolder.IMenuHolderAdapter;

/**
 * TODO: [fappel] comment
 * <p>
 * </p>
 */
public class Shell extends Composite {

  // TODO [rh] preliminary: constants extracted to be used in MenuLCA
  public static final int TITLE_BAR_HEIGHT = 15;
  public static final int MENU_BAR_HEIGHT = 20;
  
  private final Display display;
  private Menu menuBar;
  private MenuHolder menuHolder;
  private DisposeListener menuBarDisposeListener;
  private Control lastActive;
  private IShellAdapter shellAdapter;

  // TODO [rh] preliminary: yet no null-check, default/current substitute, etc
  public Shell( final Display display, final int style ) {
    super();
    this.display = display;
    display.addShell( this );
  }

  public final Shell getShell() {
    return this;
  }

  public final Display getDisplay() {
    return display;
  }

  public Rectangle getClientArea() {
    Rectangle current = getBounds();
    int width = current.width;
    int height = current.height;
    int hTitleBar = TITLE_BAR_HEIGHT;
    if( getMenuBar() != null ) {
      hTitleBar += MENU_BAR_HEIGHT;
    }
    int border = 5;
    return new Rectangle( border - 3,
                          hTitleBar + border,
                          width - border * 2,
                          height - ( hTitleBar + border * 2 ) - 3 );
  }

  public void setMenuBar( final Menu menuBar ) {
    if( this.menuBar != menuBar ) {
      if( menuBar != null ) {
        if( menuBar.isDisposed() ) {
          RWT.error( RWT.ERROR_INVALID_ARGUMENT );
        }
        if( menuBar.getParent() != this ) {
          RWT.error( RWT.ERROR_INVALID_PARENT );
        }
        if( ( menuBar.getStyle() & RWT.BAR ) == 0 ) {
          RWT.error( RWT.ERROR_MENU_NOT_BAR );
        }
      }
      removeMenuBarDisposeListener();
      this.menuBar = menuBar;
      addMenuBarDisposeListener();
    }
  }

  public Menu getMenuBar() {
    return menuBar;
  }

  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IMenuHolderAdapter.class ) {
      if( menuHolder == null ) {
        menuHolder = new MenuHolder();
      }
      result = menuHolder;
    } else if( adapter == IShellAdapter.class ) {
      if( shellAdapter == null ) {
        shellAdapter = new IShellAdapter() {
          public Control getActiveControl() {
            return Shell.this.lastActive;
          }
          public void setActiveControl( final Control control ) {
            Shell.this.setActiveControl( control );
          }
        };
      }
      result = shellAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  public void close() {
    ShellEvent shellEvent = new ShellEvent( this, ShellEvent.SHELL_CLOSED );
    shellEvent.processEvent();
    dispose();
  }

  // ///////////////////////////////////////////////
  // Event listener registration and deregistration
  
  public void addShellListener( final ShellListener listener ) {
    ShellEvent.addListener( this, listener );
  }

  public void removeShellListener( final ShellListener listener ) {
    ShellEvent.removeListener( this, listener );
  }

  // //////////
  // Overrides
  
  protected final void releaseParent() {
    display.removeShell( this );
  }

  protected final void releaseWidget() {
    removeMenuBarDisposeListener();
  }

  // ///////////////////////////////////////////////////////
  // Helping methods to observe the disposal of the menuBar
  
  private void addMenuBarDisposeListener() {
    if( menuBar != null ) {
      if( menuBarDisposeListener == null ) {
        menuBarDisposeListener = new DisposeListener() {

          public void widgetDisposed( final DisposeEvent event ) {
            Shell.this.menuBar = null;
          }
        };
      }
      menuBar.addDisposeListener( menuBarDisposeListener );
    }
  }

  private void removeMenuBarDisposeListener() {
    if( menuBar != null ) {
      menuBar.removeDisposeListener( menuBarDisposeListener );
    }
  }
  
  ////////////////////////////////////////////////////////////
  // Methods to maintain activeControl and send ActivateEvents

  private void setActiveControl( final Control activateControl ) {
    Control control = activateControl;
    if( control != null && control.isDisposed() ) {
      control = null;
    }
    if( lastActive != null && lastActive.isDisposed() ) {
      lastActive = null;
    }
    if( lastActive != control ) {
      // Compute the list of controls to be activated and deactivated by finding
      // the first common parent control.
      Control[] deactivate 
        = ( lastActive == null ) ? new Control[ 0 ] : getPath( lastActive );
      Control[] activate 
        = ( control == null ) ? new Control[ 0 ] : getPath( control );
      lastActive = control;
      
      int index = 0;
      int length = Math.min( activate.length, deactivate.length );
      while( index < length && activate[ index ] == deactivate[ index ] ) {
        index++;
      }
      // It is possible (but unlikely), that application code could have
      // destroyed some of the widgets. If this happens, keep processing those
      // widgets that are not disposed.
      ActivateEvent evt;
      for( int i = deactivate.length - 1; i >= index; --i ) {
        if( !deactivate[ i ].isDisposed() ) {
          evt = new ActivateEvent( deactivate[ i ], ActivateEvent.DEACTIVATED );
          evt.processEvent();
        }
      }
      for( int i = activate.length - 1; i >= index; --i ) {
        if( !activate[ i ].isDisposed() ) {
          evt = new ActivateEvent( activate[ i ], ActivateEvent.ACTIVATED );
          evt.processEvent();
        }
      }
    }
  }

  private Control[] getPath( final Control ctrl ) {
    int count = 0;
    Control control = ctrl;
    while( control != this ) {
      count++;
      control = control.getParent();
    }
    control = ctrl;
    Control[] result = new Control[ count ];
    while( control != this ) {
      count--;
      result[ count ] = control;
      control = control.getParent();
    }
    return result;
  }
}
