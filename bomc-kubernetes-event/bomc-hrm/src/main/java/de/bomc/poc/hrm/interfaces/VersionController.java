/**
 * Project: hrm
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
package de.bomc.poc.hrm.interfaces;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.bomc.poc.hrm.application.log.method.Loggable;
import de.bomc.poc.hrm.application.metrics.aop.Metric;
import de.bomc.poc.hrm.config.git.HrmGitConfig;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The controller for reading version and other data from 'git.properties' file.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@RestController
@RequestMapping(value = "/git")
@Api(tags = "Git properties", value = "Git version", description = "Show git repository information.", produces = "application/vnd.hrm-version-v1+json;charset=UTF-8")
public class VersionController {

	public static final String MEDIA_TYPE_JSON_V1 = "application/vnd.hrm-version-v1+json;charset=UTF-8";

	// _______________________________________________
	// Constants
	// -----------------------------------------------
	protected static final String GIT_VERSION = "git_version";
	protected static final String GIT_COMMIT_ID = "git_commit_id";
	protected static final String GIT_COMMIT_MESSAGE = "git_commit_message";
	// _______________________________________________
	// Member variables
	// -----------------------------------------------
	private final HrmGitConfig hrmGitConfig;

	public VersionController(final HrmGitConfig hrmGitConfig) {

		this.hrmGitConfig = hrmGitConfig;
	}

	// _______________________________________________
	// API
	// -----------------------------------------------
	// -at Timed: Annotation will produce bomc.metrics.requests.timed.version
	// instead of http.server.requests (see value’s value).
	// Then histogram and percentiles is enabled for two values (0.95 and 0.99).
	// Lastly, a another tag for this endpoint is added, which is version tag.
	@Timed(value = "bomc.hrm.version.controller.get.version", histogram = true, percentiles = { 0.95, 0.99 }, extraTags = {
	        "info", "extra_tags" })
	@Metric(timer = false)
	@ApiOperation(value = "Get current deployed version.", response = Map.class, notes = "The version is read from 'git.properties' file.", produces = MEDIA_TYPE_JSON_V1)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved the version."),
	        @ApiResponse(code = 401, message = "Not authorized to view the resource."),
	        @ApiResponse(code = 403, message = "Accessing the resource that trying to reach is forbidden."),
	        @ApiResponse(code = 404, message = "The resource that trying to reach is not found."),
	        @ApiResponse(code = 500, message = "A internal application error.", response = ApiErrorResponseObject.class) })
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "/version", produces = MEDIA_TYPE_JSON_V1)
	@Transactional(readOnly = true)
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public @ResponseBody Map<String, String> getVersion() {

		final Map<String, String> resultMap = new HashMap<>();
		resultMap.put(GIT_VERSION, this.hrmGitConfig.getVersion());
		resultMap.put(GIT_COMMIT_MESSAGE, this.hrmGitConfig.getCommitMessage());
		resultMap.put(GIT_COMMIT_ID, this.hrmGitConfig.getCommitId());

		return resultMap;
	}
}
