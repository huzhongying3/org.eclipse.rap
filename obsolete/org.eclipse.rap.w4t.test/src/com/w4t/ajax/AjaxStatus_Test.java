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
package com.w4t.ajax;

import junit.framework.TestCase;

import org.eclipse.rwt.internal.browser.Ie5up;
import org.eclipse.rwt.internal.browser.Ie6;
import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;
import org.eclipse.rwt.internal.service.ContextProvider;

import com.w4t.*;
import com.w4t.dhtml.*;
import com.w4t.mockup.TestForm;
import com.w4t.util.ComponentTreeVisitor;
import com.w4t.util.RendererCache;
import com.w4t.util.ComponentTreeVisitor.AllComponentVisitor;


public class AjaxStatus_Test extends TestCase {
  
  private static final class NonAjaxContainer extends WebComponent {
    private String foo;
    public void setFoo( final String foo ) {
      this.foo = foo;
    }
    public String getFoo() {
      return foo;
    }
  }
  
  protected void setUp() throws Exception {
    W4TFixture.setUp();
    W4TFixture.createContext();
  }
  
  protected void tearDown() throws Exception {
    W4TFixture.tearDown();
    W4TFixture.removeContext();
  }
  
  public void testSetMustRender() throws Exception {
    // construct form -> containing component -> containing text
    WebForm form = new TestForm();
    form.setWebLayout( new WebGridLayout() );
    WebContainer container = new WebPanel();
    form.add( container, new Position( 1, 1 ) );
    WebComponent text = new WebText();
    container.add( text );
    
    AjaxStatus formAjaxStatus = W4TFixture.getAjaxStatus( form );
    formAjaxStatus.updateStatus( true );
    assertTrue( getMustRender( form ) );
    assertTrue( getMustRender( container ) );
    assertTrue( getMustRender( text ) );
  }
  
  public void testAjaxEnvelope() throws Exception {
    WebForm form = W4TFixture.getEmptyWebFormInstance();

    WebPanel panel = new WebPanel();
    form.add( panel, WebBorderLayout.NORTH );

    WebLabel label = new WebLabel();
    panel.add( label );
    
    WebScrollPane scrollPane = new WebScrollPane();
    WebButton button = new WebButton();
    scrollPane.setContent( button );
    panel.add( scrollPane );
    
    TreeView treeView = new TreeView();
    TreeNode treeNode = new TreeNode();
    treeView.addItem( treeNode );
    TreeLeaf treeLeaf = new TreeLeaf();
    treeNode.addItem( treeLeaf );
    panel.add( treeView );

    W4TFixture.getAjaxStatus( form ).updateStatus( true );
    ComponentTreeVisitor visitor = new AllComponentVisitor() {
      public boolean doVisit( final WebComponent component ) {
        assertTrue( getMustRender( component ) );
        return true;
      }
    };
    ComponentTreeVisitor.accept( form, visitor );
    
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( panel ) );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( label ) );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( scrollPane ) );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( button ) );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( treeView ) );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( treeNode ) );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( treeLeaf ) );
    
    assertTrue( AjaxStatusUtil.isRootOfSubTreeToRender( form ) );
    W4TFixture.getAjaxStatus( form ).setMustRender( false );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( form ) );
    
    assertTrue( AjaxStatusUtil.isRootOfSubTreeToRender( panel ) );
    W4TFixture.getAjaxStatus( panel ).setMustRender( false );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( panel ) );
    
    assertTrue( AjaxStatusUtil.isRootOfSubTreeToRender( label ) );
    assertTrue( AjaxStatusUtil.isRootOfSubTreeToRender( scrollPane ) );
    assertTrue( AjaxStatusUtil.isRootOfSubTreeToRender( treeView ) );
    W4TFixture.getAjaxStatus( label ).setMustRender( false );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( label ) );
    assertTrue( AjaxStatusUtil.isRootOfSubTreeToRender( scrollPane ) );
    assertTrue( AjaxStatusUtil.isRootOfSubTreeToRender( treeView ) );
    W4TFixture.getAjaxStatus( scrollPane ).setMustRender( false );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( label ) );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( scrollPane ) );
    assertTrue( AjaxStatusUtil.isRootOfSubTreeToRender( treeView ) );
    W4TFixture.getAjaxStatus( treeView ).setMustRender( false );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( label ) );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( scrollPane ) );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( treeView ) );
    
    assertTrue( AjaxStatusUtil.isRootOfSubTreeToRender( button ) );
    W4TFixture.getAjaxStatus( button ).setMustRender( false );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( button ) );
    
    assertTrue( AjaxStatusUtil.isRootOfSubTreeToRender( treeNode ) );
    W4TFixture.getAjaxStatus( treeNode ).setMustRender( false );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( treeNode ) );
    
    assertTrue( AjaxStatusUtil.isRootOfSubTreeToRender( treeLeaf ) );
    W4TFixture.getAjaxStatus( treeLeaf ).setMustRender( false );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( treeLeaf ) );
    
    // check unparent components
    WebText text = new WebText();
    W4TFixture.getAjaxStatus( text ).setMustRender( true );
    assertTrue( AjaxStatusUtil.mustRender( text ) );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( text ) );
    
    WebAnchor anchor = new WebAnchor();
    W4TFixture.getAjaxStatus( anchor ).setMustRender( true );
    assertTrue( AjaxStatusUtil.mustRender( anchor ) );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( anchor ) );
    
    Menu menu = new Menu();
    W4TFixture.getAjaxStatus( menu ).setMustRender( true );
    assertTrue( AjaxStatusUtil.mustRender( menu ) );
    assertFalse( AjaxStatusUtil.isRootOfSubTreeToRender( menu ) );
    
    // check envelope markup
    W4TFixture.getAjaxStatus( label ).updateStatus( true );
    
    HtmlResponseWriter writer = new HtmlResponseWriter();
    ContextProvider.getStateInfo().setResponseWriter( writer );
    String labelContent = "<labelContent />";
    W4TFixture.fakeBrowser( new Ie5up( true, false ) );
    AjaxStatusUtil.startEnvelope( label );
    writer.append( labelContent );
    AjaxStatusUtil.endEnvelope( label );
    String markup = W4TFixture.getBodyMarkup( writer );
    String expected = labelContent;
    assertEquals( expected, markup );

    writer = new HtmlResponseWriter();
    ContextProvider.getStateInfo().setResponseWriter( writer );
    W4TFixture.fakeBrowser( new Ie5up( true, true ) );
    AjaxStatusUtil.startEnvelope( label );
    writer.append( labelContent );
    AjaxStatusUtil.endEnvelope( label );
    markup = W4TFixture.getBodyMarkup( writer );
    expected =   "<envelope id=\"" 
               + label.getUniqueID()
               + "\"><!--"
               + labelContent
               + "--></envelope>";
    assertEquals( expected, markup );
  }
  
  public void testNonAjaxComponent() {
    W4TFixture.fakeBrowser( new Ie6( true, true ) );
    WebForm form = W4TFixture.getEmptyWebFormInstance();
    NonAjaxContainer nonAjaxComponent = new NonAjaxContainer();
    form.add( nonAjaxComponent, WebBorderLayout.CENTER );
    assertFalse( hasAjaxRenderer( nonAjaxComponent) );
    // initially everything must be rendered
    AjaxStatusUtil.preRender( form );
    assertEquals( true, getMustRender( form ) );
    assertEquals( true, getMustRender( nonAjaxComponent ) );
    // second 'turn': nonAjaxComponent must be rendered because it has no ajax 
    // renderer, form must be rendered because it is parent of nonAjaxComponent 
    AjaxStatusUtil.postRender( form );
    AjaxStatusUtil.preRender( form );
    assertEquals( true, getMustRender( form ) );
    assertEquals( true, getMustRender( nonAjaxComponent ) );
    //
    // ??? is that correct ???
    // 
    nonAjaxComponent.setFoo( "bar" );
    AjaxStatusUtil.postRender( form );
    AjaxStatusUtil.preRender( form );
    assertEquals( true, getMustRender( form ) );
    assertEquals( true, getMustRender( nonAjaxComponent ) );
  }
  
  // Test AjaxStatusUtil#updateHashCode and ensure that only the passed-in 
  // component is updated (none of its children)
  public void testUpdateHashCode() {
    WebPanel panel = new WebPanel();
    WebText text = new WebText();
    panel.add( text );
    AjaxStatusUtil.updateHashCode( panel );
    assertEquals( true, 
                  W4TFixture.getAjaxStatus( panel ).hasComponentHashCode() );
    assertEquals( false, 
                  W4TFixture.getAjaxStatus( text ).hasComponentHashCode() );
  }

  private static boolean hasAjaxRenderer( final WebComponent component ) {
    RendererCache rendererCache = RendererCache.getInstance();
    String rendererClassName;
    rendererClassName = rendererCache.getRendererClass( component.getClass() );
    return    rendererClassName != null 
           && rendererClassName.endsWith( "_Ajax" );
  }
  
  private static boolean getMustRender( final WebComponent component ) {
    return W4TFixture.getAjaxStatus( component ).mustRender();
  }
}