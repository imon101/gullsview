package gullsview;

import javax.microedition.lcdui.Graphics;
import javax.microedition.m3g.*;


public class M3gMapCanvas extends MapCanvas {
	private Graphics3D g3d;
	private Background background;
	private VertexBuffer quadv;
	private IndexBuffer quadi;
	private Appearance quada;
	private Camera camera;
	private float azimuth;
	private float zenith;
	private Transform cameraTransform, tmpTransform;
	private float cameraDistance, scale;
	private TextureCache cache;
	
	public void init(Main main){
		super.init(main);
		this.g3d = Graphics3D.getInstance();
		this.background = new Background();
		this.background.setColor(0x0088ccff);
		this.quadv = this.createQuadVertexBuffer();
		this.quadi = this.createQuadIndexBuffer();
		this.quada = new Appearance();
		PolygonMode polygonMode = new PolygonMode();
		polygonMode.setWinding(PolygonMode.WINDING_CW);
		this.quada.setPolygonMode(polygonMode);
		this.camera = new Camera();
		this.camera.setPerspective(60, this.width / (float) this.height, 1, 10);
		this.cameraTransform = new Transform();
		this.tmpTransform = new Transform();
		this.cameraDistance = 3;
		this.scale = 5;
		this.cache = new TextureCache(9, this);
	}
	
	private VertexBuffer createQuadVertexBuffer(){
		VertexBuffer vb = new VertexBuffer();
		VertexArray positions = new VertexArray(4, 3, 1);
		positions.set(0, 4, new byte[]{ 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0 });
		vb.setPositions(positions, 1, new float[]{ 0, 0, 0 });
		// VertexArray normals = new VertexArray(4, 3, 1);
		// normals.set(0, 4, new byte[]{ 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1 });
		// this.vb.setNormals(normals);
		VertexArray texCoords = new VertexArray(4, 2, 1);
		texCoords.set(0, 4, new byte[]{ 0, 0, 1, 0, 1, 1, 0, 1 });
		vb.setTexCoords(0, texCoords, 1, new float[]{ 0, 0 });
vb.setDefaultColor(0x00ffffff);
		return vb;
	}
	
	private IndexBuffer createQuadIndexBuffer(){
		return new TriangleStripArray(new int[]{ 0, 1, 2, 0, 2, 3 }, new int[]{ 3, 3 });
	}
	
	public void paint(Graphics g){
		try {
			this.g3d.bindTarget(g, false, Graphics3D.ANTIALIAS);
			this.g3d.clear(this.background);
			this.g3d.setCamera(this.camera, this.cameraTransform);
			this.renderSegment(0, 0, 255, -255);
			this.renderSegment(1, 0, 0, -255);
			this.renderSegment(0, 1, 255, 0);
			this.renderSegment(1, 1, 0, 0);
		} finally {
			this.g3d.releaseTarget();
		}
	}
	
	private void renderSegment(int sx, int sy, int x, int y){
		Texture2D tex = (Texture2D) this.cache.get(this.map, sx, sy);
		this.quada.setTexture(0, tex);
		this.tmpTransform.setIdentity();
		this.tmpTransform.postTranslate(0, 0, -this.cameraDistance);
		this.tmpTransform.postScale(1, -1, 1);
		this.tmpTransform.postScale(this.scale, this.scale, 1);
		this.tmpTransform.postRotate(this.zenith, 1, 0, 0);
		this.tmpTransform.postRotate(this.azimuth, 0, 0, -1);
		this.tmpTransform.postTranslate(-(x / (float) this.segment), -(y / this.segment), 0);
		this.g3d.render(this.quadv, this.quadi, this.quada, this.tmpTransform);
	}
	
	protected void scrollCommand(int dx, int dy, boolean pressed){
if(!pressed) return;
this.azimuth += dx * 10;
this.zenith += dy * 5;
this.main.startScroll();
	}
	
	public boolean scroll(){
		return false;
	}
	
	public void render(){
this.repaint();
	}
	
	public void setBusy(boolean on){
		
	}
}


