package com.ps.favbean;


public class Favorite {

	int id, iLatitute, iLongitute,iTotalCount,sCouponCategory;
	

	
	String sCouponName, sFDate, sTDate, sImgPath, sComment,sSpinnerKind,imageUrl,iCouponNumber,sCouponShareImg;


	public Favorite(int id, int latitute, int lognitute, String name, String fromdate,
			String toDate, String path, String comment,int kind,String ImgUrl,String couponNumber,String shareImg) {
		this.id = id;
		iLatitute = latitute;
		iLongitute = lognitute;
		sCouponName = name;
		sFDate = fromdate;
		sTDate = toDate;
		sImgPath = path;
		sComment = comment;
		iTotalCount = kind;
		imageUrl = ImgUrl;
		iCouponNumber = couponNumber;
		sCouponShareImg = shareImg;
	}

	//save to db
	public Favorite(int latitute, int lognitute, String name, String fromdate,
			String toDate, String path, String comment,int kind,String ImgUrl,String couponNumber,String shareImg) {
		
		iLatitute = latitute;
		iLongitute = lognitute;
		sCouponName = name;
		sFDate = fromdate;
		sTDate = toDate;
		sImgPath = path;
		sComment = comment;
		iTotalCount = kind;
		imageUrl = ImgUrl;
		iCouponNumber = couponNumber;
		sCouponShareImg = shareImg;
	}

	public Favorite() {
		
	}

	public String getsSpinnerKind() {
		return sSpinnerKind;
	}

	public void setsSpinnerKind(String sSpinnerKind) {
		this.sSpinnerKind = sSpinnerKind;
	}

	public String getsImageUrl() {
		return imageUrl;
	}

	public void setsImageUrl(String sSpinnerAge) {
		this.imageUrl = sSpinnerAge;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getiLatitute() {
		return iLatitute;
	}

	public void setiLatitute(int iLatitute) {
		this.iLatitute = iLatitute;
	}

	public int getiLongitute() {
		return iLongitute;
	}

	public void setiLongitute(int iLongitute) {
		this.iLongitute = iLongitute;
	}

	public String getiCouponNumber() {
		return iCouponNumber;
	}

	public void setiCouponNumber(String iCouponNumber) {
		this.iCouponNumber = iCouponNumber;
	}

	
	public String getsCouponName() {
		return sCouponName;
	}

	public void setsCouponName(String sCouponName) {
		this.sCouponName = sCouponName;
	}
	
	public int getsCouponCategory() {
		return sCouponCategory;
	}

	public void setsCouponCategory(int sCouponCategory) {
		this.sCouponCategory = sCouponCategory;
	}

	public String getfDate() {
		return sFDate;
	}

	public void setfDate(String fDate) {
		this.sFDate = fDate;
	}

	public String gettDate() {
		return sTDate;
	}

	public void settDate(String tDate) {
		this.sTDate = tDate;
	}

	public String getsImgPath() {
		return sImgPath;
	}

	public void setsImgPath(String sImgPath) {
		this.sImgPath = sImgPath;
	}

	public String getsComment() {
		return sComment;
	}

	public void setsComment(String sComment) {
		this.sComment = sComment;
	}
	

	public String getsCouponShareImg() {
		return sCouponShareImg;
	}

	public void setsCouponShareImg(String sCouponShareImg) {
		this.sCouponShareImg = sCouponShareImg;
	}
	
	public int getiTotalCount() {
		return iTotalCount;
	}

	public void setiTotalCount(int iTotalCount) {
		this.iTotalCount = iTotalCount;
	}
}
