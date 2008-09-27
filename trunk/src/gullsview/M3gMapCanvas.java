package gullsview;

import javax.microedition.lcdui.Graphics;
import javax.microedition.m3g.*;


public class M3gMapCanvas extends MapCanvas {
	private Graphics3D g3d;
private Background background;
	private VertexBuffer quadv;
	private IndexBuffer quadi;
	private Appearance quada;
	private Camera perspectiveCamera, hudCamera;
	private float azimuth;
	private float zenith;
	private Transform cameraTransform, tmpTransform;
	private float cameraDistance, scale;
	private TextureCache cache;
	private int scrolla, scrollm;
	private int rshifta, rshiftm;
	private Texture2D bgTex;
	
	public void init(Main main){
		super.init(main);
		this.g3d = Graphics3D.getInstance();
this.background = new Background();
this.background.setColor(0x0088ccff);
		int subdivision = 8;
		this.quadv = this.createQuadVertexBuffer(subdivision);
		this.quadi = this.createQuadIndexBuffer(subdivision);
		this.quada = new Appearance();
		PolygonMode polygonMode = new PolygonMode();
		polygonMode.setWinding(PolygonMode.WINDING_CCW);
		this.quada.setPolygonMode(polygonMode);
		this.perspectiveCamera = new Camera();
		this.perspectiveCamera.setPerspective(60, this.width / (float) this.height, 1, 10);
		this.cameraTransform = new Transform();
		this.tmpTransform = new Transform();
		this.cameraDistance = 3;
		this.scale = 7;
		this.zenith = 70;
		this.bgTex = new Texture2D(new Image2D(Image2D.RGB, this.main.getResImage("/bg.jpg")));
	}
	
	public void setSegment(int segment, int xcount, int ycount){
		super.setSegment(segment, xcount, ycount);
		int count = 15;
		this.cache = new TextureCache(count, this);
	}
	
	private VertexBuffer createQuadVertexBuffer(int subdivision){
		VertexBuffer vb = new VertexBuffer();
		int count = subdivision + 1;
		int vcount = count * count;
		VertexArray positions = new VertexArray(vcount, 3, 1);
		byte[] posbuf = new byte[vcount * 3];
		for(byte y = 0; y < count; y++){
			for(byte x = 0; x < count; x++){
				int offset = ((y * count) + x) * 3;
				posbuf[offset] = x;
				posbuf[offset + 1] = y;
				posbuf[offset + 2] = 0;
			}
		}
		positions.set(0, vcount, posbuf);
		vb.setPositions(positions, 1f / subdivision, new float[]{ 0, 0, 0 });
		// VertexArray normals = new VertexArray(4, 3, 1);
		// normals.set(0, 4, new byte[]{ 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1 });
		// this.vb.setNormals(normals);
		VertexArray texCoords = new VertexArray(vcount, 2, 1);
		byte[] texbuf = new byte[vcount * 2];
		for(byte y = 0; y < count; y++){
			for(byte x = 0; x < count; x++){
				int offset = ((y * count) + x) * 2;
				texbuf[offset] = x;
				texbuf[offset + 1] = y;
			}
		}
		texCoords.set(0, vcount, texbuf);
		vb.setTexCoords(0, texCoords, 1f / subdivision, new float[]{ 0, 0 });
		vb.setDefaultColor(0x00ffffff);
		return vb;
	}
	
	private IndexBuffer createQuadIndexBuffer(int subdivision){
		int count = subdivision + 1;
		int tcount = (subdivision * subdivision) * 2;
		int[] indices = new int[tcount * 3];
		for(int y = 0; y < subdivision; y++){
			for(int x = 0; x < subdivision; x++){
				int offset = ((y * subdivision) + x) * 6;
				int a = (y * count) + x;
				int b = (y * count) + (x + 1);
				int c = ((y + 1) * count) + x;
				int d = ((y + 1) * count) + (x + 1);
				indices[offset] = a;
				indices[offset + 1] = c;
				indices[offset + 2] = b;
				indices[offset + 3] = d;
				indices[offset + 4] = b;
				indices[offset + 5] = c;
			}
		}
		int[] lengths = new int[tcount];
		for(int i = 0; i < tcount; i++) lengths[i] = 3;
		return new TriangleStripArray(indices, lengths);
	}
	
	public void paint2(Graphics g){
		try {
			this.g3d.bindTarget(g, false, Graphics3D.ANTIALIAS);
			if(this.busy){
				
				return;
			}
this.g3d.clear(this.background);
			this.g3d.setCamera(this.perspectiveCamera, this.cameraTransform);
			this.processSegments(true);
		} finally {
			this.g3d.releaseTarget();
		}
	}
	
	private void processSegments(boolean render){
		int csx = this.div(this.cx, this.segment);
		int csy = this.div(this.cy, this.segment);
		int cx = this.mod(this.cx, this.segment);
		int cy = this.mod(this.cy, this.segment);
		int level = 1;
		for(int syi = -level; syi <= level; syi++){
			int sy = csy - syi;
			int y = cy + (syi * this.segment);
			for(int sxi = -level; sxi <= level; sxi++){
				int sx = csx - sxi;
				int x = cx + (sxi * this.segment);
				this.processSegment(sx, sy, x, y, render);
			}
		}
	}
	
	private void processSegment(int sx, int sy, int x, int y, boolean render){
		Texture2D tex = (Texture2D) this.cache.get(this.map, sx, sy);
		if(!render) return;
		if(tex == null) tex = this.bgTex;
		this.quada.setTexture(0, tex);
		this.tmpTransform.setIdentity();
		if(this.landscape) this.tmpTransform.postRotate(90, 0, 0, -1);
		this.tmpTransform.postTranslate(0, 0, -this.cameraDistance);
		this.tmpTransform.postScale(1, -1, 1);
		this.tmpTransform.postScale(this.scale, this.scale, 1);
		this.tmpTransform.postRotate(this.zenith, 1, 0, 0);
		this.tmpTransform.postRotate(this.azimuth, 0, 0, -1);
		this.tmpTransform.postTranslate(-(x / (float) this.segment), -(y / (float) this.segment), 0);
		this.g3d.render(this.quadv, this.quadi, this.quada, this.tmpTransform);
	}
	
	protected void scrollCommand(int dx, int dy, boolean pressed){
		int orgscrolla = this.scrolla;
		int orgscrollm = this.scrollm;
		if(pressed){
			this.scrolla += dx;
			this.scrollm += dy;
		} else {
			this.scrolla -= dx;
			this.scrollm -= dy;
		}
		if(this.scrolla < -1) this.scrolla = -1;
		if(this.scrolla > 1) this.scrolla = 1;
		if(this.scrollm < -1) this.scrollm = -1;
		if(this.scrollm > 1) this.scrollm = 1;
		if(((this.scrolla != 0) || (this.scrollm != 0)) && ((orgscrolla == 0) || (orgscrollm == 0)))
			this.main.startScroll();
	}
	
	public boolean scroll(){
		int stepa = 10;
		int stepm = 20;
		int shifta = this.scrolla * stepa;
		int shiftm = this.scrollm * stepm;
		int coef = 6;
		this.rshifta = ((this.rshifta * coef) + shifta) / (coef + 1);
		this.rshiftm = ((this.rshiftm * coef) + shiftm) / (coef + 1);
		this.azimuth += this.rshifta;
		this.cx -= (int)(Math.sin(Math.toRadians(this.azimuth)) * shiftm);
		this.cy += (int)(Math.cos(Math.toRadians(this.azimuth)) * shiftm);
		this.correctPosition();
		return (this.rshifta != 0) || (this.rshiftm != 0);
	}
	
	public void prefetch(){
		this.processSegments(false);
	}
}


