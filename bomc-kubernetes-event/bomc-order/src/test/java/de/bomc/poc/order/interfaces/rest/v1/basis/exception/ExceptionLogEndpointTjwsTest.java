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
package de.bomc.poc.order.interfaces.rest.v1.basis.exception;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import de.bomc.poc.exception.core.app.AppRuntimeException;

import de.bomc.poc.order.CategoryBasisUnitTest;
import de.bomc.poc.order.application.basis.log.ExceptionLogController;
import de.bomc.poc.order.application.basis.log.ExceptionLogEntityListFactory;
import de.bomc.poc.order.application.internal.ApplicationUserEnum;
import de.bomc.poc.order.domain.shared.DomainObjectUtils;
import de.bomc.poc.order.interfaces.rest.PortFinder;
import de.bomc.poc.order.interfaces.rest.v1.basis.exception.ExceptionLogRestEndpointImpl;
import de.bomc.poc.order.interfaces.rest.v1.basis.exception.dto.ExceptionLogDTO;

/**
 * Tests the {@link ExceptionLogEndpointTjwsTest}.
 * 
 * <pre>
 *     mvn clean install -Dtest=ExceptionLogEndpointTjwsTest
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 07.12.2018
 */
@SuppressWarnings("deprecation")
@RunWith(MockitoJUnitRunner.class)
@Category(CategoryBasisUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExceptionLogEndpointTjwsTest {

    private static final String LOG_PREFIX = "ExceptionLogEndpointTjwsTest#";
    private static final Logger LOGGER = Logger.getLogger(ExceptionLogEndpointTjwsTest.class);
    private static int port;
    private static TJWSEmbeddedJaxrsServer server;
    @Mock
    private Logger logger;
    @Mock
    private ExceptionLogController exceptionLogControllerEJB;
    @InjectMocks
    private static final ExceptionLogRestEndpointImpl sut = new ExceptionLogRestEndpointImpl();
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    // _______________________________________________
    // Test data
    // -----------------------------------------------
    private static final String JSON_STRING = "[{\"shortErrorCodeDescription\":\"Describes an error\",\"category\":\"category\",\"responseStatus\":\"ACCEPTED\",\"exceptionUuid\":\"exceptionUuid0\",\"createDateTime\":\"04.11.2018 11:00:00\"},{\"shortErrorCodeDescription\":\"Describes an error\",\"category\":\"category\",\"responseStatus\":\"ACCEPTED\",\"exceptionUuid\":\"exceptionUuid1\",\"createDateTime\":\"04.11.2018 11:00:00\"}]";

    @Before
    public void init() {
        //
    }

    @BeforeClass
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void beforeClass() throws Exception {
        port = PortFinder.findPort();

        server = new TJWSEmbeddedJaxrsServer();
        server.setPort(port);
        server.getDeployment().setResources((List) Collections.singletonList(sut));

        server.start();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ExceptionLogEndpointTjwsTest#test010_readStoredExceptions_pass
     *
     * <b><code>test010_readStoredExceptions_pass</code>:</b><br>
     *  Tests the behavior reading the exceptions from db by the endpoint and mocked <code>ExceptionLogController</code>.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *  - A list of <code>ExceptionLogDTO</code>s must be created. 
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *
     * <b>Postconditions:</b><br>
     *  A string is received.
     * </pre>
     * 
     * @throws IOException
     */
    @Test
    public void test010_readStoredExceptions_pass() throws IOException {
        LOGGER.debug(LOG_PREFIX + "test010_readStoredExceptions_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        when(this.exceptionLogControllerEJB.readStoredExceptions(ApplicationUserEnum.SYSTEM_USER.name()))
                .thenReturn(JSON_STRING);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        final Response response = sut.getStoredExceptions(ApplicationUserEnum.SYSTEM_USER.name());

        assertThat(response, notNullValue());
        assertThat(response.getStatus(), equalTo(Status.OK.getStatusCode()));

        final String jsonArray = (String) response.getEntity();
        assertThat(jsonArray, notNullValue());

        final ObjectMapper mapper = new ObjectMapper();
        // Register a deserializer for localDate mapping.
        mapper.registerModule(new SimpleModule().addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DomainObjectUtils.DATE_PATTERN))));

        // Transform json string to array.
        final ExceptionLogDTO[] exceptionLogDTOAsArray = (ExceptionLogDTO[]) mapper.readValue(jsonArray,
                ExceptionLogDTO[].class);

        // Do asserts.
        Arrays.stream(exceptionLogDTOAsArray).forEach(exceptionLogDTO -> {
            assertThat(exceptionLogDTO.getCategory(), equalTo(ExceptionLogEntityListFactory.CATEGORY));
            assertThat(exceptionLogDTO.getShortErrorCodeDescription(),
                    equalTo(ExceptionLogEntityListFactory.SHORT_ERROR_CODE_DESCRIPTION));
            assertThat(exceptionLogDTO.getCreateDateTime(), equalTo(ExceptionLogEntityListFactory.CREATE_DATE_TIME));
            assertThat(exceptionLogDTO.getResponseStatus(), equalTo(ExceptionLogEntityListFactory.RESPONSE_STATUS));
        });
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ExceptionLogEndpointTjwsTest#test020_readStoredExceptions_fail
     *
     * <b><code>test020_readStoredExceptions_fail</code>:</b><br>
     *  Tests the behavior reading the exceptions from db by the endpoint and mocked <code>ExceptionLogController</code>.
     *  The <code>ExceptionLogController</code> throws a <code>IOException</code>.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *  - A list of <code>ExceptionLogDTO</code>s must be created. 
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *  - A IOException is thrown and catched by the endpoint.
     *  - The endpopint throws a AppRuntimeException up to the client. 
     *
     * <b>Postconditions:</b><br>
     *  A AppruntimeException is thrown.
     * </pre>
     * 
     * @throws AppruntimeException
     */
    @Test
    public void test020_readStoredExceptions_fail() throws IOException {
        LOGGER.debug(LOG_PREFIX + "test020_readStoredExceptions_fail");

        this.thrown.expect(AppRuntimeException.class);

        // ___________________________________________
        // GIVEN
        // -------------------------------------------

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        when(this.exceptionLogControllerEJB.readStoredExceptions(ApplicationUserEnum.SYSTEM_USER.name()))
                .thenThrow(IOException.class);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        sut.getStoredExceptions(ApplicationUserEnum.SYSTEM_USER.name());
    }
} // end class
