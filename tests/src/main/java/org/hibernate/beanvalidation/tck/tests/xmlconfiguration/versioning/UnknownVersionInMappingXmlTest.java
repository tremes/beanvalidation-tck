/**
 * Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.tests.xmlconfiguration.versioning;

import javax.validation.ValidationException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

import org.hibernate.beanvalidation.tck.util.TestUtil;
import org.hibernate.beanvalidation.tck.util.shrinkwrap.WebArchiveBuilder;

/**
 * @author Gunnar Morling
 */
@SpecVersion(spec = "beanvalidation", version = "2.0.0")
public class UnknownVersionInMappingXmlTest extends Arquillian {

	private static final String MAPPING_FILE = "UnknownVersionInMappingXmlTest.xml";

	@Deployment
	public static WebArchive createTestArchive() {
		return new WebArchiveBuilder()
				.withTestClass( UnknownVersionInMappingXmlTest.class )
				.withResource( MAPPING_FILE )
				.build();
	}

	@Test(expectedExceptions = ValidationException.class)
	@SpecAssertion(section = "8.2", id = "c")
	public void testConstraintMappingWithUnknownSchemaVersion() {
		TestUtil.getConfigurationUnderTest()
				.addMapping( UnknownVersionInMappingXmlTest.class.getResourceAsStream( MAPPING_FILE ) )
				.buildValidatorFactory()
				.getValidator();
	}
}
