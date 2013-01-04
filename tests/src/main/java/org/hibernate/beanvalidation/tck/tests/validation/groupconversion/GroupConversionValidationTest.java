/*
* JBoss, Home of Professional Open Source
* Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.hibernate.beanvalidation.tck.tests.validation.groupconversion;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import javax.validation.groups.Default;
import javax.validation.metadata.ElementDescriptor.Kind;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import org.hibernate.beanvalidation.tck.util.TestUtil;
import org.hibernate.beanvalidation.tck.util.shrinkwrap.WebArchiveBuilder;

import static org.hibernate.beanvalidation.tck.util.TestUtil.assertCorrectNumberOfViolations;
import static org.hibernate.beanvalidation.tck.util.TestUtil.assertCorrectPropertyPaths;
import static org.hibernate.beanvalidation.tck.util.TestUtil.assertDescriptorKinds;
import static org.hibernate.beanvalidation.tck.util.TestUtil.assertNodeNames;

/**
 * @author Gunnar Morling
 */
@SpecVersion(spec = "beanvalidation", version = "1.1.0")
public class GroupConversionValidationTest extends Arquillian {

	private Validator validator;

	@Deployment
	public static WebArchive createTestArchive() {
		return new WebArchiveBuilder()
				.withTestClassPackage( GroupConversionValidationTest.class )
				.build();
	}

	@BeforeMethod
	public void setupValidator() {
		validator = TestUtil.getValidatorUnderTest();
	}

	@Test
	@SpecAssertion(section = "4.4.5", id = "b")
	public void testGroupConversionIsAppliedOnField() {

		Set<ConstraintViolation<User>> constraintViolations = validator.validate( Users.withInvalidMainAddress() );

		assertCorrectPropertyPaths(
				constraintViolations,
				"mainAddress.street1",
				"mainAddress.zipCode"
		);
	}

	@Test
	@SpecAssertion(section = "4.4.5", id = "b")
	public void testSeveralGroupConversionsAppliedOnField() {

		Set<ConstraintViolation<User>> constraintViolations = validator.validate( Users.withInvalidPreferredShipmentAddress() );

		assertCorrectPropertyPaths(
				constraintViolations,
				"preferredShipmentAddress.street1",
				"preferredShipmentAddress.zipCode"
		);

		constraintViolations = validator.validate(
				Users.withInvalidPreferredShipmentAddress(),
				Complex.class
		);

		assertCorrectPropertyPaths(
				constraintViolations,
				"preferredShipmentAddress.doorCode"
		);

		constraintViolations = validator.validate(
				Users.withInvalidPreferredShipmentAddress(),
				Default.class,
				Complex.class
		);

		assertCorrectPropertyPaths(
				constraintViolations,
				"preferredShipmentAddress.street1",
				"preferredShipmentAddress.zipCode",
				"preferredShipmentAddress.doorCode"
		);

		constraintViolations = validator.validate(
				Users.withInvalidPreferredShipmentAddress(),
				Complete.class
		);

		assertCorrectPropertyPaths(
				constraintViolations,
				"preferredShipmentAddress.street1",
				"preferredShipmentAddress.zipCode",
				"preferredShipmentAddress.doorCode"
		);
	}

	@Test
	@SpecAssertion(section = "4.4.5", id = "b")
	public void testGroupConversionIsAppliedOnProperty() {
		Set<ConstraintViolation<User>> constraintViolations = validator.validate( Users.withInvalidShipmentAddress() );

		assertCorrectPropertyPaths(
				constraintViolations,
				"shipmentAddresses[0].street1",
				"shipmentAddresses[0].zipCode"
		);
	}

	@Test
	@SpecAssertion(section = "4.4.5", id = "b")
	public void testGroupConversionIsAppliedOnMethodReturnValue() throws Exception {
		//given
		User user = Users.validUser();
		Method method = User.class.getMethod( "retrieveMainAddress" );
		Object returnValue = Addresses.withInvalidStreet1();

		//when
		Set<ConstraintViolation<User>> constraintViolations = validator.forMethods()
				.validateReturnValue( user, method, returnValue );

		//then
		assertCorrectNumberOfViolations( constraintViolations, 1 );

		Path propertyPath = constraintViolations.iterator().next().getPropertyPath();

		assertDescriptorKinds( propertyPath, Kind.METHOD, Kind.RETURN_VALUE, Kind.PROPERTY );
		assertNodeNames( propertyPath, "retrieveMainAddress", null, "street1" );
	}

	@Test
	@SpecAssertion(section = "4.4.5", id = "b")
	public void testGroupConversionIsAppliedOnMethodParameter() throws Exception {
		//given
		User user = Users.validUser();
		Method method = User.class.getMethod( "setMainAddress", Address.class );
		Object[] arguments = new Object[] { Addresses.withInvalidStreet1() };

		//when
		Set<ConstraintViolation<User>> constraintViolations = validator.forMethods()
				.validateParameters( user, method, arguments );

		//then
		assertCorrectNumberOfViolations( constraintViolations, 1 );

		Path propertyPath = constraintViolations.iterator().next().getPropertyPath();

		assertDescriptorKinds( propertyPath, Kind.METHOD, Kind.PARAMETER, Kind.PROPERTY );
		assertNodeNames( propertyPath, "setMainAddress", "arg0", "street1" );
	}

	@Test
	@SpecAssertion(section = "4.4.5", id = "b")
	public void testGroupConversionIsAppliedOnConstructorReturnValue() throws Exception {
		//given
		Constructor<User> constructor = User.class.getConstructor( Address.class );
		User createdObject = new User( Addresses.withInvalidStreet1() );

		//when
		Set<ConstraintViolation<User>> constraintViolations = validator.forMethods()
				.validateConstructorReturnValue( constructor, createdObject );

		//then
		assertCorrectNumberOfViolations( constraintViolations, 1 );

		Path propertyPath = constraintViolations.iterator().next().getPropertyPath();

		assertDescriptorKinds(
				propertyPath,
				Kind.CONSTRUCTOR,
				Kind.RETURN_VALUE,
				Kind.PROPERTY,
				Kind.PROPERTY
		);
		assertNodeNames( propertyPath, "User", null, "address", "street1" );
	}

	@Test
	@SpecAssertion(section = "4.4.5", id = "b")
	public void testGroupConversionIsAppliedOnConstructorParameter() throws Exception {
		//given
		Constructor<User> constructor = User.class.getConstructor( Address.class );
		Object[] arguments = new Object[] { Addresses.withInvalidStreet1() };

		//when
		Set<ConstraintViolation<User>> constraintViolations = validator.forMethods()
				.validateConstructorParameters( constructor, arguments );

		//then
		assertCorrectNumberOfViolations( constraintViolations, 1 );

		Path propertyPath = constraintViolations.iterator().next().getPropertyPath();

		assertDescriptorKinds( propertyPath, Kind.CONSTRUCTOR, Kind.PARAMETER, Kind.PROPERTY );
		assertNodeNames( propertyPath, "User", "arg0", "street1" );
	}

	@Test
	@SpecAssertion(section = "4.4.5", id = "c")
	public void testGroupConversionIsNotExecutedRecursively() {
		Set<ConstraintViolation<User>> constraintViolations = validator.validate( Users.withInvalidOfficeAddress() );

		assertCorrectPropertyPaths(
				constraintViolations,
				"officeAddress.street1",
				"officeAddress.zipCode"
		);

		constraintViolations = validator.validate(
				Users.withInvalidOfficeAddress(),
				BasicPostal.class
		);

		assertCorrectPropertyPaths(
				constraintViolations,
				"officeAddress.doorCode"
		);
	}

	@Test
	@SpecAssertion(section = "4.4.5", id = "f")
	public void testGroupConversionWithSequenceAsTo() {
		User user = Users.validUser();

		Set<ConstraintViolation<User>> constraintViolations = validator.validate( user );
		assertCorrectNumberOfViolations( constraintViolations, 0 );

		user.getWeekendAddress().setDoorCode( "ABC" );
		constraintViolations = validator.validate( user );
		assertCorrectPropertyPaths( constraintViolations, "weekendAddress.doorCode" );

		user.getWeekendAddress().setStreet1( null );
		constraintViolations = validator.validate( user );
		assertCorrectPropertyPaths( constraintViolations, "weekendAddress.street1" );
	}

	private static class Users {

		public static User validUser() {
			return new User(
					Addresses.validAddress(),
					Arrays.asList( Addresses.validAddress() ),
					Addresses.validAddress(),
					Addresses.validAddress(),
					Addresses.validAddress()
			);
		}

		public static User withInvalidMainAddress() {
			User user = validUser();
			user.setMainAddress( Addresses.invalidAddress() );
			return user;
		}

		public static User withInvalidShipmentAddress() {
			User user = validUser();
			user.setShipmentAddresses( Arrays.asList( Addresses.invalidAddress() ) );
			return user;
		}

		public static User withInvalidPreferredShipmentAddress() {
			User user = validUser();
			user.setPreferredShipmentAddress( Addresses.invalidAddress() );
			return user;
		}

		public static User withInvalidOfficeAddress() {
			User user = validUser();
			user.setOfficeAddress( Addresses.invalidAddress() );
			return user;
		}
	}

	private static class Addresses {

		public static Address validAddress() {
			return new Address( "Main Street", "c/o Hitchcock", "123", "AB" );
		}

		public static Address invalidAddress() {
			return new Address( null, null, "12", "ABC" );
		}

		public static Address withInvalidStreet1() {
			Address address = validAddress();
			address.setStreet1( null );
			return address;
		}
	}
}