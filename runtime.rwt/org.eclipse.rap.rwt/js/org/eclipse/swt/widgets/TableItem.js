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

/**
 * This class provides the client-side counterpart for 
 * org.eclipse.swt.TableItem.
 */
qx.OO.defineClass(
  "org.eclipse.swt.widgets.TableItem",
  qx.core.Target,
  function( parent, index ) {
    qx.core.Target.call( this, false );  // false == no auto-dispose
    this._parent = parent;
    // HACK: Table needs one 'emptyItem' to draw the remeining space that is
    //       not occupied by actual items. This item has an index of -1
    if( index != -1 ) {
      parent._addItem( this, index );
      this._texts = new Array();
    }
  }
);

qx.Class.DIV_START_OPEN = "<div ";
qx.Class.DIV_START_CLOSE = ">";
qx.Class.DIV_END = "</div>";

qx.Class.PX = "px;";

qx.Class.CONST_STYLE 
  = "position:absolute;"
  + "overflow:hidden;" 
  + "white-space:nowrap;"
  + "cursor:default;"
  + "top:0px;"
  + ( qx.core.Client.getInstance().isGecko() ? "-moz-user-select:none;" : "" );

qx.Class.LINE_BORDER
  = "border-right:1px solid #eeeeee;" 
  + "border-bottom:1px solid #eeeeee;" 
  + "border-top:1px solid #eeeeee;";

qx.Proto.dispose = function() {
  if( this.isDisposed() ) {
    return;
  }
  if( !this._parent.isDisposed() ) {
    this._parent._removeItem( this );
  }
  org.eclipse.swt.WidgetManager.getInstance().remove( this );
  return qx.core.Target.prototype.dispose.call( this );  
}

qx.Proto.getTable = function() {
  return this._parent;
}

qx.Proto.setText = function( index, text ) {
  this._texts[ index ] = text;  
}

/**
 * Called by Table when updating visible rows to obtain HTML markup that 
 * represents the item.
 */
qx.Proto._getMarkup = function() {
  var markup = new Array();
  for( var i = 0; i < this.getTable().getColumnCount(); i++ ) {
    var column = this.getTable().getColumn( i )
    var left = column.getLeft();
    var width = column.getWidth();
    var text;
    if( this._texts ) {
      text = this._texts[ i ];
      if( text == "" ) {
        text = "&nbsp;";  // fix wrong div-dimensions when no text
      } 
    } else {
      text = "&nbsp;";
    }
    var cellMarkup
      = org.eclipse.swt.widgets.TableItem.DIV_START_OPEN
      + "style=\""
      + org.eclipse.swt.widgets.TableItem.CONST_STYLE
      + this._borderMarkup()
      + "left:" + left + org.eclipse.swt.widgets.TableItem.PX
      + "width:" + width + org.eclipse.swt.widgets.TableItem.PX
      + "\""
      + org.eclipse.swt.widgets.TableItem.DIV_START_CLOSE
      + text
      + org.eclipse.swt.widgets.TableItem.DIV_END;
    markup.push( cellMarkup );
  }
  return markup.join( "" );
}

qx.Proto._borderMarkup = function() {
  var result = "";
  if( this.getTable().getLinesVisible() ) {
    result = org.eclipse.swt.widgets.TableItem.LINE_BORDER;
  }
  return result;
}
