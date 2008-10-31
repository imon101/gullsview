package gullsview;

import javax.microedition.lcdui.Graphics;
import javax.microedition.m3g.*;


public class M3gMapCanvas extends MapCanvas {
	private Graphics3D g3d;
	
	private VertexBuffer mapQuadv, hudQuadv;
	private IndexBuffer mapQuadi, hudQuadi;
	private Appearance mapQuada, hudQuada;
	
	private Camera perspectiveCamera, hudCamera;
	private float azimuth;
	private float zenith;
	private Transform cameraTransform, tmpTransform;
	private float cameraFov, cameraDistance, cameraVertShift, scale;
	
	private TextureCache cache;
	
	private int scrolla, scrollm;
	private int rshifta, rshiftm;
	
	private Texture2D bgTex, compassTex;
	private Sprite3D pointerSprite, busySprite;
	
	public void init(Main main){
		this.hudCamera = new Camera();
		
		super.init(main);
		
		this.g3d = Graphics3D.getInstance();
		
		int subdivision = 8;
		this.mapQuadv = this.createQuadVertexBuffer(subdivision);
		this.mapQuadi = this.createQuadIndexBuffer(subdivision);
		this.mapQuada = this.createAppearance(PolygonMode.WINDING_CCW, CompositingMode.REPLACE);
		
		this.hudQuadv = this.createQuadVertexBuffer(1);
		this.hudQuadi = this.createQuadIndexBuffer(1);
		this.hudQuada = this.createAppearance(PolygonMode.WINDING_CCW, CompositingMode.MODULATE);
		
		this.perspectiveCamera = new Camera();
		this.cameraTransform = new Transform();
		this.cameraFov = 60;
		this.cameraDistance = 3;
		this.cameraVertShift = 0;
		this.scale = 5;
		this.zenith = 60;
		
		this.tmpTransform = new Transform();
		
		this.bgTex = this.createTexture(Image2D.RGB, "/bg.jpg");
		this.pointerSprite = this.createSprite("/pointer2.png");
		this.busySprite = this.createSprite("/busy.png");
		this.compassTex = this.createTexture(Image2D.RGBA, "/compass.png");
	}
	
	public void dispose(){
		this.cache.clear();
	}
	
	public void setSegment(int segment, int xcount, int ycount){
		super.setSegment(segment, xcount, ycount);
		int count = 15;
		this.cache = new TextureCache(count, this);
	}
	
	protected void updateDim(){
		super.updateDim();
		Transform hudTransform = new Transform();
		hudTransform.postScale(2f / this.getWidth(), 2f / this.getHeight(), 1);
		this.hudCamera.setGeneric(hudTransform);
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
	
	private Appearance createAppearance(int winding, int blending){
		Appearance appearance = new Appearance();
		PolygonMode polygonMode = new PolygonMode();
		polygonMode.setWinding(winding);
		appearance.setPolygonMode(polygonMode);
		CompositingMode compositingMode = new CompositingMode();
		compositingMode.setBlending(blending);
		appearance.setCompositingMode(compositingMode);
		return appearance;
	}
	
	private Texture2D createTexture(int imageFormat, String res){
		return new Texture2D(new Image2D(imageFormat, this.main.getResImage(res)));
	}
	
	private Sprite3D createSprite(String res){
		Appearance appearance = new Appearance();
		CompositingMode cm = new CompositingMode();
		cm.setBlending(CompositingMode.ALPHA);
		appearance.setCompositingMode(cm);
		return new Sprite3D(false, new Image2D(Image2D.RGBA, this.main.getResImage(res)), appearance);
	}
	
	public void paint2(Graphics g){
		try {
			this.g3d.bindTarget(g, false, Graphics3D.ANTIALIAS);
			if(this.busy){
				this.renderHudSprite(this.busySprite, 20 - (this.width / 2), (this.height / 2) - 20);
				return;
			}
			float aspectRatio = (float) this.getWidth() / (float) this.getHeight();
			float fov = this.landscape ? this.cameraFov * aspectRatio : this.cameraFov;
			this.perspectiveCamera.setPerspective(fov, aspectRatio, 1, 10);
			// this.g3d.clear(new Background());
			this.g3d.setCamera(this.perspectiveCamera, this.cameraTransform);
			this.processSegments(true);
			this.renderHudSprite(this.pointerSprite, 0, 0);
			this.renderHudTex(this.compassTex, 30 - (this.width / 2), 30 - (this.height / 2), this.azimuth, 1);
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
		tex.setFiltering(Texture2D.FILTER_LINEAR, Texture2D.FILTER_LINEAR);
		this.mapQuada.setTexture(0, tex);
		this.tmpTransform.setIdentity();
		if(this.landscape) this.tmpTransform.postRotate(90, 0, 0, -1);
		this.tmpTransform.postTranslate(0, this.cameraVertShift, -this.cameraDistance);
		this.tmpTransform.postScale(1, -1, 1);
		this.tmpTransform.postScale(this.scale, this.scale, 1);
		this.tmpTransform.postRotate(this.zenith, 1, 0, 0);
		this.tmpTransform.postRotate(this.azimuth, 0, 0, -1);
		this.tmpTransform.postTranslate(-(x / (float) this.segment), -(y / (float) this.segment), 0);
		this.g3d.render(this.mapQuadv, this.mapQuadi, this.mapQuada, this.tmpTransform);
	}
	
	private void renderHudTex(Texture2D tex, float x, float y, float angle, float scale){
		Image2D image = tex.getImage();
		float tw = image.getWidth() * scale;
		float th = image.getHeight() * scale;
		tex.setBlending(Texture2D.FUNC_DECAL);
		tex.setFiltering(Texture2D.FILTER_LINEAR, Texture2D.FILTER_LINEAR);
		this.hudQuada.setTexture(0, tex);
		this.tmpTransform.setIdentity();
		this.g3d.setCamera(this.hudCamera, this.tmpTransform);
		this.tmpTransform.postTranslate(x, y, 0);
		this.tmpTransform.postRotate(angle, 0, 0, 1);
		this.tmpTransform.postScale(1, -1, 0);
		this.tmpTransform.postTranslate(-tw / 2, -th / 2, 0);
		this.tmpTransform.postScale(tw, th, 0);
		this.g3d.render(this.hudQuadv, this.hudQuadi, this.hudQuada, this.tmpTransform);
	}
	
	private void renderHudSprite(Sprite3D sprite, float x, float y){
		this.tmpTransform.setIdentity();
		this.g3d.setCamera(this.hudCamera, this.tmpTransform);
		this.tmpTransform.postTranslate(x, y, 0);
		this.g3d.render(sprite, this.tmpTransform);
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
		this.cx -= (int)(Math.sin(Math.toRadians(this.azimuth)) * this.rshiftm);
		this.cy += (int)(Math.cos(Math.toRadians(this.azimuth)) * this.rshiftm);
		this.correctPosition();
		return (this.rshifta != 0) || (this.rshiftm != 0);
	}
	
	public void prefetch(){
		this.processSegments(false);
	}
}


