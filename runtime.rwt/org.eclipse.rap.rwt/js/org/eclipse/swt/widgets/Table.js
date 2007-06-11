
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
 * org.eclipse.swt.widgets.Table.
 * @event itemselected
 * @event itemdefaultselected
 * @event itemchecked
 */
qx.Class.define( "org.eclipse.swt.widgets.Table", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function( id, style ) {
    this.base( arguments );
    this.setAppearance( "table" );
    this.setHideFocus( true );
    // TODO [rh] this is preliminary and can be removed once a tabOrder is
    //      available
    this.setTabIndex( 1 );
    this.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    // Helper flag to swallow unwanted click-events while double-clicking
    this._suspendClicks = false;
    // Draw grid lines?
    this._linesVisible = false;
    // Default column width is used when there are no columns specified
    this._defaultColumnWidth = 0;
    // The item index that is currently displayed in the first visible row
    this._topIndex = 0;
    // indicates that topIndex was changed client-side (e.g. by scrolling)
    this._topIndexChanged = false;
    // Internally used fields to manage visible rows and scrolling
    this._itemHeight = 0;
    this._rows = new Array();
    this._items = new Array();
    this._checkBoxes = null;
    if( qx.lang.String.contains( style, "check" ) ) {
      this._checkBoxes = new Array();
    }
    // Determin multi-selection
    this._multiSelect = qx.lang.String.contains( style, "multi" );
    // Conains all item which are currently selected
    this._selected = new Array();
    // Denotes the focused TableItem 
    this._focusedItem = null;
    // An item only used to draw the area where no actual items are but that
    // needs to be drawn since the table bounds are grater than the number of
    // items
    this._emptyItem = new org.eclipse.swt.widgets.TableItem( this, -1 );
    // One resize line shown while resizing a column, provided for all columns  
    this._resizeLine = null;
    //
    var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
    // Construct a column area where columns go can be scrolled in
    this._columnArea = new qx.ui.layout.CanvasLayout();
    this._columnArea.setTop( 0 );
    this._columnArea.setLeft( 0 );
    this.add( this._columnArea );
    // Construct client area in which the table items will live
    this._clientArea = new qx.ui.layout.CanvasLayout();
    this._clientArea.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    this._clientArea.setTop( 20 );
    this._clientArea.setLeft( 0 );
    this._clientArea.addEventListener( "mousewheel", this._onClientAreaMouseWheel, this );
    this._clientArea.addEventListener( "appear", this._onClientAppear, this );
    // Create horizontal scrollBar
    this._horzScrollBar = new qx.ui.core.ScrollBar( true );
    this._horzScrollBar.setMergeEvents( true );
    this.add( this._horzScrollBar );
    this._horzScrollBar.setHeight( this._horzScrollBar.getPreferredBoxHeight() );
    this._horzScrollBar.addEventListener( "changeValue", this._onHorzScrollBarChangeValue, this );
    // Create vertical scrollBar
    this._vertScrollBar = new qx.ui.core.ScrollBar( false );
    this._vertScrollBar.setMergeEvents( true );
    this.add( this._vertScrollBar );
    this._vertScrollBar.setWidth( this._vertScrollBar.getPreferredBoxWidth() );
    this._vertScrollBar.addEventListener( "changeValue", this._onVertScrollBarChangeValue, this );
    // Listen to size changes to adjust client area size
    this.addEventListener( "changeWidth", this._onChangeSize, this );
    this.addEventListener( "changeHeight", this._onChangeSize, this );
    this.addEventListener( "changeEnabled", this._onChangeEnabled, this );
    // Listen to send event of request to report current state
    var req = org.eclipse.swt.Request.getInstance();
    req.addEventListener( "send", this._onSendRequest, this );
    //
    widgetManager.add( this._clientArea, id + "_clientArea", false );
    this.add( this._clientArea );
  },

  destruct : function() {
    var req = org.eclipse.swt.Request.getInstance();
    req.removeEventListener( "send", this._onSendRequest, this );
    this.removeEventListener( "changeWidth", this._onChangeSize, this );
    this.removeEventListener( "changeHeight", this._onChangeSize, this );
    this.removeEventListener( "changeEnabled", this._onChangeEnabled, this );
    this._emptyItem.dispose();
    var req = org.eclipse.swt.Request.getInstance();
    if( this._horzScrollBar ) {
      this._horzScrollBar.removeEventListener( "changeValue", this._onHorzScrollBarChangeValue, this );
      this._horzScrollBar.dispose();
      this._horzScrollBar = null;
    }
    if( this._vertScrollBar ) {
      this._vertScrollBar.removeEventListener( "changeValue", this._onVertScrollBarChangeValue, this );
      this._vertScrollBar.dispose();
      this._vertScrollBar = null;
    }
    if( this._clientArea ) {
      this._clientArea.removeEventListener( "mousewheel", this._onClientAreaMouseWheel, this );
      this._clientArea.removeEventListener( "appear", this._onClientAppear, this );
      this._clientArea.dispose();
      this._clientArea = null;
    }
    if( this._columnArea ) {
      this._columnArea.dispose();
      this._columnArea = null;
    }
    if( this._resizeLine ) {
      this._resizeLine.setParent( null );
      this._resizeLine.dispose();
    }
    if( this._rows ) {
      for( var i = 0; i < this._rows.length; i++ ) {
        this._rows[ i ].dispose();
      }
      this._rows = null;
    }
    if( this._checkBoxes != null ) {
      for( var i = 0; i < this._checkBoxes.length; i++ ) {
        this._checkBoxes[ i ].dispose();
      }
      this._checkBoxes = null;
    }
  },

  statics : {
    CHECK_WIDTH : 21,
    
    // Initialized at the end of the file
    ROW_BORDER : new qx.renderer.border.Border(),
    
    ////////////////////////////////////
    // Helper to determine modifier keys
    
    _isShiftOnlyPressed : function( evt ) {
      return    evt.isShiftPressed() 
             && !evt.isCtrlPressed() 
             && !evt.isAltPressed() 
             && !evt.isMetaPressed();      
    },
    
    _isCtrlOnlyPressed : function( evt ) {
      return    evt.isCtrlOrCommandPressed() 
             && !evt.isShiftPressed() 
             && !evt.isAltPressed();
    },
    
    _isCtrlShiftOnlyPressed : function( evt ) {
      return    evt.isCtrlOrCommandPressed() 
             && evt.isShiftPressed() 
             && !evt.isAltPressed();
    },
    
    _isMetaOnlyPressed : function( evt ) {
      return    evt.isAltPressed() 
             && !evt.isShiftPressed() 
             && !evt.isCtrlPressed();
    },
    
    _isNoModifierPressed : function( evt ) {
      return    !evt.isCtrlPressed() 
             && !evt.isShiftPressed() 
             && !evt.isAltPressed() 
             && !evt.isMetaPressed();      
    }
     
  },
  
  members : {

    setHeaderHeight : function( value ) {
      this._columnArea.setHeight( value );
      var columns = this._columnArea.getChildren();
      for( var i = 0; i < columns.length; i++ ) {
        columns[i].setHeight( value );
      }
      this._updateClientAreaSize();
    },

    setHeaderVisible : function( value ) {
      this._columnArea.setVisibility( value );
      this._topIndex = 0;
      this._vertScrollBar.setValue( 0 );
      this._horzScrollBar.setValue( 0 );
      this._updateClientAreaSize();
      this._updateRowCount();
      this._updateRows();
    },

    setItemHeight : function( value ) {
      this._itemHeight = value;
      this._updateScrollHeight();
      if( this._updateRowCount() ) {
        this._updateRows();
      }
    },
    
    getItemHeight : function() {
      return this._itemHeight;  
    },

    setTopIndex : function( value ) {
      var scrollPos = value * this._itemHeight;
      this._vertScrollBar.setValue( scrollPos );
      this._internalSetTopIndex( value );
      this._topIndexChanged = false;
    },

    _internalSetTopIndex : function( value ) {
      if( this._topIndex != value ) {
        this._topIndex = value;
        this._updateRows();
      }
    },

    getColumn : function( index ) {
      return this._columnArea.getChildren()[ index ];
    },

    getColumnCount : function() {
      return this._columnArea.getChildrenLength();
    },
    
    getColumns : function() {
      return this._columnArea.getChildren();  
    },
    
    getColumnsWidth : function() {
      var result = 0;
      var columns = this._columnArea.getChildren();
      for( var i = 0; i < columns.length; i++ ) {
        result += columns[ i ].getWidth();
      }
      return result;
    },

    setDefaultColumnWidth : function( value ) {
      this._defaultColumnWidth = value;
      this._updateScrollWidth();
    },

    getDefaultColumnWidth : function() {
      return this._defaultColumnWidth;
    },

    setLinesVisible : function( value ) {
      this._linesVisible = value;
      for( var i = 0; i < this._rows.length; i++ ) {
        this._rows[ i ].setBorder( org.eclipse.swt.widgets.Table.ROW_BORDER );
      }
      this._updateRows();
    },

    getLinesVisible : function() {
      return this._linesVisible;
    },
    
    hasCheckBoxes : function() {
      return this._checkBoxes != null;
    },
    
    /**
     * Select all items that are specified in the given value. Value is an array
     * of int's that denote the indices of items to be selected
     */
    setSelection : function( value ) {
      while( this._selected.length > 0 ) {
        this._unselectItem( this._selected[ 0 ] );
      }
      for( var i = 0; i < value.length; i++ ) {
        this._selectItem( this._items[ value[ i ] ] );
      }
    },
    
    setFocusedItem : function( value ) {
      if( value != this._focusedItem ) {
        var oldFocusedItem = this._focusedItem;
        this._focusedItem = value;
        // update previously focused item
        if( oldFocusedItem != null ) {
          this.updateItem( oldFocusedItem, false );
        }
        // update actual focused item
        if( this._focusedItem != null ) {
          this.updateItem( this._focusedItem, false );
        }
      }
    },

    /////////////////////////////////
    // React when enabled was changed
    
    _onChangeEnabled : function( evt ) {
      for( var i = 0; i < this._rows.length; i++ ) {
        this._updateRowState( this._rows[ i ], this._getItemFromRowIndex( i ) );
      }
    },

    ////////////////////////////
    // Listeners for client area

    _onCheckBoxClick : function( evt ) {
      var rowIndex = this._checkBoxes.indexOf( evt.getTarget() );
      this._toggleCheckBox( rowIndex );
    },
    
    _onRowClick : function( evt ) {
      var rowIndex = this._rows.indexOf( evt.getTarget() );
      var itemIndex = this._topIndex + rowIndex;
      if(    itemIndex >= 0 
          && itemIndex < this._items.length 
          && !this._suspendClicks ) 
      {
        this._suspendClicks = true;
        qx.client.Timer.once( this._resumeClicks, this, 500 );
        var item = this._items[ itemIndex ];
        if( this._multiSelect ) {
          if( org.eclipse.swt.widgets.Table._isCtrlOnlyPressed( evt ) ) {
            if( this._isItemSelected( item ) ) {
              this._unselectItem( item );
            } else {
              this._selectItem( item );
            }
          }
          if(    org.eclipse.swt.widgets.Table._isShiftOnlyPressed( evt ) 
              || org.eclipse.swt.widgets.Table._isCtrlShiftOnlyPressed( evt ) ) 
          {
            if( org.eclipse.swt.widgets.Table._isShiftOnlyPressed( evt ) ) {
              while( this._selected.length > 0 ) {
                this._unselectItem( this._selected[ 0 ] );
              }
            }
            var focusedItemIndex = this._items.indexOf( this._focusedItem );
            if( focusedItemIndex != -1 ) {
              var start = Math.min( focusedItemIndex, itemIndex );
              var end = Math.max( focusedItemIndex, itemIndex );
              for( var i = start; i <= end; i++ ) {
                this._selectItem( this._items[ i ] );
              }
            }
          } 
          if(    org.eclipse.swt.widgets.Table._isNoModifierPressed( evt ) 
              || org.eclipse.swt.widgets.Table._isMetaOnlyPressed( evt ) )
          {
            this.setSelection( [ itemIndex ] );
          }
        } else {
          this.setSelection( [ itemIndex ] );
        }
        this.setFocusedItem( item );
        this._updateSelectionParam();
        this.createDispatchDataEvent( "itemselected", item );
      }
    },
    
    _resumeClicks : function() {
      this._suspendClicks = false;
    },
    
    _onRowDblClick : function( evt ) {
      var rowIndex = this._rows.indexOf( evt.getTarget() );
      var item = this._getItemFromRowIndex( rowIndex );
      if( item != null ) {
        this.createDispatchDataEvent( "itemdefaultselected", item );
      }
    },
    
    _onRowContextMenu : function( evt ) {
      if(    org.eclipse.swt.widgets.Table._isNoModifierPressed( evt ) 
          || org.eclipse.swt.widgets.Table._isMetaOnlyPressed( evt ) ) 
      {
        // TODO [rh] avoid this call if item already selected
        this._onRowClick( evt );
        var target = evt.getTarget();
        var contextMenu = this.getContextMenu();
        if( contextMenu != null ) {
          contextMenu.setLocation( evt.getPageX(), evt.getPageY() );
          contextMenu.setOpener( this );
          contextMenu.show();
        }
      }
    },
    
    _onRowKeyDown : function( evt ) {
      var keyId = evt.getKeyIdentifier();
      switch( keyId ) {
        case "Space":
          this._toggleCheckBox( this._rows.indexOf( evt.getTarget() ) );
          break;
      }
    },

    _toggleCheckBox : function( rowIndex ) {
      if( this._checkBoxes != null ) {
        var itemIndex = this._topIndex + rowIndex;
        if( itemIndex >= 0 && itemIndex < this._items.length ) {
          var item = this._items[ itemIndex ];
          item.setChecked( !item.getChecked() );
          // Reflect changed check-state in case there is no server-side listener
          this._updateRow( rowIndex, item );
          this._updateCheckParam( item );
          this.createDispatchDataEvent( "itemchecked", item );
        }
      }
    },
    
    _updateSelectionParam : function() {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var tableId = widgetManager.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      var selectedIds = "";
      for( var i = 0; i < this._selected.length; i++ ) {
        var itemId = widgetManager.findIdByWidget( this._selected[ i ] );
        if( selectedIds != "" ) {
          selectedIds += ",";
        }
        selectedIds += itemId;
      }
      req.addParameter( tableId + ".selection", selectedIds );
    },
    
    _updateCheckParam : function( item ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( item );
      var req = org.eclipse.swt.Request.getInstance();
      req.addParameter( id + ".checked", item.getChecked() );
    },

    _onClientAreaMouseWheel : function( evt ) {
      var change = evt.getWheelDelta() * this._itemHeight * 2;
      this._vertScrollBar.setValue( this._vertScrollBar.getValue() - change );
    },

    _onChangeSize : function( evt ) {
      this._updateClientAreaSize();
    },

    _onClientAppear : function( evt ) {
      this._updateRowCount();
      this._updateRows();
    },

    ////////////////////////
    // Scroll bar listeners
    
    _onVertScrollBarChangeValue : function() {
      // Calculate new topIndex
      var newTopIndex = 0;
      if( this._itemHeight != 0 ) {
        var scrollTop = this._clientArea.isCreated() ? this._vertScrollBar.getValue() : 0;
        newTopIndex = Math.floor( scrollTop / this._itemHeight );
      }
      // update topIndex request parameter
      if( newTopIndex != this._topIndex ) {
        this._topIndexChanged = true;
      }
      // set new topIndex -> rows are updateded if necessary
      this._internalSetTopIndex( newTopIndex );
    },

    _onHorzScrollBarChangeValue : function() {
      this._columnArea.setLeft( 0 - this._horzScrollBar.getValue() );
      this._updateRowBounds();
    },

    ///////////////////////
    // TableItem management

    _addItem : function( item, index ) {
      // TODO [rh] see if this can be optimized
      this._items = qx.lang.Array.insertAt( this._items, item, index );
      this._updateScrollHeight();
      if( this._isItemVisible( item ) ) {
        this._updateRows();
      }
    },

    _removeItem : function( item ) {
      var wasItemVisible = this._isItemVisible( item );
      qx.lang.Array.remove( this._items, item );
      if( item == this._focusedItem ) {
        this._focusedItem = null;
      }
      this._unselectItem( item );
      this._updateScrollHeight();
      if( wasItemVisible ) {
        this._updateRows();
      }
    },

    _selectItem : function( item ) {
      // end of hack
      this._selected.push( item );
      this.updateItem( item, false );
      // Make item fully visible
      var rowIndex = this._getRowIndexFromItem( item );
      var row = null;
      if( rowIndex != -1 ) {
        row = this._rows[ rowIndex ];
      }
      if(    row != null 
          && row.getTop() + row.getHeight() > this._clientArea.getHeight() ) 
      {
        this.setTopIndex( this._topIndex + 1 );
      }
    },

    _unselectItem : function( item ) {
      qx.lang.Array.remove( this._selected, item );
      this.updateItem( item, false );
    },

    _isItemSelected : function( item ) {
      return qx.lang.Array.contains( this._selected, item );
    },

    _isItemVisible : function( item ) {
      var itemIndex = this._items.indexOf( item );
      return    itemIndex >= this._topIndex 
             && itemIndex <= this._topIndex + this._rows.length;
    },

    updateItem : function( item, contentChanged ) {
      var rowIndex = this._getRowIndexFromItem( item );
      if( rowIndex != -1 ) {
        if( contentChanged ) {
          this._updateRow( rowIndex, item );  // implicitly calls _updateRowState
        } else {
          this._updateRowState( this._rows[ rowIndex ], item );
        }
      }
    },

    _getRowIndexFromItem : function( item ) {
      var result = -1;
      var itemIndex = this._items.indexOf( item );
      if(    itemIndex >= this._topIndex 
          && itemIndex < this._topIndex + this._rows.length ) 
      {
        result = itemIndex - this._topIndex;
      }
      return result;
    },

    _getItemFromRowIndex : function( rowIndex ) {
      var result = null;
      var itemIndex = this._topIndex + rowIndex;
      if( itemIndex < this._items.length ) {
        result = this._items[ itemIndex ];
      }
      return result;
    },

    /////////////////////////
    // TableColumn management
    
    _addColumn : function( column ) {
      column.setHeight( this._columnArea.getHeight() );
      column.addEventListener( "changeLeft", this._onColumnChangeSize, this );
      column.addEventListener( "changeWidth", this._onColumnChangeSize, this );
      this._columnArea.add( column );
      this._updateScrollWidth();
    },

    _removeColumn : function( column ) {
      this._columnArea.remove( column );
      column.removeEventListener( "changeLeft", this._onColumnChangeSize, this );
      column.removeEventListener( "changeWidth", this._onColumnChangeSize, this );
      this._updateScrollWidth();
      this._updateRows();
    },

    _onColumnChangeSize : function( evt ) {
      this._updateScrollWidth();
      this._updateRows();
    },

    ///////////////////////////////////////////
    // UI Update upon scroll, size changes, etc
    
    _updateScrollHeight : function() {
      var height = this._itemHeight + this._items.length * this._itemHeight;
      this._vertScrollBar.setMaximum( height );
    },

    _updateScrollWidth : function() {
      var width;
      if( this.getColumnCount() == 0 ) {
        width = this.getDefaultColumnWidth();
      } else {
        width = this.getColumnsWidth();
      }
      this._horzScrollBar.setMaximum( width );
    },

    _updateClientAreaSize : function() {
      var top = 0;
      if( this._columnArea.getVisibility() ) {
        top = this._columnArea.getHeight();
      }
      var clientHeight = this.getHeight() - top - this._horzScrollBar.getHeight();
      var clientWidth = this.getWidth() - this._vertScrollBar.getWidth();
      //
      this._columnArea.setWidth( clientWidth );
      // vertical scrollBar
      this._vertScrollBar.setLeft( this.getWidth() - this._vertScrollBar.getWidth() );
      this._vertScrollBar.setTop( top );
      this._vertScrollBar.setHeight( clientHeight );
      // horizontal scrollBar
      this._horzScrollBar.setLeft( 0 );
      this._horzScrollBar.setTop( this.getHeight() - this._horzScrollBar.getHeight() );
      this._horzScrollBar.setWidth( clientWidth );
      // client area
      this._clientArea.setTop( top );
      this._clientArea.setHeight( clientHeight );
      this._clientArea.setWidth( clientWidth );
      // Adjust number of rows and update rows if necessary
      if( this._updateRowCount() ) {
        this._updateRows();
      } else {
        this._updateRowBounds();
      }
    },

    _updateRowCount : function() {
      var result = false;
      if( this._clientArea.isCreated() ) {
        var newRowCount = 0;
        // TODO [rh] this._clientArea.getHeight() might be negavive
        //      This happens when a Table is placed on a unselected CTabItem 
        //      and then the tab item gets selected and thus the table becomes 
        //      visible
        if( this._itemHeight != 0 && this._clientArea.getHeight() > 0 ) {
          newRowCount = Math.ceil( this._clientArea.getHeight() / this._itemHeight );
        }
        if( newRowCount != this._rows.length ) {
          // Remove trailing rows if rowCount was decreased
          while( this._rows.length > newRowCount ) {
            if( this._checkBoxes != null ) {
              var checkBox = this._checkBoxes.shift();
              checkBox.removeEventListener( "changeChecked", this._onCheckBoxClick, this );
              checkBox.setParent( null );
              checkBox.dispose();
            }
            var row = this._rows.shift();
            row.removeEventListener( "click", this._onRowClick, this );
            row.removeEventListener( "dblclick", this._onRowDblClick, this );
            row.removeEventListener( "keydown", this._onRowKeyDown, this );
            row.removeEventListener( "contextmenu", this._onRowContextMenu, this );
            row.setParent( null );
            row.dispose();
          }
          // Append rows if rowCount was increased
          if( this._rows.length < newRowCount ) {
            while( this._rows.length < newRowCount ) {
              if( this._checkBoxes != null ) {
                var checkBox = new qx.ui.basic.Image();
                checkBox.addEventListener( "click", this._onCheckBoxClick, this );
                checkBox.setAppearance( "table-check-box" );
                this._clientArea.add( checkBox );
                this._checkBoxes.push( checkBox );
              }
              var newRow = new org.eclipse.swt.widgets.TableRow();
              newRow.addEventListener( "click", this._onRowClick, this );
              newRow.addEventListener( "dblclick", this._onRowDblClick, this );
              newRow.addEventListener( "keydown", this._onRowKeyDown, this );
              newRow.addEventListener( "contextmenu", this._onRowContextMenu, this );
              this._clientArea.add( newRow );
              this._rows.push( newRow );
            }
          }
          // Re-calculate the position and size for each row
          this._updateRowBounds();
          result = true;
        }
      }
      return result;
    },
    
    _updateRowBounds : function() {
      var top = 0;
      var left = 0 - this._horzScrollBar.getValue();
      var checkBoxWidth = 0;
      if( this._checkBoxes != null ) {
        // TODO [rh] move to theme, needs to be in sync with TableItem#CHECK_WIDTH
        checkBoxWidth = org.eclipse.swt.widgets.Table.CHECK_WIDTH;
      }
      var width = this.getColumnsWidth() - checkBoxWidth;
      if( this._clientArea.getWidth() > width ) {
        width = this._clientArea.getWidth();
      }
      for( var i = 0; i < this._rows.length; i++ ) {
        if( this._checkBoxes != null ) {
          var checkBox = this._checkBoxes[ i ];
          checkBox.setLeft( left );
          checkBox.setTop( top + 1 );
          checkBox.setWidth( checkBoxWidth );
          checkBox.setHeight( this._itemHeight );
        }
        var row = this._rows[ i ];
        row.setTop( top );
        row.setLeft( left + checkBoxWidth );
        row.setWidth( width );
        row.setHeight( this._itemHeight );
        top += this._itemHeight;
      }
    },

    _updateRows : function() {
      for( var i = 0; i < this._rows.length; i++ ) {
        this._updateRow( i, this._getItemFromRowIndex( i ) );
      }
    },

    _updateRow : function( rowIndex, item ) {
      var row = this._rows[ rowIndex ]
      if( item != null ) {
        row.setHtml( item._getMarkup() );
      } else {
        row.setHtml( this._emptyItem._getMarkup() );
      }
      this._updateRowState( row, item );
      if( this._checkBoxes != null ) {
        this._updateRowCheck( rowIndex, item );
      }
    },

    _updateRowState : function( row, item ) {
      if( item != null ) {
        if( this._isItemSelected( item ) ) { 
          row.addState( "selected" );
        } else {
          row.removeState( "selected" );
        }
        if( this._focusedItem == item ) {
          row.addState( "itemFocused" );
        } else {
          row.removeState( "itemFocused" );
        }
      } else {
        row.removeState( "selected" );
        row.removeState( "itemFocused" );
      }
    },
    
    _updateRowCheck : function( rowIndex, item ) {
      var checkBox = this._checkBoxes[ rowIndex ];
      if( item != null ) {
        if( item.getChecked() ) {
          checkBox.addState( "checked" );
        } else {
          checkBox.removeState( "checked" );
        }
        if( item.getGrayed() ) {
          checkBox.addState( "grayed" );
        } else {
          checkBox.removeState( "grayed" );
        }
        checkBox.setVisibility( true );
      } else {
        checkBox.setVisibility( false );
      }
    },

    //////////////////////////////////////////////////////////////
    // Show and hide the resize line used by column while resizing

    _showResizeLine : function( x ) {
      if( this._resizeLine == null ) {
        this._resizeLine = new qx.ui.basic.Terminator();
        this._resizeLine.setAppearance( "table-column-resizer" );
        this.add( this._resizeLine );
        qx.ui.core.Widget.flushGlobalQueues();
      }
      var top = this._clientArea.getTop();
      this._resizeLine._applyRuntimeTop( top ); 
      var left = x - 2 - this._horzScrollBar.getValue();
      this._resizeLine._applyRuntimeLeft( left );
      var height = this._clientArea.getHeight();
      this._resizeLine._applyRuntimeHeight( height );
      this._resizeLine.removeStyleProperty( "visibility" );
    },

    _hideResizeLine : function() {
      this._resizeLine.setStyleProperty( "visibility", "hidden" );
    },
    
    ////////////////////////////////////////////////////////////
    // Event handling methods - added and removed by server-side

    onItemSelected : function( evt ) {
      // evt.getData() holds the TableItem that was selected
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( evt.getData() );
      org.eclipse.swt.EventUtil.doWidgetSelected( id, 0, 0, 0, 0 );
    },

    onItemDefaultSelected : function( evt ) {
      // evt.getData() holds the TableItem that was double-clicked
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( evt.getData() );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.widgetDefaultSelected", id );
      req.send();
    },

    onItemChecked : function( evt ) {
      // evt.getData() holds the TableItem that was checked
      var req = org.eclipse.swt.Request.getInstance();
      req.addParameter( "org.eclipse.swt.events.widgetSelected.detail", 
                        "check" );
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( evt.getData() );
      org.eclipse.swt.EventUtil.doWidgetSelected( id, 0, 0, 0, 0 );
    },

    _onSendRequest : function( evt ) {
      if( this._topIndexChanged ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( id + ".topIndex", this._topIndex );
      }
    }
  }
});

qx.Clazz.ROW_BORDER.setTop( 1, qx.constant.Style.BORDER_SOLID, "#eeeeee" );
qx.Clazz.ROW_BORDER.setBottom( 1, qx.constant.Style.BORDER_SOLID, "#eeeeee" );
