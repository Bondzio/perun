package cz.metacentrum.perun.audit.events.GeneralServiceManagerEvents;

import cz.metacentrum.perun.audit.events.AuditEvent;
import cz.metacentrum.perun.core.api.Facility;
import cz.metacentrum.perun.core.api.Service;

/**
 * @author Vojtech Sassmann <vojtech.sassmann@gmail.com>
 */
public class FreeDenialServiceOnFacility extends AuditEvent {

	private Service service;
	private Facility facility;
	private String message;

	@SuppressWarnings("unused") // used by jackson mapper
	public FreeDenialServiceOnFacility() {
	}

	public FreeDenialServiceOnFacility(Service service, Facility facility) {
		this.service = service;
		this.facility = facility;
		this.message = formatMessage("free denial: %s on %s.", service, facility);
	}

	public Service getService() {
		return service;
	}

	public Facility getFacility() {
		return facility;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return message;
	}
}
