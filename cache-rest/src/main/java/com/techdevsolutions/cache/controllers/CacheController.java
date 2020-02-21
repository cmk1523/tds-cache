package com.techdevsolutions.cache.controllers;

import com.techdevsolutions.cache.service.CacheService;
import com.techdevsolutions.common.beans.rest.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cache")
@Tag(name = "cache", description = "Cache operations")
public class CacheController extends BaseController {
    private CacheService cacheService;

    @Autowired
    public CacheController(Environment environment, CacheService cacheService) {
        super(environment);
        this.cacheService = cacheService;
    }

    @ResponseBody
    @RequestMapping(value = "{key}", method = RequestMethod.GET)
    public Object get(HttpServletRequest request,
                         @PathVariable String key
    ) {
        try {
            String i = this.cacheService.get(key);
            this.logger.info("Get - key: " + key + ", value: " + i);
            return new Response(i, this.getTimeTook(request), this.getUser(request), new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return this.generateErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
        }
    }

    @ResponseBody
    @RequestMapping(value = "{key}/{value}", method = RequestMethod.GET)
    public Object set(HttpServletRequest request,
                         @PathVariable String key,
                         @PathVariable String value
    ) {
        try {
            this.logger.info("Set - key: " + key + ", value: "+ value);
            this.cacheService.set(key, value);
            return new Response(null, this.getTimeTook(request), this.getUser(request), new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return this.generateErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
        }
    }

    @ResponseBody
    @RequestMapping(value = "{key}", method = RequestMethod.POST)
    public Object setPost(HttpServletRequest request,
                         @PathVariable String key,
                         @RequestBody String value
    ) {
        try {
            this.logger.info("Set - key: " + key + ", value: "+ value);
            this.cacheService.set(key, value);
            return new Response(null, this.getTimeTook(request), this.getUser(request), new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return this.generateErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
        }
    }

}