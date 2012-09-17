package com.pi.editor.gui.map;

import com.pi.common.constants.TileConstants;
import com.pi.editor.Editor;
import com.pi.editor.gui.EditorPage;
import com.pi.graphics.device.IGraphics;
import com.pi.gui.PIContainer;

public class MapRenderLoop implements EditorPage {
	private final Editor editor;

	// GUIz
	private PIContainer root = new PIContainer();
	private MapViewerObject mapArea;
	private MapEditorObject mapEditor;

	// End GUIz

	public MapRenderLoop(final Editor edit) {
		this.editor = edit;

		// Map Area
		mapArea = new MapViewerObject();
		mapArea.setSize(15, 15);

		// Map Editor
		mapEditor = new MapEditorObject(mapArea, this);
		mapEditor.setSize(500, 500);

		// Root
		root.setLocation(0, 0);
		root.setSize(editor.getApplet().getWidth(), editor
				.getApplet().getHeight());
		root.add(mapArea);
		root.add(mapEditor);

		root.compile();
	}

	public final Editor getEditor() {
		return editor;
	}

	@Override
	public final void register() {
		editor.getApplet().addMouseListener(root);
		editor.getApplet().addMouseMotionListener(root);
		editor.getApplet().addMouseWheelListener(root);
		editor.getApplet().addKeyListener(root);
	}

	@Override
	public final void unRegister() {
		editor.getApplet().removeMouseListener(root);
		editor.getApplet().removeMouseMotionListener(root);
		editor.getApplet().removeMouseWheelListener(root);
		editor.getApplet().removeKeyListener(root);
	}

	@Override
	public final void render(final IGraphics graphics) {
		root.render(graphics);
	}

	@Override
	public final void resize(final int width, final int height) {
		mapEditor.setSize(mapEditor.getWidth(), height - 50);
		mapEditor.setLocation(width - mapEditor.getWidth(), 0);

		mapArea.setSize((width - mapEditor.getWidth() - 50)
				/ TileConstants.TILE_WIDTH, (height - 50)
				/ TileConstants.TILE_HEIGHT);
		root.compile();
	}
}
