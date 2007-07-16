
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

qx.Class.define( "org.eclipse.swt.Application", {
  extend : qx.application.Gui,

  construct : function() {
    this.base( arguments );
  },

  statics : {
    // TODO [rh] causes JavaScript error
    //  var doc = qx.ui.core.ClientDocument.getInstance();
    //  doc.removeEventListener( "windowresize", this._onResize );
    _onResize : function( evt ) {
      var doc = qx.ui.core.ClientDocument.getInstance();
      var req = org.eclipse.swt.Request.getInstance();
      var id = req.getUIRootId();

      // TODO [rh] replace code below with qx.dom.Window.getInnerWidth( window )
      //      and getInnerHeight( window ) when available. Seems like qx 0.6 does
      //      not yet support these functions.
      var width = 0;
      var height = 0;
      if( document.layers || ( document.getElementById && !document.all ) ) {
        width = window.innerWidth;
        height = window.innerHeight;
      } else if( document.all ) {
        width = document.body.clientWidth;
        height = document.body.clientHeight;
      }
      // Append document size to request
      req.addParameter( id + ".bounds.width", String( width ) );
      req.addParameter( id + ".bounds.height", String( height ) );
      req.send();
    }
  },

  members : {
    main : function( evt ) {
      this.base( arguments );
      
      // Overwrite the default mapping for internal images. This is necessary
      // if the application is deployed under a root different from "/".
      qx.io.Alias.getInstance().add( "static", "./resource/static" );
      qx.io.Alias.getInstance().add( "org.eclipse.swt", "./resource" );
      
      // Observe window size
      var doc = qx.ui.core.ClientDocument.getInstance();
      doc.addEventListener( "windowresize", 
                            org.eclipse.swt.Application._onResize );
      // Initial request to obtain startup-shell
      var req = org.eclipse.swt.Request.getInstance();
      org.eclipse.swt.Application._onResize();  // appends bounds to the request
      req.send();
    },

    close : function( evt ) {
      this.base( arguments );
      // If a non-null (?) value is returned, the user is prompted when leaving the page.
      // TODO [rst] Make return value configurable
      var result = null;
//      if( org.eclipse.rap.confirmExit ) {
//        result = "You are leaving the application. "
//                 + "Your current session data will be lost if you proceed.";
//      }
      return result;
    },
    
    terminate : function( evt ) {
      this.base( arguments );
    }
  }
});
