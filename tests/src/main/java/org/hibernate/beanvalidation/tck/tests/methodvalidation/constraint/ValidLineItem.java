/**
 * Bean Validation TCK
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.beanvalidation.tck.tests.methodvalidation.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import org.hibernate.beanvalidation.tck.tests.methodvalidation.model.LineItem;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;

/**
 * @author Gunnar Morling
 */
@Target({ METHOD, CONSTRUCTOR, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidLineItem.Validator.class)
@Documented
public @interface ValidLineItem {
	String message() default "{ValidLineItem.message}";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };

	public class Validator implements ConstraintValidator<ValidLineItem, LineItem> {

		@Override
		public boolean isValid(LineItem object, ConstraintValidatorContext context) {
			return false;
		}
	}
}
