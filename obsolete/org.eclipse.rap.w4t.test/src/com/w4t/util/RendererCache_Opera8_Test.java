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
package com.w4t.util;

import org.eclipse.rwt.internal.browser.Opera8;
import org.eclipse.rwt.internal.browser.Opera9;

import junit.framework.TestCase;

import com.w4t.*;
import com.w4t.administration.LinkButton;
import com.w4t.administration.PoolLabel;
import com.w4t.custom.*;
import com.w4t.dhtml.*;
import com.w4t.dhtml.menubarkit.*;
import com.w4t.dhtml.menubuttonkit.*;
import com.w4t.dhtml.menuitemkit.*;
import com.w4t.dhtml.menuitemseparatorkit.*;
import com.w4t.dhtml.menukit.*;
import com.w4t.dhtml.treeleafkit.*;
import com.w4t.dhtml.treenodekit.*;
import com.w4t.dhtml.treeviewkit.*;
import com.w4t.dhtml.webscrollpanekit.*;
import com.w4t.mockup.*;
import com.w4t.mockup.nonajaxcomponentkit.NonAjaxComponentRenderer_Default_Script;
import com.w4t.webanchorkit.*;
import com.w4t.webbordercomponentkit.*;
import com.w4t.webbuttonkit.*;
import com.w4t.webcardlayoutkit.*;
import com.w4t.webcheckboxkit.*;
import com.w4t.webcontainerkit.*;
import com.w4t.webfileuploadkit.*;
import com.w4t.webformkit.*;
import com.w4t.webimagekit.*;
import com.w4t.weblabelkit.*;
import com.w4t.webradiobuttonkit.*;
import com.w4t.webselectkit.*;
import com.w4t.webtextareakit.*;
import com.w4t.webtextkit.*;


public class RendererCache_Opera8_Test extends TestCase {
  
  protected void setUp() throws Exception {
    W4TFixture.setUp();
    W4TFixture.createContext();
  }

  protected void tearDown() throws Exception {
    W4TFixture.tearDown();
    W4TFixture.removeContext();
  }

  public void testNoscript() {
    Renderer renderer;
    //
    // Opera 8 (no script)
    W4TFixture.fakeBrowser( new Opera8( false ) );
    // WebAnchor
    RendererCache cache = RendererCache.getInstance();
    renderer = cache.retrieveRenderer( WebAnchor.class );
    assertEquals( WebAnchorRenderer_Default_Noscript.class, 
                  renderer.getClass() );
    // WebBorderComponent
    renderer = cache.retrieveRenderer( WebBorderComponent.class );
    assertEquals( WebBorderComponentRenderer_Default_Noscript.class,
                  renderer.getClass() );
    // WebScrollPane
    renderer = cache.retrieveRenderer( WebScrollPane.class );
    assertEquals( WebScrollPaneRenderer_Default_Noscript.class,
                  renderer.getClass() );
    // MenuItem
    renderer = cache.retrieveRenderer( MenuItem.class );
    assertEquals( MenuItemRenderer_Default_Noscript.class, 
                  renderer.getClass() );
    // MenuItemSeparator
    renderer = cache.retrieveRenderer( MenuItemSeparator.class );
    assertEquals( MenuItemSeparatorRenderer_Default_Noscript.class,
                  renderer.getClass() );
    // TreeLeaf
    renderer = cache.retrieveRenderer( TreeLeaf.class );
    assertEquals( TreeLeafRenderer_Default_Noscript.class, 
                  renderer.getClass() );
    // Menu
    renderer = cache.retrieveRenderer( Menu.class );
    assertEquals( MenuRenderer_Default_Noscript.class, 
                  renderer.getClass() );
    // MenuBar
    renderer = cache.retrieveRenderer( MenuBar.class );
    assertEquals( MenuBarRenderer_Opera8up_Noscript.class, 
                  renderer.getClass() );
    // TreeNode
    renderer = cache.retrieveRenderer( TreeNode.class );
    assertEquals( TreeNodeRenderer_Default_Noscript.class, 
                  renderer.getClass() );
    // TreeView
    renderer = cache.retrieveRenderer( TreeView.class );
    assertEquals( TreeViewRenderer_Default_Noscript.class, 
                  renderer.getClass() );
    // MenuButton
    renderer = cache.retrieveRenderer( MenuButton.class );
    assertEquals( MenuButtonRenderer_Default_Noscript.class,
                  renderer.getClass() );
    // WebButton
    renderer = cache.retrieveRenderer( WebButton.class );
    assertEquals( WebButtonRenderer_Default_Noscript.class, 
                  renderer.getClass() );
    // AreaSelector
    renderer = cache.retrieveRenderer( WebButton.class );
    assertEquals( WebButtonRenderer_Default_Noscript.class, 
                  renderer.getClass() );
    // LinkButton
    renderer = cache.retrieveRenderer( LinkButton.class );
    assertEquals( WebButtonRenderer_Default_Noscript.class, 
                  renderer.getClass() );
    // WebCheckBox
    renderer = cache.retrieveRenderer( WebCheckBox.class );
    assertEquals( WebCheckBoxRenderer_Default_Noscript.class,
                  renderer.getClass() );
    // WebContainer
    renderer = cache.retrieveRenderer( WebContainer.class );
    assertEquals( WebContainerRenderer_Default_Noscript.class,
                  renderer.getClass() );
    // CItemList
    renderer = cache.retrieveRenderer( CItemList.class );
    assertEquals( WebContainerRenderer_Default_Noscript.class,
                  renderer.getClass() );
    // CTabbedPane
    renderer = cache.retrieveRenderer( CTabbedPane.class );
    assertEquals( WebContainerRenderer_Default_Noscript.class,
                  renderer.getClass() );
    // CTable
    renderer = cache.retrieveRenderer( CTable.class );
    assertEquals( WebContainerRenderer_Default_Noscript.class,
                  renderer.getClass() );
    // WebForm
    renderer = cache.retrieveRenderer( WebForm.class );
    assertEquals( WebFormRenderer_Default_Noscript.class, 
                  renderer.getClass() );
    // TestForm
    renderer = cache.retrieveRenderer( TestForm.class );
    assertEquals( WebFormRenderer_Default_Noscript.class, 
                  renderer.getClass() );
    // WebPanel
    renderer = cache.retrieveRenderer( WebPanel.class );
    assertEquals( WebContainerRenderer_Default_Noscript.class,
                  renderer.getClass() );
    // CMenu
    renderer = cache.retrieveRenderer( CMenu.class );
    assertEquals( WebContainerRenderer_Default_Noscript.class,
                  renderer.getClass() );
    // CToolBar
    renderer = cache.retrieveRenderer( CToolBar.class );
    assertEquals( WebContainerRenderer_Default_Noscript.class,
                  renderer.getClass() );
    // TreeView
    renderer = cache.retrieveRenderer( TreeView.class );
    assertEquals( TreeViewRenderer_Default_Noscript.class, 
                  renderer.getClass() );
    // WebFileUpload
    renderer = cache.retrieveRenderer( WebFileUpload.class );
    assertEquals( WebFileUploadRenderer_Default_Noscript.class,
                  renderer.getClass() );
    // WebImage
    renderer = cache.retrieveRenderer( WebImage.class );
    assertEquals( WebImageRenderer_Default_Noscript.class, 
                  renderer.getClass() );
    // WebLabel
    renderer = cache.retrieveRenderer( WebLabel.class );
    assertEquals( WebLabelRenderer_Default_Noscript.class, 
                  renderer.getClass() );
    // PoolLabel
    renderer = cache.retrieveRenderer( PoolLabel.class );
    assertEquals( WebLabelRenderer_Default_Noscript.class, 
                  renderer.getClass() );
    // SuperWebLabel
    renderer = cache.retrieveRenderer( SuperWebLabel.class );
    assertEquals( WebLabelRenderer_Default_Noscript.class, 
                  renderer.getClass() );
    // WebRadioButton
    renderer = cache.retrieveRenderer( WebRadioButton.class );
    assertEquals( WebRadioButtonRenderer_Default_Noscript.class,
                  renderer.getClass() );
    // WebSelect
    renderer = cache.retrieveRenderer( WebSelect.class );
    assertEquals( WebSelectRenderer_Default_Noscript.class, 
                  renderer.getClass() );
    // WebText
    renderer = cache.retrieveRenderer( WebText.class );
    assertEquals( WebTextRenderer_Default_Noscript.class, 
                  renderer.getClass() );
    // WebTextArea
    renderer = cache.retrieveRenderer( WebTextArea.class );
    assertEquals( WebTextAreaRenderer_Default_Noscript.class,
                  renderer.getClass() );
    // WebCardLayout
    renderer = cache.retrieveRenderer( WebCardLayout.class );
    assertEquals( WebCardLayoutRenderer_Default_Noscript.class,
                  renderer.getClass() );
  }

  public void testScript() {
    Renderer renderer;
    //
    // Opera8 (script)
    W4TFixture.fakeBrowser( new Opera8( true ) );
    // WebAnchor
    RendererCache cache = RendererCache.getInstance();
    renderer = cache.retrieveRenderer( WebAnchor.class );
    assertEquals( WebAnchorRenderer_Default_Script.class, 
                  renderer.getClass() );
    // WebBorderComponent
    renderer = cache.retrieveRenderer( WebBorderComponent.class );
    assertEquals( WebBorderComponentRenderer_Default_Script.class,
                  renderer.getClass() );
    // WebScrollPane
    renderer = cache.retrieveRenderer( WebScrollPane.class );
    assertEquals( WebScrollPaneRenderer_Default_Script.class,
                  renderer.getClass() );
    // MenuItem
    renderer = cache.retrieveRenderer( MenuItem.class );
    assertEquals( MenuItemRenderer_Default_Script.class, 
                  renderer.getClass() );
    // MenuItemSeparator
    renderer = cache.retrieveRenderer( MenuItemSeparator.class );
    assertEquals( MenuItemSeparatorRenderer_Default_Script.class,
                  renderer.getClass() );
    // Menu
    renderer = cache.retrieveRenderer( Menu.class );
    assertEquals( MenuRenderer_Default_Script.class, 
                  renderer.getClass() );
    // MenuBar
    renderer = cache.retrieveRenderer( MenuBar.class );
    assertEquals( MenuBarRenderer_Opera8up_Script.class, 
                  renderer.getClass() );
    // TreeLeaf
    renderer = cache.retrieveRenderer( TreeLeaf.class );
    assertEquals( TreeLeafRenderer_Opera8up_Script.class, 
                  renderer.getClass() );
    // TreeNode
    renderer = cache.retrieveRenderer( TreeNode.class );
    assertEquals( TreeNodeRenderer_Opera8up_Script.class, 
                  renderer.getClass() );
    // TreeView
    renderer = cache.retrieveRenderer( TreeView.class );
    assertEquals( TreeViewRenderer_Opera8up_Script.class, 
                  renderer.getClass() );
    // MenuButton
    renderer = cache.retrieveRenderer( MenuButton.class );
    assertEquals( MenuButtonRenderer_Default_Script.class, 
                  renderer.getClass() );
    // NonAjaxComponent
    renderer = cache.retrieveRenderer( NonAjaxComponent.class );
    assertEquals( NonAjaxComponentRenderer_Default_Script.class,
                  renderer.getClass() );
    // WebButton
    renderer = cache.retrieveRenderer( WebButton.class );
    assertEquals( WebButtonRenderer_Default_Script.class, 
                  renderer.getClass() );
    // AreaSelector
    renderer = cache.retrieveRenderer( WebButton.class );
    assertEquals( WebButtonRenderer_Default_Script.class, 
                  renderer.getClass() );
    // LinkButton
    renderer = cache.retrieveRenderer( LinkButton.class );
    assertEquals( WebButtonRenderer_Default_Script.class, 
                  renderer.getClass() );
    // WebCheckBox
    renderer = cache.retrieveRenderer( WebCheckBox.class );
    assertEquals( WebCheckBoxRenderer_Default_Script.class, 
                  renderer.getClass() );
    // WebContainer
    renderer = cache.retrieveRenderer( WebContainer.class );
    assertEquals( WebContainerRenderer_Default_Script.class,
                  renderer.getClass() );
    // CItemList
    renderer = cache.retrieveRenderer( CItemList.class );
    assertEquals( WebContainerRenderer_Default_Script.class,
                  renderer.getClass() );
    // CTabbedPane
    renderer = cache.retrieveRenderer( CTabbedPane.class );
    assertEquals( WebContainerRenderer_Default_Script.class,
                  renderer.getClass() );
    // CTable
    renderer = cache.retrieveRenderer( CTable.class );
    assertEquals( WebContainerRenderer_Default_Script.class,
                  renderer.getClass() );
    // WebForm
    renderer = cache.retrieveRenderer( WebForm.class );
    assertEquals( WebFormRenderer_Default_Script.class, 
                  renderer.getClass() );
    // TestForm
    renderer = cache.retrieveRenderer( TestForm.class );
    assertEquals( WebFormRenderer_Default_Script.class, 
                  renderer.getClass() );
    // WebPanel
    renderer = cache.retrieveRenderer( WebPanel.class );
    assertEquals( WebContainerRenderer_Default_Script.class,
                  renderer.getClass() );
    // CMenu
    renderer = cache.retrieveRenderer( CMenu.class );
    assertEquals( WebContainerRenderer_Default_Script.class,
                  renderer.getClass() );
    // CToolBar
    renderer = cache.retrieveRenderer( CToolBar.class );
    assertEquals( WebContainerRenderer_Default_Script.class,
                  renderer.getClass() );
    // TreeViewer
    renderer = cache.retrieveRenderer( TreeView.class );
    assertEquals( TreeViewRenderer_Opera8up_Script.class, 
                  renderer.getClass() );
    // WebFileUpload
    renderer = cache.retrieveRenderer( WebFileUpload.class );
    assertEquals( WebFileUploadRenderer_Default_Script.class,
                  renderer.getClass() );
    // WebImage
    renderer = cache.retrieveRenderer( WebImage.class );
    assertEquals( WebImageRenderer_Default_Script.class, 
                  renderer.getClass() );
    // WebLabel
    renderer = cache.retrieveRenderer( WebLabel.class );
    assertEquals( WebLabelRenderer_Default_Script.class, 
                  renderer.getClass() );
    // WebRadioButton
    renderer = cache.retrieveRenderer( WebRadioButton.class );
    assertEquals( WebRadioButtonRenderer_Default_Script.class,
                  renderer.getClass() );
    // WebSelect
    renderer = cache.retrieveRenderer( WebSelect.class );
    assertEquals( WebSelectRenderer_Default_Script.class, 
                  renderer.getClass() );
    // WebText
    renderer = cache.retrieveRenderer( WebText.class );
    assertEquals( WebTextRenderer_Default_Script.class, 
                  renderer.getClass() );
    // WebTextArea
    renderer = cache.retrieveRenderer( WebTextArea.class );
    assertEquals( WebTextAreaRenderer_Default_Script.class, 
                  renderer.getClass() );
    // WebCardLayout
    renderer = cache.retrieveRenderer( WebCardLayout.class );
    assertEquals( WebCardLayoutRenderer_Opera8up_Script.class,
                  renderer.getClass() );
  }

  public void testAjax() {
    Renderer renderer;
    // Opera8 (Script)
    W4TFixture.fakeBrowser( new Opera9( true, true ) );
    // WebAnchor
    RendererCache cache = RendererCache.getInstance();
    renderer = cache.retrieveRenderer( WebAnchor.class );
    assertEquals( WebAnchorRenderer_Default_Ajax.class, 
                  renderer.getClass() );
    // WebBorderComponent
    renderer = cache.retrieveRenderer( WebBorderComponent.class );
    assertEquals( WebBorderComponentRenderer_Default_Ajax.class,
                  renderer.getClass() );
    // WebScrollPane
    renderer = cache.retrieveRenderer( WebScrollPane.class );
    assertEquals( WebScrollPaneRenderer_Opera8up_Ajax.class,
                  renderer.getClass() );
    // MenuItem
    renderer = cache.retrieveRenderer( MenuItem.class );
    assertEquals( MenuItemRenderer_Default_Ajax.class, 
                  renderer.getClass() );
    // MenuItemSeparator
    renderer = cache.retrieveRenderer( MenuItemSeparator.class );
    assertEquals( MenuItemSeparatorRenderer_Default_Ajax.class,
                  renderer.getClass() );
    // Menu
    renderer = cache.retrieveRenderer( Menu.class );
    assertEquals( MenuRenderer_Default_Ajax.class, 
                  renderer.getClass() );
    // MenuBar
    renderer = cache.retrieveRenderer( MenuBar.class );
    assertEquals( MenuBarRenderer_Opera8up_Ajax.class, 
                  renderer.getClass() );
    // TreeLeaf
    renderer = cache.retrieveRenderer( TreeLeaf.class );
    assertEquals( TreeLeafRenderer_Opera8up_Ajax.class, 
                  renderer.getClass() );
    // TreeNode
    renderer = cache.retrieveRenderer( TreeNode.class );
    assertEquals( TreeNodeRenderer_Opera8up_Ajax.class, 
                  renderer.getClass() );
    // TreeView
    renderer = cache.retrieveRenderer( TreeView.class );
    assertEquals( TreeViewRenderer_Opera8up_Ajax.class, 
                  renderer.getClass() );
    // MenuButton
    renderer = cache.retrieveRenderer( MenuButton.class );
    assertEquals( MenuButtonRenderer_Default_Ajax.class, 
                  renderer.getClass() );
    // WebButton
    renderer = cache.retrieveRenderer( WebButton.class );
    assertEquals( WebButtonRenderer_Default_Ajax.class, 
                  renderer.getClass() );
    // WebCheckBox
    renderer = cache.retrieveRenderer( WebCheckBox.class );
    assertEquals( WebCheckBoxRenderer_Default_Ajax.class, 
                  renderer.getClass() );
    // WebContainer
    renderer = cache.retrieveRenderer( WebContainer.class );
    assertEquals( WebContainerRenderer_Default_Ajax.class,
                  renderer.getClass() );
    // CItemList
    renderer = cache.retrieveRenderer( CItemList.class );
    assertEquals( WebContainerRenderer_Default_Ajax.class,
                  renderer.getClass() );
    // CTabbedPane
    renderer = cache.retrieveRenderer( CTabbedPane.class );
    assertEquals( WebContainerRenderer_Default_Ajax.class,
                  renderer.getClass() );
    // CTable
    renderer = cache.retrieveRenderer( CTable.class );
    assertEquals( WebContainerRenderer_Default_Ajax.class,
                  renderer.getClass() );
    // WebForm
    renderer = cache.retrieveRenderer( WebForm.class );
    assertEquals( WebFormRenderer_Default_Ajax.class, 
                  renderer.getClass() );
    // WebPanel
    renderer = cache.retrieveRenderer( WebPanel.class );
    assertEquals( WebContainerRenderer_Default_Ajax.class,
                  renderer.getClass() );
    // CMenu
    renderer = cache.retrieveRenderer( CMenu.class );
    assertEquals( WebContainerRenderer_Default_Ajax.class,
                  renderer.getClass() );
    // CToolBar
    renderer = cache.retrieveRenderer( CToolBar.class );
    assertEquals( WebContainerRenderer_Default_Ajax.class,
                  renderer.getClass() );
    // WebFileUpload
    renderer = cache.retrieveRenderer( WebFileUpload.class );
    assertEquals( WebFileUploadRenderer_Opera8up_Ajax.class,
                  renderer.getClass() );
    // WebImage
    renderer = cache.retrieveRenderer( WebImage.class );
    assertEquals( WebImageRenderer_Default_Ajax.class, 
                  renderer.getClass() );
    // WebLabel
    renderer = cache.retrieveRenderer( WebLabel.class );
    assertEquals( WebLabelRenderer_Default_Ajax.class, 
                  renderer.getClass() );
    // WebRadioButton
    renderer = cache.retrieveRenderer( WebRadioButton.class );
    assertEquals( WebRadioButtonRenderer_Default_Ajax.class,
                  renderer.getClass() );
    // WebSelect
    renderer = cache.retrieveRenderer( WebSelect.class );
    assertEquals( WebSelectRenderer_Default_Ajax.class, 
                  renderer.getClass() );
    // WebText
    renderer = cache.retrieveRenderer( WebText.class );
    assertEquals( WebTextRenderer_Default_Ajax.class, 
                  renderer.getClass() );
    // WebTextArea
    renderer = cache.retrieveRenderer( WebTextArea.class );
    assertEquals( WebTextAreaRenderer_Default_Ajax.class, 
                  renderer.getClass() );
    // WebCardLayout
    renderer = cache.retrieveRenderer( WebCardLayout.class );
    assertEquals( WebCardLayoutRenderer_Opera8up_Ajax.class,
                  renderer.getClass() );
  }
}
