package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.BasicCrudDao;
import org.ayfaar.app.dao.UserDao;
import org.ayfaar.app.model.User;
import org.ayfaar.app.services.moderation.AccessLevel;
import org.ayfaar.app.utils.authentication.CustomAuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.IOException;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Inject
    UserDao userDao;
    @Inject
    BasicCrudDao<User> basicCrudDao;
    @Inject
    CustomAuthenticationProvider customAuthenticationProvider;

    @RequestMapping(method = RequestMethod.POST)
    /**
     * Регистрируем нового пользователя и/или (если такой уже есть) назначаем его текущим для этой сессии
     */
    public void auth(User user) throws IOException{
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
        createOrUpdateUser(user);
        setAuthentication(user);
    }

    private void createOrUpdateUser(User user){
        user.setRole(AccessLevel.ROLE_EDITOR);
        basicCrudDao.save(user);
    }

    public User getCurrentUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }else return null;
    }

    @RequestMapping("current")
    public User getCurrentUser(@AuthenticationPrincipal User currentUser){
        return currentUser;
    }

    private Authentication setAuthentication(User user) {

        Authentication request = new UsernamePasswordAuthenticationToken(user, null);
        Authentication authentication = customAuthenticationProvider.authenticate(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
