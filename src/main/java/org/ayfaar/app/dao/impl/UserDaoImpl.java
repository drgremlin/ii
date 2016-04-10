package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.UserDao;
import org.ayfaar.app.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserDaoImpl extends AbstractHibernateDAO<User> implements UserDao{
    public UserDaoImpl() {
        super(User.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public User save(User user) {

        currentSession().saveOrUpdate(user);
        return user;
    }
}
