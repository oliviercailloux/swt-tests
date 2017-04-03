package io.github.oliviercailloux.y2017.swt_tests;

import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableEditorApp {
	@SuppressWarnings("unused")
	private static Logger LOGGER = LoggerFactory.getLogger(TableEditorApp.class);

	public static void main(String[] args) throws Exception {
		TableEditorApp app = new TableEditorApp();
		app.showBogusTable();
	}

	public void capture(Display display, Control control, String fileName) {
		GC gc = new GC(control);
		Image image = new Image(display, control.getBounds());
		gc.copyArea(image, 0, 0);
		gc.dispose();
		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { image.getImageData() };
		loader.save(fileName, SWT.IMAGE_PNG);
		image.dispose();
	}

	public void showBogusTable() throws Exception {
		Display display = new Display();
		Shell shell = new Shell(display);
		Table table = new Table(shell, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		TableColumn c1 = new TableColumn(table, SWT.NONE);
		c1.setText("c1");
		TableColumn c2 = new TableColumn(table, SWT.NONE);
		c2.setText("c2");
		TableItem item1 = new TableItem(table, SWT.NONE);
		Text text = new Text(table, SWT.NONE);
		text.setText(String.join("", Collections.nCopies(50, "a")));
		TableEditor tableEditor = new TableEditor(table);
		tableEditor.setEditor(text, item1, 0);
		tableEditor.grabHorizontal = true;
		tableEditor.grabVertical = true;
		shell.setSize(1000, 200);
		c1.setWidth(500);
		c2.setWidth(500);
		table.setBounds(0, 0, 1000, 200);
		LOGGER.info("All widths set.");
		tableEditor.layout();
		shell.open();
		LOGGER.info("Text bounds: {}.", text.getBounds());
		display.timerExec(500, () -> {
			LOGGER.info("Text bounds start: {}.", text.getBounds());
			capture(display, shell, "swt-start.png");
		});
		display.timerExec(4000, () -> {
			LOGGER.info("Text bounds later: {}.", text.getBounds());
			capture(display, shell, "swt-later.png");
		});
		/**
		 * Calling layout() after opening (and not just before opening) is
		 * required to position the control appropriately. Otherwise, must wait
		 * a bit for table editor’s own timer-triggered layout, hence the text
		 * control may appear wrongly positioned in the table for a short time
		 * then “jump” to the right position.
		 */
//		tableEditor.layout();
//		LOGGER.info("Text bounds after opening and re-layout: {}.", text.getBounds());
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
				/**
				 * Sleep a bit here to more easily spot the problem visually.
				 */
//				Thread.sleep(2000);
			}
		}
		display.dispose();
	}
}
