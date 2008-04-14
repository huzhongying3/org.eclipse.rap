/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.demo;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.*;

public class DemoTreeViewPart extends ViewPart implements IDoubleClickListener {

  private TreeViewer viewer;
  private IPropertySheetPage propertyPage;

//  TODO [rst] Add via extension
//  private final class LeafStarLabelDecorator extends LabelProvider
//    implements ILabelDecorator
//  {
//
//    public String decorateText( final String text, final Object element ) {
//      if( text.startsWith( "Leaf" ) ) {
//        return text + "*";
//      }
//      return text;
//    }
//    
//    public Image decorateImage( final Image image, final Object element ) {
//      return null;
//    }
//  }
  
  class ViewLabelProvider extends LabelProvider {

    public Image getImage( Object element ) {
      return PlatformUI.getWorkbench()
        .getSharedImages()
        .getImage( ISharedImages.IMG_OBJ_ELEMENT );
    }
  }
  
  class TreeObject implements IPropertySource {
    private static final String PROP_ID_LOCATION = "location";
    private static final String PROP_ID_NAME = "name";
    private String name;
    private String location;
    private TreeParent parent;

    public TreeObject( final String name ) {
      this.name = name;
    }

    public TreeObject( final String name, final String location ) {
      this.name = name;
      this.location = location;
    }

    public String getName() {
      return name;
    }

    public String getLocation() {
      return location;
    }

    public void setParent( final TreeParent parent ) {
      this.parent = parent;
    }

    public TreeParent getParent() {
      return parent;
    }

    public String toString() {
      return getName();
    }

    public Object getEditableValue() {
      return null;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
      return new IPropertyDescriptor[] {
        new PropertyDescriptor( PROP_ID_NAME, "Name" ),
        new PropertyDescriptor( PROP_ID_LOCATION, "Location" ),
      };
    }

    public Object getPropertyValue( final Object id ) {
      Object result = null;
      if( PROP_ID_NAME.equals( id ) ) {
        result = name;
      } else if( PROP_ID_LOCATION.equals( id ) ) {
        result = location;
      }
      return result;
    }

    public boolean isPropertySet( final Object id ) {
      boolean result = false;
      if( PROP_ID_NAME.equals( id ) ) {
        result = name != null && !"".equals( name );
      } else if( PROP_ID_LOCATION.equals( id ) ) {
        result = location != null && !"".equals( location );
      }
      return result;
    }

    public void resetPropertyValue( final Object id ) {
      if( PROP_ID_NAME.equals( id ) ) {
        name = "";
      } else if( PROP_ID_LOCATION.equals( id ) ) {
        location = "";
      }
    }

    public void setPropertyValue( final Object id, final Object value ) {
      if( PROP_ID_NAME.equals( id ) ) {
        name = ( String )value;
      } else if( PROP_ID_LOCATION.equals( id ) ) {
        location = ( String )value;
      }
    }
  }
  
  /**
   * Instances of this type are decorated with an error marker
   */
  class BrokenTreeObject extends TreeObject {
    
    public BrokenTreeObject( final String name ) {
      super( name );
    }
  }

  class TreeParent extends TreeObject {

    private final ArrayList children;

    public TreeParent( final String name ) {
      super( name );
      children = new ArrayList();
    }

    public void addChild( final TreeObject child ) {
      children.add( child );
      child.setParent( this );
    }

    public void removeChild( final TreeObject child ) {
      children.remove( child );
      child.setParent( null );
    }

    public TreeObject[] getChildren() {
      TreeObject[] result = new TreeObject[ children.size() ];
      children.toArray( result );
      return result;
    }

    public boolean hasChildren() {
      return children.size() > 0;
    }
  }
  class TreeViewerContentProvider
    implements IStructuredContentProvider, ITreeContentProvider
  {

    private TreeParent invisibleRoot;

    public void inputChanged( final Viewer v,
                              final Object oldInput,
                              final Object newInput )
    {
    }

    public void dispose() {
    }

    public Object[] getElements( final Object parent ) {
      if( parent instanceof IViewPart ) {
        if( invisibleRoot == null ) {
          initialize();
        }
        return getChildren( invisibleRoot );
      }
      return getChildren( parent );
    }

    public Object getParent( final Object child ) {
      if( child instanceof TreeObject ) {
        return ( ( TreeObject )child ).getParent();
      }
      return null;
    }

    public Object[] getChildren( final Object parent ) {
      if( parent instanceof TreeParent ) {
        return ( ( TreeParent )parent ).getChildren();
      }
      return new Object[ 0 ];
    }

    public boolean hasChildren( final Object parent ) {
      if( parent instanceof TreeParent ) {
        return ( ( TreeParent )parent ).hasChildren();
      }
      return false;
    }

    /*
     * We will set up a dummy model to initialize tree heararchy. In a real
     * code, you will connect to a real model and expose its hierarchy.
     */
    private void initialize() {
      TreeObject to1 = new TreeObject( "EclipseCon location",
                                       "http://maps.google.com/maps?q=5001%20Great%20America%20Pkwy%20Santa%20Clara%20CA%2095054" );
      TreeObject to2 = new TreeObject( "Eclipse Foundation",
                                       "http://maps.google.com/maps?q=Ottawa" );
      TreeObject to3 = new TreeObject( "Innoopract Inc",
                                       "http://maps.google.com/maps?q=Portland" );
      TreeParent p1 = new TreeParent( "Locate in browser view" );
      p1.addChild( to1 );
      p1.addChild( to2 );
      p1.addChild( to3 );
      TreeObject to4 = new BrokenTreeObject( "Leaf 4" );
      TreeParent p2 = new TreeParent( "Parent 2" );
      p2.addChild( to4 );
      TreeParent root = new TreeParent( "Root" );
      TreeParent p3 = new TreeParent( "Child X - filter me!" );
      root.addChild( p1 );
      root.addChild( p2 );
      root.addChild( p3 );
      invisibleRoot = new TreeParent( "" );
      invisibleRoot.addChild( root );
    }
  }

  public void createPartControl( final Composite parent ) {
    viewer = new TreeViewer( parent );
    viewer.setContentProvider( new TreeViewerContentProvider() );
    ILabelDecorator labelDecorator
      = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
    ILabelProvider labelProvider
      = new DecoratingLabelProvider( new ViewLabelProvider(), labelDecorator );    
    viewer.setLabelProvider( labelProvider );
    viewer.setInput( this );
    viewer.addDoubleClickListener( this );
    getSite().setSelectionProvider( viewer );
  }
  
  public Object getAdapter( final Class adapter ) {
    Object result = super.getAdapter( adapter );
    if( adapter == IPropertySheetPage.class ) {
      if( propertyPage == null ) {
        propertyPage = new PropertySheetPage();
      }
      result = propertyPage;
    }
    return result;
  }

  public void setFocus() {
    viewer.getTree().setFocus();
  }

  public void doubleClick( final DoubleClickEvent event ) {
    MessageDialog.openInformation( viewer.getTree().getShell(),
                                   "Treeviewer",
                                   "You doubleclicked on "
                                       + event.getSelection().toString() );
  }

  public TreeViewer getViewer() {
    return viewer;
  }
}
