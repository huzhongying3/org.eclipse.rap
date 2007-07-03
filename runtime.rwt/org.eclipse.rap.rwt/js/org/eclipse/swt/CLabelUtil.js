
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
 * This class contains static functions needed for labels.
 * To represent an RWT Label object, the qx.ui.basic.Atom widget is used.
 */
qx.Class.define( "org.eclipse.swt.CLabelUtil", {

  statics : {
    SHOW_BOTH : "both",
    
    APPEARANCE : "c-label-wrapper",
    
    initialize : function( widget ) {
      widget.setVerticalChildrenAlign( qx.constant.Layout.ALIGN_MIDDLE );
      widget.setHorizontalChildrenAlign( qx.constant.Layout.ALIGN_LEFT );
      widget.setAppearance( org.eclipse.swt.CLabelUtil.APPEARANCE );
      widget.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
      // TODO [rh] workaround for weird getLabelObject behaviour
      widget.setLabel( "(empty)" );
      // end workaround
      var labelObject = widget.getLabelObject();
      labelObject.setMode( org.eclipse.swt.LabelUtil.MODE_TEXT );
      labelObject.setTextOverflow( false );
      labelObject.setAppearance( "label-graytext" );
      widget.getLabelObject().setWrap( false );
      // TODO [rh] workaround for weird getLabelObject behaviour
      widget.setLabel( "" );
      // end workaround
      widget.setHideFocus( true );
      widget.setShow( org.eclipse.swt.CLabelUtil.SHOW_BOTH );
    }

  }
});
