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
qx.OO.defineClass( 
  "org.eclipse.swt.widgets.CoolItem", 
  qx.ui.layout.CanvasLayout,
  function( orientation ) {
    qx.ui.layout.CanvasLayout.call( this );
    this.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    this._orientation = orientation;
    // Create handle to drag this CoolItem around
    this._handle = new qx.ui.basic.Terminator();
    this._handle.setBorder( qx.renderer.border.BorderPresets.getInstance().thinOutset );
    this._handle.setCursor( org.eclipse.swt.widgets.CoolItem.DRAG_CURSOR );
    this._handle.addEventListener( "mousedown", this._onHandleMouseDown, this );
    this._handle.addEventListener( "mousemove", this._onHandleMouseMove, this );
    this._handle.addEventListener( "mouseup", this._onHandleMouseUp, this );
    this.add( this._handle );
    // buffers zIndex and background during drag to be restored when dropped
    this._bufferedZIndex = null;
    this._bufferedBackground = null;
  }
);

// TODO [rh] move to a central place, e.g. qx.constant.Style or similar
org.eclipse.swt.widgets.CoolItem.DRAG_CURSOR = "w-resize";

qx.Proto.setLocked = function( value ) {
  this._handle.setVisibility( !value );
}

/** Updates the size and position of the handle. */
qx.Proto.updateHandleBounds = function() {
  // parameter order for setSpace: x, width, y, height
  if( this._orientation == "vertical" ) {
    this._handle.setSpace( 0, this.getWidth(), 0, 3 );
  } else {
    this._handle.setSpace( 0, 3, 0, this.getHeight() );
  }
}

/** React on mouseDown events on _handle widget. */
qx.Proto._onHandleMouseDown = function( evt ) {
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
}

/** React on mouseMove events on _handle widget. */
qx.Proto._onHandleMouseMove = function( evt ) {
  if( this._handle.getCapture() ) {
    this.setLeft( evt.getPageX() - this._offsetX );
    this.setTop( evt.getPageY() - this._offsetY );
  }
}

/** React on mouseUp events on _handle widget. */
qx.Proto._onHandleMouseUp = function( evt ) {
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
}

qx.Proto.dispose = function() {
  if( this.getDisposed() ) {
    return true;
  }
  this._handle.removeEventListener( "mousedown", this._onHandleMouseDown, this );
  this._handle.removeEventListener( "mousemove", this._onHandleMouseMove, this );
  this._handle.removeEventListener( "mouseup", this._onHandleMouseUp, this );
  if( this._handle != null ) {
    this._handle.dispose();
  }
  return qx.ui.layout.CanvasLayout.prototype.dispose.call( this );
}


qx.Proto._findBackground = function() {
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