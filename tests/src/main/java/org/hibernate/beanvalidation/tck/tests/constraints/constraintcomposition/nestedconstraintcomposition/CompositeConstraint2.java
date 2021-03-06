/**
 * Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.tests.constraints.constraintcomposition.nestedconstraintcomposition;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import javax.validation.constraints.Pattern;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

/**
 * @author Hardy Ferentschik
 */
@Pattern(regexp = "abc")
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = { })
@NestedConstraintSingleViolation
@ReportAsSingleViolation
public @interface CompositeConstraint2 {
	public abstract String message() default "CompositeConstraint2 failed.";

	public abstract Class<?>[] groups() default { };

	public abstract Class<? extends Payload>[] payload() default { };
}
