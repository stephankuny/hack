/**
 * Project: MY_POC
 * <p/>
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * <p/>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.auth.pact.usermanagement;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRule;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;
import de.bomc.poc.api.generic.Parameter;
import de.bomc.poc.api.generic.transfer.request.RequestObjectDTO;
import de.bomc.poc.api.generic.types.StringType;
import de.bomc.poc.api.http.CustomHttpResponseCode;
import de.bomc.poc.auth.rest.application.JaxRsActivator;
import de.bomc.poc.auth.rest.endpoint.v1.usermanagement.AuthUserManagementRestEndpoint;
import de.bomc.poc.rest.ext.RequestObjDtoMessageBodyWriter;
import de.bomc.poc.rest.filter.authorization.AuthorizationTokenHeaderRequestFilter;
import de.bomc.poc.rest.logger.ResteasyClientLogger;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Creates the consumer contract for {@link AuthUserManagementRestEndpoint}}
 * <pre>
 *	mvn clean install -Pcdc-tests
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UsermanagementV1ConsumerContractPactTest {
	private static final Logger LOGGER = Logger.getLogger(UsermanagementV1ConsumerContractPactTest.class);
	private static final String BASE_URI = "/auth-microservice" + "/" + JaxRsActivator.APPLICATION_PATH;
    private static final String USERMANGEMENT_V1_FIND_ALL_ROLES_BY_USERNAME_REST_PROVIDER = "UsermanagementV1FindAllRolesByUsernameRestProvider";
    @Rule
    public PactProviderRule rule_FindAllRolesByUsername = new PactProviderRule(USERMANGEMENT_V1_FIND_ALL_ROLES_BY_USERNAME_REST_PROVIDER, "localhost", 8080, this);

    /**
     * @param builder
     * @return
     */
    @Pact(provider = USERMANGEMENT_V1_FIND_ALL_ROLES_BY_USERNAME_REST_PROVIDER, consumer = "AuthUserManagementRestEndpoint")
    public PactFragment createFindAllRolesByUsernameV1Fragment(final PactDslWithProvider builder) {
        return builder.given("Describe the state the provider needs to be in for the pact test to be verified")
                      .uponReceiving("Description of the request that is expected to be received")
                      .path(BASE_URI + JaxRsActivator.USERMANAGEMENT_ENDPOINT_PATH + "/roles-by-username")
                      .method("POST")
                      .headers(this.requestHeaders())
                      .body(this.requestXml(), AuthUserManagementRestEndpoint.MEDIA_TYPE_XML_V1)
                      .willRespondWith()
                      .headers(this.responseHeaders())
                      .body(this.responseXml(), AuthUserManagementRestEndpoint.MEDIA_TYPE_XML_V1)
                      .status(299)
                      .toFragment();
    }

    private Map<String, String> requestHeaders() {
        final Map<String, String> requestHeaders = new HashMap<>();
        //
        // The request header is not set here. Because the necessary header informations are set by the resteasy proxy framework.
        //
        requestHeaders.put("X-BOMC-AUTHORIZATION", "BOMC_USER");
        return Collections.unmodifiableMap(requestHeaders);
    }

    private Map<String, String> responseHeaders() {
        final Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", AuthUserManagementRestEndpoint.MEDIA_TYPE_XML_V1);

        return Collections.unmodifiableMap(responseHeaders);
    }

    /**
     * mvn clean install -Dtest=UsermanagementV1ConsumerContractPactTest#test01_findAllRolesByUsername_Pass
     * @throws Exception
     */
    @Test
    @PactVerification(USERMANGEMENT_V1_FIND_ALL_ROLES_BY_USERNAME_REST_PROVIDER)
    public void test01_findAllRolesByUsername_Pass() throws Exception {
        System.out.println("UsermanagementV1ConsumerContractPactTest#test01_findAllRolesByUsername_Pass");

		final ResteasyClient
            client = new ResteasyClientBuilder().establishConnectionTimeout(100, TimeUnit.SECONDS).socketTimeout(2, TimeUnit.SECONDS).build();
		client.register(new ResteasyClientLogger(LOGGER, true));
		client.register(new AuthorizationTokenHeaderRequestFilter("BOMC_USER"));
        client.register(RequestObjDtoMessageBodyWriter.class);
        final ResteasyWebTarget webTarget = client.target(rule_FindAllRolesByUsername.getConfig().url() + BASE_URI);
        final AuthUserManagementRestEndpoint proxy = webTarget.proxy(AuthUserManagementRestEndpoint.class);

        @SuppressWarnings("deprecation")
		final Response response = proxy.findAllRolesByUsername(getReRequestObjectDTO_With_Username());

        assertThat(response.getStatus(), is(equalTo(CustomHttpResponseCode.DEPRECATED_API)));
    }

    /**
     * @return RequestObjectDTO with 'username' as given parameter.
     */
    private RequestObjectDTO getReRequestObjectDTO_With_Username() {
        return RequestObjectDTO.with(new Parameter("username", new StringType("Default-System_user")));
    }

    private String requestXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
               + "<requestObjectDTO>"
               + "<parameters>"
               + "<entry>"
               + "<key xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\">username</key>"
               + "<value xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"parameter\" name=\"username\">"
               + "<type xsi:type=\"stringType\" value=\"Default-System_user\"/>"
               + "</value>"
               + "</entry>"
               + "</parameters>"
               + "</requestObjectDTO>";
    }

    private String responseXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><generic-entity><collection><ResponseObjectDTO><parameters><entry><key xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
               + "xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\">roleId</key><value xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"parameter\" name=\"roleId\"><type "
               + "xsi:type=\"longType\" value=\"3\"/></value></entry><entry><key xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" "
               + "xsi:type=\"xs:string\">roleName</key><value xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"parameter\" name=\"roleName\"><type xsi:type=\"stringType\" "
               + "value=\"Default-System_user\"/></value></entry><entry><key xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" "
               + "xsi:type=\"xs:string\">roleDescription</key><value xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"parameter\" name=\"roleDescription\"><type xsi:type=\"stringType\" value=\"This "
               + "role allows restricted access to the system\"/></value></entry></parameters></ResponseObjectDTO></collection></generic-entity>";
    }
}
