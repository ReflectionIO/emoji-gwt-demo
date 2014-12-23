package emoji.gwt.demo.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.builder.shared.HtmlBuilderFactory;
import com.google.gwt.dom.builder.shared.HtmlTableBuilder;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.builder.shared.TableSectionBuilder;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import emoji.gwt.emoji.Emoji;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EmojiDemo implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final Button convertButton = new Button("Convert");
		final TextBox nameField = new TextBox();
		nameField.setText(":smile:");
		final Label errorLabel = new Label();

		// We can add style names to widgets
		convertButton.addStyleName("convertButton");

		// Add the nameField and convertButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("nameFieldContainer").add(nameField);
		RootPanel.get("convertButtonContainer").add(convertButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);

		// Focus the cursor on the name field when the app loads
		nameField.setFocus(true);
		nameField.selectAll();

		RootPanel.get("refContainer").add(new HTML(buildRefTable()));

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML responseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Converted emoji:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>To image:</b>"));
		dialogVPanel.add(responseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				convertButton.setEnabled(true);
				convertButton.setFocus(true);
			}
		});

		// Create a handler for the convertButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the convertButton.
			 */
			public void onClick(ClickEvent event) {
				resolve();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					resolve();
				}
			}

			/**
			 * convert the name from the nameField to the server and wait for a response.
			 */
			private void resolve() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = nameField.getText();

				if (!Emoji.get().isValid(textToServer)) {
					errorLabel.setText("Not a valid emoji");
					return;
				}

				// Then, we convert the input to the server.
				convertButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				responseLabel.setText("");

				dialogBox.setText("Convert emoji to image");
				responseLabel.removeStyleName("responseLabelError");

				responseLabel.setHTML("<img src=\"" + Emoji.get().uri(textToServer) + "\" />");
				dialogBox.center();
				closeButton.setFocus(true);
			}
		}

		// Add a handler to convert the name to the server
		MyHandler handler = new MyHandler();
		convertButton.addClickHandler(handler);
		nameField.addKeyUpHandler(handler);
	}

	private SafeHtml buildRefTable() {
		HtmlTableBuilder b = HtmlBuilderFactory.get().createTableBuilder();
		TableSectionBuilder sectionBuilder = b.startTBody();

		List<String> list = new ArrayList<String>(Emoji.get().keyWords());
		int count = list.size();
		final int cols = 3;
		int mod = count % cols;
		int loop = mod > 0 ? count + (cols - mod) : count;

		TableRowBuilder rowBuilder = null;
		TableCellBuilder cellBuilder = null;
		String keyWord;
		for (int i = 0; i < loop; i++) {
			keyWord = i < count ? list.get(i) : null;

			if (i % cols == 0) {
				rowBuilder = sectionBuilder.startTR();
			}

			cellBuilder = rowBuilder.startTD();
			if (keyWord != null) {
				cellBuilder.html(SafeHtmlUtils.fromString(keyWord));
			} else {
				cellBuilder.html(SafeHtmlUtils.fromSafeConstant("&nbsp;"));
			}
			cellBuilder.endTD();

			if (i % cols == cols - 1) {
				rowBuilder.endTR();
			}
		}

		sectionBuilder.endTBody();

		return b.asSafeHtml();
	}
}
