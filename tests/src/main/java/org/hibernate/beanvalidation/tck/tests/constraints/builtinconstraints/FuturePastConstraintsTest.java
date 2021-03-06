/**
 * Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.tests.constraints.builtinconstraints;

import static org.hibernate.beanvalidation.tck.util.TestUtil.assertCorrectNumberOfViolations;
import static org.hibernate.beanvalidation.tck.util.TestUtil.assertCorrectPropertyPaths;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.HijrahDate;
import java.time.chrono.JapaneseDate;
import java.time.chrono.MinguoDate;
import java.time.chrono.ThaiBuddhistDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Future;
import javax.validation.constraints.Past;

import org.hibernate.beanvalidation.tck.tests.time.FixedClockProvider;
import org.hibernate.beanvalidation.tck.tests.time.FutureDummyEntity;
import org.hibernate.beanvalidation.tck.tests.time.FutureRelativePartialDummyEntity;
import org.hibernate.beanvalidation.tck.tests.time.PastDummyEntity;
import org.hibernate.beanvalidation.tck.tests.time.PastRelativePartialDummyEntity;
import org.hibernate.beanvalidation.tck.util.TestUtil;
import org.hibernate.beanvalidation.tck.util.shrinkwrap.WebArchiveBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

/**
 * Tests for {@link Future} and {@link Past} built-in constraints.
 *
 * @author Hardy Ferentschik
 * @author Guillaume Smet
 */
@SpecVersion(spec = "beanvalidation", version = "2.0.0")
public class FuturePastConstraintsTest {

	private static final ZoneId TZ_BERLIN = ZoneId.of( "Europe/Berlin" );

	@Deployment
	public static WebArchive createTestArchive() {
		return new WebArchiveBuilder()
				.withTestClass( FuturePastConstraintsTest.class )
				.build();
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "7", id = "a"),
			@SpecAssertion(section = "7", id = "m")
	})
	public void testPastConstraint() {
		Validator validator = TestUtil.getValidatorUnderTest();
		PastDummyEntity dummy = new PastDummyEntity();

		Set<ConstraintViolation<PastDummyEntity>> constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 0 );

		ZonedDateTime reference = ZonedDateTime.now( TZ_BERLIN );

		ZonedDateTime future = reference.plusYears( 1 ).plusMonths( 1 ).plusHours( 1 );
		dummy = new PastDummyEntity( future );

		constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 13 );
		assertCorrectPropertyPaths(
				constraintViolations, "date", "calendar", "instant", "hijrahDate", "japaneseDate", "localDate", "localDateTime",
				"minguoDate", "offsetDateTime", "thaiBuddhistDate", "year", "yearMonth", "zonedDateTime"
		);

		ZonedDateTime past = reference.minusYears( 1 ).minusMonths( 1 ).minusHours( 1 );
		dummy = new PastDummyEntity( past );

		constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 0 );
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "7", id = "a"),
			@SpecAssertion(section = "7", id = "m")
	})
	public void testPastConstraintForRelativePartial() {
		// For partials not referencing a precise point in time, we need to use the FixedClockProvider
		// to make sure the tests are working at any date

		ZonedDateTime reference = ZonedDateTime.of( 2016, 6, 6, 14, 0, 0, 0, TZ_BERLIN );

		ValidatorFactory validatorFactory = TestUtil.getConfigurationUnderTest()
				.clockProvider( new FixedClockProvider( reference ) )
				.buildValidatorFactory();
		Validator validator = validatorFactory.getValidator();

		PastRelativePartialDummyEntity dummy = new PastRelativePartialDummyEntity();

		Set<ConstraintViolation<PastRelativePartialDummyEntity>> constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 0 );

		ZonedDateTime future = reference.plusMonths( 1 ).plusHours( 1 );
		dummy = new PastRelativePartialDummyEntity( future );

		constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 3 );
		assertCorrectPropertyPaths(
				constraintViolations, "localTime", "monthDay", "offsetTime"
		);

		ZonedDateTime past = reference.minusMonths( 1 ).minusHours( 1 );
		dummy = new PastRelativePartialDummyEntity( past );

		constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 0 );
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "7", id = "a"),
			@SpecAssertion(section = "7", id = "n")
	})
	public void testFutureConstraint() {
		Validator validator = TestUtil.getValidatorUnderTest();
		FutureDummyEntity dummy = new FutureDummyEntity();

		Set<ConstraintViolation<FutureDummyEntity>> constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 0 );

		ZonedDateTime reference = ZonedDateTime.now( TZ_BERLIN );

		ZonedDateTime past = reference.minusYears( 1 ).minusMonths( 1 ).minusHours( 1 );
		dummy = new FutureDummyEntity( past );

		constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 13 );
		assertCorrectPropertyPaths(
				constraintViolations, "date", "calendar", "instant", "hijrahDate", "japaneseDate", "localDate", "localDateTime",
				"minguoDate", "offsetDateTime", "thaiBuddhistDate", "year", "yearMonth", "zonedDateTime"
		);

		ZonedDateTime future = reference.plusYears( 1 ).plusMonths( 1 ).plusHours( 1 );
		dummy = new FutureDummyEntity( future );

		constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 0 );
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "7", id = "a"),
			@SpecAssertion(section = "7", id = "n")
	})
	public void testFutureConstraintForRelativePartial() {
		// For partials not referencing a precise point in time, we need to use the FixedClockProvider
		// to make sure the tests are working at any date

		ZonedDateTime reference = ZonedDateTime.of( 2016, 6, 6, 14, 0, 0, 0, TZ_BERLIN );

		ValidatorFactory validatorFactory = TestUtil.getConfigurationUnderTest()
				.clockProvider( new FixedClockProvider( reference ) )
				.buildValidatorFactory();
		Validator validator = validatorFactory.getValidator();

		FutureRelativePartialDummyEntity dummy = new FutureRelativePartialDummyEntity();

		Set<ConstraintViolation<FutureRelativePartialDummyEntity>> constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 0 );

		ZonedDateTime past = reference.minusMonths( 1 ).minusHours( 1 );
		dummy = new FutureRelativePartialDummyEntity( past );

		constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 3 );
		assertCorrectPropertyPaths(
				constraintViolations, "localTime", "monthDay", "offsetTime"
		);

		ZonedDateTime future = reference.plusMonths( 1 ).plusHours( 1 );
		dummy = new FutureRelativePartialDummyEntity( future );

		constraintViolations = validator.validate( dummy );
		assertCorrectNumberOfViolations( constraintViolations, 0 );
	}

	@Test
	@SpecAssertion(section = "7", id = "m")
	public void testPastOrPresentConstraint() {
		ZonedDateTime reference = ZonedDateTime.of( 2016, 6, 6, 14, 26, 0, 0, TZ_BERLIN );

		ValidatorFactory validatorFactory = TestUtil.getConfigurationUnderTest()
				.clockProvider( new FixedClockProvider( reference ) )
				.buildValidatorFactory();
		Validator validator = validatorFactory.getValidator();

		PastDummyEntity pastDummy = new PastDummyEntity( reference );

		Set<ConstraintViolation<PastDummyEntity>> pastConstraintViolations = validator.validate( pastDummy );
		assertCorrectNumberOfViolations( pastConstraintViolations, 13 );
		assertCorrectPropertyPaths(
				pastConstraintViolations, "date", "calendar", "instant", "hijrahDate", "japaneseDate", "localDate", "localDateTime",
				"minguoDate", "offsetDateTime", "thaiBuddhistDate", "year", "yearMonth", "zonedDateTime"
		);

		PastOrPresentDummyEntity pastOrPresentDummy = new PastOrPresentDummyEntity();
		Set<ConstraintViolation<PastOrPresentDummyEntity>> pastOrPresentConstraintViolations = validator.validate( pastOrPresentDummy );
		assertCorrectNumberOfViolations( pastOrPresentConstraintViolations, 0 );

		pastOrPresentDummy = new PastOrPresentDummyEntity( reference );
		pastOrPresentConstraintViolations = validator.validate( pastOrPresentDummy );
		assertCorrectNumberOfViolations( pastOrPresentConstraintViolations, 0 );

		ZonedDateTime pastDate = ZonedDateTime.of( 2015, 5, 5, 13, 14, 0, 0, TZ_BERLIN );
		pastOrPresentDummy = new PastOrPresentDummyEntity( pastDate );
		pastOrPresentConstraintViolations = validator.validate( pastOrPresentDummy );
		assertCorrectNumberOfViolations( pastOrPresentConstraintViolations, 0 );

		ZonedDateTime futureDate = ZonedDateTime.of( 2017, 7, 7, 15, 32, 0, 0, TZ_BERLIN );
		pastOrPresentDummy = new PastOrPresentDummyEntity( futureDate );
		pastOrPresentConstraintViolations = validator.validate( pastOrPresentDummy );
		assertCorrectNumberOfViolations( pastConstraintViolations, 13 );
		assertCorrectPropertyPaths(
				pastConstraintViolations, "date", "calendar", "instant", "hijrahDate", "japaneseDate", "localDate", "localDateTime",
				"minguoDate", "offsetDateTime", "thaiBuddhistDate", "year", "yearMonth", "zonedDateTime"
		);
	}

	@Test
	@SpecAssertion(section = "7", id = "m")
	public void testPastOrPresentConstraintForRelativePartial() {
		ZonedDateTime reference = ZonedDateTime.of( 2016, 6, 6, 14, 26, 0, 0, TZ_BERLIN );

		ValidatorFactory validatorFactory = TestUtil.getConfigurationUnderTest()
				.clockProvider( new FixedClockProvider( reference ) )
				.buildValidatorFactory();
		Validator validator = validatorFactory.getValidator();

		PastRelativePartialDummyEntity pastDummy = new PastRelativePartialDummyEntity( reference );

		Set<ConstraintViolation<PastRelativePartialDummyEntity>> pastConstraintViolations = validator.validate( pastDummy );
		assertCorrectNumberOfViolations( pastConstraintViolations, 3 );
		assertCorrectPropertyPaths(
				pastConstraintViolations, "localTime", "monthDay", "offsetTime"
		);

		PastOrPresentRelativePartialDummyEntity pastOrPresentDummy = new PastOrPresentRelativePartialDummyEntity();
		Set<ConstraintViolation<PastOrPresentRelativePartialDummyEntity>> pastOrPresentConstraintViolations = validator.validate( pastOrPresentDummy );
		assertCorrectNumberOfViolations( pastOrPresentConstraintViolations, 0 );

		pastOrPresentDummy = new PastOrPresentRelativePartialDummyEntity( reference );
		pastOrPresentConstraintViolations = validator.validate( pastOrPresentDummy );
		assertCorrectNumberOfViolations( pastOrPresentConstraintViolations, 0 );

		ZonedDateTime pastDate = ZonedDateTime.of( 2015, 5, 5, 13, 14, 0, 0, TZ_BERLIN );
		pastOrPresentDummy = new PastOrPresentRelativePartialDummyEntity( pastDate );
		pastOrPresentConstraintViolations = validator.validate( pastOrPresentDummy );
		assertCorrectNumberOfViolations( pastOrPresentConstraintViolations, 0 );

		ZonedDateTime futureDate = ZonedDateTime.of( 2017, 7, 7, 15, 32, 0, 0, TZ_BERLIN );
		pastOrPresentDummy = new PastOrPresentRelativePartialDummyEntity( futureDate );
		pastOrPresentConstraintViolations = validator.validate( pastOrPresentDummy );
		assertCorrectNumberOfViolations( pastOrPresentConstraintViolations, 3 );
		assertCorrectPropertyPaths(
				pastOrPresentConstraintViolations, "localTime", "monthDay", "offsetTime"
		);
	}

	@Test
	@SpecAssertion(section = "7", id = "n")
	public void testFutureOrPresentConstraint() {
		ZonedDateTime reference = ZonedDateTime.of( 2016, 6, 6, 14, 26, 0, 0, TZ_BERLIN );

		ValidatorFactory validatorFactory = TestUtil.getConfigurationUnderTest()
				.clockProvider( new FixedClockProvider( reference ) )
				.buildValidatorFactory();
		Validator validator = validatorFactory.getValidator();

		FutureDummyEntity futureDummy = new FutureDummyEntity( reference );

		Set<ConstraintViolation<FutureDummyEntity>> futureConstraintViolations = validator.validate( futureDummy );
		assertCorrectNumberOfViolations( futureConstraintViolations, 13 );
		assertCorrectPropertyPaths(
				futureConstraintViolations, "date", "calendar", "instant", "hijrahDate", "japaneseDate", "localDate", "localDateTime",
				"minguoDate", "offsetDateTime", "thaiBuddhistDate", "year", "yearMonth", "zonedDateTime"
		);

		FutureOrPresentDummyEntity futureOrPresentDummy = new FutureOrPresentDummyEntity();
		Set<ConstraintViolation<FutureOrPresentDummyEntity>> futureOrPresentConstraintViolations = validator.validate( futureOrPresentDummy );
		assertCorrectNumberOfViolations( futureOrPresentConstraintViolations, 0 );

		futureOrPresentDummy = new FutureOrPresentDummyEntity( reference );
		futureOrPresentConstraintViolations = validator.validate( futureOrPresentDummy );
		assertCorrectNumberOfViolations( futureOrPresentConstraintViolations, 0 );

		ZonedDateTime futureDate = ZonedDateTime.of( 2017, 7, 7, 15, 32, 0, 0, TZ_BERLIN );
		futureOrPresentDummy = new FutureOrPresentDummyEntity( futureDate );
		futureOrPresentConstraintViolations = validator.validate( futureOrPresentDummy );
		assertCorrectNumberOfViolations( futureOrPresentConstraintViolations, 0 );

		ZonedDateTime pastDate = ZonedDateTime.of( 2015, 4, 3, 12, 20, 0, 0, TZ_BERLIN );
		futureOrPresentDummy = new FutureOrPresentDummyEntity( pastDate );
		futureOrPresentConstraintViolations = validator.validate( futureOrPresentDummy );
		assertCorrectNumberOfViolations( futureConstraintViolations, 13 );
		assertCorrectPropertyPaths(
				futureConstraintViolations, "date", "calendar", "instant", "hijrahDate", "japaneseDate", "localDate", "localDateTime",
				"minguoDate", "offsetDateTime", "thaiBuddhistDate", "year", "yearMonth", "zonedDateTime"
		);
	}

	@Test
	@SpecAssertion(section = "7", id = "n")
	public void testFutureOrPresentConstraintForRelativePartial() {
		ZonedDateTime reference = ZonedDateTime.of( 2016, 6, 6, 14, 26, 0, 0, TZ_BERLIN );

		ValidatorFactory validatorFactory = TestUtil.getConfigurationUnderTest()
				.clockProvider( new FixedClockProvider( reference ) )
				.buildValidatorFactory();
		Validator validator = validatorFactory.getValidator();

		FutureRelativePartialDummyEntity futureDummy = new FutureRelativePartialDummyEntity( reference );

		Set<ConstraintViolation<FutureRelativePartialDummyEntity>> futureConstraintViolations = validator.validate( futureDummy );
		assertCorrectNumberOfViolations( futureConstraintViolations, 3 );
		assertCorrectPropertyPaths(
				futureConstraintViolations, "localTime", "monthDay", "offsetTime"
		);

		FutureOrPresentRelativePartialDummyEntity futureOrPresentDummy = new FutureOrPresentRelativePartialDummyEntity();
		Set<ConstraintViolation<FutureOrPresentRelativePartialDummyEntity>> futureOrPresentConstraintViolations = validator.validate( futureOrPresentDummy );
		assertCorrectNumberOfViolations( futureOrPresentConstraintViolations, 0 );

		futureOrPresentDummy = new FutureOrPresentRelativePartialDummyEntity( reference );
		futureOrPresentConstraintViolations = validator.validate( futureOrPresentDummy );
		assertCorrectNumberOfViolations( futureOrPresentConstraintViolations, 0 );

		ZonedDateTime futureDate = ZonedDateTime.of( 2017, 7, 7, 15, 32, 0, 0, TZ_BERLIN );
		futureOrPresentDummy = new FutureOrPresentRelativePartialDummyEntity( futureDate );
		futureOrPresentConstraintViolations = validator.validate( futureOrPresentDummy );
		assertCorrectNumberOfViolations( futureOrPresentConstraintViolations, 0 );

		ZonedDateTime pastDate = ZonedDateTime.of( 2015, 4, 3, 12, 20, 0, 0, TZ_BERLIN );
		futureOrPresentDummy = new FutureOrPresentRelativePartialDummyEntity( pastDate );
		futureOrPresentConstraintViolations = validator.validate( futureOrPresentDummy );
		assertCorrectNumberOfViolations( futureOrPresentConstraintViolations, 3 );
		assertCorrectPropertyPaths(
				futureOrPresentConstraintViolations, "localTime", "monthDay", "offsetTime"
		);
	}

	private static class PastOrPresentDummyEntity {

		@Past(orPresent = true)
		private Calendar calendar;

		@Past(orPresent = true)
		private Date date;

		@Past(orPresent = true)
		private HijrahDate hijrahDate;

		@Past(orPresent = true)
		private Instant instant;

		@Past(orPresent = true)
		private JapaneseDate japaneseDate;

		@Past(orPresent = true)
		private LocalDate localDate;

		@Past(orPresent = true)
		private LocalDateTime localDateTime;

		@Past(orPresent = true)
		private MinguoDate minguoDate;

		@Past(orPresent = true)
		private OffsetDateTime offsetDateTime;

		@Past(orPresent = true)
		private ThaiBuddhistDate thaiBuddhistDate;

		@Past(orPresent = true)
		private Year year;

		@Past(orPresent = true)
		private YearMonth yearMonth;

		@Past(orPresent = true)
		private ZonedDateTime zonedDateTime;

		public PastOrPresentDummyEntity() {
		}

		public PastOrPresentDummyEntity(ZonedDateTime dateTime) {
			calendar = GregorianCalendar.from( dateTime );
			date = calendar.getTime();

			instant = dateTime.toInstant();
			localDateTime = dateTime.toLocalDateTime();

			hijrahDate = HijrahDate.from( dateTime );
			japaneseDate = JapaneseDate.from( dateTime );
			localDate = LocalDate.from( dateTime );
			minguoDate = MinguoDate.from( dateTime );
			offsetDateTime = dateTime.toOffsetDateTime();
			thaiBuddhistDate = ThaiBuddhistDate.from( dateTime );
			year = Year.from( dateTime );
			yearMonth = YearMonth.from( dateTime );
			zonedDateTime = dateTime;
		}
	}

	private static class PastOrPresentRelativePartialDummyEntity {

		@Past(orPresent = true)
		private LocalTime localTime;

		@Past(orPresent = true)
		private MonthDay monthDay;

		@Past(orPresent = true)
		private OffsetTime offsetTime;

		public PastOrPresentRelativePartialDummyEntity() {
		}

		public PastOrPresentRelativePartialDummyEntity(ZonedDateTime dateTime) {
			localTime = dateTime.toLocalTime();
			monthDay = MonthDay.from( dateTime );
			offsetTime = OffsetTime.from( dateTime );
		}
	}

	private static class FutureOrPresentDummyEntity {

		@Future(orPresent = true)
		private Calendar calendar;

		@Future(orPresent = true)
		private Date date;

		@Future(orPresent = true)
		private HijrahDate hijrahDate;

		@Future(orPresent = true)
		private Instant instant;

		@Future(orPresent = true)
		private JapaneseDate japaneseDate;

		@Future(orPresent = true)
		private LocalDate localDate;

		@Future(orPresent = true)
		private LocalDateTime localDateTime;

		@Future(orPresent = true)
		private MinguoDate minguoDate;

		@Future(orPresent = true)
		private OffsetDateTime offsetDateTime;

		@Future(orPresent = true)
		private ThaiBuddhistDate thaiBuddhistDate;

		@Future(orPresent = true)
		private Year year;

		@Future(orPresent = true)
		private YearMonth yearMonth;

		@Future(orPresent = true)
		private ZonedDateTime zonedDateTime;

		private FutureOrPresentDummyEntity() {
		}

		private FutureOrPresentDummyEntity(ZonedDateTime dateTime) {
			calendar = GregorianCalendar.from( dateTime );
			date = calendar.getTime();

			instant = dateTime.toInstant();
			localDateTime = dateTime.toLocalDateTime();

			hijrahDate = HijrahDate.from( dateTime );
			japaneseDate = JapaneseDate.from( dateTime );
			localDate = LocalDate.from( dateTime );
			minguoDate = MinguoDate.from( dateTime );
			offsetDateTime = dateTime.toOffsetDateTime();
			thaiBuddhistDate = ThaiBuddhistDate.from( dateTime );
			year = Year.from( dateTime );
			yearMonth = YearMonth.from( dateTime );
			zonedDateTime = dateTime;
		}
	}

	private static class FutureOrPresentRelativePartialDummyEntity {

		@Future(orPresent = true)
		private LocalTime localTime;

		@Future(orPresent = true)
		private MonthDay monthDay;

		@Future(orPresent = true)
		private OffsetTime offsetTime;

		public FutureOrPresentRelativePartialDummyEntity() {
		}

		public FutureOrPresentRelativePartialDummyEntity(ZonedDateTime dateTime) {
			localTime = dateTime.toLocalTime();
			monthDay = MonthDay.from( dateTime );
			offsetTime = OffsetTime.from( dateTime );
		}
	}

}
