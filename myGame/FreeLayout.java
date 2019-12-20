package myGame;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 * @author T.Ryu(FSI)
 */
public class FreeLayout implements LayoutManager
{	
	/**
	 * @see java.awt.LayoutManager#addLayoutComponent
	 */
	public final void addLayoutComponent(String s, Component c) 
	{
	}

	/**
	 * @see java.awt.LayoutManager#removeLayoutComponent
	 */
	public final void removeLayoutComponent(Component c)
	{
		/*
		 */
		c.getParent().repaint();		
	}

	/**
	 * @see java.awt.LayoutManager#preferredLayoutSize
	 */
	public final Dimension preferredLayoutSize(Container target)
	{
		return new Dimension(0, 0);
	}

	/**
	 * @return Dimension Dimension(0, 0)
	 * @see java.awt.LayoutManager#minimumLayoutSize
	 */
	public final Dimension minimumLayoutSize(Container target)
	{
		return new Dimension(0, 0);
	}

	/**
	 * @see java.awt.LayoutManager#layoutContainer
	 */
	public final void layoutContainer(Container target)
	{
	}
}