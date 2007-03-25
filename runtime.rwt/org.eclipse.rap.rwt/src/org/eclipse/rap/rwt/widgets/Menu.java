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
import org.eclipse.rap.rwt.graphics.Point;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.widgets.IItemHolderAdapter;
import org.eclipse.rap.rwt.internal.widgets.ItemHolder;

public class Menu extends Widget {

  private final Shell parent;
  private final ItemHolder itemHolder;
  private int x;
  private int y;
  private boolean visible = false;
  MenuItem cascade;

  public Menu( final Menu menu ) {
    this( menu.getParent(), RWT.DROP_DOWN );
  }

  public Menu( final MenuItem parent ) {
    this( parent.getParent().getParent(), RWT.DROP_DOWN );
  }

  public Menu( final Control parent ) {
    this( parent.getShell(), RWT.POP_UP );
  }

  public Menu( final Shell parent, final int style ) {
    super( parent, checkStyle( style ) );
    this.parent = parent;
    itemHolder = new ItemHolder( MenuItem.class );
    MenuHolder.addMenu( parent, this );
  }

  public final Display getDisplay() {
    checkWidget();
    return parent.getDisplay();
  }

  public final Shell getParent() {
    checkWidget();
    return parent;
  }

  public MenuItem getParentItem() {
    checkWidget ();
    return cascade;
  }
  
  public Menu getParentMenu() {
    checkWidget();
    if( cascade != null ) {
      return cascade.getParent();
    }
    return null;
  }
  
  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IItemHolderAdapter.class ) {
      result = itemHolder;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }
  
  public void setLocation( final int x, final int y ) {
    checkWidget();
    if( ( style & ( RWT.BAR | RWT.DROP_DOWN ) ) == 0 ) {
      this.x = x;
      this.y = y;
    }
  }
  
  public void setLocation( final Point location ) {
    checkWidget();
    if( location == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    setLocation( location.x, location.y );
  }
  
  public Rectangle getBounds() {
    checkWidget();
    // TODO: [fappel] how to calculate width and height?
    return new Rectangle( x, y, 0, 0 );
  }
  
  ///////////
  // Visible
  
  public void setVisible( final boolean visible ) {
    checkWidget();
    if( ( style & ( RWT.BAR | RWT.DROP_DOWN ) ) == 0 ) {
      this.visible = visible;
    }
  }
  
  public boolean isVisible(){
    checkWidget();
    return visible;
  }

  ///////////
  // Enabled
  
  public void setEnabled( final boolean enabled ) {
    checkWidget();
    state &= ~DISABLED;
    if( !enabled ) {
      state |= DISABLED;
    }
  }

  public boolean getEnabled() {
    checkWidget();
    return ( state & DISABLED ) == 0;
  }

  public boolean isEnabled() {
    checkWidget();
    Menu parentMenu = getParentMenu();
    if( parentMenu == null ) {
      return getEnabled();
    }
    return getEnabled() && parentMenu.isEnabled();
  }
  
  ////////////////////////////
  // Management of menu items
  
  public int getItemCount() {
    checkWidget();
    return itemHolder.size();
  }

  public MenuItem[] getItems() {
    checkWidget();
    return ( MenuItem[] )itemHolder.getItems();
  }

  public MenuItem getItem( final int index ) {
    checkWidget();
    return ( MenuItem )itemHolder.getItem( index );
  }
  
  public int indexOf( final MenuItem menuItem ) {
    checkWidget();
    if( menuItem == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( menuItem.isDisposed() ) {
      RWT.error( RWT.ERROR_INVALID_ARGUMENT );
    }
    return itemHolder.indexOf( menuItem );
  }
  
  ////////////////////
  // Widget overrides
  
  // TODO [rh] disposal of Menu and its items not yet completely implemented
  protected final void releaseChildren() {
    MenuItem[] menuItems = ( MenuItem[] )ItemHolder.getItems( this );
    for( int i = 0; i < menuItems.length; i++ ) {
      menuItems[ i ].dispose();
    }
  }

  protected final void releaseParent() {
    // do nothing
  }

  protected final void releaseWidget() {
    MenuHolder.removeMenu( parent, this );
  }

  //////////////////
  // Helping methods
  
  private static int checkStyle( final int style ) {
    return checkBits( style, RWT.POP_UP, RWT.BAR, RWT.DROP_DOWN, 0, 0, 0 );
  }
}
