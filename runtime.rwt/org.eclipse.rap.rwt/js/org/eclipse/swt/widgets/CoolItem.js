
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
/**
 * The parameter orientation must be one of "vertical" or "horizontal".
 * Note that updateHandleBounds must be called after each size manipulation.
 */
qx.Class.define( "org.eclipse.swt.widgets.CoolItem", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function( orientation ) {
    this.base( arguments );
    this.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    this._orientation = orientation;
    // Create handle to drag this CoolItem around
    this._handle = new qx.ui.basic.Terminator();
    this._handle.setAppearance( "coolitem-handle" );
    //this._handle.setHeight( "100%" );
    this._handle.addEventListener( "mousedown", this._onHandleMouseDown, this );
    this._handle.addEventListener( "mousemove", this._onHandleMouseMove, this );
    this._handle.addEventListener( "mouseup", this._onHandleMouseUp, this );
    this.add( this._handle );
    // buffers zIndex and background during drag to be restored when dropped
    this._bufferedZIndex = null;
    this._bufferedBackground = null;
  },

  destruct : function() {
    if( this._handle != null ) {
      this._handle.removeEventListener( "mousedown", this._onHandleMouseDown, this );
      this._handle.removeEventListener( "mousemove", this._onHandleMouseMove, this );
      this._handle.removeEventListener( "mouseup", this._onHandleMouseUp, this );
      this._handle.dispose();
    }
  },

  statics : {
    // TODO [rh] move to a central place, e.g. qx.constant.Style or similar
    DRAG_CURSOR : "w-resize"
  },

  members : {
    setLocked : function( value )  {
      this._handle.setDisplay( !value );
    },

    updateHandleBounds : function() {
      // parameter order for setSpace: x, width, y, height
//      this.debug( "_____ updateHandleBounds from " + this.getWidth() + "x" + this.getHeight() );
//      this.setBackgroundColor( "#ffff00" );
      if( this._orientation == "vertical" ) {
        this._handle.setSpace( 0, this.getWidth(), 0, 4 );
      } else {
        this._handle.setSpace( 0, 4, 0, this.getHeight() );
      }
    },

    _onHandleMouseDown : function( evt ) {
      this._handle.setCapture( true );
      this.getTopLevelWidget().setGlobalCursor( org.eclipse.swt.widgets.CoolItem.DRAG_CURSOR );
      this._offsetX = evt.getPageX() - this.getLeft();
      this._offsetY = evt.getPageY() - this.getTop();
      this._bufferedZIndex = this.getZIndex();
      this.setZIndex( Infinity );
      // In some cases the coolItem appeare transparent when dragged around
      // To fix this, walk along the parent hierarchy and use the first explicitly
      // set background color.
      this._bufferedBackground = this.getBackgroundColor();
      this.setBackgroundColor( this._findBackground() );
    },

    _onHandleMouseMove : function( evt ) {
      if( this._handle.getCapture() ) {
        this.setLeft( evt.getPageX() - this._offsetX );
        this.setTop( evt.getPageY() - this._offsetY );
      }
    },

    _onHandleMouseUp : function( evt ) {
      this._handle.setCapture( false );
      this.setZIndex( this._bufferedZIndex );
      this.setBackgroundColor( this._bufferedBackground );
      this.getTopLevelWidget().setGlobalCursor( null );

      // Send request that informs about dragged CoolItem
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addEvent( "org.eclipse.swt.events.widgetMoved", id );
        req.addParameter( id + ".bounds.x", this.getLeft() );
        req.addParameter( id + ".bounds.y", this.getTop() );
        req.send();
      }
    },

    _findBackground : function() {
      var hasParent = true;
      var result = null;
      var parent = this.getParent();
      while( hasParent && parent != null && result == null ) {
        if( parent.getBackgroundColor ) {
          result = parent.getBackgroundColor();
        }
        if( parent.getParent ) {
          parent = parent.getParent();
        } else {
          hasParent = false;
        }
      }
      return result;
    }
  }
});
