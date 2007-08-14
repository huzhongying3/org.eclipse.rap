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
package com.w4t.dhtml.treeleafkit;



/** <p>The renderer for org.eclipse.rap.dhtml.TreeLeaf on Microsoft Internet 
  * Explorer 6 and later without javascript support.</p>
  */
public class TreeLeafRenderer_Ie5up_Noscript 
  extends TreeLeafRenderer_Base_Noscript
{
  
  String getVerticalAlign() {
    return "super";
  }                                          
}