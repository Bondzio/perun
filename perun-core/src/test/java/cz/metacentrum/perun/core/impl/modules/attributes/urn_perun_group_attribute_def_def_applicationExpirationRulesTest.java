package cz.metacentrum.perun.core.impl.modules.attributes;

import cz.metacentrum.perun.core.api.Attribute;
import cz.metacentrum.perun.core.api.Group;
import cz.metacentrum.perun.core.api.exceptions.WrongAttributeValueException;
import cz.metacentrum.perun.core.impl.PerunSessionImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static cz.metacentrum.perun.core.implApi.modules.attributes.AbstractApplicationExpirationRulesModule.applicationIgnoredByAdminKeyName;
import static cz.metacentrum.perun.core.implApi.modules.attributes.AbstractApplicationExpirationRulesModule.applicationWaitingForEmailVerificationKeyName;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

public class urn_perun_group_attribute_def_def_applicationExpirationRulesTest {

	private static urn_perun_group_attribute_def_def_applicationExpirationRules classInstance;
	private static final PerunSessionImpl session = mock(PerunSessionImpl.class, RETURNS_DEEP_STUBS);
	private static Attribute attributeToCheck;
	private static Group group;

	@Before
	public void setUp() {
		classInstance = new urn_perun_group_attribute_def_def_applicationExpirationRules();
		attributeToCheck = new Attribute();
		group = new Group();
	}

	// ------------- Syntax ------------- //

	@Test
	public void autoExpirationWaitingForEmailCorrectSyntax() throws Exception {
		Map<String, String> value = new LinkedHashMap<>();
		value.put(applicationWaitingForEmailVerificationKeyName, "14");
		attributeToCheck.setValue(value);
		classInstance.checkAttributeSyntax(session, group, attributeToCheck);
	}

	@Test
	public void autoExpirationWaitingForEmailIncorrectSyntax() {
		Map<String, String> value = new LinkedHashMap<>();
		value.put(applicationWaitingForEmailVerificationKeyName, "-14");
		attributeToCheck.setValue(value);
		assertThatExceptionOfType(WrongAttributeValueException.class)
			.isThrownBy(() -> classInstance.checkAttributeSyntax(session, group, attributeToCheck));
	}

	@Test
	public void autoExpirationIgnoredByAdminCorrectSyntax() throws Exception {
		Map<String, String> value = new LinkedHashMap<>();
		value.put(applicationIgnoredByAdminKeyName, "60");
		attributeToCheck.setValue(value);
		classInstance.checkAttributeSyntax(session, group, attributeToCheck);
	}

	@Test
	public void autoExpirationIgnoredByAdminIncorrectSyntax()  {
		Map<String, String> value = new LinkedHashMap<>();
		value.put(applicationIgnoredByAdminKeyName, "-60");
		attributeToCheck.setValue(value);
		assertThatExceptionOfType(WrongAttributeValueException.class)
			.isThrownBy(() -> classInstance.checkAttributeSyntax(session, group, attributeToCheck));
	}
}
