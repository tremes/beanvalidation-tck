/**
 * Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.tests.integration.ee;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

import org.hibernate.beanvalidation.tck.util.IntegrationTest;
import org.hibernate.beanvalidation.tck.util.shrinkwrap.WebArchiveBuilder;

import static org.testng.Assert.assertNotNull;

/**
 * @author Gunnar Morling
 */
@IntegrationTest
@SpecVersion(spec = "beanvalidation", version = "2.0.0")
public class DefaultInjectionTest extends Arquillian {

	@EJB
	private ValidationTestEjb testEjb;

	@Deployment
	public static WebArchive createTestArchive() {
		return new WebArchiveBuilder()
				.withTestClass( DefaultInjectionTest.class )
				.withClass( ConstantMessageInterpolator.class )
				.withClass( Foo.class )
				.withClass( ValidationTestEjb.class )
				.withValidationXml( "test-validation.xml" )
				.withEmptyBeansXml()
				.build();
	}

	@Test
	@SpecAssertion(section = "10.2", id = "b")
	private void testDefaultValidatorFactoryGetsInjectedAtResource() throws Exception {
		assertNotNull( testEjb );
		testEjb.assertDefaultValidatorFactoryGetsInjected();
	}

	@Test
	@SpecAssertion(section = "10.2", id = "b")
	private void testDefaultValidatorGetsInjectedWithAtResource() {
		assertNotNull( testEjb );
		testEjb.assertDefaultValidatorGetsInjected();
	}
}
