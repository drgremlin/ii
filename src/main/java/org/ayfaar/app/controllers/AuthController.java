package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.UserDao;
import org.ayfaar.app.dao.impl.UserDaoImpl;
import org.ayfaar.app.model.CurrentUser;
import org.ayfaar.app.model.User;

import org.ayfaar.app.model.UserRoleEnum;
import org.ayfaar.app.services.user.CurrentUserDetailsService;
import org.ayfaar.app.services.user.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    UserPresentation userPresentation = new UserPresentation();

    CurrentUserDetailsService currentUserDetailsService;

    @Inject
    public AuthController(CurrentUserDetailsService currentUserDetailsService) {
        this.currentUserDetailsService = currentUserDetailsService;

    }

    String name;
    ResponseEntity responseEntity;
    List<String> list = new ArrayList<>();
    @RequestMapping(method = RequestMethod.POST)
    /**
     * Регистрируем нового пользователя и/или (если такой уже есть) назначаем его текущим для этой сессии
     */

    public void auth(@RequestParam Map<String,String> requestParams) throws IOException { //@RequestParam Map<String,String> requestParams
        /**
         Пример входных данных (requestParams):

         access_token:CAANCEx9hQ8ABACe5zBAPE1fThMsaJDHQ0oolOvZCsiOAoFgbj65BiZC5qFG557wYl71CRLZBBipi1JeZCZABkeD7PuurKplra04wvaGSiNnHdnWQZAqZBt1sLtps38DDOJ0RAUNlSDKnMjAkt7bZClUtxLCCF1lQk4NLIXMtuxXiKkLCnojk7KtoQbZBRbPTqzdadfbifnGUrOAZDZD
         email:sllouyssgort@gmail.com
         first_name:Sllouyssgort
         id:1059404344124694
         last_name:Smaay-Grriyss
         name:Sllouyssgort Smaay-Grriyss
         picture:https://graph.facebook.com/1059404344124694/picture
         thumbnail:https://graph.facebook.com/1059404344124694/picture
         timezone:3
         verified:true
         auth_provider:vk
         */
//        userPresentation.getEmail();
//        userPresentation.getFirstname();
//        userPresentation.getId();
//        userPresentation.getLastname();

        //userPresentation.getThumbnail();
        //userPresentation.getTimezone();
        //userPresentation.getVerified();
        //userPresentation.getAuthProvider();

        userPresentation.email = requestParams.get("email");
        userPresentation.firstname = requestParams.get("first_name");
        userPresentation.id = Long.valueOf(requestParams.get("id"));
        userPresentation.lastname = requestParams.get("last_name");
//
//        userPresentation.thumbnail = requestParams.get("thumbnail");
//        userPresentation.timezone = requestParams.get("timezone");
//        userPresentation.verified = requestParams.get("verified");
//        userPresentation.authProvider = requestParams.get("auth_provider");

        createOrUpdateUser();

    }

    @RequestMapping(value = "/current-user")
    public UserPresentation getCurrentUser() {
        currentUserDetailsService.loadUserByUsername(userPresentation.email);
        return userPresentation;
    }

    public class UserPresentation {
        private Long id;
        private String firstname;
        private String lastname;
        private String email;
//        public String thumbnail;
//        public String timezone;
//        public String verified;
//        public String authProvider;

        UserPresentation() {
        }


        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }
//
        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }
//
        public String getEmail() {
            return email;
        }
//
        public void setEmail(String email) {
            this.email = email;
        }
//
//        public String getThumbnail() {
//            return thumbnail;
//        }
//
//        public void setThumbnail(String thumbnail) {
//            this.thumbnail = thumbnail;
//        }
//
//        public String getTimezone() {
//            return timezone;
//        }
//
//        public void setTimezone(String timezone) {
//            this.timezone = timezone;
//        }
//
//        public String getVerified() {
//            return verified;
//        }
//
//        public void setVerified(String verified) {
//            this.verified = verified;
//        }
//
//        public String getAuthProvider() {
//            return authProvider;
//        }
//
//        public void setAuthProvider(String authProvider) {
//            this.authProvider = authProvider;
//        }
    }


    @RequestMapping(value = "/principal")
    public String principal(Principal principal, Model model) {
        model.addAttribute("content", "Principal: " + (principal == null ? "null" : principal.getName()));
        name = principal.getName();
        return name;
    }

    @Inject
    UserServiceImpl userService;

    public void createOrUpdateUser(){
        userService.createOrUpdate(userPresentation);
    } //тут сначала я сделал чтоб сразу create юзера, но потом переделал с userPresentation, если надо вернем назад))

}
