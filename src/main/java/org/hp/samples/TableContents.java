package org.hp.samples;

public class TableContents {

	private Integer textId;
	private String value;
	
	public TableContents(Integer textId, String value) {
		this.textId = textId;
		this.value = value;
	}
	
	public Integer getId() {
		return textId;
	
	}
	
	public String getValue() {
		return value;
	}
}
