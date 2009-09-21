/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

/**
 * This class encapulates the qx.ui.tree.TreeFolder to make it more
 * suitable for usage in RWT.
 */
qx.Class.define( "org.eclipse.swt.widgets.TreeItem", {
  extend : qx.ui.tree.TreeFolder,

  construct : function( parentItem, tree ) {
    this._row = qx.ui.tree.TreeRowStructure.getInstance().newRow();
    // Indentation
    this._row.addIndent();
    // CheckBox
    this._checkBox = null;
    this._checked = false;
    // TODO reactivate
    if ( qx.lang.String.contains( parentItem.getTree().getParent().getRWTStyle(), "check" ) )
    {
      this._checkBox = new qx.ui.basic.Image();
      this._checkBox.setAppearance( "tree-check-box" );
      this._checkBox.addEventListener( "click", this._onCheckBoxClick, this );
      this._checkBox.addEventListener( "mouseover", this._onCheckBoxOver, this );
      this._checkBox.addEventListener( "mouseout", this._onCheckBoxOut, this );
      this._checkBox.addEventListener( "dblclick", this._onCheckBoxDblClick, this );
      this._row.addObject( this._checkBox, false );
    }

    this._texts = null;
    this._images = new Array();
    this._colLabels = new Array();
    this._foreground = null;
    this._background = null;
    this._cellForegrounds = null;
    this._cellBackgrounds = null;
    this._fonts = null;

    // Image
    this._row.addIcon( null );

    // Text
    this._row.addLabel( "" );

    // Virtual
    this._materialized = true;

    // Construct TreeItem
    for( var c = 0; c < tree._columns.length; c++ ) {
        this.columnAdded();
    }
    this.base( arguments, this._row );
    this.addEventListener( "click", this._onClick, this );
    this.addEventListener( "dblclick", this._onDblClick, this );
    this.addEventListener( "appear", this._onAppear, this );
    //this.addEventListener( "changeBackgroundColor", this._onChangeBackgroundColor, this );
    parentItem.add( this );

    this.getLabelObject().setMode( "html" );

    // TODO [bm] need to set the color to prevent inheritance of colors
    this.setBackgroundColor( "transparent" );
  },

  destruct : function() {
    if( this._checkBox != null ) {
      this._checkBox.removeEventListener( "click", this._onCheckBoxClick, this );
      this._checkBox.removeEventListener( "mouseover", this._onCheckBoxOver, this );
      this._checkBox.removeEventListener( "mouseout", this._onCheckBoxOut, this );
      this._checkBox.removeEventListener( "dblclick", this._onCheckBoxDblClick, this );
      this._checkBox.dispose();
    }
    this.removeEventListener( "click", this._onClick, this );
    this.removeEventListener( "dblclick", this._onDblClick, this );
    this.removeEventListener( "appear", this._onAppear, this );
  },

  members : {

    // [if] Workaround for bug
    // 244952: [Tree] replacing child nodes leads to overlap
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=244952
    destroy : function() {
      if( !this.isSeeable() ) {
        this._isDisplayable = true;
      }
      this.base( arguments );
    },

    setTextColor : function ( value ) {
      this._foreground = value;
      if( this.isCreated() ) {
        this.updateItem();
      }
    },

    resetTextColor : function ( value ) {
      this._foreground = null;
      if( this.isCreated() ) {
        this.updateItem();
      }
    },

    setBackgroundColor : function ( value ) {
      this._background = value;
      if( this.isCreated() ) {
        this.updateItem();
      }
    },

    resetBackgroundColor : function () {
      this._background = null;
      if( this.isCreated() ) {
        this.updateItem();
      }
    },

    setChecked : function( value ) {
      if( this._checkBox != null ) {
        if( value ) {
          this._checkBox.addState( "checked" );
        } else {
          this._checkBox.removeState( "checked" );
        }
      }
    },

    setMaterialized : function( value ) {
      this._materialized = value;
    },

    isMaterialized : function( value ) {
      return this._materialized;
    },

    setGrayed : function( value ) {
      if( this._checkBox != null ) {
        if( value ) {
            this._checkBox.addState( "grayed" );
        } else {
            this._checkBox.removeState( "grayed" );
        }
      }
    },

    setSelection : function( value, focus ) {
      var manager = this.getTree().getManager();
      if( manager.getMultiSelection() || value ) {
        manager.setItemSelected( this, value );
      } else if( manager.getItemSelected( this ) ) {
        // [if] Because of qx SelectionManager it is not possible to deselect
        // the item in "single" selection mode with manager.setItemSelected
        manager.deselectAll();
      }
      if( focus ) {
        manager.setLeadItem( this );
      }
    },

    // TODO [rh] workaround for qx bug #260 (TreeFullControl doesn't update icon
    //      when it is changed)
    setImage : function( image ) {
      this.setIcon( image );
      this.getIconObject().setSource( image );
      this.setIconSelected( image );
    },

    addState : function( state ) {
      this.base( arguments, state );
      if( state == "disabled" || state == "selected" ) {
        this.updateItem();
      }
    },

    removeState : function( state ) {
      this.base( arguments, state );
      if( state == "disabled" || state == "selected" ) {
        this.updateItem();
      }
    },

    /*
     * Notifies tree of clicks on the item's area.
     */
    _onClick : function( evt ) {
      if( this._checkEventTarget( evt ) ) {
        this.getTree().getParent()._notifyItemClick( this );
        // [if] Select item manually because of problems with the tree 
        // SelectionManager with enabled multi selection.
        if( !evt.isCtrlPressed() ) {
          this.getTree().getManager().setItemSelected( this, true );
        }
      }
    },

    /*
     * Notifies tree of double clicks in the item's area.
     */
    _onDblClick : function( evt ) {
      if( this._checkEventTarget( evt ) ) {
        this.getTree().getParent()._notifyItemDblClick( this );
      }
    },

    /*
     * Checks if a given event should be handled or not. Returns true if the
     * event's original target is either the icon or the label.
     */
    _checkEventTarget : function( evt ) {
      var result = false;
      var target = evt.getOriginalTarget();
      // TODO [rh] 'target &&' is either unnecessary or the if statement yields
      //      a wrong result
      if( target && target == this._iconObject || target == this._labelObject ) {
        result = true;
      }
      return result;
    },
    
    _onCheckBoxOver : function( evt ) {
      this._checkBox.addState( "over" );
    },
    
    _onCheckBoxOut : function( evt ) {
      this._checkBox.removeState( "over" );
    },

    _onCheckBoxClick : function( evt ) {
      this._checked = !this._checked;
      if( this._checked ) {
        this._checkBox.addState( "checked" );
      } else {
        this._checkBox.removeState( "checked" );
      }
      this._onChangeChecked( evt );
    },

    /*
     * Prevent double clicks on check boxes from being propageted to the tree.
     */
    _onCheckBoxDblClick : function( evt ) {
      evt.stopPropagation();
    },

    _onChangeChecked : function( evt ) {
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var id = wm.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      req.addParameter( id + ".checked", this._checked );
      this.getTree().getParent()._notifyChangeItemCheck( this );
    },

    /*
     * Prevent auto expand on click
     */
    _onmouseup : function( evt ) {
    },

    _onAppear : function( evt ) {
      this.updateItem();
      this.updateColumnsWidth();
    },

    setTexts : function( texts ) {
      this._texts = texts;
      if( this.isCreated() ) {
        this.updateItem();
        this.updateColumnsWidth(); // Fix for bug 289089
      }
    },

    setImages : function( images ) {
      this._images = images;
      if( this.isCreated() ) {
        this.updateItem();
        this.updateColumnsWidth();
      }
    },

    setBackgrounds : function( backgrounds ) {
      this._cellBackgrounds = backgrounds;
      if( this.isCreated() ) {
        this.updateItem();
      }
    },

    setForegrounds : function( foregrounds ) {
      this._cellForegrounds = foregrounds;
      if( this.isCreated() ) {
        this.updateItem();
      }
    },

    setFonts : function( fonts ) {
      this._fonts = fonts;
    },

    columnAdded : function() {
      var obj = new qx.ui.basic.Atom( "" );
      obj.setHorizontalChildrenAlign( "left" );
      this._row.addObject( obj, true );
      this._colLabels[ this._colLabels.length ] = obj;
    },

    updateItem : function() {
      var colOrder = this.getTree().getParent().getColumnOrder();
      var colCount = Math.max ( 1, this.getTree().getParent()._columns.length );
      if( this._texts != null ) {
        // TODO [rst] Why is background only rendered when text != null?
        for( var c = 0; c < colCount; c++ ) {
          var col = colOrder[ c ];
          var text = this._texts[ col ] == "" ? " " : this._texts[ col ];
          if( text != null ) {
            if( c == 0 ) {
              this.setLabel( text );
              this.setImage( this._images[ col ] );
              this._updateCellForeground( col, this.getLabelObject() );
              this._updateCellBackground( col, this.getLabelObject() );
              if( this._fonts && this._fonts[ col ] ) {
                // TODO
              }
            } else {
              this._colLabels[ c - 1 ].setHeight( this.getIndentObject().getHeight() );
              this._colLabels[ c - 1 ].setLabel( text );
              this._colLabels[ c - 1 ].setIcon( this._images[ col ] );
              // colors and fonts
              this._updateCellForeground( col, this._colLabels[ c - 1 ] );
              this._updateCellBackground( col, this._colLabels[ c - 1 ] );
              if( this._fonts && this._fonts[ col ] ) {
                // TODO
              }
            }
          }
        }
      }
    },

    _updateCellForeground : function( col, widget ) {
      var show =    ( col > 0 || !this.hasState( "selected" ) )
                 && !this.hasState( "disabled" );
      if( show && this._cellForegrounds && this._cellForegrounds[ col ] ) {
        widget.setTextColor( this._cellForegrounds[ col ] );
      } else if( show && this._foreground ) {
        widget.setTextColor( this._foreground );
      } else {
        widget.resetTextColor();
      }
    },

    _updateCellBackground : function( col, widget ) {
      var show = ( col == 0 ) && !this.hasState( "selected" );
      if( show && this._cellBackgrounds && this._cellBackgrounds[ col ] ) {
        widget.setBackgroundColor( this._cellBackgrounds[ col ] );
      } else if( show && this._background ) {
        widget.setBackgroundColor( this._background );
      } else {
        widget.resetBackgroundColor();
      }
    },

    updateColumnsWidth : function() {
      var columnWidth = new Array();
      var fullWidth = this.getTree().getParent().getColumnsWidth();
      this.setWidth( fullWidth );
      for( var c = 0; c < this.getTree().getParent()._columns.length; c++ ) {
        columnWidth[ c ] = this.getTree().getParent()._columns[ c ].getWidth();
      }
      if( columnWidth.length > 0 ) {
        var checkboxWidth = this._checkBox == null ? 0 : 16; // 13 width + 3 checkbox margin
        var imageWidth = this._images[ 0 ] == null ? 0 : this.getIconObject().getWidth();
        var spacing = imageWidth > 0 ? this.getIconObject().getMarginRight() : 0;
        this.getLabelObject().setWidth( columnWidth[ 0 ]
          - ( this.getLevel() * 19 )   // TODO: [bm] replace with computed indent width
          - checkboxWidth
          - imageWidth
          - spacing );
        var coLabel;
        for( var i = 1; i < columnWidth.length; i++ ) {
          coLabel = this._colLabels[ i - 1 ];
          imageWidth = this._images[ i ] == null ? 0 : 16;
          spacing = imageWidth > 0 ? coLabel.getSpacing() : 0;
          if( coLabel != null && coLabel.getLabelObject() != null ) {
            coLabel.getLabelObject().setWidth( columnWidth[ i ]
              - imageWidth
              - spacing );
          }
        }
      }
    }

  }
} );
