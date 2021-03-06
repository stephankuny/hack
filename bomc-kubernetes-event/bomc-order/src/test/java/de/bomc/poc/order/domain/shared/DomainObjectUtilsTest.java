/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.order.domain.shared;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import de.bomc.poc.order.CategoryFastUnitTest;

/**
 * Tests the {@link DomainObjectUtils}.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@Category(CategoryFastUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DomainObjectUtilsTest {

    private static final String LOG_PREFIX = "DomainObjectUtilsTest#";
    private static final Logger LOGGER = Logger.getLogger(DomainObjectUtilsTest.class);
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void test010_nullSafe_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_nullSafe_pass");

        final String actual = "actual";
        final String safe = "safe";

        final String returnActual = DomainObjectUtils.nullSafe(actual, safe);
        assertThat(returnActual, equalTo(actual));

        final String returnSafe = DomainObjectUtils.nullSafe(null, safe);
        assertThat(returnSafe, equalTo(safe));
    }

    @Test
    public void test020_randomNumber_pass() {
        LOGGER.debug(LOG_PREFIX + "test020_randomNumber_pass");

        final long min = 12L;
        final long max = 42L;

        final long randomNumber = DomainObjectUtils.getRandomNumberInRange(min, max);

        assertThat(randomNumber, greaterThanOrEqualTo(min));
        assertThat(randomNumber, lessThanOrEqualTo(max));
    }

    @Test
    public void test030_randomNumber_fail() {
        LOGGER.debug(LOG_PREFIX + "test030_randomNumber_fail");

        thrown.expect(IllegalArgumentException.class);

        final long min = 12L;
        final long max = 42L;

        DomainObjectUtils.getRandomNumberInRange(max, min);
    }

    @Test
    public void test040_dateTimeFormatter_pass() {
        LOGGER.debug(LOG_PREFIX + "test040_dateTimeFormatter_pass");

        final int year = 2018;
        final int month = 11;
        final int dayOfMonth = 4;
        
        final LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(year, month, dayOfMonth), LocalTime.now());
        
        final String strDateTime = DomainObjectUtils.localDateTimeToString(localDateTime);

        assertThat(strDateTime, containsString(Integer.toString(dayOfMonth)));
        assertThat(strDateTime, containsString(Integer.toString(month)));
        assertThat(strDateTime, containsString(Integer.toString(year)));
    }
    
    @Test
    public void test050_localDateTimeToDate_pass() {
        LOGGER.debug(LOG_PREFIX + "test050_localDateTimeToDate_pass");
        
        final Date date = DomainObjectUtils.convertLocalDateTimeToDate(LocalDateTime.now());
        
        System.out.println(date);
    }
}
