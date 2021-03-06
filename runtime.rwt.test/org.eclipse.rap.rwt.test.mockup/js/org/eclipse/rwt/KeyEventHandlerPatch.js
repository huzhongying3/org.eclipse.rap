/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

qx.Mixin.define( "org.eclipse.rwt.KeyEventHandlerPatch",
{
  "members" : {
    
    _idealKeyHandler : function( keyCode, charCode, eventType, domEvent ) {
      var util;      
      if( qx.core.Variant.isSet( "qx.client", "gecko" ) ) {
        util = org.eclipse.rwt.AsyncKeyEventUtil.getInstance();
      } else {
        util = org.eclipse.rwt.SyncKeyEventUtil.getInstance();
      }
      if( !util.intercept( eventType, keyCode, charCode, domEvent ) ) {
        this.base( arguments, keyCode, charCode, eventType, domEvent );
      }
    }
    
  }
} );

