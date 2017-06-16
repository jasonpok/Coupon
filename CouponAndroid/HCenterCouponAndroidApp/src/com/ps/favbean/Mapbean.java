package com.ps.favbean;

public class Mapbean {
	int id;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getiLatitute() {
		return iLatitute;
	}

	public void setiLatitute(String iLatitute) {
		this.iLatitute = iLatitute;
	}

	public String getiLongitute() {
		return iLongitute;
	}

	public void setiLongitute(String iLongitute) {
		this.iLongitute = iLongitute;
	}

	public String getsImgPath() {
		return sImgPath;
	}

	public void setsImgPath(String sImgPath) {
		this.sImgPath = sImgPath;
	}

	public String getsDescription() {
		return sDiscription;
	}

	public void setsDescription(String sComment) {
		this.sDiscription = sComment;
	}

	String  sImgPath, sDiscription,iLatitute, iLongitute,sStoreName,sStoreText;;
	
	public String getsStoreText() {
		return sStoreText;
	}

	public void setsStoreText(String sStoreText) {
		this.sStoreText = sStoreText;
	}

	public String getsStoreName() {
		return sStoreName;
	}

	public void setsStoreName(String sStoreName) {
		this.sStoreName = sStoreName;
	}

	Mapbean(int id, String latitute, String lognitute, String path, String comment) {
		this.id = id;
		iLatitute = latitute;
		iLongitute = lognitute;
		
		sImgPath = path;
		sDiscription = comment;
		
	}
	public Mapbean()
	{
		
	}
}
