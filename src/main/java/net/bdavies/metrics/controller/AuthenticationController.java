package net.bdavies.metrics.controller;

import lombok.RequiredArgsConstructor;
import net.bdavies.metrics.model.ApiUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    @GetMapping("/whoami")
    public ApiUser whoami() {
        return (ApiUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
