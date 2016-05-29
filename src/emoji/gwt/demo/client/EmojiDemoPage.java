//
//  EmojiDemoPage.java
//  emoji-gwt-demo
//
//  Created by William Shakour (billy1380) on 4 Jan 2015.
//  Copyright © 2015 SPACEHOPPER STUDIOS Ltd. All rights reserved.
//
package emoji.gwt.demo.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import emoji.gwt.emoji.Emoji;
import emoji.gwt.emoji.Emoji.Ready;

/**
 * @author William Shakour (billy1380)
 *
 */
public class EmojiDemoPage extends Composite implements SelectionChangeEvent.Handler, ValueChangeHandler<String>, SelectionHandler<Suggestion> {

	private static EmojiDemoPageUiBinder uiBinder = GWT.create(EmojiDemoPageUiBinder.class);

	interface EmojiDemoPageUiBinder extends UiBinder<Widget, EmojiDemoPage> {}

	private static final int TIMER_TIMEOUT = 15000;

	MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
	@UiField(provided = true) SuggestBox nameField = new SuggestBox(oracle);
	@UiField(provided = true) CellList<String> emojiLookup = new CellList<String>(new AbstractCell<String>() {

		@Override
		public void render(Context context, String value, SafeHtmlBuilder sb) {
			sb.appendHtmlConstant("<div class=\"col-sm-3\">");
			sb.appendHtmlConstant("<img style=\"width:20px;\" src=\"");
			sb.appendHtmlConstant(Emoji.get().uri(value));
			sb.appendHtmlConstant("\" alt=\"");
			sb.appendHtmlConstant(value);
			sb.appendHtmlConstant("\" /> <span style=\"color:black;\">");
			sb.appendHtmlConstant(value);
			sb.appendHtmlConstant("</span></div>");
		}
	});
	@UiField Image image;
	@UiField ListBox cboTheme;

	private SingleSelectionModel<String> model;
	private Timer timer;
	private Random random = new Random();

	private int themeIndex = 0;

	public EmojiDemoPage() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtSuggestBox.INSTANCE.styles().ensureInjected();

		Emoji.get(null, new Ready() {

			@Override
			public void ready(Emoji emoji) {
				oracle.addAll(Emoji.get().keyWords());
				nameField.addValueChangeHandler(EmojiDemoPage.this);
				nameField.addSelectionHandler(EmojiDemoPage.this);

				// Focus the cursor on the name field when the app loads
				nameField.setFocus(true);

				final List<String> keywords = new ArrayList<String>(Emoji.get().keyWords());
				emojiLookup.setPageSize(Integer.MAX_VALUE);
				ListDataProvider<String> provider = new ListDataProvider<String>(keywords);
				provider.addDataDisplay(emojiLookup);

				model = new com.google.gwt.view.client.SingleSelectionModel<String>();
				emojiLookup.setSelectionModel(model);
				model.addSelectionChangeHandler(EmojiDemoPage.this);

				timer = new Timer() {

					@Override
					public void run() {
						setImage(keywords.get(random.nextInt(keywords.size())));
					}
				};

				setImage(keywords.get(random.nextInt(keywords.size())));
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.SelectionChangeEvent.Handler#onSelectionChange (com.google.gwt.view.client.SelectionChangeEvent)
	 */
	@Override
	public void onSelectionChange(SelectionChangeEvent event) {
		setImage(model.getSelectedObject());

		Window.scrollTo(0, 0);
	}

	/**
	 * @param selectedObject
	 */
	private void setImage(String selectedObject) {
		timer.cancel();

		nameField.setText(selectedObject);

		ImageResource i = Emoji.get().resource(selectedObject);
		if (i != null) {
			image.setResource(i);
		}

		timer.schedule(TIMER_TIMEOUT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.logical.shared.SelectionHandler#onSelection(com. google.gwt.event.logical.shared.SelectionEvent)
	 */
	@Override
	public void onSelection(SelectionEvent<Suggestion> event) {
		setImage(event.getSelectedItem().getReplacementString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange( com.google.gwt.event.logical.shared.ValueChangeEvent)
	 */
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		setImage(event.getValue());
	}

	@UiHandler("cboTheme")
	void onCboThemeValueChanged(ChangeEvent event) {
		if (themeIndex != cboTheme.getSelectedIndex()) {
			themeIndex = cboTheme.getSelectedIndex();
		}

		String name = null;
		switch (themeIndex) {
		case 0:
			name = "apple";
			break;
		case 1:
			name = "noto";
			break;
		case 2:
			name = "twemoji";
			break;
		default:
			break;
		}

		Emoji.get(name, new Ready() {

			@Override
			public void ready(Emoji emoji) {
				emojiLookup.redraw();
				setImage(nameField.getText());
			}
		});

	}

}
