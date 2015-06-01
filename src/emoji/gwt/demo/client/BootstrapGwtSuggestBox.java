//
//  BootstrapGwtSuggestBox.java
//  emoji-gwt-demo
//
//  Created by William Shakour (billy1380) on 4 Jan 2015.
//  Copyright © 2015 SPACEHOPPER STUDIOS Ltd. All rights reserved.
//
package emoji.gwt.demo.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * @author William Shakour (billy1380)
 *
 */
public interface BootstrapGwtSuggestBox extends ClientBundle {
	public static final BootstrapGwtSuggestBox INSTANCE = GWT.create(BootstrapGwtSuggestBox.class);

	public interface BootstrapGwtSuggestBoxStyle extends CssResource {};
	
	@Source("res/bootstrap-gwt-suggestbox.css")
	BootstrapGwtSuggestBoxStyle styles();
};