package org.bibsonomy.webapp.command.actions;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author jensi
 */
public class PublicationRendererCommand extends PostPublicationCommand {
	private MultipartFile pica;
	private MultipartFile marc;
	/**
	 * @return the pica
	 */
	public MultipartFile getPica() {
		return this.pica;
	}
	/**
	 * @param pica the pica to set
	 */
	public void setPica(MultipartFile pica) {
		this.pica = pica;
	}
	/**
	 * @return the marc
	 */
	public MultipartFile getMarc() {
		return this.marc;
	}
	/**
	 * @param marc the marc to set
	 */
	public void setMarc(MultipartFile marc) {
		this.marc = marc;
	}
	
}
