/*******************************************************************************
 * Copyright (c) 2007, 2010 Innoopract Informationssysteme GmbH.
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
 * Store for theme values that cannot be kept in a qooxdoo theme. The store is
 * filled from the server at startup.
 */
qx.Class.define( "org.eclipse.swt.theme.ThemeStore", {

  type : "singleton",

  extend : qx.core.Object,

  construct : function() {
    this._values = {
      dimensions : {},
      boxdims : {},
      images : {},
      gradients : {},
      fonts : {},
      colors : {},
      borders : {},
      cursors : {},
      animations : {}
    };
    this._cssValues = {};
    this._statesMap = {
      "*" : {
        "hover" : "over"
      },
      "DateTime-Calendar-Day" : {
        "unfocused" : "parent_unfocused"
      },
      "List-Item" : {
        "unfocused" : "parent_unfocused"
      },
      "Text" : {
        "read-only" : "readonly"
      },
      "TreeItem" : {
        "unfocused" : "parent_unfocused"
      },
      "TreeColumn" : {
        "hover" : "mouseover"
      },
      "Shell" : {
        "inactive" : "!active"
      },
      "Shell-Titlebar" : {
        "inactive" : "!active"
      },
      "Shell-MinButton" : {
        "inactive" : "!active"
      },
      "Shell-MaxButton" : {
        "inactive" : "!active"
      },
      "Shell-CloseButton" : {
        "inactive" : "!active"
      },
      "TableColumn" : {
        "hover" : "mouseover"
      },
      "TableItem" : {
        "unfocused" : "parent_unfocused"
      },
      "TabItem" : {
        "selected" : "checked"
      }
    };
  },

  members : {

    /**
     * Returns the values container.
     */
    getThemeValues : function() {
      return this._values;
    },

    defineValues : function( values ) {
      for( var type in this._values ) {
        if( type in values ) {
          for( key in values[ type ] ) {
            if( !( key in this._values[ type ] ) ) {
              this._values[ type ][ key ] = values[ type ][ key ];
            }
          }
        }
      }
      this._resolveFonts();
      this._resolveBorders();
      this._resolveGradients();
    },

    _resolveFonts : function() {
      for( var key in this._values.fonts ) {
        var value = this._values.fonts[ key ];
        // TODO [rst] remove this check when values are rendered only once
        if( !( value instanceof qx.ui.core.Font ) ) {
          var font = new qx.ui.core.Font();
          font.setSize( value.size );
          font.setFamily( value.family );
          font.setBold( value.bold );
          font.setItalic( value.italic );
          this._values.fonts[ key ] = font;
        }
      }
    },

    _resolveBorders : function() {
      for( var key in this._values.borders ) {
        var value = this._values.borders[ key ];
        // TODO [rst] remove this check when values are rendered only once
        if( !( value instanceof qx.ui.core.Border ) && typeof( value ) != "string" ) {
          var border = null;
          if( value.color == null ) {
            if( value.width == 1 ) {
              if( value.style == "outset" ) {
                border = "thinOutset";
              } else if( value.style == "inset" ) {
                border = "thinInset";
              }
            } else if( value.width == 2 ) {
              if( value.style == "outset" ) {
                border = "outset";
              } else if( value.style == "inset" ) {
                border = "inset";
              } else if( value.style == "ridge" ) {
                border = "ridget";
              } else if( value.style == "groove" ) {
                border = "groove";
              }
            }
          }
          if( border == null ) {
            border = new qx.ui.core.Border( value.width, value.style );
            if( value.color ) {
              border.setColor( value.color );
            }
          }
          this._values.borders[ key ] = border;
        }
      }
    },
    
    _resolveGradients : function() {
      for( var key in this._values.gradients ) {
        var value = this._values.gradients[ key ];
        if( value != null ) {
          // TODO [if] remove this check when values are rendered only once
          if( value.colors && value.percents ) {
            var gradient = new Array();
            for( var i = 0; i < value.colors.length; i++ ) {
              gradient[ i ] = [ value.percents[ i ] / 100, 
                                value.colors[ i ] ];
            }
            this._values.gradients[ key ] = gradient;
          }
        }
      }
    },

    setThemeCssValues : function( theme, values, isDefault ) {
      if( this._cssValues[ theme ] === undefined ) {
        this._cssValues[ theme ] = values;
      }
      if( isDefault ) {
        this.defaultTheme = theme;
      }
      this._fillColors( theme );
    },

    // Fills qx color theme with some named colors necessary for BordersBase
    _fillColors : function( theme ) {
      var ct = qx.Theme.getByName( theme + "Colors" );
      ct.colors[ "widget.darkshadow" ]
        = this._getColor( "Display", {}, "rwt-darkshadow-color", theme );
      ct.colors[ "widget.highlight" ]
        = this._getColor( "Display", {}, "rwt-highlight-color", theme );
      ct.colors[ "widget.lightshadow" ]
        = this._getColor( "Display", {}, "rwt-lightshadow-color", theme );
      ct.colors[ "widget.shadow" ]
        = this._getColor( "Display", {}, "rwt-shadow-color", theme );
      ct.colors[ "widget.thinborder" ]
        = this._getColor( "Display", {}, "rwt-thinborder-color", theme );
      // TODO [rst] eliminate these properties
      ct.colors[ "widget.selection-marker" ]
        = this._getColor( "Display", {}, "rwt-selectionmarker-color", theme );
      ct.colors[ "widget.background" ]
        = this._getColor( "*", {}, "background-color", theme );
      ct.colors[ "widget.foreground" ]
        = this._getColor( "*", {}, "color", theme );
      ct.colors[ "widget.info.foreground" ]
        = this._getColor( "ToolTip", {}, "color", theme );
    },

    _getColor : function( element, states, property, theme ) {
      var vkey = this.getCssValue( element, states, property, theme );
      return this._values.colors[ vkey ];
    },

    getCssValue : function( element, states, property, theme ) {
      var result;
      if( theme == null ) {
        theme = qx.theme.manager.Meta.getInstance().getTheme().name;
      }
      if(    this._cssValues[ theme ] !== undefined
          && this._cssValues[ theme ][ element ] !== undefined 
          && this._cssValues[ theme ][ element ][ property ] !== undefined )
      {
        var values = this._cssValues[ theme ][ element ][ property ];
        var found = false;
        for( var i = 0; i < values.length && !found; i++ ) {
          if( this._matches( states, element, values[ i ][ 0 ] ) ) {
            result = values[ i ][ 1 ];
            found = true;
          }
        }
      }
      if( result === undefined && theme != this.defaultTheme ) {
        result = this.getCssValue( element, states, property, this.defaultTheme );
      }
      return result;
    },
    
    getImageSize : function( source ) {
      var key = source.slice( "rwt-resources/themes/images/".length );
      var image = this._values.images[ key ];      
      return image != null ? [ image[ 1 ], image[ 2 ] ] : [ 0, 0 ];
    },

    _matches : function( states, element, constraints ) {
      var result = true;
      for( var i = 0; i < constraints.length && result; i++ ) {
        var cond = constraints[ i ];
        if( cond.length > 0 ) {
          var c = cond.charAt( 0 );
          if( c == "." ) {
            result = "variant_" + cond.substr( 1 ) in states;
          } else if( c == ":" ) {
            var state = this._translateState( cond.substr( 1 ), element );
            if( state.charAt( 0 ) == "!" ) {
              result = ! ( state.substr( 1 ) in states );
            } else {
              result = state in states;
            }
          } else if( c == "[" ) {
            result = "rwt_" + cond.substr( 1 ) in states;
          }
        }
      }
      return result;
    },

    _translateState : function( state, element ) {
      var result = state;
      if( element in this._statesMap && state in this._statesMap[ element ] ) {
        result = this._statesMap[ element ][ state ];
      } else if( state in this._statesMap[ "*" ] ) {
        result = this._statesMap[ "*" ][ state ];
      }
      return result;
    }
  }
} );
