package com.web.servlet;

/**
 * @author Administrator
 *
 */
public class ImageUpLoadEntity {

    private String imagetype = "";
    private String txtfilename = "";
    private long    filesize = 0 ;
    
	public long getFilesize() {
		return filesize;
	}
	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}
	public String getImagetype() {
		return imagetype;
	}
	public void setImagetype(String imagetype) {
		this.imagetype = imagetype;
	}
	public String getTxtfilename() {
		return txtfilename;
	}
	public void setTxtfilename(String txtfilename) {
		this.txtfilename = txtfilename;
	}
    
}
