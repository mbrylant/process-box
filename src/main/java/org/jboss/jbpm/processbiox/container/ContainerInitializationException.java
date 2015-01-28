package org.jboss.jbpm.processbiox.container;

public class ContainerInitializationException extends Exception {

	private static final long serialVersionUID = -3444596545598617638L;

	public ContainerInitializationException() {
	}

	public ContainerInitializationException(String message) {
		super(message);
	}

	public ContainerInitializationException(Throwable cause) {
		super(cause);
	}

	public ContainerInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

}
