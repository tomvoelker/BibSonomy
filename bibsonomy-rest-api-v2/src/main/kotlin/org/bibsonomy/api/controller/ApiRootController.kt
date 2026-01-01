package org.bibsonomy.api.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Controller that redirects the API root to Swagger UI documentation.
 *
 * Visiting /api/v2 or /api/v2/ will redirect to the interactive API docs.
 * Swagger UI is served at the standard /swagger-ui.html path by SpringDoc.
 */
@Controller
@RequestMapping("/api/v2")
class ApiRootController {

    @GetMapping("", "/")
    fun redirectToSwagger(): String {
        return "redirect:/swagger-ui.html"
    }
}
