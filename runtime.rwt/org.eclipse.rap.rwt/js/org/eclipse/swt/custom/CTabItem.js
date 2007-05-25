
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
qx.Class.define("org.eclipse.swt.custom.CTabItem", {
  extend : qx.ui.basic.Atom,

  construct : function( canClose, closeToolTipText ) {
    qx.ui.basic.Atom.call( this );
    this.setAppearance( "c-tab-item" );
    this.setVerticalChildrenAlign( "middle" );
    this.setHorizontalChildrenAlign( "left" );
    this.setTabIndex( -1 );
    this._selected = false;
    this._closeButton = null;
    this._unselectedCloseVisible = true;
    this._selectionBackground = null;
    this._selectionForeground = null;
    if( canClose ) {
      this._closeButton = new qx.ui.basic.Image();
      this._closeButton.setWidth( 20 );
      this._closeButton.setHeight( "80%" );
      this._closeButton.addEventListener( "click", this._onClose, this );
      if( closeToolTipText != null ) {
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        wm.setToolTip( this._closeButton, closeToolTipText );
      }
      this._updateCloseButton();
      this.add( this._closeButton );
    }
    this._control = null;
    this.addEventListener( "mouseover", this._onMouseOver, this );
    this.addEventListener( "mouseout", this._onMouseOut, this );
    this.addEventListener( "click", this._onClick, this );
    this.addEventListener( "dblclick", this._onDblClick, this );
  },

  statics : {
    IMG_CLOSE : "resource/widget/rap/ctabfolder/close.gif",
    IMG_CLOSE_HOVER : "resource/widget/rap/ctabfolder/close_hover.gif"
  },

  members : {
    dispose : function() {
      if( this.getDisposed() ) {
        return;
      }
      this.removeEventListener( "mouseover", this._onMouseOver, this );
      this.removeEventListener( "mouseout", this._onMouseOut, this );
      this.removeEventListener( "click", this._onClick, this );
      this.removeEventListener( "dblclick", this._onDblClick, this );
      if( this._closeButton != null ) {
        this._closeButton.removeEventListener( "click", this._onClose, this );
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        wm.setToolTip( this._closeButton, null );
        this._closeButton.dispose();
        this._closeButton = null;
      }
      return qx.ui.basic.Atom.prototype.dispose.call(this);
    },

    setControl : function( control ) {
      this._control = control;
    },

    setSelected : function( selected ) {
      this._selected = selected;
      if( selected ) {
        this.addState( "checked" );
        this.setBackgroundColor( this._selectionBackground );
        this.setTextColor( this._selectionForeground );
      }
      else {
        this.removeState( "checked" );
        this.setBackgroundColor( null );
        this.setTextColor( null );
      }
      this._updateCloseButton();
    },

    isSelected : function() {
      return this._selected;
    },

    setUnselectedCloseVisible : function( value ) {
      this._unselectedCloseVisible = value;
    },

    setSelectionBackground : function( color ) {
      this._selectionBackground = color;
      if( this.isSelected() ) {
        this.setBackgroundColor( this._selectionBackground );
      }
    },

    setSelectionForeground : function(color) {
      this._selectionForeground = color;
      if( this.isSelected() ) {
        this.setTextColor( this._selectionForeground );
      }
    },

    _applyStateAppearance : function() {
      this._states.firstChild = this.isFirstVisibleChild();
      this._states.lastChild = this.isLastVisibleChild();
      this._states.alignLeft = true;
      this._states.barTop = true;
      this._states.checked = this.isSelected();
      qx.ui.basic.Atom.prototype._applyStateAppearance.call(this);
    },

    _updateCloseButton : function() {
      if( this._closeButton != null ) {
        var visible 
          =      ( !this._unselectedCloseVisible && this.isSelected() ) 
              && ( this.hasState( "over" ) || this.isSelected() );
        this._closeButton.setVisibility( visible );

        if( this._closeButton.hasState( "over" ) ) {
          this._closeButton.setSource( org.eclipse.swt.custom.CTabItem.IMG_CLOSE_HOVER );
        } else {
          this._closeButton.setSource( org.eclipse.swt.custom.CTabItem.IMG_CLOSE );
        }
      }
    },

    _onMouseOver : function( evt ) {
      this.addState( "over" );
      if (evt.getTarget() == this._closeButton) {
        this._closeButton.addState( "over" );
      }
      this._updateCloseButton();
    },

    _onMouseOut : function( evt ) {
      this.removeState( "over" );
      if( evt.getTarget() == this._closeButton ) {
        this._closeButton.removeState( "over" );
      }
      this._updateCloseButton();
    },

    _onClick : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        if( evt.getTarget() != this._closeButton ) {
          evt.getTarget().getParent()._notifyItemClick( evt.getTarget() );
        }
      }
    },

    _onDblClick : function( evt ) {
      if( evt.getTarget() != this._closeButton ) {
        evt.getTarget().getParent()._notifyItemDblClick( evt.getTarget() );
      }
    },

    _onClose : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var req = org.eclipse.swt.Request.getInstance();
        var id = widgetManager.findIdByWidget( this );
        req.addEvent( "org.eclipse.swt.events.ctabItemClosed", id );
        req.send();
      }
    }
  }
});
